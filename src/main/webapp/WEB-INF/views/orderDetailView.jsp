<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="_meta.jsp"/>
    <title>Thông tin đơn hàng #${requestScope.order.id}</title>
</head>

<body>
<jsp:include page="_header.jsp"/>

<section class="section-pagetop bg-light py-4">
    <div class="container">
        <h2 class="h4 mb-0">Chi tiết đơn hàng #${requestScope.order.id}</h2>
    </div>
</section>

<section class="py-4 bg-white">
    <div class="container">
        <div class="row">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <jsp:include page="_navPanel.jsp">
                        <jsp:param name="active" value="ORDER"/>
                    </jsp:include>

                    <main class="col-md-9">
                        <article class="card shadow-sm mb-4 border-0">
                            <header class="card-header bg-white border-bottom py-3 d-flex justify-content-between align-items-center flex-wrap">
                                <div>
                                    <strong class="me-3 text-dark fs-5">Đơn hàng #${requestScope.order.id}</strong>
                                    <span class="text-muted"><i class="far fa-calendar-alt me-1"></i> Ngày đặt: ${requestScope.createdAt}</span>
                                </div>
                                <div class="mt-2 mt-sm-0">
                                    <c:choose>
                                        <c:when test="${requestScope.order.status == 0}">
                                            <span class="badge bg-warning text-white py-2 px-3"><i class="fas fa-clock me-1"></i> Chờ xử lý</span>
                                        </c:when>
                                        <c:when test="${requestScope.order.status == 1}">
                                            <span class="badge bg-warning text-white py-2 px-3"><i class="fas fa-truck me-1"></i> Đang giao hàng</span>
                                        </c:when>
                                        <c:when test="${requestScope.order.status == 2}">
                                            <span class="badge bg-success py-2 px-3"><i class="fas fa-check me-1"></i> Giao thành công</span>
                                        </c:when>
                                        <c:when test="${requestScope.order.status == 3}">
                                            <span class="badge bg-danger py-2 px-3"><i class="fas fa-times me-1"></i> Đã hủy</span>
                                        </c:when>
                                    </c:choose>
                                </div>
                            </header>

                            <div class="card-body p-4">
                                <div class="row g-4">

                                    <div class="col-lg-8">
                                        <h6 class="text-dark fw-bold mb-3">
                                            <i class="fas fa-map-marker-alt text-danger me-2"></i> Thông tin người nhận
                                        </h6>
                                        <div class="bg-light p-3 rounded mb-4 small lh-lg">
                                            <p class="mb-1"><strong><i class="fas fa-user text-muted me-2" style="width: 15px;"></i>tên</strong></p>
                                            <p class="mb-1 text-muted"><i class="fas fa-phone text-muted me-2" style="width: 15px;"></i>số điện thoại</p>
                                            <p class="mb-0 text-muted"><i class="fas fa-home text-muted me-2" style="width: 15px;"></i>địa chỉ</p>
                                        </div>

                                        <h6 class="text-dark fw-bold mb-3">
                                            <i class="fas fa-box text-secondary me-2"></i> Danh sách sản phẩm
                                        </h6>
                                        <div class="table-responsive border rounded">
                                            <table class="table table-borderless align-middle mb-0">
                                                <thead class="table-light text-muted small text-uppercase">
                                                <tr>
                                                    <th scope="col" class="ps-3" style="min-width: 250px;">Sản phẩm</th>
                                                    <th scope="col" style="min-width: 110px;">Giá bán</th>
                                                    <th scope="col" class="text-center" style="min-width: 80px;">SL</th>
                                                    <th scope="col" class="text-end pe-3" style="min-width: 110px;">Tạm tính</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="orderItem" items="${requestScope.orderItems}">
                                                    <tr class="border-bottom">
                                                        <td class="ps-3 py-3">
                                                            <div class="d-flex align-items-center">
                                                                <div class="flex-shrink-0 me-3 border rounded p-1 bg-white">
                                                                    <c:choose>
                                                                        <c:when test="${empty orderItem.product.imageName}">
                                                                            <img width="60" height="60" style="object-fit: cover;" src="${pageContext.request.contextPath}/img/280px.png" alt="No image">
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <img width="60" height="60" style="object-fit: cover;" src="${pageContext.request.contextPath}/image/${orderItem.product.imageName}" alt="Product image">
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                                <div>
                                                                    <a href="${pageContext.request.contextPath}/product?id=${orderItem.product.id}" target="_blank" class="text-dark fw-bold text-decoration-none d-block mb-1 text-truncate" style="max-width: 200px;">
                                                                            ${orderItem.product.name}
                                                                    </a>
                                                                    <small class="text-muted">Mã SP: #${orderItem.product.id}</small>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${orderItem.discount == 0}">
                                                                    <span class="fw-bold text-dark"><fmt:formatNumber pattern="#,##0" value="${orderItem.price}"/>₫</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div>
                                                                        <span class="fw-bold text-danger"><fmt:formatNumber pattern="#,##0" value="${orderItem.price * (100 - orderItem.discount) / 100}"/>₫</span>
                                                                        <span class="ms-1 badge bg-danger small">-${orderItem.discount}%</span>
                                                                    </div>
                                                                    <small class="text-muted text-decoration-line-through small"><fmt:formatNumber pattern="#,##0" value="${orderItem.price}"/>₫</small>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td class="text-center fw-bold text-secondary">${orderItem.quantity}</td>
                                                        <td class="text-end pe-3 fw-bold text-dark">
                                                            <c:choose>
                                                                <c:when test="${orderItem.discount == 0}">
                                                                    <fmt:formatNumber pattern="#,##0" value="${orderItem.price * orderItem.quantity}"/>₫
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <fmt:formatNumber pattern="#,##0" value="${(orderItem.price * (100 - orderItem.discount) / 100) * orderItem.quantity}"/>₫
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>

                                        <div class="mt-4 d-flex justify-content-start">
                                            <c:choose>
                                                <c:when test="${requestScope.order.status == 0 || requestScope.order.status == 1}">
                                                    <c:choose>
                                                        <c:when test="${requestScope.payment.status == 1}">
                                                            <form action="${pageContext.request.contextPath}/vnpay/refund" method="post" class="d-inline">
                                                                <input type="hidden" name="oId" value="${requestScope.order.id}">
                                                                <input type="hidden" name="pId" value="${requestScope.payment.id}">
                                                                <button type="submit" class="btn btn-danger px-4" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn và hoàn tiền không?')">
                                                                    <i class="fas fa-undo me-1"></i> Hủy đơn và hoàn tiền
                                                                </button>
                                                            </form>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <form action="${pageContext.request.contextPath}/cancelOrder" method="post" class="d-inline">
                                                                <input type="hidden" name="oId" value="${requestScope.order.id}">
                                                                <input type="hidden" name="pId" value="${requestScope.payment.id}">
                                                                <button type="submit" class="btn btn-outline-danger px-4" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')">
                                                                    <i class="fas fa-times me-1"></i> Hủy đơn hàng
                                                                </button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:when>
                                                <c:otherwise>
                                                    <form action="${pageContext.request.contextPath}/rebuy" method="post" class="d-flex align-items-center flex-wrap gap-2">
                                                        <input type="hidden" name="oId" value="${requestScope.order.id}">
                                                        <button type="submit" class="btn btn-primary px-4">
                                                            <i class="fas fa-shopping-cart me-1"></i> Mua lại đơn hàng
                                                        </button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                    <div class="col-lg-4">
                                        <div class="card bg-light border-0 p-3 rounded">
                                            <h6 class="text-dark fw-bold mb-3 pb-2 border-bottom">
                                                <i class="fas fa-credit-card text-primary me-2"></i> Thanh toán & Vận chuyển
                                            </h6>

                                            <div class="mb-3 small lh-lg">
                                                <div class="mb-2">
                                                    <span class="text-muted d-block">Phương thức vận chuyển:</span>
                                                    <strong class="text-dark">${requestScope.order.deliveryMethod == 1 ? "Giao hàng tiêu chuẩn" : "Giao hàng hỏa tốc"}</strong>
                                                </div>

                                                <div class="mb-2">
                                                    <span class="text-muted d-block">Trạng thái thanh toán:</span>
                                                    <c:choose>
                                                        <c:when test="${requestScope.payment.status == 1}">
                                                            <span class="badge bg-success w-100 py-2 my-1 text-start ps-2"><i class="fas fa-check-circle me-1"></i> Đã thanh toán</span>
                                                            <span class="text-dark d-block">Cổng: <strong>VNPay (${requestScope.payment.bankCode})</strong></span>
                                                            <span class="text-dark d-block text-truncate">Mã GD: <strong>${requestScope.payment.vnpTransactionNo}</strong></span>
                                                            <span class="text-muted d-block small"><i class="far fa-clock me-1"></i> ${requestScope.payment.payDate}</span>
                                                        </c:when>
                                                        <c:when test="${requestScope.payment.status == 2}">
                                                            <span class="badge bg-danger w-100 py-2 my-1 text-start ps-2"><i class="fas fa-times-circle me-1"></i> Thanh toán thất bại</span>
                                                            <c:choose>
                                                                <c:when test="${not empty requestScope.payment.vnpResponseCode}">
                                                                    <span class="text-danger d-block">
                                                                        <strong>${requestScope.vnpMessage} (${requestScope.payment.vnpResponseCode})</strong>
                                                                    </span>
                                                                </c:when>
                                                                <c:when test="${requestScope.payment.expired}">
                                                                    <span class="text-danger d-block">
                                                                        <strong>Hết hạn thanh toán</strong>
                                                                    </span>
                                                                </c:when>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:when test="${requestScope.payment.status == 3}">
                                                            <span class="badge bg-warning text-dark w-100 py-2 my-1 text-start ps-2"><i class="fas fa-sync fa-spin me-1"></i> Đang xử lý hoàn tiền</span>
                                                            <span class="text-secondary d-block">Đang xử lý hoàn tiền qua VNPay</span>
                                                        </c:when>

                                                        <c:when test="${requestScope.payment.status == 4}">
                                                            <span class="badge bg-secondary w-100 py-2 my-1 text-start ps-2"><i class="fas fa-undo me-1"></i> Đã hoàn tiền</span>
                                                            <span class="text-muted d-block">Hoàn tiền thành công!</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-warning text-dark w-100 py-2 my-1 text-start ps-2"><i class="fas fa-exclamation-circle me-1"></i> Chưa thanh toán</span>
                                                            <span class="text-secondary d-block">Chờ thanh toán qua VNPay</span>
                                                            <c:if test="${not empty requestScope.payment.expiredAt}">
                                                                <span class="text-muted d-block small mb-2"><i class="fas fa-hourglass-half me-1"></i> Hạn: <span class="text-danger fw-bold">${requestScope.payment.expiredAt}</span></span>
                                                            </c:if>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>

                                            <div class="border-top pt-3 small lh-lg">
                                                <div class="d-flex justify-content-between text-muted mb-1">
                                                    <span>Tạm tính:</span>
                                                    <span><fmt:formatNumber pattern="#,##0" value="${requestScope.tempPrice}"/> ₫</span>
                                                </div>
                                                <div class="d-flex justify-content-between text-muted mb-2">
                                                    <span>Phí vận chuyển:</span>
                                                    <span>+ <fmt:formatNumber pattern="#,##0" value="${requestScope.order.deliveryPrice}"/> ₫</span>
                                                </div>
                                                <div class="d-flex justify-content-between align-items-center pt-2 border-top mb-3">
                                                    <strong class="text-dark">Tổng thanh toán:</strong>
                                                    <strong class="text-danger h5 mb-0 fw-bold">
                                                        <fmt:formatNumber pattern="#,##0" value="${requestScope.tempPrice + requestScope.order.deliveryPrice}"/> ₫
                                                    </strong>
                                                </div>
                                                <c:if test="${(requestScope.order.status == 0 || requestScope.order.status == 1) && (requestScope.payment.status == 0 || requestScope.isRetryAble) && !requestScope.payment.expired}">
                                                    <a id="payment-btn" href="${pageContext.request.contextPath}/vnpay/checkout?vnpTxnRef=${requestScope.payment.vnpTxnRef}" class="btn btn-primary btn-lg py-2.5 fs-6 fw-bold w-100 shadow-sm mt-2">
                                                        <c:choose>
                                                            <c:when test="${requestScope.payment.status == 2}">
                                                                <i class="fas fa-redo me-1"></i> THANH TOÁN LẠI
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
                        </article>
                    </main>
                </c:when>
                <c:otherwise>
                    <div class="col-12 text-center py-5">
                        <p class="text-muted mb-3">Vui lòng đăng nhập để xem thông tin chi tiết đơn hàng của bạn.</p>
                        <a href="${pageContext.request.contextPath}/signin" class="btn btn-primary px-4">Đăng nhập ngay</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<jsp:include page="_footer.jsp"/>
</body>

</html>