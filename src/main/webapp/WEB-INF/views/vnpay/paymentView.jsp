<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="../_meta.jsp" />
    <title>Thanh toán VNPAY</title>
</head>
<body class="bg-light">
<jsp:include page="../_header.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <img src="https://stcd02206177151.cloud.edgevnpay.vn/assets/images/logo-icon/logo-primary.svg" alt="VNPAY" height="40">
                        <h5 class="fw-bold mt-3">Xác nhận thanh toán</h5>
                    </div>

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
</body>
</html>