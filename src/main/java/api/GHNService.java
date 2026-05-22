package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import beans.ShippingMethod;
import beans.Shipment;
import beans.ShipmentTracking;
import dao.ShippingMethodDAO;
import utils.ShippingStatus;

public class GHNService {

    private static final String GHN_API_BASE_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api";
    private static final Gson gson = new Gson();

    private final ShippingMethodDAO methodDAO = new ShippingMethodDAO();

    public String createShippingOrder(Shipment shipment) throws Exception {
        ShippingMethod method = methodDAO.getById(shipment.getShippingMethodId());
        if (method == null || !"GHN".equals(method.getProviderType())) {
            throw new Exception("Invalid GHN shipping method");
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("payment_type_id", 1);
        payload.addProperty("note", shipment.getCustomerNote() != null ? shipment.getCustomerNote() : "");
        payload.addProperty("required_note", "CHOTHUHANG");

        JsonObject fromAddress = new JsonObject();
        fromAddress.addProperty("district_id", Integer.parseInt(method.getGhnFromDistrictId()));
        fromAddress.addProperty("ward_code", method.getGhnFromWardCode());
        payload.add("from_address_id", new JsonObject());
        payload.add("from_address", fromAddress);

        JsonObject toAddress = new JsonObject();
        toAddress.addProperty("name", shipment.getReceiverName());
        toAddress.addProperty("phone", shipment.getReceiverPhone());
        toAddress.addProperty("address", shipment.getAddressDetail());
        toAddress.addProperty("ward_code", "");
        toAddress.addProperty("district_id", 0);
        payload.add("to_address", toAddress);

        JsonArray items = new JsonArray();
        JsonObject item = new JsonObject();
        item.addProperty("name", "Sách");
        item.addProperty("quantity", 1);
        item.addProperty("weight", (int) (shipment.getTotalWeight() * 1000));
        items.add(item);
        payload.add("items", items);

        payload.addProperty("service_id", method.getGhnServiceId());
        payload.addProperty("weight", (int) (shipment.getTotalWeight() * 1000));
        payload.addProperty("length", 20);
        payload.addProperty("width", 15);
        payload.addProperty("height", 10);

        return callGHNAPI("/v2/shipping-order/create", "POST", payload.toString(), method.getGhnToken());
    }

    public JsonObject parseCreateOrderResponse(String response) {
        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            if (json.has("code") && json.get("code").getAsInt() == 200) {
                return json.getAsJsonObject("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTrackingInfo(String orderCode, String token) throws Exception {
        JsonObject payload = new JsonObject();
        payload.addProperty("order_code", orderCode);
        return callGHNAPI("/v2/shipping-order/detail", "POST", payload.toString(), token);
    }

    public JsonObject getShippingFee(int districtId, int weight, int serviceId, String token) {
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("shop_id", 0);
            payload.addProperty("from_district_id", 0);
            payload.addProperty("to_district_id", districtId);
            payload.addProperty("weight", weight);
            payload.addProperty("service_id", serviceId);
            payload.addProperty("insurance_value", 0);

            String response = callGHNAPI("/v2/shipping-order/fee", "POST", payload.toString(), token);
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            if (json.has("code") && json.get("code").getAsInt() == 200) {
                return json.getAsJsonObject("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonArray getDistrictList(String token) {
        try {
            String response = callGHNAPI("/master-data/district", "GET", null, token);
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            if (json.has("code") && json.get("code").getAsInt() == 200) {
                return json.getAsJsonArray("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String callGHNAPI(String endpoint, String method, String body, String token) throws Exception {
        URL url = new URL(GHN_API_BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Token", token);
        conn.setRequestProperty("ShopId", "");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        if (body != null && (method.equals("POST") || method.equals("PUT"))) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    public String convertGHNStatusToInternal(String ghnStatus) {
        if (ghnStatus == null) return ShippingStatus.WAITING_PICKUP;

        switch (ghnStatus.toLowerCase()) {
            case "picked":
            case "pickup":
                return ShippingStatus.PICKED_UP;
            case "transporting":
            case "in_transit":
                return ShippingStatus.IN_TRANSIT;
            case "delivering":
            case "out_for_delivery":
                return ShippingStatus.OUT_FOR_DELIVERY;
            case "delivered":
            case "complete":
                return ShippingStatus.DELIVERED;
            case "failed":
            case "return":
            case "returning":
                return ShippingStatus.FAILED;
            case "returned":
                return ShippingStatus.RETURNED;
            case "cancel":
            case "cancelled":
                return ShippingStatus.CANCELLED;
            default:
                return ShippingStatus.WAITING_PICKUP;
        }
    }

    public String getInternalStatusDisplay(String status) {
        if (status == null) return "Không xác định";

        switch (status) {
            case ShippingStatus.WAITING_PICKUP: return "Chờ lấy hàng";
            case ShippingStatus.PICKED_UP: return "Đã lấy hàng";
            case ShippingStatus.IN_TRANSIT: return "Đang vận chuyển";
            case ShippingStatus.OUT_FOR_DELIVERY: return "Đang giao hàng";
            case ShippingStatus.DELIVERED: return "Đã giao hàng";
            case ShippingStatus.FAILED: return "Giao thất bại";
            case ShippingStatus.RETURNED: return "Đã trả hàng";
            case ShippingStatus.CANCELLED: return "Đã hủy";
            default: return status;
        }
    }
}
