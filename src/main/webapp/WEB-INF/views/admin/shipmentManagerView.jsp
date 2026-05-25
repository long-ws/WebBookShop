<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
<head>
<jsp:include page="../_meta.jsp" />
<title>Quản lý vận chuyển</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/shipmentManagerView.css">
</head>
<body class="d-flex flex-column min-vh-100">
<jsp:include page="../_headerAdmin.jsp" />
<section class="section-content">
    <div class="container">
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert-soft alert-soft-success" role="alert">
                <i class="bi bi-check-circle-fill"></i>
                <span>${sessionScope.successMessage}</span>
            </div>
        </c:if>
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert-soft alert-soft-danger" role="alert">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <span>${sessionScope.errorMessage}</span>
            </div>
        </c:if>
        <c:remove var="successMessage" scope="session" />
        <c:remove var="errorMessage" scope="session" />
        <div class="page-header">
            <h1>
                <span class="header-icon"><i class="bi bi-truck"></i></span>
                Quản lý vận chuyển
            </h1>
            <div class="page-header-actions">
                <a href="${pageContext.request.contextPath}/admin/shippingMethod" class="btn-header-action">
                    <i class="bi bi-gear"></i> Phương thức vận chuyển
                </a>
            </div>
        </div>
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon total"><i class="bi bi-box-seam"></i></div>
                <div class="stat-info">
                    <h3>${requestScope.totalCount}</h3>
                    <p>Tổng số vận đơn</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon waiting"><i class="bi bi-clock"></i></div>
                <div class="stat-info">
                    <h3>${requestScope.waitingCount}</h3>
                    <p>Chờ lấy hàng</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon transit"><i class="bi bi-truck-front"></i></div>
                <div class="stat-info">
                    <h3>${requestScope.transitCount}</h3>
                    <p>Đang vận chuyển</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon delivered"><i class="bi bi-check-circle"></i></div>
                <div class="stat-info">
                    <h3>${requestScope.deliveredCount}</h3>
                    <p>Đã giao hàng</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon failed"><i class="bi bi-x-circle"></i></div>
                <div class="stat-info">
                    <h3>${requestScope.failedCount}</h3>
                    <p>Thất bại / Trả lại</p>
                </div>
            </div>
        </div>
        <div class="filter-bar">
            <div class="filter-label"><i class="bi bi-funnel"></i> Lọc trạng thái</div>
            <div class="filter-tabs">
                <a href="${pageContext.request.contextPath}/admin/shipmentManager?status=all" class="filter-tab ${requestScope.statusFilter == 'all' ? 'active' : ''}">Tất cả <span class="badge-count">${requestScope.totalCount}</span></a>
                <a href="${pageContext.request.contextPath}/admin/shipmentManager?status=WAITING_PICKUP" class="filter-tab ${requestScope.statusFilter == 'WAITING_PICKUP' ? 'active' : ''}"><i class="bi bi-clock"></i> Chờ lấy</a>
                <a href="${pageContext.request.contextPath}/admin/shipmentManager?status=IN_TRANSIT" class="filter-tab ${requestScope.statusFilter == 'IN_TRANSIT' ? 'active' : ''}"><i class="bi bi-truck-front"></i> Đang chuyển</a>
                <a href="${pageContext.request.contextPath}/admin/shipmentManager?status=DELIVERED" class="filter-tab ${requestScope.statusFilter == 'DELIVERED' ? 'active' : ''}"><i class="bi bi-check-circle"></i> Đã giao</a>
            </div>
        </div>
        <div class="table-card">
            <div class="table-header">
                <h5><i class="bi bi-list-ul text-primary"></i> Danh sách vận đơn</h5>
                <span class="text-muted" style="font-size:0.8rem;">Hiển thị <strong>${requestScope.shipments.size()}</strong> vận đơn</span>
            </div>
            <c:choose>
                <c:when test="${not empty requestScope.shipments}">
                    <div style="overflow-x: auto;">
                        <table class="table table-hover align-middle mb-0">
                            <thead>
                                <tr>
                                    <th style="width:40px;">#</th>
                                    <th>Mã vận đơn</th>
                                    <th>Mã đơn hàng</th>
                                    <th>Người nhận</th>
                                    <th>Địa chỉ</th>
                                    <th>Tracking</th>
                                    <th>Phương thức</th>
                                    <th>Phí vận chuyển</th>
                                    <th>Trạng thái</th>
                                    <th style="width: 80px;">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="shipment" varStatus="loop" items="${requestScope.shipments}">
                                    <tr>
                                        <td class="text-muted" style="font-size:0.8rem;">${loop.index + 1}</td>
                                        <td><strong>#${shipment.id}</strong></td>
                                        <td><a href="${pageContext.request.contextPath}/admin/orderManager/detail?id=${shipment.orderId}" class="text-primary fw-semibold text-decoration-none">#${shipment.orderId}</a></td>
                                        <td>
                                            <div class="customer-info">
                                                <strong>${shipment.receiverName}</strong>
                                                <small><i class="bi bi-telephone"></i> ${shipment.receiverPhone}</small>
                                            </div>
                                        </td>
                                        <td style="max-width: 180px;"><small class="text-muted">${shipment.addressDetail}</small></td>
                                        <td><code class="tracking-code">${shipment.trackingCode}</code></td>
                                        <td>
                                            <c:set var="methodKey" value="method_${shipment.id}" />
                                            <c:set var="method" value="${requestScope[methodKey]}" />
                                            <c:choose>
                                                <c:when test="${not empty method}">${method.name}</c:when>
                                                <c:otherwise><small class="text-muted">—</small></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fw-semibold text-end"><fmt:formatNumber pattern="#,##0" value="${shipment.shippingFee}" />₫</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${shipment.shippingStatus == 'WAITING_PICKUP'}"><span class="status-badge status-waiting"><span class="dot"></span>Chờ lấy</span></c:when>
                                                <c:when test="${shipment.shippingStatus == 'PICKED_UP'}"><span class="status-badge status-picked"><span class="dot"></span>Đã lấy</span></c:when>
                                                <c:when test="${shipment.shippingStatus == 'IN_TRANSIT'}"><span class="status-badge status-transit"><span class="dot"></span>Đang chuyển</span></c:when>
                                                <c:when test="${shipment.shippingStatus == 'OUT_FOR_DELIVERY'}"><span class="status-badge status-out"><span class="dot"></span>Đang giao</span></c:when>
                                                <c:when test="${shipment.shippingStatus == 'DELIVERED'}"><span class="status-badge status-delivered"><span class="dot"></span>Đã giao</span></c:when>
                                                <c:when test="${shipment.shippingStatus == 'FAILED'}"><span class="status-badge status-failed"><span class="dot"></span>Thất bại</span></c:when>
                                                <c:otherwise><span class="status-badge status-returned"><span class="dot"></span>${shipment.shippingStatus}</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="action-group">
                                                <a href="${pageContext.request.contextPath}/admin/shipmentManager/detail?id=${shipment.id}" class="btn-action btn-view" title="Chi tiết"><i class="bi bi-eye"></i></a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-icon"><i class="bi bi-inbox"></i></div>
                        <h5>Không có vận đơn nào</h5>
                        <p>Danh sách vận đơn trống.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <c:if test="${requestScope.totalPages != null && requestScope.totalPages > 0}">
            <div class="table-card mt-3">
                <div class="pagination-wrap">
                    <div class="pagination-info">Trang <strong>${requestScope.page}</strong> / <strong>${requestScope.totalPages}</strong> — Tổng <strong>${requestScope.totalCount}</strong> vận đơn</div>
                    <nav>
                        <ul class="pagination pagination-soft mb-0">
                            <li class="page-item ${requestScope.page == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/shipmentManager?page=${requestScope.page - 1}&status=${requestScope.statusFilter}"><i class="bi bi-chevron-left"></i></a>
                            </li>
                            <c:forEach begin="1" end="${requestScope.totalPages > 5 ? 5 : requestScope.totalPages}" var="i">
                                <c:set var="startPage" value="${requestScope.page > 3 ? requestScope.page - 2 : 1}" />
                                <c:set var="endPage" value="${startPage + 4}" />
                                <c:if test="${endPage > requestScope.totalPages}"><c:set var="endPage" value="${requestScope.totalPages}" /><c:set var="startPage" value="${endPage - 4 < 1 ? 1 : endPage - 4}" /></c:if>
                                <c:if test="${i >= startPage && i <= endPage}">
                                    <li class="page-item ${requestScope.page == i ? 'active' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/admin/shipmentManager?page=${i}&status=${requestScope.statusFilter}">${i}</a>
                                    </li>
                                </c:if>
                            </c:forEach>
                            <li class="page-item ${requestScope.page == requestScope.totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/shipmentManager?page=${requestScope.page + 1}&status=${requestScope.statusFilter}"><i class="bi bi-chevron-right"></i></a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </c:if>
    </div>
</section>
<jsp:include page="../_footerAdmin.jsp" />
</body>
</html>
