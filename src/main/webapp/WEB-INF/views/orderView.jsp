<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="_meta.jsp" />
    <title>Đơn hàng của tôi</title>
    <style>
        .nav-tabs .nav-link {
            color: #495057;
            font-weight: 500;
            padding: 12px 20px;
            border: none;
            border-bottom: 3px solid transparent;
        }
        .nav-tabs .nav-link.active {
            color: #0d6efd;
            border: none;
            border-bottom: 3px solid #0d6efd;
            background: transparent;
        }
        .order-card {
            border: 1px solid #e3e6f0;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,.02);
            transition: all 0.2s ease;
        }
        .order-card:hover {
            box-shadow: 0 4px 8px rgba(0,0,0,.05);
        }
    </style>
</head>

<body>
<jsp:include page="_header.jsp" />

<section class="section-pagetop bg-light py-4">
    <div class="container">
        <h2 class="h4 mb-0">Đơn hàng của tôi</h2>
    </div>
</section>

<section class="py-4 bg-white">
    <div class="container">
        <div class="row">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <jsp:include page="_navPanel.jsp">
                        <jsp:param name="active" value="ORDER" />
                    </jsp:include>

                    <main class="col-md-9">

                        <ul class="nav nav-tabs mb-4 bg-white shadow-sm rounded justify-content-between text-center">
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${empty param.status ? 'active' : ''}" href="${pageContext.request.contextPath}/order">Tất cả</a>
                            </li>
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${param.status == '0' ? 'active' : ''}" href="${pageContext.request.contextPath}/order?status=0">Chờ xử lý</a>
                            </li>
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${param.status == '1' ? 'active' : ''}" href="${pageContext.request.contextPath}/order?status=1">Đang giao</a>
                            </li>
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${param.status == '2' ? 'active' : ''}" href="${pageContext.request.contextPath}/order?status=2">Đã giao</a>
                            </li>
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${param.status == '3' ? 'active' : ''}" href="${pageContext.request.contextPath}/order?status=3">Đã hủy</a>
                            </li>
                        </ul>

                        <c:choose>
                            <c:when test="${not empty requestScope.orders}">
                                <c:forEach var="order" items="${requestScope.orders}">
                                    <div class="card order-card mb-3 bg-white border-0 shadow-sm">
                                        <div class="card-header bg-white d-flex justify-content-between align-items-center py-3 border-bottom">
                                            <div>
                                                <strong class="text-dark me-2">Đơn hàng #${order.id}</strong>
                                                <span class="text-muted small"><i class="far fa-calendar-alt me-1"></i> ${order.createdAt}</span>
                                            </div>
                                            <div>
                                                <c:choose>
                                                    <c:when test="${order.status == 0}">
                                                        <span class="badge bg-warning text-white py-2 px-3"><i class="fas fa-clock me-1"></i> Chờ xử lý</span>
                                                    </c:when>
                                                    <c:when test="${order.status == 1}">
                                                        <span class="badge bg-warning text-white py-2 px-3"><i class="fas fa-truck me-1"></i> Đang giao hàng</span>
                                                    </c:when>
                                                    <c:when test="${order.status == 2}">
                                                        <span class="badge bg-success py-2 px-3"><i class="fas fa-check me-1"></i> Giao thành công</span>
                                                    </c:when>
                                                    <c:when test="${order.status == 3}">
                                                        <span class="badge bg-danger py-2 px-3"><i class="fas fa-times me-1"></i> Đã hủy</span>
                                                    </c:when>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="card-body p-4">
                                            <div class="d-flex align-items-center justify-content-between">
                                                <div class="d-flex align-items-center">
                                                    <div>
                                                        <h6 class="mb-2 text-dark fw-bold"><i class="fas fa-user text-muted me-2"></i>Người nhận: ${order.name}</h6>
                                                        <span class="text-muted small"><i class="fas fa-credit-card text-muted me-2"></i>Thanh toán khi nhận hàng</span>
                                                    </div>
                                                </div>
                                                <div class="text-end">
                                                    <p class="mb-1 text-muted small">Tổng thanh toán</p>
                                                    <h5 class="text-danger fw-bold mb-0">
                                                        <fmt:formatNumber pattern="#,##0" value="${order.total}" /> ₫
                                                    </h5>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card-footer bg-white d-flex justify-content-end py-3 border-top">
                                            <a class="btn btn-outline-primary btn-sm px-4"
                                               href="${pageContext.request.contextPath}/orderDetail?id=${order.id}"
                                               role="button"><i class="fas fa-eye me-1"></i> Xem chi tiết</a>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5 bg-white shadow-sm rounded border">
                                    <p class="text-muted mb-0">Không tìm thấy đơn hàng nào ở trạng thái này.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <c:if test="${requestScope.totalPages > 1}">
                            <nav class="mt-4">
                                <ul class="pagination justify-content-center">
                                    <li class="page-item ${requestScope.page == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/order?page=${requestScope.page - 1}&status=${param.status}">Trang trước</a>
                                    </li>
                                    <c:forEach begin="1" end="${requestScope.totalPages}" var="i">
                                        <c:choose>
                                            <c:when test="${requestScope.page == i}">
                                                <li class="page-item active"><a class="page-link">${i}</a></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li class="page-item">
                                                    <a class="page-link" href="${pageContext.request.contextPath}/order?page=${i}&status=${param.status}">${i}</a>
                                                </li>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                    <li class="page-item ${requestScope.page == requestScope.totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/order?page=${requestScope.page + 1}&status=${param.status}">Trang sau</a>
                                    </li>
                                </ul>
                            </nav>
                        </c:if>

                    </main>
                </c:when>
                <c:otherwise>
                    <div class="col-12 text-center py-5">
                        <p class="text-muted mb-3">Vui lòng đăng nhập để sử dụng trang này.</p>
                        <a href="${pageContext.request.contextPath}/signin" class="btn btn-primary px-4">Đăng nhập ngay</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<jsp:include page="_footer.jsp" />
</body>

</html>