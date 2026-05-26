<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="_meta.jsp" />
    <title>Đặt hàng thành công</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
</head>

<body>
<jsp:include page="_header.jsp" />

<section class="section-content padding-y bg-light" style="min-height: 80vh">
    <div class="container">
        <div class="card shadow-sm mx-auto" style="max-width: 650px; border-radius: 15px;">
            <div class="card-body p-5">
                <div class="text-center mb-4">
                    <div class="display-1 text-success mb-2">
                        <i class="bi bi-bag-check-fill"></i>
                    </div>
                    <h2 class="fw-bold">Đặt hàng thành công!</h2>
                    <p class="text-muted">Đơn hàng của bạn đã được hệ thống ghi nhận.</p>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-6 text-muted">Mã đơn hàng:</div>
                    <div class="col-6 text-end fw-bold">#${payment.orderId}</div>

                    <div class="col-6 text-muted">Thời gian đặt:</div>
                    <div class="col-6 text-end">
                        <fmt:formatDate value="${payment.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                    </div>

                    <div class="col-6 text-muted">Trạng thái đơn:</div>
                    <div class="col-6 text-end">
                        <c:choose>
                            <c:when test="${payment.status == 0}">
                                <span class="badge bg-warning text-dark">Chờ thanh toán</span>
                            </c:when>
                            <c:when test="${payment.status == 1}">
                                <span class="badge bg-success">Đã thanh toán</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-danger">Thất bại</span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="col-12"><hr class="my-2"></div>

                    <div class="col-6 h5 fw-bold">Tổng số tiền:</div>
                    <div class="col-6 text-end h5 fw-bold text-danger">
                        <fmt:formatNumber value="${payment.amount}" type="currency" />
                    </div>
                </div>

                <div class="d-grid gap-3">
                    <c:if test="${payment.status == 0}">
                        <a href="${pageContext.request.contextPath}/vnpay/checkout?vnpTxnRef=${payment.vnpTxnRef}"
                           class="btn btn-primary btn-lg py-3 fw-bold w-100 shadow-sm">
                            <i class="bi bi-credit-card-2-back"></i> THANH TOÁN NGAY QUA VNPAY
                        </a>
                    </c:if>

                    <div class="row g-2">
                        <div class="col-6">
                            <a href="${pageContext.request.contextPath}/orderDetail?id=${payment.orderId}" class="btn btn-outline-secondary w-100">
                                <i class="bi bi-eye"></i> Xem đơn hàng
                            </a>
                        </div>
                        <div class="col-6">
                            <a href="${pageContext.request.contextPath}/" class="btn btn-light w-100 text-muted">
                                Tiếp tục mua sắm
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card-footer bg-white border-0 text-center pb-4">
                <small class="text-muted">Mã tham chiếu: <span class="font-monospace">${payment.vnpTxnRef}</span></small>
            </div>
        </div>
    </div>
</section>

<jsp:include page="_footer.jsp" />
</body>
</html>