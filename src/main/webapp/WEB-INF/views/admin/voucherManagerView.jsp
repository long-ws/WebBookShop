<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="../_meta.jsp"/>
    <title>Quản lý voucher</title>
</head>

<body class="d-flex flex-column min-vh-100">
<jsp:include page="../_headerAdmin.jsp"/>

<section class="section-content flex-fill">
    <div class="container d-flex flex-column flex-fill">
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success mb-0 mt-4" role="alert">${sessionScope.successMessage}</div>
        </c:if>
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger mb-0 mt-4" role="alert">${sessionScope.errorMessage}</div>
        </c:if>
        <c:remove var="successMessage" scope="session"/>
        <c:remove var="errorMessage" scope="session"/>

        <header class="section-heading py-4 d-flex justify-content-between">
            <h3 class="section-title">Quản lý voucher</h3>
            <a class="btn btn-primary"
               href="${pageContext.request.contextPath}/admin/voucherManager/create"
               role="button" style="height: fit-content;"> Thêm voucher </a>
        </header>

        <main id="voucherTableContainer" class="table-responsive-xl mb-5">
            <table class="table table-bordered table-striped table-hover align-middle">
                <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Mã / Tên Voucher</th>
                    <th scope="col">Mức giảm</th>
                    <th scope="col">Phạm vi</th>
                    <th scope="col">Thời hạn</th>
                    <th scope="col">Sử dụng</th>
                    <th scope="col">Trạng thái</th>
                    <th scope="col" style="width: 150px;">Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${requestScope.vouchers}" var="v">
                    <tr>
                        <th scope="row">${v.id}</th>
                        <td>
                            <strong class="text-primary">${v.code}</strong><br>
                            <small class="text-muted">${v.name}</small>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${v.calculationMethod == 0}">
                                    <span class="badge bg-info text-dark">${v.value}%</span>
                                    <div class="small text-muted">Tối đa: <fmt:formatNumber value="${v.maxDiscount}" type="currency" currencyCode="VND"/></div>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-success"><fmt:formatNumber value="${v.value}" type="currency" currencyCode="VND"/></span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <span class="badge bg-secondary">
                                    ${v.applyTo == 0 ? 'Đơn hàng' : 'Vận chuyển'}
                            </span>
                        </td>
                        <td class="small">
                            Từ: <fmt:parseDate value="${v.startDate}" pattern="yyyy-MM-dd'T'HH:mm" var="sDate" type="both" />
                            <fmt:formatDate value="${sDate}" pattern="dd/MM/yyyy HH:mm" /><br>
                            Đến: <fmt:parseDate value="${v.endDate}" pattern="yyyy-MM-dd'T'HH:mm" var="eDate" type="both" />
                            <fmt:formatDate value="${eDate}" pattern="dd/MM/yyyy HH:mm" />
                        </td>
                        <td>
                            <div class="progress" style="height: 10px;">
                                <div class="progress-bar bg-warning" role="progressbar"
                                     style="width: ${v.usageLimit > 0 ? (v.usedCount / v.usageLimit) * 100 : 0}%"></div>
                            </div>
                            <small>${v.usedCount}/${v.usageLimit}</small>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${v.active}">
                                    <span class="badge bg-success">Đang chạy</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger">Tạm dừng</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <div class="d-flex gap-2">
                                <a href="${pageContext.request.contextPath}/admin/voucherManager/update?id=${v.id}"
                                   class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil"></i> Sửa
                                </a>
                                <form action="${pageContext.request.contextPath}/admin/voucherManager/delete" method="post"
                                      style="display:inline;"
                                      onsubmit="return confirm('Xóa voucher ID: ${v.id}?')">
                                    <input type="hidden" name="id" value="${v.id}">
                                    <input type="hidden" name="currentPage" value="${requestScope.currentPage}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">
                                        <i class="bi bi-trash"></i> Xóa
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </main>
    </div>
</section>

<c:if test="${requestScope.totalPages > 1}">
    <nav class="mt-4 mb-4">
        <ul class="pagination justify-content-center">
            <c:if test="${requestScope.currentPage > 3}">
                <li class="page-item">
                    <a class="page-link" href="${pageContext.request.contextPath}/admin/voucherManager/view?page=1">
                        Trang đầu
                    </a>
                </li>
            </c:if>
            <li class="page-item ${requestScope.currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link"
                   href="${pageContext.request.contextPath}/admin/voucherManager/view?page=${requestScope.currentPage - 1}">
                    <i class="bi bi-chevron-left"></i>
                </a>
            </li>
            <c:set var="begin" value="${requestScope.currentPage - 2}" />
            <c:set var="end" value="${requestScope.currentPage + 2}" />
            <c:if test="${begin < 1}">
                <c:set var="begin" value="1" />
                <c:set var="end" value="5" />
            </c:if>
            <c:if test="${end > requestScope.totalPages}">
                <c:set var="end" value="${requestScope.totalPages}" />
                <c:set var="begin" value="${requestScope.totalPages - 4}" />
                <c:if test="${begin < 1}">
                    <c:set var="begin" value="1" />
                </c:if>
            </c:if>
            <c:forEach begin="${begin}" end="${end}" var="i">
                <li class="page-item ${requestScope.currentPage == i ? 'active' : ''}">
                    <a class="page-link" href="${pageContext.request.contextPath}/admin/voucherManager/view?page=${i}">${i}</a>
                </li>
            </c:forEach>
            <li class="page-item ${requestScope.currentPage == requestScope.totalPages ? 'disabled' : ''}">
                <a class="page-link"
                   href="${pageContext.request.contextPath}/admin/voucherManager/view?page=${requestScope.currentPage + 1}">
                    <i class="bi bi-chevron-right"></i>
                </a>
            </li>
            <c:if test="${requestScope.currentPage < requestScope.totalPages - 2}">
                <li class="page-item">
                    <a class="page-link" href="${pageContext.request.contextPath}/admin/voucherManager/view?page=${requestScope.totalPages}">
                        Trang cuối
                    </a>
                </li>
            </c:if>
        </ul>
    </nav>
</c:if>

<jsp:include page="../_footerAdmin.jsp"/>
</body>

</html>