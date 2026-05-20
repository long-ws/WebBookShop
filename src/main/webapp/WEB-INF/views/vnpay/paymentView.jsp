<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="../_meta.jsp" />
    <title>Thanh toán VNPAY</title>
    <style>
        .animate-pulse {
            animation: pulse 1s infinite;
        }
        @keyframes pulse {
            0% { opacity: 1; }
            50% { opacity: 0.4; }
            100% { opacity: 1; }
        }
    </style>
</head>
<body class="bg-light">
<jsp:include page="../_header.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div id="timeout-card" class="card border-0 shadow-sm d-none">
                <div class="card-body p-5 text-center">
                    <div class="text-danger mb-4">
                        <i class="fas fa-exclamation-circle fa-4x animate-pulse"></i>
                    </div>
                    <h4 class="fw-bold text-dark mb-3">Đã hết thời gian thanh toán</h4>
                    <p class="text-muted mb-4 small">
                        Giao dịch này đã quá thời gian quy định của hệ thống. Đơn hàng của bạn có thể đã bị hủy hoặc tạm khóa. Vui lòng quay lại kiểm tra lịch sử đơn hàng.
                    </p>
                    <div class="d-grid">
                        <a href="${pageContext.request.contextPath}/order" class="btn btn-outline-secondary">
                            <i class="fas fa-history me-2"></i>Xem lịch sử đơn hàng
                        </a>
                    </div>
                </div>
            </div>

            <div id="payment-card" class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <img src="https://stcd02206177151.cloud.edgevnpay.vn/assets/images/logo-icon/logo-primary.svg" alt="VNPAY" height="40">
                        <h5 class="fw-bold mt-3">Xác nhận thanh toán</h5>
                    </div>

                    <c:if test="${not empty payment.expiredAt}">
                        <div class="alert alert-danger py-2 px-3 border-0 d-flex justify-content-between align-items-center mb-3">
                            <span class="small fw-medium text-danger">
                                <i class="fas fa-clock me-1 animate-pulse"></i> Thời gian giữ đơn còn lại:
                            </span>
                            <span id="vnpay-countdown" class="font-monospace fw-bold fs-5 text-danger">--:--</span>
                        </div>
                    </c:if>

                    <div class="alert alert-secondary py-2 border-0">
                        <div class="d-flex justify-content-between small">
                            <span>Mã đơn: <strong>#${payment.orderId}</strong></span>
                            <span>Số tiền: <strong class="text-danger"><fmt:formatNumber value="${payment.amount}" pattern="#,##0"/> ₫</strong></span>
                        </div>
                    </div>

                    <form action="${pageContext.request.contextPath}/vnpay/payment" method="post">
                        <input type="hidden" name="vnpTxnRef" value="${payment.vnpTxnRef}">

                        <div class="mb-3">
                            <label class="form-label fw-bold small text-uppercase text-muted">Chọn phương thức</label>
                            <div class="list-group">
                                <label class="list-group-item d-flex gap-2">
                                    <input class="form-check-input flex-shrink-0" type="radio" name="bankCode" value="VNBANK">
                                    <span>Thẻ ATM nội địa</span>
                                </label>
                                <label class="list-group-item d-flex gap-2">
                                    <input class="form-check-input flex-shrink-0" type="radio" name="bankCode" value="" checked>
                                    <span>Cổng thanh toán VNPAY</span>
                                </label>
                                <label class="list-group-item d-flex gap-2">
                                    <input class="form-check-input flex-shrink-0" type="radio" name="bankCode" value="VNPAYQR">
                                    <span>Ứng dụng VNPAYQR</span>
                                </label>
                                <label class="list-group-item d-flex gap-2">
                                    <input class="form-check-input flex-shrink-0" type="radio" name="bankCode" value="INTCARD">
                                    <span>Thẻ quốc tế (Visa/Master)</span>
                                </label>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small text-uppercase text-muted">Ngôn ngữ</label>
                            <div class="d-flex gap-3">
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="language" id="vn" value="vn" checked>
                                    <label class="form-check-label" for="vn">Tiếng Việt</label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="language" id="en" value="en">
                                    <label class="form-check-label" for="en">English</label>
                                </div>
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary btn-lg fw-bold">
                                THANH TOÁN NGAY
                            </button>
                            <a href="${pageContext.request.contextPath}/" class="btn btn-link btn-sm text-muted">Quay lại</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../_footer.jsp" />

<c:if test="${not empty payment.expiredAt}">
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const expiredStr = "${payment.expiredAt}".replace(/-/g, "/");
            if (!expiredStr) return;

            const targetTime = new Date(expiredStr).getTime();
            const clock = document.getElementById("vnpay-countdown");
            const paymentCard = document.getElementById("payment-card");
            const timeoutCard = document.getElementById("timeout-card");

            const timer = setInterval(function () {
                const now = new Date().getTime();
                const remain = targetTime - now;

                const minutes = Math.floor((remain % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((remain % (1000 * 60)) / 1000);

                const strMin = minutes < 10 ? "0" + minutes : minutes;
                const strSec = seconds < 10 ? "0" + seconds : seconds;

                if (clock) {
                    clock.innerHTML = strMin + ":" + strSec;
                }

                if (remain <= 0) {
                    clearInterval(timer);

                    if (paymentCard && timeoutCard) {
                        paymentCard.classList.add("d-none");
                        timeoutCard.classList.remove("d-none");
                    }
                }
            }, 1000);
        });
    </script>
</c:if>
</body>
</html>