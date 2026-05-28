<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Theo dõi đơn hàng - Shop Bán Sách</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/trackingView.css">
</head>

<body>
<jsp:include page="_header.jsp" />

<div class="tracking-page">
    <div class="container">
        <c:choose>
            <%-- Error State --%>
            <c:when test="${not empty requestScope.error}">
                <div class="empty-state-card">
                    <i class="bi bi-exclamation-triangle"></i>
                    <h4>${requestScope.error}</h4>
                    <p>Vui lòng kiểm tra lại mã đơn hàng hoặc mã vận đơn</p>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">
                        <i class="bi bi-house"></i> Quay về trang chủ
                    </a>
                </div>
            </c:when>

            <%-- Order Found --%>
            <c:when test="${not empty requestScope.order and not empty requestScope.shipment}">
            
                <%-- =============================================
                    TRACKING HEADER CARD
                    ============================================= --%>
                <div class="tracking-header-card">
                    <div class="tracking-header-top">
                        <div class="tracking-header-left">
                            <div class="tracking-header-icon">
                                <i class="bi bi-geo-alt-fill"></i>
                            </div>
                            <div class="tracking-header-info">
                                <h1>Theo dõi đơn hàng</h1>
                                <div class="order-id">
                                    Mã đơn hàng: <code>#${requestScope.order.id}</code>
                                </div>
                            </div>
                        </div>
                        <div class="tracking-status-badge">
                            <span class="status-dot"></span>
                            <span>
                                <c:choose>
                                    <c:when test="${requestScope.shipment.shippingStatus == 'WAITING_PICKUP'}">Đang chờ lấy hàng</c:when>
                                    <c:when test="${requestScope.shipment.shippingStatus == 'PICKED_UP'}">Đã lấy hàng</c:when>
                                    <c:when test="${requestScope.shipment.shippingStatus == 'IN_TRANSIT'}">Đang vận chuyển</c:when>
                                    <c:when test="${requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY'}">Đang giao hàng</c:when>
                                    <c:when test="${requestScope.shipment.shippingStatus == 'DELIVERED'}">Đã giao hàng thành công</c:when>
                                    <c:when test="${requestScope.shipment.shippingStatus == 'FAILED'}">Giao hàng thất bại</c:when>
                                    <c:otherwise>${requestScope.shipment.shippingStatus}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>

                <%-- =============================================
                    PROGRESS BAR
                    ============================================= --%>
                <div class="shipping-progress">
                    <div class="progress-steps">
                        <div class="progress-step ${requestScope.shipment.shippingStatus == 'WAITING_PICKUP' || requestScope.shipment.shippingStatus == 'PICKED_UP' || requestScope.shipment.shippingStatus == 'IN_TRANSIT' || requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY' || requestScope.shipment.shippingStatus == 'DELIVERED' ? 'current' : ''} ${requestScope.shipment.shippingStatus == 'PICKED_UP' || requestScope.shipment.shippingStatus == 'IN_TRANSIT' || requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY' || requestScope.shipment.shippingStatus == 'DELIVERED' ? 'completed' : ''}">
                            <div class="step-icon"><i class="bi bi-bag-check"></i></div>
                            <div class="step-label">Đặt hàng</div>
                        </div>
                        <div class="progress-step ${requestScope.shipment.shippingStatus == 'PICKED_UP' || requestScope.shipment.shippingStatus == 'IN_TRANSIT' || requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY' || requestScope.shipment.shippingStatus == 'DELIVERED' ? 'completed' : ''}">
                            <div class="step-icon"><i class="bi bi-check2-all"></i></div>
                            <div class="step-label">Xác nhận</div>
                        </div>
                        <div class="progress-step ${requestScope.shipment.shippingStatus == 'PICKED_UP' || requestScope.shipment.shippingStatus == 'IN_TRANSIT' || requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY' || requestScope.shipment.shippingStatus == 'DELIVERED' ? 'completed' : ''} ${requestScope.shipment.shippingStatus == 'PICKED_UP' ? 'current' : ''}">
                            <div class="step-icon"><i class="bi bi-box-seam"></i></div>
                            <div class="step-label">Lấy hàng</div>
                        </div>
                        <div class="progress-step ${requestScope.shipment.shippingStatus == 'IN_TRANSIT' || requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY' || requestScope.shipment.shippingStatus == 'DELIVERED' ? 'completed' : ''} ${requestScope.shipment.shippingStatus == 'IN_TRANSIT' ? 'current' : ''}">
                            <div class="step-icon"><i class="bi bi-truck"></i></div>
                            <div class="step-label">Vận chuyển</div>
                        </div>
                        <div class="progress-step ${requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY' || requestScope.shipment.shippingStatus == 'DELIVERED' ? 'completed' : ''} ${requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY' ? 'current' : ''}">
                            <div class="step-icon"><i class="bi bi-bicycle"></i></div>
                            <div class="step-label">Đang giao</div>
                        </div>
                        <div class="progress-step ${requestScope.shipment.shippingStatus == 'DELIVERED' ? 'completed current' : ''}">
                            <div class="step-icon"><i class="bi bi-check-circle"></i></div>
                            <div class="step-label">Đã giao</div>
                        </div>
                    </div>
                </div>

                <%-- =============================================
                    INFO CARDS ROW - 2 Columns
                    ============================================= --%>
                <div class="info-cards-row">
                    <%-- Card 1: Recipient Info --%>
                    <div class="info-card">
                        <div class="info-card-header">
                            <div class="card-icon recipient">
                                <i class="bi bi-person-fill"></i>
                            </div>
                            <h3>Người nhận</h3>
                        </div>
                        <div class="info-card-body">
                            <div class="info-item">
                                <div class="info-icon-box person">
                                    <i class="bi bi-person"></i>
                                </div>
                                <div class="info-content">
                                    <div class="info-label">Họ tên</div>
                                    <div class="info-value large">
                                        <c:choose>
                                            <c:when test="${not empty requestScope.shipment.receiverName}">${requestScope.shipment.receiverName}</c:when>
                                            <c:otherwise>${sessionScope.currentUser.profile.fullname}</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-icon-box phone">
                                    <i class="bi bi-telephone"></i>
                                </div>
                                <div class="info-content">
                                    <div class="info-label">Số điện thoại</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty requestScope.shipment.receiverPhone}">${requestScope.shipment.receiverPhone}</c:when>
                                            <c:otherwise>${sessionScope.currentUser.profile.phoneNumber}</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-icon-box address">
                                    <i class="bi bi-house"></i>
                                </div>
                                <div class="info-content">
                                    <div class="info-label">Địa chỉ giao hàng</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty requestScope.shipment.addressDetail}">
                                                ${requestScope.shipment.addressDetail}<c:if test="${not empty requestScope.shipment.ward}">, ${requestScope.shipment.ward}</c:if><c:if test="${not empty requestScope.shipment.district}">, ${requestScope.shipment.district}</c:if><c:if test="${not empty requestScope.shipment.province}">, ${requestScope.shipment.province}</c:if>
                                            </c:when>
                                            <c:otherwise>
                                                Chưa có thông tin địa chỉ
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <c:if test="${not empty requestScope.shipment.customerNote}">
                            <div class="info-item">
                                <div class="info-icon-box note">
                                    <i class="bi bi-sticky"></i>
                                </div>
                                <div class="info-content">
                                    <div class="info-label">Ghi chú giao hàng</div>
                                    <div class="info-value">${requestScope.shipment.customerNote}</div>
                                </div>
                            </div>
                            </c:if>
                        </div>
                    </div>

                    <%-- Card 2: Shipping Info --%>
                    <div class="info-card">
                        <div class="info-card-header">
                            <div class="card-icon shipping">
                                <i class="bi bi-truck"></i>
                            </div>
                            <h3>Thông tin vận chuyển</h3>
                        </div>
                        <div class="info-card-body">
                            <div class="info-item">
                                <div class="info-icon-box tracking">
                                    <i class="bi bi-qr-code"></i>
                                </div>
                                <div class="info-content">
                                    <div class="info-label">Mã vận đơn</div>
                                    <div class="info-value mono highlight">${requestScope.shipment.trackingCode}</div>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-icon-box provider">
                                    <i class="bi bi-building"></i>
                                </div>
                                <div class="info-content">
                                    <div class="info-label">Đơn vị vận chuyển</div>
                                    <div class="info-value large">
                                        <c:choose>
                                            <c:when test="${not empty requestScope.shipment.shippingMethod.name}">${requestScope.shipment.shippingMethod.name}</c:when>
                                            <c:otherwise>GHN - Giao Hàng Nhanh</c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-icon-box fee">
                                    <i class="bi bi-currency-dollar"></i>
                                </div>
                                <div class="info-content">
                                    <div class="info-label">Phí vận chuyển</div>
                                    <div class="info-value">
                                        <fmt:formatNumber pattern="#,##0" value="${requestScope.shipment.shippingFee}" />đ
                                    </div>
                                </div>
                            </div>
                            <c:if test="${not empty requestScope.shipment.estimatedDeliveryDate}">
                                <div class="info-item">
                                    <div class="info-icon-box calendar">
                                        <i class="bi bi-calendar-event"></i>
                                    </div>
                                    <div class="info-content">
                                        <div class="info-label">Dự kiến giao hàng</div>
                                        <div class="info-value">
                                            ${requestScope.shipment.estimatedDeliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <%-- =============================================
                    TIMELINE CARD
                    ============================================= --%>
                <div class="timeline-card">
                    <div class="timeline-card-header">
                        <h2>
                            <i class="bi bi-activity"></i>
                            Lịch sử vận chuyển
                        </h2>
                    </div>

                    <c:choose>
                        <c:when test="${not empty requestScope.trackingHistory}">
                            <div class="timeline-container">
                                <div class="timeline-track"></div>
                                <c:forEach var="tracking" items="${requestScope.trackingHistory}" varStatus="loop">
                                    <div class="timeline-item ${loop.first ? 'current' : 'completed'}">
                                        <div class="timeline-dot">
                                            <c:if test="${loop.first || loop.index == 1}">
                                                <i class="bi bi-check"></i>
                                            </c:if>
                                        </div>
                                        <div class="timeline-content">
                                            <div class="timeline-status">
                                                ${tracking.status == 'WAITING_PICKUP' ? 'Chờ lấy hàng' :
                                                  tracking.status == 'PICKED_UP' ? 'Đã lấy hàng' :
                                                  tracking.status == 'IN_TRANSIT' ? 'Đang vận chuyển' :
                                                  tracking.status == 'OUT_FOR_DELIVERY' ? 'Đang giao hàng' :
                                                  tracking.status == 'DELIVERED' ? 'Đã giao hàng' :
                                                  tracking.status == 'FAILED' ? 'Giao hàng thất bại' :
                                                  tracking.status}
                                            </div>
                                            <div class="timeline-meta">
                                                <span>
                                                    <i class="bi bi-clock"></i>
                                                    ${tracking.updatedAt.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))}
                                                </span>
                                                <c:if test="${not empty tracking.location}">
                                                    <span>
                                                        <i class="bi bi-geo-alt"></i>
                                                        ${tracking.location}
                                                    </span>
                                                </c:if>
                                            </div>
                                            <c:if test="${not empty tracking.note}">
                                                <div class="timeline-note">${tracking.note}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="timeline-empty">
                                <i class="bi bi-clock-history"></i>
                                <p>Chưa có lịch sử vận chuyển</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <%-- =============================================
                    ACTION BUTTONS
                    ============================================= --%>
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/orderDetail?id=${requestScope.order.id}" class="action-btn back">
                        <i class="bi bi-arrow-left"></i>
                        Chi tiết đơn hàng
                    </a>
                    <a href="${pageContext.request.contextPath}/invoice?id=${requestScope.order.id}" 
                       class="action-btn invoice" 
                       target="_blank"
                       onclick="window.print(); return false;">
                        <i class="bi bi-printer"></i>
                        In hóa đơn
                    </a>
                </div>
            </c:when>

            <%-- Order Not Found --%>
            <c:otherwise>
                <div class="empty-state-card">
                    <i class="bi bi-search"></i>
                    <h4>Không tìm thấy đơn hàng</h4>
                    <p>Vui lòng kiểm tra lại mã đơn hàng hoặc mã vận đơn của bạn</p>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">
                        <i class="bi bi-house"></i> Quay về trang chủ
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="_footer.jsp" />
</body>
</html>
