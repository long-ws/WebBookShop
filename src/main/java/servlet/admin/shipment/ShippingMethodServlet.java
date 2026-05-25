package servlet.admin.shipment;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
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

        if ("true".equals(statsExport)) {
            handleStatsExport(request, response, statsPeriod);
            return;
        }

        String requestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(requestedWith);
        if (isAjax && request.getParameter("stats") != null) {
            handleStatsAjax(request, response, statsPeriod);
            return;
        }

        List<ShippingMethod> allMethods = new ArrayList<>();
        List<ShippingZone> allZones = new ArrayList<>();
        List<Province> allProvinces = new ArrayList<>();
        List<beans.ShippingWeightFee> allWeightFees = new ArrayList<>();

        try {
            List<ShippingMethod> methods = methodDAO.getAll();
            allMethods = (methods != null) ? methods : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error loading shipping methods", e);
            allMethods = new ArrayList<>();
        }

        try {
            List<ShippingZone> zones = zoneDAO.getAll();
            allZones = (zones != null) ? zones : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error loading shipping zones", e);
            allZones = new ArrayList<>();
        }

        try {
            List<Province> provList = provinceDAO.getAll();
            allProvinces = (provList != null) ? provList : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error loading provinces", e);
            allProvinces = new ArrayList<>();
        }

        try {
            List<beans.ShippingWeightFee> fees = weightFeeDAO.getAll();
            allWeightFees = (fees != null) ? fees : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error loading weight fees", e);
            allWeightFees = new ArrayList<>();
        }

        int totalMethods = allMethods.size();
        int activeMethods = 0;
        int expressMethods = 0;
        int inactiveMethods = 0;

        for (ShippingMethod method : allMethods) {
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

        request.setAttribute("shippingMethods", allMethods);
        request.setAttribute("methods", allMethods);
        request.setAttribute("shippingZones", allZones);
        request.setAttribute("provinces", allProvinces);
        request.setAttribute("weightFees", allWeightFees);
        request.setAttribute("totalMethods", totalMethods);
        request.setAttribute("activeMethods", activeMethods);
        request.setAttribute("inactiveMethods", inactiveMethods);
        request.setAttribute("expressMethods", expressMethods);
        request.setAttribute("totalZones", allZones.size());
        request.setAttribute("totalProvinces", allProvinces.size());
        request.setAttribute("totalWeightFees", allWeightFees.size());

        String defaultPeriod = statsPeriod != null ? statsPeriod : "7";
        loadStatisticsData(request, defaultPeriod);

        request.getRequestDispatcher("/WEB-INF/views/admin/shippingMethodManagerView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("toggleStatus".equals(action)) {
            handleToggleStatus(request, response);
            return;
        }

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

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            long id = Long.parseLong(request.getParameter("id"));
            int newStatus = Integer.parseInt(request.getParameter("status"));

            ShippingMethod method = methodService.getById(id);

            if (method == null) {
                response.getWriter().write("{ \"success\": false, \"message\": \"Khong tim thay phuong thuc van chuyen!\" }");
                return;
            }

            boolean success = methodService.updateStatus(id, newStatus);

            if (success) {
                String msg = newStatus == 1 ? "Da kich hoat phuong thuc!" : "Da vo hieu hoa phuong thuc!";
                response.getWriter().write("{ \"success\": true, \"message\": \"" + msg + "\", \"status\": " + newStatus + " }");
            } else {
                response.getWriter().write("{ \"success\": false, \"message\": \"Cap nhat trang thai that bai!\" }");
            }
        } catch (NumberFormatException e) {
            response.getWriter().write("{ \"success\": false, \"message\": \"Du lieu khong hop le!\" }");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{ \"success\": false, \"message\": \"Loi server!\" }");
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
            request.getSession().setAttribute("successMessage", "Da them phuong thuc van chuyen moi!");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Them phuong thuc that bai: " + e.getMessage());
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

                String ghnServiceId = request.getParameter("ghnServiceId");
                if (ghnServiceId != null && !ghnServiceId.isEmpty()) {
                    method.setGhnServiceId(Integer.parseInt(ghnServiceId));
                }
                method.setGhnFromDistrictId(request.getParameter("ghnFromDistrictId"));
                method.setGhnFromWardCode(request.getParameter("ghnFromWardCode"));
                method.setGhnShopId(request.getParameter("ghnShopId"));
                method.setGhnToken(request.getParameter("ghnToken"));

                methodService.update(method);
                request.getSession().setAttribute("successMessage", "Da cap nhat phuong thuc van chuyen!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Cap nhat that bai: " + e.getMessage());
        }
    }

    private void deleteMethod(HttpServletRequest request) {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            methodService.delete(id);
            request.getSession().setAttribute("successMessage", "Da xoa phuong thuc van chuyen!");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Xoa that bai!");
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
            int pendingOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.WAITING_PICKUP}, days);
            int pickedOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.PICKED_UP}, days);
            int shippingOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.IN_TRANSIT, ShippingStatus.SHIPPING}, days);
            int deliveringOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.OUT_FOR_DELIVERY}, days);
            int completedOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.DELIVERED}, days);
            int failedOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.FAILED, ShippingStatus.RETURNED, ShippingStatus.CANCELLED}, days);
            String topProvince = shipmentDAO.getTopProvinceSince(days);
            List<TopShippingMethod> topMethods = shipmentDAO.getTopShippingMethodsSince(5, days);
            List<Shipment> recentShipments = shipmentDAO.getRecentShipmentsSince(5, days);
            List<ShippingActivity> recentActivities = buildRecentActivities(recentShipments);

            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append(" \"totalShippingOrders\": ").append(totalOrders).append(",");
            json.append(" \"shippingRevenue\": ").append(String.format("%.0f", shippingRevenue)).append(",");
            json.append(" \"pendingOrders\": ").append(pendingOrders).append(",");
            json.append(" \"pickedOrders\": ").append(pickedOrders).append(",");
            json.append(" \"shippingOrders\": ").append(shippingOrders).append(",");
            json.append(" \"deliveringOrders\": ").append(deliveringOrders).append(",");
            json.append(" \"completedOrders\": ").append(completedOrders).append(",");
            json.append(" \"failedOrders\": ").append(failedOrders).append(",");
            json.append(" \"topProvince\": \"").append(escapeJson(topProvince != null ? topProvince : "—")).append("\",");
            json.append(" \"topProvinceName\": \"").append(escapeJson(topProvince != null ? topProvince : "—")).append("\",");
            json.append(" \"totalStatistics\": ").append(totalOrders).append(",");
            json.append(" \"topMethods\": [");

            for (int i = 0; i < topMethods.size(); i++) {
                TopShippingMethod tsm = topMethods.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append(" \"methodName\": \"").append(escapeJson(tsm.getMethodName())).append("\",");
                json.append(" \"providerType\": \"").append(escapeJson(tsm.getProviderType())).append("\",");
                json.append(" \"totalOrders\": ").append(tsm.getTotalOrders()).append(",");
                json.append(" \"totalRevenue\": ").append(String.format("%.0f", tsm.getTotalRevenue())).append(",");
                json.append(" \"usagePercent\": ").append(String.format("%.1f", tsm.getUsagePercent())).append(",");
                json.append(" \"status\": ").append(tsm.getStatus());
                json.append("}");
            }
            json.append("],");
            json.append(" \"recentActivities\": [");

            for (int i = 0; i < recentActivities.size(); i++) {
                ShippingActivity act = recentActivities.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append(" \"title\": \"").append(escapeJson(act.getTitle())).append("\",");
                json.append(" \"description\": \"").append(escapeJson(act.getDescription())).append("\",");
                json.append(" \"timestamp\": \"").append(escapeJson(act.getTimestamp())).append("\",");
                json.append(" \"icon\": \"").append(escapeJson(act.getIcon())).append("\",");
                json.append(" \"dotColor\": \"").append(escapeJson(act.getDotColor())).append("\"");
                json.append("}");
            }
            json.append("]}");

            response.getWriter().write(json.toString());
        } catch (Exception e) {
            logger.error("Error in handleStatsAjax", e);
            response.getWriter().write("{ \"error\": \"Loi tai server\" }");
        }
    }

    private void handleStatsExport(HttpServletRequest request, HttpServletResponse response, String period) throws IOException {
        int days = resolveDays(period);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fileName = "bao-cao-van-chuyen-" + period + "ngay-" + LocalDate.now().format(dtf) + ".csv";

        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        PrintWriter out = response.getWriter();
        out.write("\uFEFF");

        out.write("BAO CAO THONG KE VAN CHUYEN\n");
        out.write("Ky bao cao: " + getPeriodLabel(period) + "\n");
        out.write("Ngay xuat: " + LocalDate.now().format(dtf) + "\n\n");

        int totalOrders = shipmentDAO.countTotalSince(days);
        double revenue = shipmentDAO.sumShippingFeeSince(days);
        int pending = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.WAITING_PICKUP}, days);
        int picked = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.PICKED_UP}, days);
        int shipping = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.IN_TRANSIT, ShippingStatus.SHIPPING}, days);
        int delivering = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.OUT_FOR_DELIVERY}, days);
        int completed = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.DELIVERED}, days);
        int failed = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.FAILED, ShippingStatus.RETURNED, ShippingStatus.CANCELLED}, days);

        out.write("TONG QUAN\n");
        out.write("Tong don van chuyen," + totalOrders + "\n");
        out.write("Doanh thu phi ship," + String.format("%.0f", revenue) + " VND\n");
        out.write("Don cho xac nhan," + pending + "\n");
        out.write("Don da lay," + picked + "\n");
        out.write("Don dang van chuyen," + shipping + "\n");
        out.write("Don dang giao," + delivering + "\n");
        out.write("Don hoan thanh," + completed + "\n");
        out.write("Don that bai / hoan," + failed + "\n");
        out.write("\n");

        out.write("TOP PHUONG THUC VAN CHUYEN\n");
        out.write("STT,Ten phuong thuc,Nha cung cap,Tong don,Doanh thu (VND),Ti le (%)\n");
        List<TopShippingMethod> topMethods = shipmentDAO.getTopShippingMethodsSince(10, days);
        int idx = 1;
        for (TopShippingMethod tsm : topMethods) {
            out.write(idx++ + ",\"" + tsm.getMethodName() + "\",");
            out.write(tsm.getProviderType() + ",");
            out.write(tsm.getTotalOrders() + ",");
            out.write(String.format("%.0f", tsm.getTotalRevenue()) + ",");
            out.write(String.format("%.1f", tsm.getUsagePercent()) + "\n");
        }
        out.write("\n");

        out.write("DON VAN CHUYEN GAN DAY\n");
        out.write("ID,Trang thai,Tinh/Thanh,Phi ship (VND),Ngay tao\n");
        List<Shipment> recentShipments = shipmentDAO.getRecentShipmentsSince(20, days);
        for (Shipment s : recentShipments) {
            out.write(s.getId() + ",\"" + (s.getShippingStatus() != null ? s.getShippingStatus() : "—") + "\",");
            out.write("\"" + (s.getProvince() != null ? s.getProvince() : "—") + "\",");
            out.write(String.format("%.0f", s.getShippingFee()) + ",");
            out.write((s.getCreatedAt() != null ? s.getCreatedAt().format(dtf) : "—") + "\n");
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
        if ("today".equals(period)) return "Hom nay";
        if ("7days".equals(period)) return "7 ngay gan nhat";
        if ("30days".equals(period)) return "30 ngay gan nhat";
        if ("90days".equals(period)) return "90 ngay gan nhat";
        return "7 ngay gan nhat";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private void loadStatisticsData(HttpServletRequest request, String period) {
        int days = resolveDays(period);
        try {
            int totalOrders = shipmentDAO.countTotalSince(days);
            double shippingRevenue = shipmentDAO.sumShippingFeeSince(days);
            int pendingOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.WAITING_PICKUP}, days);
            int pickedOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.PICKED_UP}, days);
            int shippingOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.IN_TRANSIT, ShippingStatus.SHIPPING}, days);
            int deliveringOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.OUT_FOR_DELIVERY}, days);
            int completedOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.DELIVERED}, days);
            int failedOrders = shipmentDAO.countByStatusesSince(new String[]{ShippingStatus.FAILED, ShippingStatus.RETURNED, ShippingStatus.CANCELLED}, days);
            String topProvince = shipmentDAO.getTopProvinceSince(days);
            List<TopShippingMethod> topMethods = shipmentDAO.getTopShippingMethodsSince(5, days);
            List<Shipment> recentShipments = shipmentDAO.getRecentShipmentsSince(5, days);
            List<ShippingActivity> recentActivities = buildRecentActivities(recentShipments);

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
            request.setAttribute("expressOrders", expressOrders);
            request.setAttribute("standardOrders", standardOrders);

            logger.debug("Statistics loaded (period={}): totalOrders={}, revenue={}", period, totalOrders, shippingRevenue);
        } catch (Exception e) {
            logger.error("Error loading statistics data", e);
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

            if ("pending".equalsIgnoreCase(status) || "waiting_pickup".equalsIgnoreCase(status)
                    || "WAITING_PICKUP".equals(status)) {
                title = "Don vua duoc tao";
                icon = "fa-plus-circle";
                dotColor = "warning";
                description = "Van don #" + s.getId() + " dang cho xac nhan";
            } else if ("picked_up".equalsIgnoreCase(status) || "PICKED_UP".equals(status)) {
                title = "Don da duoc lay hang";
                icon = "fa-box-open";
                dotColor = "info";
                description = "Van don #" + s.getId() + " da duoc shipper lay";
            } else if ("shipping".equalsIgnoreCase(status) || "IN_TRANSIT".equals(status) || "INTRANSIT".equals(status)) {
                title = "Don dang van chuyen";
                icon = "fa-truck";
                dotColor = "purple";
                description = "Van don #" + s.getId() + " dang tren duong giao";
            } else if ("delivering".equalsIgnoreCase(status) || "OUT_FOR_DELIVERY".equals(status)) {
                title = "Don dang giao";
                icon = "fa-truck-fast";
                dotColor = "success";
                description = "Van don #" + s.getId() + " dang duoc giao den khach";
            } else if ("delivered".equalsIgnoreCase(status) || "DELIVERED".equals(status)) {
                title = "Don giao thanh cong";
                icon = "fa-circle-check";
                dotColor = "success";
                description = "Van don #" + s.getId() + " da giao thanh cong";
            } else if ("cancelled".equalsIgnoreCase(status) || "returned".equalsIgnoreCase(status)
                    || "CANCELLED".equals(status) || "RETURNED".equals(status) || "FAILED".equals(status)) {
                title = "Don bi huy / hoan";
                icon = "fa-xmark-circle";
                dotColor = "danger";
                description = "Van don #" + s.getId() + " da bi huy hoac hoan";
            } else {
                title = "Cap nhat van don";
                icon = "fa-sync";
                dotColor = "info";
                description = "Van don #" + s.getId() + " - " + (status != null ? status : "khong xac dinh");
            }

            String timestamp;
            if (s.getUpdatedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                timestamp = s.getUpdatedAt().format(formatter);
            } else if (s.getCreatedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                timestamp = s.getCreatedAt().format(formatter);
            } else {
                timestamp = "—";
            }

            activities.add(new ShippingActivity(title, description, timestamp, icon, dotColor));
        }
        return activities;
    }
}
