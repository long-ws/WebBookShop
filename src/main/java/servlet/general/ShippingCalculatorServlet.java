package servlet.general;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import beans.ShippingMethod;
import beans.Province;
import dao.ProvinceDAO;
import dao.ShippingMethodDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ShippingCalculatorService;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ShippingCalculatorServlet", urlPatterns = {"/api/shipping/calculate", "/api/shipping/methods"})
public class ShippingCalculatorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ShippingCalculatorService calculatorService = new ShippingCalculatorService();
    private final ShippingMethodDAO methodDAO = new ShippingMethodDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleGetMethods(request, response);
        } else if (pathInfo.equals("/provinces")) {
            handleGetProvinces(response);
        } else {
            sendError(response, 404, "Endpoint not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/calculate")) {
            handleCalculateFee(request, response);
        } else {
            sendError(response, 404, "Endpoint not found");
        }
    }

    private void handleGetMethods(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject result = new JsonObject();
        try {
            List<ShippingMethod> methods = methodDAO.getAllActive();
            JsonArray methodsArray = new JsonArray();

            for (ShippingMethod method : methods) {
                JsonObject m = new JsonObject();
                m.addProperty("id", method.getId());
                m.addProperty("name", method.getName());
                m.addProperty("providerType", method.getProviderType());
                m.addProperty("isExpress", method.isExpress());
                m.addProperty("estimatedDays", method.getEstimatedDays());
                m.addProperty("pricePerKg", method.getPricePerKg());
                m.addProperty("description", method.isExpress() ? "Giao hàng nhanh (1-2 ngày)" : "Giao hàng tiêu chuẩn (3-5 ngày)");
                methodsArray.add(m);
            }

            result.addProperty("success", true);
            result.add("data", methodsArray);
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi: " + e.getMessage());
        }
        sendJson(response, result);
    }

    private void handleGetProvinces(HttpServletResponse response) throws IOException {
        JsonObject result = new JsonObject();
        try {
            List<Province> provinces = provinceDAO.getAll();
            JsonArray provincesArray = new JsonArray();

            for (Province province : provinces) {
                JsonObject p = new JsonObject();
                p.addProperty("code", province.getProvinceCode());
                p.addProperty("name", province.getProvinceName());
                p.addProperty("type", province.getProvinceType());
                p.addProperty("isMetroCity", province.getMetroCity());
                if (province.getShippingZone() != null) {
                    p.addProperty("zoneType", province.getShippingZone().getZoneType());
                    p.addProperty("zoneName", province.getShippingZone().getZoneName());
                }
                provincesArray.add(p);
            }

            result.addProperty("success", true);
            result.add("data", provincesArray);
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi: " + e.getMessage());
        }
        sendJson(response, result);
    }

    private void handleCalculateFee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (java.io.BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
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
            long methodId = body.has("methodId") ? body.get("methodId").getAsLong() : 1;
            String provinceCode = body.has("provinceCode") ? body.get("provinceCode").getAsString() : "79";
            double weightKg = body.has("weight") ? body.get("weight").getAsDouble() : 1.0;
            double volumeCm3 = body.has("volume") ? body.get("volume").getAsDouble() : 0.0;

            ShippingCalculatorService.ShippingCalculationResult calcResult = 
                    calculatorService.calculateFee(methodId, provinceCode, weightKg, volumeCm3);

            if (calcResult != null) {
                JsonObject data = new JsonObject();
                data.addProperty("methodId", calcResult.getMethodId());
                data.addProperty("methodName", calcResult.getMethodName());
                data.addProperty("isExpress", calcResult.isIsExpress());
                data.addProperty("provinceCode", calcResult.getProvinceCode());
                data.addProperty("provinceName", calcResult.getProvinceName());
                data.addProperty("zoneType", calcResult.getZoneType());
                data.addProperty("zoneName", calcResult.getZoneDisplayName());
                data.addProperty("weightKg", calcResult.getWeightKg());
                data.addProperty("volumeCm3", calcResult.getVolumeCm3());
                data.addProperty("volumetricWeight", calcResult.getVolumetricWeight());
                data.addProperty("chargeableWeight", calcResult.getChargeableWeight());
                data.addProperty("baseFee", calcResult.getBaseFee());
                data.addProperty("feePerKg", calcResult.getFeePerKg());
                data.addProperty("pricePerVolume", calcResult.getPricePerVolume());
                data.addProperty("surchargeMultiplier", calcResult.getSurchargeMultiplier());
                data.addProperty("shippingFee", calcResult.getShippingFee());
                data.addProperty("estimatedDaysMin", calcResult.getEstimatedDaysMin());
                data.addProperty("estimatedDaysMax", calcResult.getEstimatedDaysMax());
                data.addProperty("estimatedDelivery", calcResult.getEstimatedDeliveryDisplay());
                data.addProperty("freeShipping", calcResult.isFreeShipping());
                data.addProperty("freeShippingThreshold", calcResult.getFreeShippingThreshold());
                data.addProperty("serviceTypeName", calcResult.getServiceTypeName());

                result.addProperty("success", true);
                result.add("data", data);
            } else {
                result.addProperty("success", false);
                result.addProperty("message", "Không tìm thấy phương thức vận chuyển");
            }
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        sendJson(response, result);
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
