package servlet.webhook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.Shipment;
import beans.ShipmentTracking;
import dao.ShipmentTrackingDAO;
import service.ShipmentService;
import utils.ShippingStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "GHNWebhookServlet", value = "/webhook/ghn")
public class GHNWebhookServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String WEBHOOK_TOKEN = "EAKI11D5A87E123";
	private final ShipmentService shipmentService = new ShipmentService();
	private final ShipmentTrackingDAO trackingDAO = new ShipmentTrackingDAO();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String token = request.getHeader("Token");
		if (token == null || !token.equals(WEBHOOK_TOKEN)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{\"success\": false, \"message\": \"Unauthorized\"}");
			return;
		}

		String body = request.getReader().lines().collect(Collectors.joining());
		System.out.println("[GHN Webhook] Received: " + body);

		try {
			JsonObject payload = JsonParser.parseString(body).getAsJsonObject();
			processGHNPayload(payload);
			response.getWriter().write("{\"success\": true}");
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
		}
	}

	private void processGHNPayload(JsonObject payload) throws Exception {
		if (payload.has("order_code")) {
			String orderCode = payload.get("order_code").getAsString();
			Shipment shipment = shipmentService.getByTrackingCode(orderCode);

			if (shipment == null) {
				System.out.println("[GHN Webhook] Shipment not found for order_code: " + orderCode);
				return;
			}

			String newStatus = mapGHNStatus(payload);
			String note = payload.has("note") ? payload.get("note").getAsString() : "";
			String location = "";

			if (payload.has("location")) {
				location = payload.get("location").getAsString();
			} else if (payload.has("city_name")) {
				location = payload.get("city_name").getAsString();
			}

			if (newStatus != null && !newStatus.isEmpty()) {
				ShipmentTracking tracking = new ShipmentTracking();
				tracking.setShipmentId(shipment.getId());
				tracking.setStatus(newStatus);
				tracking.setNote(note);
				tracking.setLocation(location);
				tracking.setUpdatedBy("GHN Webhook");
				tracking.setUpdatedAt(LocalDateTime.now());

				trackingDAO.insert(tracking);
				shipmentService.updateStatus(shipment.getId(), newStatus);

				System.out.println("[GHN Webhook] Updated shipment " + shipment.getId() + " to status: " + newStatus);
			}
		}
	}

	private String mapGHNStatus(JsonObject payload) {
		if (!payload.has("status")) {
			return null;
		}

		String ghnStatus = payload.get("status").getAsString().toLowerCase();

		switch (ghnStatus) {
			case "picking":
			case "pickup":
			case "picked":
				return ShippingStatus.PICKED_UP;
			case "shipping":
			case "intransit":
				return ShippingStatus.IN_TRANSIT;
			case "delivering":
			case "out_for_delivery":
				return ShippingStatus.OUT_FOR_DELIVERY;
			case "delivered":
			case "complete":
				return ShippingStatus.DELIVERED;
			case "failed":
			case "delivery_failed":
				return ShippingStatus.FAILED;
			case "returning":
			case "return":
			case "returned":
				return ShippingStatus.RETURNED;
			case "cancel":
			case "cancelled":
			case "canceled":
				return ShippingStatus.CANCELLED;
			case "waiting":
			case "ready_to_pick":
				return ShippingStatus.WAITING_PICKUP;
			default:
				return ShippingStatus.IN_TRANSIT;
		}
	}
}
