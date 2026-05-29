<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN"/>

<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="_meta.jsp"/>
    <title>Chi tiết đơn hàng #${requestScope.order.id} - Shop Bán Sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderStatusCheck.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/progress-bar.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>

<body>
    <jsp:include page="_header.jsp"/>

    <c:if test="${not empty sessionScope.currentUser}">
        <input type="hidden" id="currentOrderId" value="${requestScope.order.id}">
        <input type="hidden" id="initialOrderStatus" value="${requestScope.order.status}">
    </c:if>

    <section class="section-pagetop bg-light order-detail-header">
        <div class="container">
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                <div>
                    <h2 class="title-page mb-1">
                        <i class="bi bi-receipt me-2"></i>Chi tiết đơn hàng #${requestScope.order.id}
                    </h2>
                    <p class="text-muted mb-0">
                        <i class="bi bi-calendar3 me-1"></i>Ngày đặt: ${requestScope.createdAt}
                    </p>
                </div>
                <div class="d-flex align-items-center gap-3">
                    <div class="realtime-indicator">
                        <span class="pulse-dot"></span>
                        <span>Theo dõi realtime</span>
                    </div>
                    <span class="badge ${requestScope.order.status == 1 ? 'bg-warning text-dark' : requestScope.order.status == 2 ? 'bg-info' : requestScope.order.status == 3 ? 'bg-primary' : requestScope.order.status == 4 ? 'bg-primary' : requestScope.order.status == 5 ? 'bg-info' : requestScope.order.status == 6 ? 'bg-success' : requestScope.order.status == 7 ? 'bg-danger' : 'bg-secondary'} fs-6 px-3 py-2">
                        <i class="bi bi-${requestScope.order.status == 1 ? 'clock' : requestScope.order.status == 2 ? 'check2-all' : requestScope.order.status == 3 ? 'box-seam' : requestScope.order.status == 4 ? 'truck' : requestScope.order.status == 5 ? 'geo-alt' : requestScope.order.status == 6 ? 'check-circle' : requestScope.order.status == 7 ? 'x-circle' : 'question-circle'} me-1"></i>
                        ${requestScope.order.status == 1 ? 'Đã đặt hàng' : requestScope.order.status == 2 ? 'Đã xác nhận' : requestScope.order.status == 3 ? 'Đã lấy hàng' : requestScope.order.status == 4 ? 'Đang vận chuyển' : requestScope.order.status == 5 ? 'Đang giao hàng' : requestScope.order.status == 6 ? 'Đã giao thành công' : requestScope.order.status == 7 ? 'Đã hủy' : 'Không xác định'}
                    </span>
                </div>
            </div>
        </div>
    </section>

    <%-- ORDER PROGRESS BAR (Shopee-style) --%>
    <c:set var="orderStatusText">
        <c:choose>
            <c:when test="${requestScope.order.status == 1}">Đã đặt hàng</c:when>
            <c:when test="${requestScope.order.status == 2}">Đã xác nhận</c:when>
            <c:when test="${requestScope.order.status == 3}">Đã lấy hàng</c:when>
            <c:when test="${requestScope.order.status == 4}">Đang vận chuyển</c:when>
            <c:when test="${requestScope.order.status == 5}">Đang giao hàng</c:when>
            <c:when test="${requestScope.order.status == 6}">Đã giao thành công</c:when>
            <c:when test="${requestScope.order.status == 7}">Đã hủy</c:when>
            <c:otherwise>Không xác định</c:otherwise>
        </c:choose>
    </c:set>
    <c:set var="orderStatusForProgress" value="${requestScope.order.status}" scope="request"/>
    <c:set var="orderStatusTextForProgress" value="${orderStatusText}" scope="request"/>
    <section class="section-content padding-y pt-3 pb-0">
        <div class="container">
            <jsp:include page="/WEB-INF/views/components/order-progress-bar.jsp">
                <jsp:param name="orderStatus" value="${requestScope.order.status}"/>
                <jsp:param name="orderStatusText" value="${orderStatusText}"/>
            </jsp:include>
        </div>
    </section>

    <%-- Cancelled order display --%>
    <c:if test="${requestScope.order.status == 7}">
        <section class="section-content padding-y pt-0">
            <div class="container">
                <div class="card order-card-enhanced mb-4 border-danger">
                    <div class="card-body text-center py-5">
                        <div class="mb-4">
                            <div class="d-inline-flex align-items-center justify-content-center rounded-circle bg-danger text-white" style="width: 80px; height: 80px;">
                                <i class="bi bi-x-circle-fill fs-1"></i>
                            </div>
                        </div>
                        <h4 class="text-danger mb-2">Đơn hàng đã bị hủy</h4>
                        <p class="text-muted mb-0">
                            <c:if test="${not empty requestScope.cancelledAt}">
                                Hủy lúc: ${requestScope.cancelledAt}
                            </c:if>
                        </p>
                    </div>
                </div>
            </div>
        </section>
    </c:if>

    <section class="section-content padding-y pt-0">
        <div class="container">
            <div class="row">
                <main class="col-12">
                    <div class="card order-card-enhanced mb-4">
                        <div class="card-header bg-white py-3">
                            <div class="d-flex justify-content-between align-items-center">
                                <h5 class="mb-0"><i class="bi bi-box-seam me-2 text-primary"></i>Sản phẩm đã đặt</h5>
                                <a href="${pageContext.request.contextPath}/invoice?id=${requestScope.order.id}"
                                   class="btn btn-outline-primary btn-sm" target="_blank">
                                    <i class="bi bi-printer"></i> In hóa đơn
                                </a>
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead class="bg-light">
                                        <tr>
                                            <th style="min-width: 300px;">Sản phẩm</th>
                                            <th class="text-center">Đơn giá</th>
                                            <th class="text-center">Số lượng</th>
                                            <th class="text-end">Thành tiền</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="orderItem" items="${requestScope.orderItems}">
                                            <tr>
                                                <td>
                                                    <figure class="d-flex align-items-center gap-3 mb-0">
                                                        <div>
                                                            <c:choose>
                                                                <c:when test="${empty orderItem.product.imageName}">
                                                                    <img width="70" height="70" class="rounded object-fit-cover"
                                                                         src="${pageContext.request.contextPath}/img/280px.png" alt="Product">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img width="70" height="70" class="rounded object-fit-cover"
                                                                         src="${pageContext.request.contextPath}/image/${orderItem.product.imageName}"
                                                                         alt="${orderItem.product.name}">
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                        <figcaption>
                                                            <a href="${pageContext.request.contextPath}/product?id=${orderItem.product.id}"
                                                               class="text-decoration-none text-dark fw-medium">
                                                                ${orderItem.product.name}
                                                            </a>
                                                            <p class="text-muted small mb-0">Tác giả: ${orderItem.product.author}</p>
                                                        </figcaption>
                                                    </figure>
                                                </td>
                                                <td class="text-center align-middle">
                                                    <c:choose>
                                                        <c:when test="${orderItem.discount > 0}">
                                                            <div class="fw-bold text-danger">
                                                                <fmt:formatNumber pattern="#,##0" value="${orderItem.price * (100 - orderItem.discount) / 100}"/>đ
                                                            </div>
                                                            <small class="text-muted text-decoration-line-through">
                                                                <fmt:formatNumber pattern="#,##0" value="${orderItem.price}"/>đ
                                                            </small>
                                                            <br><span class="badge bg-info small">-<fmt:formatNumber pattern="#,##0" value="${orderItem.discount}"/>%</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="fw-bold">
                                                                <fmt:formatNumber pattern="#,##0" value="${orderItem.price}"/>đ
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-center align-middle">
                                                    <span class="badge bg-secondary">x${orderItem.quantity}</span>
                                                </td>
                                                <td class="text-end align-middle fw-bold text-primary">
                                                    <fmt:formatNumber pattern="#,##0" value="${orderItem.discount > 0 ? orderItem.price * (100 - orderItem.discount) / 100 * orderItem.quantity : orderItem.price * orderItem.quantity}"/>đ
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <div class="row g-4">
                        <div class="col-md-6">
                            <div class="card order-card-enhanced h-100">
                                <div class="card-header bg-white py-3">
                                    <h5 class="mb-0"><i class="bi bi-geo-alt me-2 text-danger"></i>Địa chỉ nhận hàng</h5>
                                </div>
                                <div class="card-body">
                                    <div class="shipping-info-card">
                                        <p class="mb-2">
                                            <strong class="fs-5">
                                                <i class="bi bi-person me-2 text-muted"></i>
                                                ${not empty requestScope.shipment.receiverName ? requestScope.shipment.receiverName : sessionScope.currentUser.profile.fullname}
                                            </strong>
                                        </p>
                                        <p class="text-muted mb-1">
                                            <i class="bi bi-telephone me-2"></i>
                                            ${not empty requestScope.shipment.receiverPhone ? requestScope.shipment.receiverPhone : sessionScope.currentUser.profile.phoneNumber}
                                        </p>
                                        <p class="text-muted mb-0">
                                            <i class="bi bi-house me-2"></i>
                                            <c:choose>
                                                <c:when test="${not empty requestScope.shipment.addressDetail}">
                                                    ${requestScope.shipment.addressDetail}, ${requestScope.shipment.ward}, ${requestScope.shipment.district}, ${requestScope.shipment.province}
                                                </c:when>
                                                <c:otherwise>
                                                    Chưa có thông tin địa chỉ
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <c:if test="${not empty requestScope.shipment.trackingCode}">
                                            <div class="mt-3 p-2 bg-white rounded border">
                                                <small class="text-muted d-block mb-1">Mã vận đơn:</small>
                                                <strong class="text-primary fs-6">${requestScope.shipment.trackingCode}</strong>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="card order-card-enhanced h-100">
                                <div class="card-header bg-white py-3">
                                    <h5 class="mb-0"><i class="bi bi-truck me-2 text-info"></i>Vận chuyển & Thanh toán</h5>
                                </div>
                                <div class="card-body">
                                    <div class="shipping-info-card">
                                        <div class="mb-3">
                                            <h6 class="text-muted mb-2"><i class="bi bi-box me-2"></i>Phương thức giao hàng</h6>
                                            <p class="mb-1 fw-medium">
                                                <i class="bi bi-${requestScope.shipment.shippingMethod != null && requestScope.shipment.shippingMethod.express ? 'lightning' : 'truck'} me-2"></i>
                                                <c:choose>
                                                    <c:when test="${not empty requestScope.shipment.shippingMethod.name}">${requestScope.shipment.shippingMethod.name}</c:when>
                                                    <c:otherwise>${requestScope.order.deliveryMethod == 1 ? 'Giao tiêu chuẩn' : 'Giao nhanh GHN'}</c:otherwise>
                                                </c:choose>
                                            </p>
                                            <small class="text-muted">
                                                <i class="bi bi-clock me-1"></i>
                                                <c:choose>
                                                    <c:when test="${requestScope.shipment.shippingMethod != null && requestScope.shipment.shippingMethod.estimatedDays > 0}">Dự kiến ${requestScope.shipment.shippingMethod.estimatedDays} ngày</c:when>
                                                    <c:otherwise>Dự kiến 3-5 ngày làm việc</c:otherwise>
                                                </c:choose>
                                            </small>
                                        </div>
                                        <hr>
                                        <div class="mb-0">
                                            <h6 class="text-muted mb-3"><i class="bi bi-credit-card me-2"></i>Thanh toán</h6>

                                            <c:choose>
                                                <c:when test="${requestScope.payment.status == 1}">
                                                    <span class="badge bg-success w-100 py-2 mb-2 text-start"><i class="bi bi-check-circle-fill me-1"></i> Đã thanh toán</span>
                                                    <p class="mb-1"><strong>Cổng:</strong> VNPay (${requestScope.payment.bankCode})</p>
                                                    <p class="mb-1 text-truncate"><strong>Mã GD:</strong> ${requestScope.payment.vnpTransactionNo}</p>
                                                    <p class="text-muted small mb-0"><i class="bi bi-clock me-1"></i> ${requestScope.payment.payDate}</p>
                                                </c:when>

                                                <c:when test="${requestScope.payment.status == 2}">
                                                    <span class="badge bg-danger w-100 py-2 mb-2 text-start"><i class="bi bi-x-circle-fill me-1"></i> Thanh toán thất bại</span>
                                                    <c:choose>
                                                        <c:when test="${not empty requestScope.payment.vnpResponseCode}">
                                                            <p class="text-danger mb-2">${requestScope.vnpMessage} (${requestScope.payment.vnpResponseCode})</p>
                                                        </c:when>
                                                        <c:when test="${requestScope.payment.expired}">
                                                            <p class="text-danger mb-2">Hết hạn thanh toán</p>
                                                        </c:when>
                                                    </c:choose>
                                                </c:when>

                                                <c:when test="${requestScope.payment.status == 3}">
                                                    <span class="badge bg-warning text-dark w-100 py-2 mb-2 text-start"><i class="bi bi-arrow-repeat me-1"></i> Đang xử lý hoàn tiền</span>
                                                    <p class="text-muted mb-0">Hoàn tiền đang được xử lý qua VNPay</p>
                                                </c:when>

                                                <c:when test="${requestScope.payment.status == 4}">
                                                    <span class="badge bg-secondary w-100 py-2 mb-2 text-start"><i class="bi bi-arrow-counterclockwise me-1"></i> Đã hoàn tiền</span>
                                                    <p class="text-muted mb-0">Hoàn tiền thành công</p>
                                                </c:when>

                                                <c:otherwise>
                                                    <span class="badge bg-warning text-dark w-100 py-2 mb-2 text-start"><i class="bi bi-exclamation-circle me-1"></i> Chưa thanh toán</span>
                                                    <p class="text-muted mb-2">Chờ thanh toán qua VNPay</p>
                                                    <c:if test="${not empty requestScope.payment.expiredAt}">
                                                        <p class="small text-muted mb-0"><i class="bi bi-hourglass-split me-1"></i> Hạn: <span class="text-danger fw-bold">${requestScope.payment.expiredAt}</span></p>
                                                    </c:if>
                                                </c:otherwise>
                                            </c:choose>

                                            <c:if test="${not empty requestScope.shipment.shippingStatus}">
                                                <div class="mt-3">
                                                    <span class="badge ${requestScope.shipment.shippingStatus == 'PENDING' ? 'bg-warning text-dark' : requestScope.shipment.shippingStatus == 'SHIPPING' ? 'bg-info' : requestScope.shipment.shippingStatus == 'DELIVERED' ? 'bg-success' : 'bg-secondary'}">
                                                        <i class="bi bi-${requestScope.shipment.shippingStatus == 'PENDING' ? 'clock' : requestScope.shipment.shippingStatus == 'SHIPPING' ? 'truck' : requestScope.shipment.shippingStatus == 'DELIVERED' ? 'check-circle' : 'question-circle'} me-1"></i>
                                                        ${requestScope.shipment.shippingStatus == 'PENDING' ? 'Chờ xử lý' : requestScope.shipment.shippingStatus == 'SHIPPING' ? 'Đang giao' : requestScope.shipment.shippingStatus == 'DELIVERED' ? 'Đã giao' : requestScope.shipment.shippingStatus}
                                                    </span>
                                                </div>
                                            </c:if>
                                            <c:if test="${(requestScope.order.status == 1 || requestScope.order.status == 2 || requestScope.order.status == 3 || requestScope.order.status == 4 || requestScope.order.status == 5) && (requestScope.payment.status == 0 || requestScope.isRetryAble) && !requestScope.payment.expired}">
                                                <a id="payment-btn" href="${pageContext.request.contextPath}/vnpay/checkout?vnpTxnRef=${requestScope.payment.vnpTxnRef}" class="btn btn-primary w-100 mt-3 fw-bold">
                                                    <c:choose>
                                                        <c:when test="${requestScope.payment.status == 2}">
                                                            <i class="bi bi-arrow-repeat me-1"></i> THANH TOÁN LẠI
                                                        </c:when>
                                                        <c:otherwise>
                                                            <i class="bi bi-credit-card-2-back me-1"></i> THANH TOÁN NGAY
                                                        </c:otherwise>
                                                    </c:choose>
                                                </a>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <c:if test="${not empty requestScope.trackingHistory}">
                        <div class="card order-card-enhanced mt-4">
                            <div class="card-header bg-white py-3">
                                <h5 class="mb-0"><i class="bi bi-activity me-2 text-success"></i>Lịch sử vận chuyển</h5>
                            </div>
                            <div class="card-body p-0 px-md-3">
                                <div class="tracking-timeline-realtime">
                                    <c:forEach var="tracking" items="${requestScope.trackingHistory}" varStatus="loop">
                                        <div class="tracking-item ${loop.first ? 'latest' : ''} ${loop.index > 0 ? 'completed' : ''}">
                                            <div class="tracking-icon">
                                                <i class="bi ${loop.first ? 'bi-circle-fill' : 'bi-check-circle-fill'}"></i>
                                            </div>
                                            <div class="tracking-content">
                                                <div class="tracking-header">
                                                    <span class="tracking-status">
                                                        <c:choose>
                                                            <c:when test="${tracking.status == 'WAITING_PICKUP'}">Chờ lấy hàng</c:when>
                                                            <c:when test="${tracking.status == 'PICKED_UP'}">Đã lấy hàng</c:when>
                                                            <c:when test="${tracking.status == 'IN_TRANSIT'}">Đang vận chuyển</c:when>
                                                            <c:when test="${tracking.status == 'OUT_FOR_DELIVERY'}">Đang giao hàng</c:when>
                                                            <c:when test="${tracking.status == 'DELIVERED'}">Đã giao hàng thành công</c:when>
                                                            <c:when test="${tracking.status == 'FAILED'}">Giao hàng thất bại</c:when>
                                                            <c:when test="${tracking.status == 'RETURNED'}">Đã trả hàng</c:when>
                                                            <c:when test="${tracking.status == 'CANCELLED'}">Đã hủy</c:when>
                                                            <c:otherwise>${tracking.status}</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                    <span class="tracking-time-badge">
                                                        <i class="bi bi-clock me-1"></i>
                                                        <fmt:formatDate value="${tracking.updatedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                    </span>
                                                </div>
                                                <c:if test="${not empty tracking.location}">
                                                    <div class="tracking-location">
                                                        <i class="bi bi-geo-alt-fill me-1"></i>${tracking.location}
                                                    </div>
                                                </c:if>
                                                <c:if test="${not empty tracking.note}">
                                                    <div class="tracking-note">
                                                        <i class="bi bi-chat-left-text me-1"></i>${tracking.note}
                                                    </div>
                                                </c:if>
                                                <c:if test="${not empty tracking.updatedBy}">
                                                    <div class="tracking-updater">
                                                        <i class="bi bi-person me-1"></i>${tracking.updatedBy}
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <div class="card order-card-enhanced mt-4">
                        <div class="card-header bg-white py-3">
                            <h5 class="mb-0"><i class="bi bi-receipt me-2 text-warning"></i>Tổng quan đơn hàng</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-12">
                                    <table class="table table-borderless mb-0">
                                        <tr>
                                            <td class="text-muted ps-0">Tạm tính:</td>
                                            <td class="text-end fw-medium pe-0">
                                                <fmt:formatNumber pattern="#,##0" value="${requestScope.tempPrice}"/>đ
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-muted ps-0">Phí vận chuyển:</td>
                                            <td class="text-end fw-medium pe-0">
                                                <fmt:formatNumber pattern="#,##0" value="${requestScope.order.deliveryPrice}"/>đ
                                            </td>
                                        </tr>
                                        <tr class="border-top pt-2">
                                            <td class="ps-0 fw-bold fs-5">Tổng cộng:</td>
                                            <td class="text-end pe-0 fw-bold fs-4 text-danger">
                                                <fmt:formatNumber pattern="#,##0" value="${requestScope.tempPrice + requestScope.order.deliveryPrice}"/>đ
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div class="card-footer bg-white d-flex flex-wrap justify-content-between align-items-center gap-2">
                            <div class="d-flex flex-wrap gap-2">
                                <c:choose>
                                    <c:when test="${requestScope.order.status == 1 || requestScope.order.status == 2 || requestScope.order.status == 3 || requestScope.order.status == 4 || requestScope.order.status == 5}">
                                        <c:choose>
                                            <c:when test="${requestScope.payment.status == 1}">
                                                <form action="${pageContext.request.contextPath}/vnpay/refund" method="post" class="d-inline">
                                                    <input type="hidden" name="oId" value="${requestScope.order.id}">
                                                    <input type="hidden" name="pId" value="${requestScope.payment.id}">
                                                    <button type="submit" class="btn btn-danger" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn và hoàn tiền không?')">
                                                        <i class="bi bi-arrow-counterclockwise me-1"></i> Hủy đơn và hoàn tiền
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <form action="${pageContext.request.contextPath}/cancelOrder" method="post" class="d-inline">
                                                    <input type="hidden" name="oId" value="${requestScope.order.id}">
                                                    <input type="hidden" name="pId" value="${requestScope.payment.id}">
                                                    <c:choose>
                                                        <c:when test="${empty requestScope.confirmCancel}">
                                                            <button type="submit" class="btn btn-outline-danger" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')">
                                                                <i class="bi bi-x-circle me-1"></i> Hủy đơn hàng
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted me-2">Bạn có chắc chắn muốn hủy đơn?</span>
                                                            <button type="submit" class="btn btn-danger me-2">Xác nhận hủy</button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/rebuy" method="post" class="d-inline">
                                            <input type="hidden" name="oId" value="${requestScope.order.id}">
                                            <button type="submit" class="btn btn-primary"><i class="bi bi-bag-plus me-1"></i> Mua lại đơn hàng</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                                <a href="${pageContext.request.contextPath}/order" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> Quay lại danh sách</a>
                            </div>

                            <div class="d-flex flex-wrap gap-2">
                                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-primary"><i class="bi bi-bag-plus"></i> Tiếp tục mua sắm</a>
                                <a href="${pageContext.request.contextPath}/invoice?id=${requestScope.order.id}" class="btn btn-primary" target="_blank"><i class="bi bi-printer"></i> In hóa đơn</a>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </div>
    </section>

    <jsp:include page="_footer.jsp"/>

    <script src="${pageContext.request.contextPath}/js/orderStatusChecker.js"></script>
</body>
</html>
