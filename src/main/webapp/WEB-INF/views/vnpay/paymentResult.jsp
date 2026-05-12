<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>

<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="../_meta.jsp"/>
    <title>Kết quả thanh toán</title>
</head>
<body class="bg-light">
<jsp:include page="../_header.jsp"/>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow border-0 text-center p-4" style="border-radius: 15px;">
                <div class="card-body">
                    <c:choose>
                        <c:when test="${isSuccess}">
                            <div class="display-1 text-success mb-3">
                                <i class="bi bi-check-circle-fill"></i>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="display-1 text-danger mb-3">
                                <i class="bi bi-exclamation-triangle-fill"></i>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <h2 class="fw-bold">${message}</h2>
                    <hr class="my-4">

                    <div class="text-start mb-4">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Mã đơn hàng:</span>
                            <span class="fw-bold">#${payment.vnpTxnRef}</span>
                        </div>

                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Số tiền:</span>
                            <span class="fw-bold text-primary">
                            <fmt:formatNumber value="${payment.amount}" type="currency"/>
                        </span>
                        </div>

                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Ngân hàng:</span>
                            <span>${payment.bankCode}</span>
                        </div>

                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Thời gian:</span>
                            <span class="fw-bold">
                                <fmt:formatDate value="${payment.payDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                            </span>
                        </div>
                    </div>

                    <div class="d-grid gap-2">
                        <a href="${pageContext.request.contextPath}/orderDetail?id=${payment.orderId}}" class="btn btn-primary btn-lg">
                            Xem lịch sử đơn hàng
                        </a>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-link text-muted">
                            Quay về trang chủ
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../_footer.jsp"/>
</body>
</html>