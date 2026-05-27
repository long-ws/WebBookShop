package servlet.general;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;

import beans.ShippingMethod;
import beans.shipping.ShippingCalculationResult;
import service.ShippingCalculatorService;
import service.ShippingMethodService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShippingCalculatorServlet", value = "/api/shipping/calculate")
public class ShippingCalculatorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ShippingCalculatorService calculatorService = new ShippingCalculatorService();
	private final ShippingMethodService methodService = new ShippingMethodService();
	private final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");

		try {
			if ("methods".equals(action)) {
				List<ShippingMethod> methods = methodService.getAllActive();
				sendJson(response, gson.toJson(methods));
				return;
			}

			String provinceCode = request.getParameter("provinceCode");
			String weightStr = request.getParameter("weight");
			String volumeStr = request.getParameter("volume");
			String methodIdStr = request.getParameter("methodId");

			if (provinceCode == null || provinceCode.isEmpty()) {
				sendError(response, "provinceCode is required");
				return;
			}

			double weight = 1.0;
			double volume = 0.0;

			if (weightStr != null && !weightStr.isEmpty()) {
				weight = Double.parseDouble(weightStr);
			}
			if (volumeStr != null && !volumeStr.isEmpty()) {
				volume = Double.parseDouble(volumeStr);
			}

			if (methodIdStr != null && !methodIdStr.isEmpty()) {
				long methodId = Long.parseLong(methodIdStr);
				ShippingCalculationResult result = calculatorService.calculateFee(methodId, provinceCode, weight, volume);
				if (result != null) {
					sendJson(response, gson.toJson(result));
				} else {
					sendError(response, "Phuong thuc van chuyen khong hop le hoac khong hoat dong");
				}
			} else {
				List<ShippingCalculationResult> results = calculatorService.calculateAllMethods(provinceCode, weight, volume);
				sendJson(response, gson.toJson(results));
			}
		} catch (NumberFormatException e) {
			sendError(response, "Thong so khong hop le: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			sendError(response, "Loi tinh phi van chuyen: " + e.getMessage());
		}
	}

	private void sendJson(HttpServletResponse response, String json) throws IOException {
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
	}

	private void sendError(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		PrintWriter out = response.getWriter();
		out.print("{\"error\": true, \"message\": \"" + message.replace("\"", "\\\"") + "\"}");
		out.flush();
	}
}
