package servlet.general;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dao.ProvinceDAO;
import service.ShippingCalculatorService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "GHNAddressServlet", urlPatterns = {"/api/ghn/*"})
public class GHNAddressServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Thông tin GHN của bạn - Giao hàng nhanh
    private static final String GHN_TOKEN = "418104dd-4ee7-11f1-a973-aee5264794df";
    private static final String GHN_CLIENT_ID = "2511536";
    private static final String GHN_SHOP_ID = "200281";
    private static final String GHN_BASE_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api";

    // Địa chỉ gửi hàng mặc định (kho của shop)
    private static final String FROM_DISTRICT_ID = "1567";
    private static final String FROM_WARD_CODE = "550307";

    // Service Type ID cho Giao hàng nhanh
    private static final int SERVICE_TYPE_FAST = 2; // Giao hàng nhanh
    private static final int SERVICE_TYPE_STANDARD = 1; // Giao hàng tiêu chuẩn

    private final Gson gson = new Gson();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final ShippingCalculatorService shippingCalculator = new ShippingCalculatorService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            sendError(response, 400, "Missing path");
            return;
        }

        switch (pathInfo) {
            case "/provinces":
                handleGetProvinces(response);
                break;
            case "/districts":
                handleGetDistricts(request, response);
                break;
            case "/wards":
                handleGetWards(request, response);
                break;
            case "/services":
                handleGetServices(request, response);
                break;
            default:
                sendError(response, 404, "Endpoint not found: " + pathInfo);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            sendError(response, 400, "Missing path");
            return;
        }

        switch (pathInfo) {
            case "/calculate-fee":
                handleCalculateFee(request, response);
                break;
            case "/available-services":
                handleGetAvailableServices(request, response);
                break;
            case "/shipping-options":
                handleGetShippingOptions(request, response);
                break;
            default:
                sendError(response, 404, "Endpoint not found: " + pathInfo);
        }
    }

    /**
     * Lấy danh sách tỉnh/thành phố từ GHN API
     */
    private void handleGetProvinces(HttpServletResponse response) throws IOException {
        JsonObject result = new JsonObject();
        
        // Luôn dùng GHN API để lấy provinces (cần integer ProvinceID)
        String url = GHN_BASE_URL + "/master-data/province";
        try {
            String ghnResp = callGHN("GET", url, null);
            if (ghnResp != null) {
                JsonObject parsed = gson.fromJson(ghnResp, JsonObject.class);
                if (parsed.has("code") && parsed.get("code").getAsInt() == 200) {
                    result.addProperty("success", true);
                    result.add("data", parsed.getAsJsonArray("data"));
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", "Lỗi GHN API");
                }
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Không thể kết nối GHN API");
            }
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi: " + e.getMessage());
        }
        
        sendJson(response, result);
    }

    /**
     * Lấy danh sách quận/huyện từ GHN
     */
    private void handleGetDistricts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String provinceId = request.getParameter("province_id");

        JsonObject result = new JsonObject();

        if (provinceId == null || provinceId.isBlank()) {
            result.addProperty("success", false);
            result.addProperty("message", "Thiếu province_id");
            sendJson(response, result);
            return;
        }

        String url = GHN_BASE_URL + "/master-data/district";
        Map<String, Object> body = new HashMap<>();
        body.put("province_id", Integer.parseInt(provinceId));

        try {
            String ghnResp = callGHN("POST", url, body);
            if (ghnResp != null) {
                JsonObject parsed = gson.fromJson(ghnResp, JsonObject.class);
                if (parsed.has("code") && parsed.get("code").getAsInt() == 200) {
                    result.addProperty("success", true);
                    result.add("data", parsed.getAsJsonArray("data"));
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", parsed.has("message") ? parsed.get("message").getAsString() : "Lỗi GHN API");
                }
            }
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi: " + e.getMessage());
        }
        sendJson(response, result);
    }

    /**
     * Lấy danh sách phường/xã từ GHN
     */
    private void handleGetWards(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String districtId = request.getParameter("district_id");

        JsonObject result = new JsonObject();

        if (districtId == null || districtId.isBlank()) {
            result.addProperty("success", false);
            result.addProperty("message", "Thiếu district_id");
            sendJson(response, result);
            return;
        }

        String url = GHN_BASE_URL + "/master-data/ward?district_id=" + districtId;

        try {
            String ghnResp = callGHN("GET", url, null);
            if (ghnResp != null) {
                JsonObject parsed = gson.fromJson(ghnResp, JsonObject.class);
                if (parsed.has("code") && parsed.get("code").getAsInt() == 200) {
                    result.addProperty("success", true);
                    result.add("data", parsed.getAsJsonArray("data"));
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", parsed.has("message") ? parsed.get("message").getAsString() : "Lỗi GHN API");
                }
            }
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi: " + e.getMessage());
        }
        sendJson(response, result);
    }

    /**
     * Lấy danh sách dịch vụ có sẵn
     */
    private void handleGetServices(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String districtId = request.getParameter("district_id");

        JsonObject result = new JsonObject();

        if (districtId == null || districtId.isBlank()) {
            result.addProperty("success", false);
            result.addProperty("message", "Thiếu district_id");
            sendJson(response, result);
            return;
        }

        String url = GHN_BASE_URL + "/v2/shipping-order/available-services";
        Map<String, Object> body = new HashMap<>();
        body.put("shop_id", Integer.parseInt(GHN_SHOP_ID));
        body.put("from_district", Integer.parseInt(FROM_DISTRICT_ID));
        body.put("to_district", Integer.parseInt(districtId));

        try {
            String ghnResp = callGHN("POST", url, body);
            if (ghnResp != null) {
                JsonObject parsed = gson.fromJson(ghnResp, JsonObject.class);
                if (parsed.has("code") && parsed.get("code").getAsInt() == 200) {
                    result.addProperty("success", true);
                    result.add("data", parsed.getAsJsonArray("data"));
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", parsed.has("message") ? parsed.get("message").getAsString() : "Lỗi GHN API");
                }
            }
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi: " + e.getMessage());
        }
        sendJson(response, result);
    }

    /**
     * Lấy các dịch vụ khả dụng (POST)
     */
    private void handleGetAvailableServices(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JsonObject body = gson.fromJson(sb.toString(), JsonObject.class);
        JsonObject result = new JsonObject();

        if (body == null || !body.has("to_district_id")) {
            result.addProperty("success", false);
            result.addProperty("message", "Thiếu to_district_id");
            sendJson(response, result);
            return;
        }

        int toDistrictId = body.get("to_district_id").getAsInt();

        String url = GHN_BASE_URL + "/v2/shipping-order/available-services";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("shop_id", Integer.parseInt(GHN_SHOP_ID));
        requestBody.put("from_district", Integer.parseInt(FROM_DISTRICT_ID));
        requestBody.put("to_district", toDistrictId);

        try {
            String ghnResp = callGHN("POST", url, requestBody);
            if (ghnResp != null) {
                JsonObject parsed = gson.fromJson(ghnResp, JsonObject.class);
                if (parsed.has("code") && parsed.get("code").getAsInt() == 200) {
                    result.addProperty("success", true);
                    result.add("data", parsed.getAsJsonArray("data"));
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", parsed.has("message") ? parsed.get("message").getAsString() : "Lỗi GHN API");
                }
            }
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi: " + e.getMessage());
        }
        sendJson(response, result);
    }

    /**
     * Lấy phí vận chuyển SỬ DỤNG GIÁ CỦA SHOP CHÚNG TÔI
     * Thay vì dùng GHN API, chúng ta tính phí dựa trên:
     * - Khu vực (zone type)
     * - Trọng lượng (weight)
     * - Thể tích (volume)
     */
    private void handleGetShippingOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JsonObject body = gson.fromJson(sb.toString(), JsonObject.class);
        JsonObject result = new JsonObject();

        if (body == null) {
            result.addProperty("success", false);
            result.addProperty("message", "Invalid request body");
            sendJson(response, result);
            return;
        }

        try {
            int toDistrictId = body.has("to_district_id") ? body.get("to_district_id").getAsInt() : 0;
            String toWardCode = body.has("to_ward_code") ? body.get("to_ward_code").getAsString() : "";
            int weight = body.has("weight") ? body.get("weight").getAsInt() : 500; // gram
            int length = body.has("length") ? body.get("length").getAsInt() : 20;
            int width = body.has("width") ? body.get("width").getAsInt() : 15;
            int height = body.has("height") ? body.get("height").getAsInt() : 10;

            // Chuyển đổi sang đơn vị của chúng ta
            double weightKg = weight / 1000.0; // gram -> kg
            double volumeCm3 = length * width * height; // cm3

            // Lấy province code từ district ID
            String provinceCode = getProvinceCodeFromDistrict(toDistrictId);

            JsonArray shippingOptions = new JsonArray();

            // Option 1: Giao hàng nhanh (method_id = 1, service_type_id = 2)
            ShippingCalculatorService.ShippingCalculationResult expressResult =
                shippingCalculator.calculateFee(1, provinceCode, weightKg, volumeCm3);
            if (expressResult != null) {
                JsonObject expressOption = new JsonObject();
                expressOption.addProperty("service_id", 1);
                expressOption.addProperty("service_type_id", SERVICE_TYPE_FAST);
                expressOption.addProperty("short_name", "GHN");
                expressOption.addProperty("service_name", "Giao hàng nhanh");
                expressOption.addProperty("fee", expressResult.getShippingFee());
                expressOption.addProperty("estimated_days", expressResult.getEstimatedDaysMin() + "-" + expressResult.getEstimatedDaysMax());
                expressOption.addProperty("zone_type", expressResult.getZoneType());
                expressOption.addProperty("zone_name", expressResult.getZoneDisplayName());
                expressOption.addProperty("is_express", true);
                shippingOptions.add(expressOption);
            }

            // Option 2: Giao hàng tiêu chuẩn (method_id = 2, service_type_id = 1)
            ShippingCalculatorService.ShippingCalculationResult standardResult =
                shippingCalculator.calculateFee(2, provinceCode, weightKg, volumeCm3);
            if (standardResult != null) {
                JsonObject standardOption = new JsonObject();
                standardOption.addProperty("service_id", 2);
                standardOption.addProperty("service_type_id", SERVICE_TYPE_STANDARD);
                standardOption.addProperty("short_name", "GHN");
                standardOption.addProperty("service_name", "Giao hàng tiêu chuẩn");
                standardOption.addProperty("fee", standardResult.getShippingFee());
                standardOption.addProperty("estimated_days", standardResult.getEstimatedDaysMin() + "-" + standardResult.getEstimatedDaysMax());
                standardOption.addProperty("zone_type", standardResult.getZoneType());
                standardOption.addProperty("zone_name", standardResult.getZoneDisplayName());
                standardOption.addProperty("is_express", false);
                shippingOptions.add(standardOption);
            }

            // Nếu không có dữ liệu từ database, sử dụng giá mặc định
            if (shippingOptions.size() == 0) {
                addDefaultShippingOptions(shippingOptions, weightKg, volumeCm3);
            }

            result.addProperty("success", true);
            result.add("data", shippingOptions);

        } catch (Exception e) {
            // Fallback về giá mặc định
            JsonArray shippingOptions = new JsonArray();
            addDefaultShippingOptions(shippingOptions, 0.5, 3000);
            result.addProperty("success", true);
            result.add("data", shippingOptions);
        }

        sendJson(response, result);
    }

    /**
     * Thêm các option vận chuyển mặc định (fallback)
     */
    private void addDefaultShippingOptions(JsonArray shippingOptions, double weightKg, double volumeCm3) {
        // Tính volumetric weight
        double volumetricWeight = volumeCm3 / 5000;
        double chargeableWeight = Math.max(weightKg, volumetricWeight);

        // Tính phí mặc định dựa trên trọng lượng
        double baseFee = 15000 + (chargeableWeight * 8000); // Base 15k + 8k/kg
        baseFee = Math.round(baseFee / 1000) * 1000; // Làm tròn

        // Express option
        JsonObject expressOption = new JsonObject();
        expressOption.addProperty("service_id", 1);
        expressOption.addProperty("service_type_id", SERVICE_TYPE_FAST);
        expressOption.addProperty("short_name", "GHN");
        expressOption.addProperty("service_name", "Giao hàng nhanh");
        expressOption.addProperty("fee", baseFee);
        expressOption.addProperty("estimated_days", "1-2");
        expressOption.addProperty("is_express", true);
        shippingOptions.add(expressOption);

        // Standard option
        JsonObject standardOption = new JsonObject();
        standardOption.addProperty("service_id", 2);
        standardOption.addProperty("service_type_id", SERVICE_TYPE_STANDARD);
        standardOption.addProperty("short_name", "GHN");
        standardOption.addProperty("service_name", "Giao hàng tiêu chuẩn");
        standardOption.addProperty("fee", (long)(baseFee * 0.8)); // 80% của express
        standardOption.addProperty("estimated_days", "3-5");
        standardOption.addProperty("is_express", false);
        shippingOptions.add(standardOption);
    }

    /**
     * Lấy province code từ district ID (GHN district ID)
     * Đây là mapping đơn giản - trong thực tế cần lookup table
     */
    private String getProvinceCodeFromDistrict(int districtId) {
        // GHN District IDs cho các TP lớn
        // Hà Nội: các quận thường có district_id < 1500
        // TP.HCM: các quận thường có district_id > 1500 và < 4000
        
        // Mapping đơn giản: 
        // - Nếu là HCM (Quận 1-12, Bình Thạch...) -> code "79"
        // - Nếu là HN (Ba Đình, Hoàn Kiếm...) -> code "01"
        // - Mặc định -> "79" (TP.HCM)
        
        if (districtId >= 1450 && districtId <= 1700) {
            return "01"; // Hà Nội
        }
        return "79"; // TP.HCM và các tỉnh khác
    }

    /**
     * Tính phí vận chuyển
     */
    private void handleCalculateFee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JsonObject body = gson.fromJson(sb.toString(), JsonObject.class);
        JsonObject result = new JsonObject();

        if (body == null) {
            result.addProperty("success", false);
            result.addProperty("message", "Invalid request body");
            sendJson(response, result);
            return;
        }

        try {
            int serviceTypeId = body.has("service_type_id") ? body.get("service_type_id").getAsInt() : SERVICE_TYPE_FAST;
            int weight = body.has("weight") ? body.get("weight").getAsInt() : 500;
            int length = body.has("length") ? body.get("length").getAsInt() : 20;
            int width = body.has("width") ? body.get("width").getAsInt() : 15;
            int height = body.has("height") ? body.get("height").getAsInt() : 10;

            // Chuyển đổi
            double weightKg = weight / 1000.0;
            double volumeCm3 = length * width * height;

            // Map service type sang method ID của chúng ta
            long methodId = (serviceTypeId == SERVICE_TYPE_FAST) ? 1 : 2;

            // Tính phí sử dụng service của chúng ta
            ShippingCalculatorService.ShippingCalculationResult calcResult =
                shippingCalculator.calculateFee(methodId, "79", weightKg, volumeCm3);

            if (calcResult != null) {
                result.addProperty("success", true);
                result.addProperty("fee", calcResult.getShippingFee());
                result.addProperty("estimated_days", calcResult.getEstimatedDaysMin() + "-" + calcResult.getEstimatedDaysMax());
                result.addProperty("zone_type", calcResult.getZoneType());
            } else {
                // Fallback
                long fee = 15000 + (long)(weightKg * 8000);
                fee = Math.round(fee / 1000) * 1000;
                result.addProperty("success", true);
                result.addProperty("fee", fee);
                result.addProperty("estimated_days", serviceTypeId == SERVICE_TYPE_FAST ? "1-2" : "3-5");
            }

        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi: " + e.getMessage());
        }

        sendJson(response, result);
    }

    /**
     * Gọi API GHN
     */
    private String callGHN(String method, String urlStr, Map<String, Object> body) {
        HttpURLConnection conn = null;
        try {
            URL url = URI.create(urlStr).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Token", GHN_TOKEN);
            conn.setRequestProperty("ShopId", GHN_SHOP_ID);
            conn.setRequestProperty("ClientId", GHN_CLIENT_ID);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            if (body != null && ("POST".equals(method) || "PUT".equals(method))) {
                conn.setDoOutput(true);
                String json = toJson(body);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }
            }

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                System.err.println("[GHN] HTTP Error " + responseCode + " for " + urlStr);
                StringBuilder errorMsg = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorMsg.append(errorLine);
                    }
                }
                System.err.println("[GHN] Error response: " + errorMsg);
                return null;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            return response.toString();

        } catch (Exception e) {
            System.err.println("[GHN] Lỗi khi gọi GHN API: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Convert Map to JSON string
     */
    private String toJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }

            sb.append("\"").append(entry.getKey()).append("\":");

            Object value = entry.getValue();
            if (value == null) {
                sb.append("null");
            } else if (value instanceof String) {
                sb.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Number) {
                sb.append(value);
            } else if (value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("\"").append(escapeJson(value.toString())).append("\"");
            }

            first = false;
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * Escape special characters in JSON string
     */
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    private void sendJson(HttpServletResponse response, JsonObject obj) throws IOException {
        response.getWriter().write(gson.toJson(obj));
    }

    private void sendError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        JsonObject obj = new JsonObject();
        obj.addProperty("success", false);
        obj.addProperty("message", message);
        sendJson(response, obj);
    }
}
