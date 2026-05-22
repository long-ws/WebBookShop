package servlet.admin.shipment;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.Province;
import beans.Shipment;
import beans.ShippingActivity;
import beans.ShippingMethod;
import beans.ShippingZone;
import beans.TopShippingMethod;
import dao.ProvinceDAO;
import dao.ShipmentDAO;
import dao.ShippingMethodDAO;
import dao.ShippingWeightFeeDAO;
import dao.ShippingZoneDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ShippingMethodService;
import utils.ShippingStatus;

@WebServlet(name = "ShippingMethodServlet", value = "/admin/shippingMethod")
public class ShippingMethodServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ShippingMethodServlet.class);

	private final ShippingMethodService methodService = new ShippingMethodService();
	private final ShippingMethodDAO methodDAO = new ShippingMethodDAO();
	private final ShippingZoneDAO zoneDAO = new ShippingZoneDAO();
	private final ProvinceDAO provinceDAO = new ProvinceDAO();
	private final ShippingWeightFeeDAO weightFeeDAO = new ShippingWeightFeeDAO();
	private final ShipmentDAO shipmentDAO = new ShipmentDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("ShippingMethodServlet doGet called");

		String statsExport = request.getParameter("statsExport");
		String statsPeriod = request.getParameter("period");

		// Handle CSV export request
		if ("true".equals(statsExport)) {
			handleStatsExport(request, response, statsPeriod);
			return;
		}

		// Handle AJAX stats request
		String requestedWith = request.getHeader("X-Requested-With");
		boolean isAjax = "XMLHttpRequest".equals(requestedWith);
		if (isAjax && request.getParameter("stats") != null) {
			handleStatsAjax(request, response, statsPeriod);
			return;
		}

		List<ShippingMethod> shippingMethods = new ArrayList<>();
		List<ShippingZone> shippingZones = new ArrayList<>();
		List<Province> provinces = new ArrayList<>();
		List<beans.ShippingWeightFee> weightFees = new ArrayList<>();

		try {
			logger.debug("Loading shipping methods...");
			List<ShippingMethod> methods = methodService.getAll();
			shippingMethods = (methods != null) ? methods : new ArrayList<>();
			logger.debug("Loaded {} shipping methods", shippingMethods.size());
		} catch (Exception e) {
			logger.error("Error loading shipping methods", e);
			e.printStackTrace();
			shippingMethods = new ArrayList<>();
		}

		try {
			logger.debug("Loading shipping zones...");
			List<ShippingZone> zones = zoneDAO.getAll();
			shippingZones = (zones != null) ? zones : new ArrayList<>();
			logger.debug("Loaded {} shipping zones", shippingZones.size());
		} catch (Exception e) {
			logger.error("Error loading shipping zones", e);
			e.printStackTrace();
			shippingZones = new ArrayList<>();
		}

		try {
			logger.debug("Loading provinces...");
			List<Province> provList = provinceDAO.getAll();
			provinces = (provList != null) ? provList : new ArrayList<>();
			logger.debug("Loaded {} provinces", provinces.size());
		} catch (Exception e) {
			logger.error("Error loading provinces", e);
			e.printStackTrace();
			provinces = new ArrayList<>();
		}

		try {
			logger.debug("Loading weight fees...");
			List<beans.ShippingWeightFee> fees = weightFeeDAO.getAll();
			weightFees = (fees != null) ? fees : new ArrayList<>();
			logger.debug("Loaded {} weight fees", weightFees.size());
		} catch (Exception e) {
			logger.error("Error loading weight fees", e);
			e.printStackTrace();
			weightFees = new ArrayList<>();
		}

		try {
			int totalMethods = 0;
			int activeMethods = 0;
			int expressMethods = 0;
			int inactiveMethods = 0;

			if (shippingMethods != null) {
				totalMethods = shippingMethods.size();
				for (ShippingMethod method : shippingMethods) {
					if (method != null) {
						if (method.getStatus() == 1) {
							activeMethods++;
						} else {
							inactiveMethods++;
						}
						if (method.isExpress()) {
							expressMethods++;
						}
					}
				}
			}

			request.setAttribute("shippingMethods", shippingMethods);
			request.setAttribute("shippingZones", shippingZones);
			request.setAttribute("provinces", provinces);
			request.setAttribute("weightFees", weightFees);
			request.setAttribute("totalMethods", totalMethods);
			request.setAttribute("activeMethods", activeMethods);
			request.setAttribute("inactiveMethods", inactiveMethods);
			request.setAttribute("expressMethods", expressMethods);
			request.setAttribute("totalZones", shippingZones.size());
			request.setAttribute("totalProvinces", provinces.size());
			request.setAttribute("totalWeightFees", weightFees.size());

			// Load statistics data
			String defaultPeriod = statsPeriod != null ? statsPeriod : "7";
			loadStatisticsData(request, defaultPeriod);

			logger.info("Successfully loaded data - Methods: {}, Zones: {}, Provinces: {}, WeightFees: {}",
					shippingMethods.size(), shippingZones.size(), provinces.size(), weightFees.size());

			request.getRequestDispatcher("/WEB-INF/views/admin/shippingMethodManagerView.jsp").forward(request, response);

		} catch (Exception e) {
			logger.error("Error during forward or final processing", e);
			e.printStackTrace();
			request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tải trang: " + e.getMessage());
			try {
				request.getRequestDispatcher("/WEB-INF/views/admin/shippingMethodManagerView.jsp").forward(request, response);
			} catch (Exception ex) {
				logger.error("Fallback forward also failed", ex);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"Lỗi nghiêm trọng khi tải trang quản lý vận chuyển");
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// Check if this is an AJAX request
		String requestedWith = request.getHeader("X-Requested-With");
		boolean isAjax = "XMLHttpRequest".equals(requestedWith);

		String action = request.getParameter("action");

		if ("toggleStatus".equals(action)) {
			handleToggleStatus(request, response, isAjax);
			return;
		}

		// For non-AJAX requests, continue with normal flow
		if (action != null) {
			switch (action) {
			case "create":
				createMethod(request);
				break;
			case "update":
				updateMethod(request);
				break;
			case "delete":
				deleteMethod(request);
				break;
			default:
				break;
			}
			response.sendRedirect(request.getContextPath() + "/admin/shippingMethod");
		}
	}

	private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response, boolean isAjax)
			throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		try {
			long id = Long.parseLong(request.getParameter("id"));
			int newStatus = Integer.parseInt(request.getParameter("status"));

			ShippingMethod method = methodService.getById(id);

			if (method == null) {
				response.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy phương thức vận chuyển!\"}");
				return;
			}

			boolean success = methodService.updateStatus(id, newStatus);

			if (success) {
				String message = newStatus == 1 ? "Đã kích hoạt phương thức!" : "Đã vô hiệu hóa phương thức!";
				response.getWriter().write("{\"success\":true,\"message\":\"" + message + "\",\"status\":" + newStatus + "}");
			} else {
				response.getWriter().write("{\"success\":false,\"message\":\"Cập nhật trạng thái thất bại!\"}");
			}
		} catch (NumberFormatException e) {
			response.getWriter().write("{\"success\":false,\"message\":\"Dữ liệu không hợp lệ!\"}");
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"success\":false,\"message\":\"Lỗi server: " + e.getMessage() + "\"}");
		}
	}

	private void createMethod(HttpServletRequest request) {
		try {
			ShippingMethod method = new ShippingMethod();
			method.setName(request.getParameter("name"));
			method.setEstimatedDays(Integer.parseInt(request.getParameter("estimatedDays")));
			method.setPricePerKg(Double.parseDouble(request.getParameter("pricePerKg")));
			method.setSupportPhone(request.getParameter("supportPhone"));
			method.setSupportEmail(request.getParameter("supportEmail"));
			method.setProviderType(request.getParameter("providerType"));
			method.setStatus(1);

			// Pricing fields
			String isExpressStr = request.getParameter("isExpress");
			method.setExpress(isExpressStr != null && "true".equals(isExpressStr));
			
			String expressSurcharge = request.getParameter("expressSurcharge");
			if (expressSurcharge != null && !expressSurcharge.isEmpty()) {
				method.setExpressSurcharge(Double.parseDouble(expressSurcharge));
			} else {
				method.setExpressSurcharge(1.5);
			}
			
			String minWeight = request.getParameter("minWeightKg");
			if (minWeight != null && !minWeight.isEmpty()) {
				method.setMinWeightKg(Double.parseDouble(minWeight));
			} else {
				method.setMinWeightKg(0.5);
			}
			
			String maxWeight = request.getParameter("maxWeightKg");
			if (maxWeight != null && !maxWeight.isEmpty()) {
				method.setMaxWeightKg(Double.parseDouble(maxWeight));
			} else {
				method.setMaxWeightKg(50.0);
			}
			
			String freeThreshold = request.getParameter("freeShippingThreshold");
			if (freeThreshold != null && !freeThreshold.isEmpty()) {
				method.setFreeShippingThreshold(Double.parseDouble(freeThreshold));
			} else {
				method.setFreeShippingThreshold(0);
			}

			// GHN fields
			String ghnServiceId = request.getParameter("ghnServiceId");
			if (ghnServiceId != null && !ghnServiceId.isEmpty()) {
				method.setGhnServiceId(Integer.parseInt(ghnServiceId));
			} else {
				method.setGhnServiceId(2);
			}
			method.setGhnFromDistrictId(request.getParameter("ghnFromDistrictId"));
			method.setGhnFromWardCode(request.getParameter("ghnFromWardCode"));
			method.setGhnShopId(request.getParameter("ghnShopId"));
			method.setGhnToken(request.getParameter("ghnToken"));

			method.setCreatedAt(LocalDateTime.now());
			method.setUpdatedAt(LocalDateTime.now());

			methodService.insert(method);
			request.getSession().setAttribute("successMessage", "Đã thêm phương thức vận chuyển mới!");
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("errorMessage", "Thêm phương thức thất bại: " + e.getMessage());
		}
	}

	private void updateMethod(HttpServletRequest request) {
		try {
			long id = Long.parseLong(request.getParameter("id"));
			ShippingMethod method = methodService.getById(id);

			if (method != null) {
				method.setName(request.getParameter("name"));
				method.setEstimatedDays(Integer.parseInt(request.getParameter("estimatedDays")));
				method.setPricePerKg(Double.parseDouble(request.getParameter("pricePerKg")));
				method.setSupportPhone(request.getParameter("supportPhone"));
				method.setSupportEmail(request.getParameter("supportEmail"));
				method.setProviderType(request.getParameter("providerType"));
				method.setUpdatedAt(LocalDateTime.now());

				// Pricing fields
				String isExpressStr = request.getParameter("isExpress");
				method.setExpress(isExpressStr != null && "true".equals(isExpressStr));
				
				String expressSurcharge = request.getParameter("expressSurcharge");
				if (expressSurcharge != null && !expressSurcharge.isEmpty()) {
					method.setExpressSurcharge(Double.parseDouble(expressSurcharge));
				}
				
				String minWeight = request.getParameter("minWeightKg");
				if (minWeight != null && !minWeight.isEmpty()) {
					method.setMinWeightKg(Double.parseDouble(minWeight));
				}
				
				String maxWeight = request.getParameter("maxWeightKg");
				if (maxWeight != null && !maxWeight.isEmpty()) {
					method.setMaxWeightKg(Double.parseDouble(maxWeight));
				}
				
				String freeThreshold = request.getParameter("freeShippingThreshold");
				if (freeThreshold != null && !freeThreshold.isEmpty()) {
					method.setFreeShippingThreshold(Double.parseDouble(freeThreshold));
				}

				// GHN fields
				String ghnServiceId = request.getParameter("ghnServiceId");
				if (ghnServiceId != null && !ghnServiceId.isEmpty()) {
					method.setGhnServiceId(Integer.parseInt(ghnServiceId));
				}
				method.setGhnFromDistrictId(request.getParameter("ghnFromDistrictId"));
				method.setGhnFromWardCode(request.getParameter("ghnFromWardCode"));
				method.setGhnShopId(request.getParameter("ghnShopId"));
				method.setGhnToken(request.getParameter("ghnToken"));

				methodService.update(method);
				request.getSession().setAttribute("successMessage", "Đã cập nhật phương thức vận chuyển!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
		}
	}

	private void toggleStatus(HttpServletRequest request) {
		try {
			long id = Long.parseLong(request.getParameter("id"));
			ShippingMethod method = methodService.getById(id);

			if (method != null) {
				int newStatus = method.getStatus() == 1 ? 0 : 1;
				methodService.updateStatus(id, newStatus);
				request.getSession().setAttribute("successMessage",
						newStatus == 1 ? "Đã kích hoạt phương thức!" : "Đã vô hiệu hóa phương thức!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("errorMessage", "Thay đổi trạng thái thất bại!");
		}
	}

	private void deleteMethod(HttpServletRequest request) {
		try {
			long id = Long.parseLong(request.getParameter("id"));
			methodService.delete(id);
			request.getSession().setAttribute("successMessage", "Đã xóa phương thức vận chuyển!");
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("errorMessage", "Xóa thất bại!");
		}
	}

	private void handleStatsAjax(HttpServletRequest request, HttpServletResponse response, String period) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

		int days = resolveDays(period);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		try {
			int totalOrders = shipmentDAO.countTotalSince(days);
			double shippingRevenue = shipmentDAO.sumShippingFeeSince(days);
			int pendingOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.WAITING_PICKUP}, days);
			int pickedOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.PICKED_UP}, days);
			int shippingOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.IN_TRANSIT, ShippingStatus.SHIPPING}, days);
			int deliveringOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.OUT_FOR_DELIVERY}, days);
			int completedOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.DELIVERED}, days);
			int failedOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.FAILED, ShippingStatus.RETURNED, ShippingStatus.CANCELLED}, days);
			String topProvince = shipmentDAO.getTopProvinceSince(days);
			List<TopShippingMethod> topMethods = shipmentDAO.getTopShippingMethodsSince(5, days);
			List<Shipment> recentShipments = shipmentDAO.getRecentShipmentsSince(5, days);
			List<ShippingActivity> recentActivities = buildRecentActivities(recentShipments);

			StringBuilder json = new StringBuilder();
			json.append("{");
			json.append("\"totalShippingOrders\":").append(totalOrders).append(",");
			json.append("\"shippingRevenue\":").append(String.format("%.0f", shippingRevenue)).append(",");
			json.append("\"pendingOrders\":").append(pendingOrders).append(",");
			json.append("\"pickedOrders\":").append(pickedOrders).append(",");
			json.append("\"shippingOrders\":").append(shippingOrders).append(",");
			json.append("\"deliveringOrders\":").append(deliveringOrders).append(",");
			json.append("\"completedOrders\":").append(completedOrders).append(",");
			json.append("\"failedOrders\":").append(failedOrders).append(",");
			json.append("\"topProvince\":\"").append(escapeJson(topProvince != null ? topProvince : "—")).append("\",");
			json.append("\"topProvinceName\":\"").append(escapeJson(topProvince != null ? topProvince : "—")).append("\",");
			json.append("\"totalStatistics\":").append(totalOrders).append(",");
			json.append("\"topMethods\":[");

			for (int i = 0; i < topMethods.size(); i++) {
				TopShippingMethod tsm = topMethods.get(i);
				if (i > 0) json.append(",");
				json.append("{");
				json.append("\"methodName\":\"").append(escapeJson(tsm.getMethodName())).append("\",");
				json.append("\"providerType\":\"").append(escapeJson(tsm.getProviderType())).append("\",");
				json.append("\"totalOrders\":").append(tsm.getTotalOrders()).append(",");
				json.append("\"totalRevenue\":").append(String.format("%.0f", tsm.getTotalRevenue())).append(",");
				json.append("\"usagePercent\":").append(String.format("%.1f", tsm.getUsagePercent())).append(",");
				json.append("\"status\":").append(tsm.getStatus());
				json.append("}");
			}
			json.append("],");
			json.append("\"recentActivities\":[");

			for (int i = 0; i < recentActivities.size(); i++) {
				ShippingActivity act = recentActivities.get(i);
				if (i > 0) json.append(",");
				json.append("{");
				json.append("\"title\":\"").append(escapeJson(act.getTitle())).append("\",");
				json.append("\"description\":\"").append(escapeJson(act.getDescription())).append("\",");
				json.append("\"timestamp\":\"").append(escapeJson(act.getTimestamp())).append("\",");
				json.append("\"icon\":\"").append(escapeJson(act.getIcon())).append("\",");
				json.append("\"dotColor\":\"").append(escapeJson(act.getDotColor())).append("\"");
				json.append("}");
			}
			json.append("]}");

			response.getWriter().write(json.toString());
		} catch (Exception e) {
			logger.error("Error in handleStatsAjax", e);
			response.getWriter().write("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
		}
	}

	private void handleStatsExport(HttpServletRequest request, HttpServletResponse response, String period) throws IOException {
		int days = resolveDays(period);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String fileName = "bao-cao-van-chuyen-" + period + "ngay-" + java.time.LocalDate.now().format(dtf) + ".csv";

		response.setContentType("text/csv; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		PrintWriter out = response.getWriter();

		// BOM for Excel UTF-8
		out.write("\uFEFF");

		// Header row
		out.write("BÁO CÁO THỐNG KÊ VẬN CHUYỂN\n");
		out.write("Kỳ báo cáo: " + getPeriodLabel(period) + "\n");
		out.write("Ngày xuất: " + java.time.LocalDate.now().format(dtf) + "\n\n");

		// Summary stats
		int totalOrders = shipmentDAO.countTotalSince(days);
		double revenue = shipmentDAO.sumShippingFeeSince(days);
		int pending = shipmentDAO.countByStatusesSince(
			new String[]{ShippingStatus.WAITING_PICKUP}, days);
		int picked = shipmentDAO.countByStatusesSince(
			new String[]{ShippingStatus.PICKED_UP}, days);
		int shipping = shipmentDAO.countByStatusesSince(
			new String[]{ShippingStatus.IN_TRANSIT, ShippingStatus.SHIPPING}, days);
		int delivering = shipmentDAO.countByStatusesSince(
			new String[]{ShippingStatus.OUT_FOR_DELIVERY}, days);
		int completed = shipmentDAO.countByStatusesSince(
			new String[]{ShippingStatus.DELIVERED}, days);
		int failed = shipmentDAO.countByStatusesSince(
			new String[]{ShippingStatus.FAILED, ShippingStatus.RETURNED, ShippingStatus.CANCELLED}, days);

		out.write("TỔNG QUAN\n");
		out.write("Tổng đơn vận chuyển," + totalOrders + "\n");
		out.write("Doanh thu phí ship," + String.format("%.0f", revenue) + " VND\n");
		out.write("Đơn chờ xác nhận," + pending + "\n");
		out.write("Đơn đã lấy," + picked + "\n");
		out.write("Đơn đang vận chuyển," + shipping + "\n");
		out.write("Đơn đang giao," + delivering + "\n");
		out.write("Đơn hoàn thành," + completed + "\n");
		out.write("Đơn thất bại / hoàn," + failed + "\n");
		out.write("\n");

		// Top methods
		out.write("TOP PHƯƠNG THỨC VẬN CHUYỂN\n");
		out.write("STT,Tên phương thức,Nhà cung cấp,Tổng đơn,Doanh thu (VND),Tỷ lệ (%)\n");
		List<TopShippingMethod> topMethods = shipmentDAO.getTopShippingMethodsSince(10, days);
		int idx = 1;
		for (TopShippingMethod tsm : topMethods) {
			out.write(idx++ + ",");
			out.write("\"" + tsm.getMethodName() + "\",");
			out.write(tsm.getProviderType() + ",");
			out.write(tsm.getTotalOrders() + ",");
			out.write(String.format("%.0f", tsm.getTotalRevenue()) + ",");
			out.write(String.format("%.1f", tsm.getUsagePercent()) + "\n");
		}
		out.write("\n");

		// Recent shipments
		out.write("ĐƠN VẬN CHUYỂN GẦN ĐÂY\n");
		out.write("ID,Trạng thái,Tỉnh/Thành,Phí ship (VND),Ngày tạo\n");
		List<Shipment> recentShipments = shipmentDAO.getRecentShipmentsSince(20, days);
		for (Shipment s : recentShipments) {
			out.write(s.getId() + ",");
			out.write("\"" + (s.getShippingStatus() != null ? s.getShippingStatus() : "—") + "\",");
			out.write("\"" + (s.getProvince() != null ? s.getProvince() : "—") + "\",");
			out.write(String.format("%.0f", s.getShippingFee()) + ",");
			out.write(s.getCreatedAt() != null ? s.getCreatedAt().format(dtf) : "—" + "\n");
		}

		out.flush();
	}

	private int resolveDays(String period) {
		if ("today".equals(period)) return 1;
		if ("7days".equals(period)) return 7;
		if ("30days".equals(period)) return 30;
		if ("90days".equals(period)) return 90;
		return 7;
	}

	private String getPeriodLabel(String period) {
		if ("today".equals(period)) return "Hôm nay";
		if ("7days".equals(period)) return "7 ngày gần nhất";
		if ("30days".equals(period)) return "30 ngày gần nhất";
		if ("90days".equals(period)) return "90 ngày gần nhất";
		return "7 ngày gần nhất";
	}

	private String escapeJson(String s) {
		if (s == null) return "";
		return s.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}

	private void loadStatisticsData(HttpServletRequest request, String period) {
		int days = resolveDays(period);
		try {
			int totalOrders = shipmentDAO.countTotalSince(days);
			double shippingRevenue = shipmentDAO.sumShippingFeeSince(days);
			int pendingOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.WAITING_PICKUP}, days);
			int pickedOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.PICKED_UP}, days);
			int shippingOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.IN_TRANSIT, ShippingStatus.SHIPPING}, days);
			int deliveringOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.OUT_FOR_DELIVERY}, days);
			int completedOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.DELIVERED}, days);
			int failedOrders = shipmentDAO.countByStatusesSince(
				new String[]{ShippingStatus.FAILED, ShippingStatus.RETURNED, ShippingStatus.CANCELLED}, days);
			String topProvince = shipmentDAO.getTopProvinceSince(days);
			List<TopShippingMethod> topMethods = shipmentDAO.getTopShippingMethodsSince(5, days);
			List<Shipment> recentShipments = shipmentDAO.getRecentShipmentsSince(5, days);
			List<ShippingActivity> recentActivities = buildRecentActivities(recentShipments);

			request.setAttribute("totalShippingOrders", totalOrders);
			request.setAttribute("shippingRevenue", (long) shippingRevenue);
			request.setAttribute("pendingOrders", pendingOrders);
			request.setAttribute("pickedOrders", pickedOrders);
			request.setAttribute("shippingOrders", shippingOrders);
			request.setAttribute("deliveringOrders", deliveringOrders);
			request.setAttribute("completedOrders", completedOrders);
			request.setAttribute("failedOrders", failedOrders);
			request.setAttribute("topProvince", topProvince != null ? topProvince : "—");
			request.setAttribute("topProvinceName", topProvince != null ? topProvince : "—");
			request.setAttribute("topShippingMethods", topMethods);
			request.setAttribute("recentActivities", recentActivities);
			request.setAttribute("totalStatistics", totalOrders);
			request.setAttribute("currentStatsPeriod", period);

			// Express / Standard counts from all-time since method express flag doesn't change
			int expressOrders = 0;
			int standardOrders = 0;
			for (ShippingMethod m : methodDAO.getAll()) {
				if (m != null && m.getStatus() == 1) {
					int count = shipmentDAO.countByShippingMethod(m.getId());
					if (m.isExpress()) {
						expressOrders += count;
					} else {
						standardOrders += count;
					}
				}
			}
			request.setAttribute("expressOrders", expressOrders);
			request.setAttribute("standardOrders", standardOrders);

			logger.debug("Statistics loaded (period={}): totalOrders={}, revenue={}", period, totalOrders, shippingRevenue);
		} catch (Exception e) {
			logger.error("Error loading statistics data", e);
			e.printStackTrace();
			request.setAttribute("totalStatistics", 0);
			request.setAttribute("totalShippingOrders", 0);
			request.setAttribute("expressOrders", 0);
			request.setAttribute("standardOrders", 0);
			request.setAttribute("shippingRevenue", 0);
			request.setAttribute("pendingOrders", 0);
			request.setAttribute("pickedOrders", 0);
			request.setAttribute("shippingOrders", 0);
			request.setAttribute("deliveringOrders", 0);
			request.setAttribute("completedOrders", 0);
			request.setAttribute("failedOrders", 0);
			request.setAttribute("currentStatsPeriod", period);
		}
	}

	private List<ShippingActivity> buildRecentActivities(List<Shipment> shipments) {
		List<ShippingActivity> activities = new ArrayList<>();
		if (shipments == null) return activities;

		for (Shipment s : shipments) {
			if (s == null) continue;
			String status = s.getShippingStatus();
			String title, icon, dotColor, description;

			if ("pending".equalsIgnoreCase(status)) {
				title = "Đơn vừa được tạo";
				icon = "fa-plus-circle";
				dotColor = "warning";
				description = "Vận đơn #" + s.getId() + " đang chờ xác nhận";
			} else if ("picked_up".equalsIgnoreCase(status) || "PICKED_UP".equals(status)) {
				title = "Đơn đã được lấy hàng";
				icon = "fa-box-open";
				dotColor = "info";
				description = "Vận đơn #" + s.getId() + " đã được shipper lấy";
			} else if ("shipping".equalsIgnoreCase(status) || "IN_TRANSIT".equals(status) || "INTRANSIT".equals(status)) {
				title = "Đơn đang vận chuyển";
				icon = "fa-truck";
				dotColor = "purple";
				description = "Vận đơn #" + s.getId() + " đang trên đường giao";
			} else if ("delivering".equalsIgnoreCase(status) || "OUT_FOR_DELIVERY".equals(status)) {
				title = "Đơn đang giao";
				icon = "fa-truck-fast";
				dotColor = "success";
				description = "Vận đơn #" + s.getId() + " đang được giao đến khách";
			} else if ("delivered".equalsIgnoreCase(status) || "DELIVERED".equals(status)) {
				title = "Đơn giao thành công";
				icon = "fa-circle-check";
				dotColor = "success";
				description = "Vận đơn #" + s.getId() + " đã giao thành công";
			} else if ("cancelled".equalsIgnoreCase(status) || "returned".equalsIgnoreCase(status)
					|| "CANCELLED".equals(status) || "RETURNED".equals(status) || "FAILED".equals(status)) {
				title = "Đơn bị hủy / hoàn";
				icon = "fa-xmark-circle";
				dotColor = "danger";
				description = "Vận đơn #" + s.getId() + " đã bị hủy hoặc hoàn";
			} else if ("waiting_pickup".equalsIgnoreCase(status) || "WAITING_PICKUP".equals(status)) {
				title = "Đơn vừa được tạo";
				icon = "fa-plus-circle";
				dotColor = "warning";
				description = "Vận đơn #" + s.getId() + " đang chờ xác nhận";
			} else {
				title = "Cập nhật vận đơn";
				icon = "fa-sync";
				dotColor = "info";
				description = "Vận đơn #" + s.getId() + " - " + (status != null ? status : "không xác định");
			}

			String timestamp;
			if (s.getUpdatedAt() != null) {
				java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
				timestamp = s.getUpdatedAt().format(formatter);
			} else if (s.getCreatedAt() != null) {
				java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
				timestamp = s.getCreatedAt().format(formatter);
			} else {
				timestamp = "—";
			}

			activities.add(new ShippingActivity(title, description, timestamp, icon, dotColor));
		}
		return activities;
	}
}
