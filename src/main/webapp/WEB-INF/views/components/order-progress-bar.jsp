<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%
    // Nhận các tham số từ request
    Integer orderStatusInt = null;

    // Thử đọc từ request parameter trước
    String statusParam = request.getParameter("orderStatus");
    if (statusParam != null && !statusParam.isEmpty()) {
        try {
            orderStatusInt = Integer.parseInt(statusParam);
        } catch (NumberFormatException e) {
            orderStatusInt = null;
        }
    }

    // Fallback: thử đọc từ request attribute
    if (orderStatusInt == null) {
        Object orderStatusObj = request.getAttribute("orderStatus");
        if (orderStatusObj != null) {
            if (orderStatusObj instanceof Integer) {
                orderStatusInt = (Integer) orderStatusObj;
            } else {
                try {
                    orderStatusInt = Integer.parseInt(orderStatusObj.toString());
                } catch (NumberFormatException e) {
                    orderStatusInt = null;
                }
            }
        }
    }

    // Fallback: thử đọc từ attribute của order object
    if (orderStatusInt == null) {
        Object orderObj = request.getAttribute("order");
        if (orderObj != null) {
            try {
                java.lang.reflect.Method m = orderObj.getClass().getMethod("getStatus");
                Object status = m.invoke(orderObj);
                if (status instanceof Integer) {
                    orderStatusInt = (Integer) status;
                } else if (status != null) {
                    orderStatusInt = Integer.parseInt(status.toString());
                }
            } catch (Exception e) {
                orderStatusInt = null;
            }
        }
    }

    if (orderStatusInt == null) {
        orderStatusInt = 1;
    }

    final int orderStatus = orderStatusInt;

    // Lấy orderStatusText
    String orderStatusText = request.getParameter("orderStatusText");
    if (orderStatusText == null || orderStatusText.isEmpty()) {
        orderStatusText = (String) request.getAttribute("orderStatusText");
    }
    if (orderStatusText == null || orderStatusText.isEmpty()) {
        switch (orderStatus) {
            case 1: orderStatusText = "Đã đặt hàng"; break;
            case 2: orderStatusText = "Đã xác nhận"; break;
            case 3: orderStatusText = "Đã lấy hàng"; break;
            case 4: orderStatusText = "Đang vận chuyển"; break;
            case 5: orderStatusText = "Đang giao hàng"; break;
            case 6: orderStatusText = "Đã giao thành công"; break;
            case 7: orderStatusText = "Đã hủy"; break;
            default: orderStatusText = "Không xác định";
        }
    }

    // Các bước trong progress bar (giống Shopee)
    final java.util.List<java.util.Map<String, Object>> steps = new java.util.ArrayList<>();
    steps.add(java.util.Map.of("step", 1, "label", "Đã đặt hàng", "icon", "bag-check", "shortLabel", "Đặt hàng"));
    steps.add(java.util.Map.of("step", 2, "label", "Đã xác nhận", "icon", "check2-all", "shortLabel", "Xác nhận"));
    steps.add(java.util.Map.of("step", 3, "label", "Đã lấy hàng", "icon", "box-seam", "shortLabel", "Lấy hàng"));
    steps.add(java.util.Map.of("step", 4, "label", "Đang vận chuyển", "icon", "truck", "shortLabel", "Vận chuyển"));
    steps.add(java.util.Map.of("step", 5, "label", "Đang giao", "icon", "geo-alt", "shortLabel", "Đang giao"));
    steps.add(java.util.Map.of("step", 6, "label", "Đã giao", "icon", "check-circle", "shortLabel", "Đã giao"));

    boolean isCancelled = (orderStatus == 7);
    boolean isDelivered = (orderStatus == 6);

    // Tính % hoàn thành cho progress line
    double progressPercent = 0;
    if (!isCancelled) {
        progressPercent = ((double)(orderStatus - 1) / 5.0) * 100;
    } else if (isDelivered) {
        progressPercent = 100;
    }

    // Xác định CSS class cho status badge
    String statusBadgeClass = "status-pending";
    String statusIcon = "clock-fill";
    if (orderStatus == 7) {
        statusBadgeClass = "status-cancelled";
        statusIcon = "x-circle-fill";
    } else if (orderStatus == 6) {
        statusBadgeClass = "status-delivered";
        statusIcon = "check-circle-fill";
    } else if (orderStatus >= 4) {
        statusBadgeClass = "status-shipping";
        statusIcon = "truck";
    } else if (orderStatus == 3) {
        statusBadgeClass = "status-picked";
        statusIcon = "box-seam";
    } else if (orderStatus == 2) {
        statusBadgeClass = "status-confirmed";
        statusIcon = "check2-all";
    }
%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/progress-bar.css">

<div class="order-progress-container">
    <div class="order-progress-header">
        <div class="order-progress-title">
            <i class="bi bi-truck"></i>
            <span>Tiến trình đơn hàng</span>
        </div>
        <div class="order-progress-status <%= statusBadgeClass %>">
            <i class="bi bi-<%= statusIcon %>"></i>
            <%= orderStatusText %>
        </div>
    </div>

    <% if (isCancelled) { %>
    <div class="cancelled-overlay">
        <i class="bi bi-x-circle-fill"></i>
        <div class="cancelled-text">Đơn hàng đã bị hủy</div>
        <div class="cancelled-sub">Cảm ơn bạn đã sử dụng dịch vụ</div>
    </div>
    <% } else { %>

    <div class="order-progress-steps" style="--progress: <%= String.format("%.1f", progressPercent) %>;">
        <div class="progress-line-bg" aria-hidden="true"></div>
        <div class="progress-line-fill" aria-hidden="true"></div>

        <% for (int i = 0; i < steps.size(); i++) {
            java.util.Map<String, Object> step = steps.get(i);
            int stepNum = (Integer) step.get("step");
            String label = (String) step.get("label");
            String icon = (String) step.get("icon");
            String shortLabel = (String) step.get("shortLabel");

            String stepClass = "";
            if (isDelivered || stepNum <= orderStatus) {
                if (stepNum < orderStatus || isDelivered) {
                    stepClass = "completed";
                } else if (stepNum == orderStatus) {
                    stepClass = "active";
                }
            } else {
                stepClass = "pending";
            }
        %>
        <div class="progress-step-item <%= stepClass %>">
            <div class="step-icon-container <%= stepClass %>">
                <i class="bi bi-<%= icon %>"></i>
            </div>
            <div class="step-label step-label-long"><%= label %></div>
            <div class="step-label step-label-short"><%= shortLabel %></div>
        </div>
        <% } %>
    </div>

    <% } %>
</div>
