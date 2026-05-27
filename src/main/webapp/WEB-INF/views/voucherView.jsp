<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="_meta.jsp" />
    <title>Voucher của tôi</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
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
        .voucher-card {
            border: 1px dashed #e3e6f0;
            border-left: 8px solid #0d6efd;
            border-radius: 8px;
            background: #fff;
            position: relative;
            overflow: hidden;
        }
        .voucher-type-0 { border-left-color: #0d6efd; }
        .voucher-type-1 { border-left-color: #0d6efd; }
        .voucher-type-2 { border-left-color: #0d6efd; }
        .voucher-type-3 { border-left-color: #198754; }
        .voucher-disabled {
            border-left-color: #6c757d !important;
            opacity: 0.7;
        }
        .voucher-card::before, .voucher-card::after {
            content: "";
            position: absolute;
            left: -5px;
            width: 10px;
            height: 10px;
            background: #ffffff;
            border-radius: 50%;
            border: 1px solid #e3e6f0;
            z-index: 2;
        }
        .voucher-card::before { top: -5px; }
        .voucher-card::after { bottom: -5px; }
    </style>
</head>

<body>
<jsp:include page="_header.jsp" />

<jsp:useBean id="now" class="java.util.Date" />

<section class="py-4 bg-white">
    <div class="container">
        <div class="row">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <jsp:include page="_navPanel.jsp">
                        <jsp:param name="active" value="VOUCHER" />
                    </jsp:include>

                    <main class="col-md-9">
                        <ul class="nav nav-tabs mb-4 bg-white shadow-sm rounded justify-content-between text-center">
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${empty param.applyTo ? 'active' : ''}" href="${pageContext.request.contextPath}/vouchers">Tất cả</a>
                            </li>
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${param.applyTo == '0' ? 'active' : ''}" href="${pageContext.request.contextPath}/vouchers?applyTo=0">Toàn bộ sản phẩm</a>
                            </li>
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${param.applyTo == '1' ? 'active' : ''}" href="${pageContext.request.contextPath}/vouchers?applyTo=1">Theo sản phẩm</a>
                            </li>
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${param.applyTo == '2' ? 'active' : ''}" href="${pageContext.request.contextPath}/vouchers?applyTo=2">Theo danh mục</a>
                            </li>
                            <li class="nav-item flex-fill">
                                <a class="nav-link ${param.applyTo == '3' ? 'active' : ''}" href="${pageContext.request.contextPath}/vouchers?applyTo=3">Vận chuyển</a>
                            </li>
                        </ul>

                        <c:choose>
                            <c:when test="${not empty requestScope.vouchers}">
                                <div class="row g-3">
                                    <c:forEach var="v" items="${requestScope.vouchers}">
                                        <c:set var="statusLabel" value="" />
                                        <c:set var="statusClass" value="bg-success text-white" />
                                        <c:set var="isInactive" value="false" />

                                        <c:choose>
                                            <c:when test="${now.time < v.startDate.time}">
                                                <c:set var="statusLabel" value="Sắp diễn ra" />
                                                <c:set var="statusClass" value="bg-warning text-white" />
                                                <c:set var="isInactive" value="true" />
                                            </c:when>
                                            <c:when test="${now.time > v.endDate.time}">
                                                <c:set var="statusLabel" value="Hết hạn" />
                                                <c:set var="statusClass" value="bg-danger text-white" />
                                                <c:set var="isInactive" value="true" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="statusLabel" value="Đang diễn ra" />
                                                <c:set var="statusClass" value="bg-success text-white" /> <%-- Đổi thành xanh lá --%>
                                            </c:otherwise>
                                        </c:choose>

                                        <div class="col-12 col-lg-6">
                                            <div class="voucher-card p-3 shadow-sm d-flex justify-content-between align-items-center voucher-type-${v.applyTo} ${isInactive ? 'voucher-disabled' : ''}">

                                                <div class="flex-fill pe-2">
                                                    <div class="d-flex align-items-center gap-2 mb-1">
                                                        <c:choose>
                                                            <c:when test="${v.applyTo == 0}"><span class="badge bg-primary-subtle text-primary small">Toàn bộ sản phẩm</span></c:when>
                                                            <c:when test="${v.applyTo == 1}"><span class="badge bg-primary-subtle text-primary small">Một số sản phẩm</span></c:when>
                                                            <c:when test="${v.applyTo == 2}"><span class="badge bg-primary-subtle text-primary small">Một số danh mục</span></c:when>
                                                            <c:when test="${v.applyTo == 3}"><span class="badge bg-success-subtle text-success small">FreeShip</span></c:when>
                                                        </c:choose>

                                                        <span class="badge ${statusClass} small" style="font-size: 11px;">${statusLabel}</span>
                                                    </div>

                                                    <h5 class="fw-bold text-danger mb-1">
                                                        Giảm
                                                        <c:choose>
                                                            <c:when test="${v.calculationMethod == 0}">
                                                                <fmt:formatNumber value="${v.value}" pattern="#.##"/>%
                                                            </c:when>
                                                            <c:otherwise>
                                                                <fmt:formatNumber value="${v.value}" pattern="#,##0"/> ₫
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </h5>

                                                    <p class="text-dark small mb-1 fw-bold">${v.name}</p>
                                                    <p class="text-muted small mb-2" style="font-size: 12px;">
                                                        <i class="far fa-calendar-alt me-1"></i>Hạn dùng: <fmt:formatDate value="${v.endDate}" pattern="dd/MM/yyyy HH:mm" />
                                                    </p>

                                                    <div class="text-muted small" style="font-size: 11px;">
                                                        Đơn tối thiểu: <fmt:formatNumber value="${v.minPurchase}" pattern="#,##0"/> ₫
                                                        <c:if test="${v.calculationMethod == 0}">
                                                            - Tối đa: <fmt:formatNumber value="${v.maxDiscount}" pattern="#,##0"/> ₫
                                                        </c:if>
                                                    </div>
                                                </div>

                                                <div class="text-center ps-2 d-flex flex-column align-items-center justify-content-center" style="width: 95px; height: 80px;">
                                                    <c:choose>
                                                        <c:when test="${statusLabel == 'Đang diễn ra'}">
                                                            <a href="${pageContext.request.contextPath}/products" class="btn btn-sm text-white px-2 py-1 fw-bold" style="background-color: #0d6efd; font-size: 12px; white-space: nowrap;">Dùng ngay</a>
                                                        </c:when>
                                                        <c:when test="${statusLabel == 'Sắp diễn ra'}">
                                                            <button class="btn btn-sm btn-outline-warning px-2 py-1 disabled" style="font-size: 12px; white-space: nowrap;">Chờ mở</button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button class="btn btn-sm btn-secondary text-white px-2 py-1 disabled" style="font-size: 12px; white-space: nowrap;">Hết hạn</button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5 bg-white shadow-sm rounded border">
                                    <p class="text-muted mb-0">Không tìm thấy voucher nào ở trạng thái này.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <c:if test="${requestScope.totalPages > 1}">
                            <nav class="mt-4">
                                <ul class="pagination justify-content-center">
                                    <li class="page-item ${requestScope.page == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/vouchers?page=${requestScope.page - 1}&applyTo=${param.applyTo}">Trang trước</a>
                                    </li>
                                    <c:forEach begin="1" end="${requestScope.totalPages}" var="i">
                                        <c:choose>
                                            <c:when test="${requestScope.page == i}">
                                                <li class="page-item active"><a class="page-link">${i}</a></li>
                                            </c:when>
                                            <c:otherwise>
                                                <li class="page-item">
                                                    <a class="page-link" href="${pageContext.request.contextPath}/vouchers?page=${i}&applyTo=${param.applyTo}">${i}</a>
                                                </li>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                    <li class="page-item ${requestScope.page == requestScope.totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}/vouchers?page=${requestScope.page + 1}&applyTo=${param.applyTo}">Trang sau</a>
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