package servlet.webhook;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.ShippingMethod;
import beans.Shipment;
import beans.ShipmentTracking;
import dao.ShipmentDAO;
import dao.ShipmentTrackingDAO;
import dao.ShippingMethodDAO;
import api.GHNService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ShipmentService;
import utils.ShippingStatus;

@WebServlet(name = "GHNWebhookServlet", urlPatterns = {"/webhook/ghn", "/api/webhook/ghn"})
public class GHNWebhookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final GHNService ghnService = new GHNService();
    private final ShipmentTrackingDAO trackingDAO = new ShipmentTrackingDAO();
    private final ShipmentDAO shipmentDAO = new ShipmentDAO();
    private final ShippingMethodDAO methodDAO = new ShippingMethodDAO();
    private final ShipmentService shipmentService = new ShipmentService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String payload = sb.toString();
        System.out.println("GHN Webhook received: " + payload);

        try {
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            processGHNWebhook(json);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void processGHNWebhook(JsonObject json) {
        try {
            String action = json.has("action") ? json.get("action").getAsString() : "";

            switch (action) {
                case "update_status":
                    handleUpdateStatus(json);
                    break;
                case "order_created":
                    handleOrderCreated(json);
                    break;
                case "order_cancelled":
                    handleOrderCancelled(json);
                    break;
                default:
                    if (json.has("order_code")) {
                        handleOrderStatusUpdate(json);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUpdateStatus(JsonObject json) {
        String orderCode = json.has("order_code") ? json.get("order_code").getAsString() : "";
        String status = json.has("status") ? json.get("status").getAsString() : "";
        String location = json.has("location") ? json.get("location").getAsString() : "";
        String note = json.has("note") ? json.get("note").getAsString() : "";

        updateShipmentFromGHN(orderCode, status, location, note);
    }

    private void handleOrderCreated(JsonObject json) {
        String orderCode = json.has("order_code") ? json.get("order_code").getAsString() : "";
        System.out.println("GHN Order Created: " + orderCode);
    }

    private void handleOrderCancelled(JsonObject json) {
        String orderCode = json.has("order_code") ? json.get("order_code").getAsString() : "";
        String reason = json.has("reason") ? json.get("reason").getAsString() : "";

        Shipment shipment = shipmentDAO.getByTrackingCode(orderCode);
        if (shipment != null) {
            shipmentService.addTrackingEvent(
                    shipment.getId(),
                    ShippingStatus.CANCELLED,
                    "Hủy đơn: " + reason,
                    "Hệ thống GHN",
                    "GHN Webhook"
            );
        }
    }

    private void handleOrderStatusUpdate(JsonObject json) {
        String orderCode = json.has("order_code") ? json.get("order_code").getAsString() : "";
        String status = json.has("status") ? json.get("status").getAsString() : "";
        String location = json.has("location") ? json.get("location").getAsString() : "";
        String note = json.has("description") ? json.get("description").getAsString() : "";

        updateShipmentFromGHN(orderCode, status, location, note);
    }

    private void updateShipmentFromGHN(String orderCode, String ghnStatus, String location, String note) {
        Shipment shipment = shipmentDAO.getByTrackingCode(orderCode);
        if (shipment == null) {
            System.out.println("Shipment not found for order code: " + orderCode);
            return;
        }

        String internalStatus = ghnService.convertGHNStatusToInternal(ghnStatus);

        shipmentService.addTrackingEvent(
                shipment.getId(),
                internalStatus,
                note,
                location,
                "GHN Webhook"
        );

        System.out.println("Updated shipment " + shipment.getId() + " status to: " + internalStatus);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\": \"active\", \"service\": \"GHN Webhook\"}");
    }
}
