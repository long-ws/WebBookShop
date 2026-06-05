<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="_meta.jsp"/>
    <title>Sổ địa chỉ nhận hàng</title>
</head>
<body>
<jsp:include page="_header.jsp"/>
<section class="section-content padding-y">
    <div class="container">
        <div class="row">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <jsp:include page="_navPanel.jsp">
                        <jsp:param name="active" value="ADDRESSBOOK"/>
                    </jsp:include>
                    <div class="col-md-9">
                        <div class="row g-3">
                            <div class="col-12">
                                <button type="button" onclick="openCreateModal()"
                                        class="btn btn-light border w-100 py-3 rounded d-flex align-items-center justify-content-center fw-bold text-secondary">
                                    <i class="bi bi-plus-lg me-2 fs-5"></i> Thêm địa chỉ mới
                                </button>
                            </div>

                            <c:choose>
                                <c:when test="${not empty addressList}">
                                    <div class="col-12"
                                         style="max-height: 320px; overflow-y: auto; padding-right: 4px;">
                                        <div class="row g-3">

                                            <c:forEach var="address" items="${addressList}">
                                                <div class="col-12">
                                                    <div class="p-3 rounded border d-flex justify-content-between align-items-start shadow-sm
                                                        ${address.isDefault ? 'border-warning bg-warning-subtle' : 'bg-white'}">

                                                        <div>
                                                            <div class="d-flex align-items-center mb-2">
                                                                <span class="fw-bold me-2">${address.fullname}</span>
                                                                <span class="text-muted border-start ps-2">${address.phone}</span>
                                                            </div>
                                                            <p class="mb-1 text-secondary small">${address.addressDetail}</p>
                                                            <p class="mb-2 text-secondary small">${address.fullAddress}</p>
                                                            <c:if test="${address.isDefault}">
                                                                <span class="badge bg-warning text-dark px-2 py-1 small">Mặc định</span>
                                                            </c:if>
                                                        </div>

                                                        <div class="text-end d-flex flex-column align-items-end justify-content-between"
                                                             style="min-height: 85px;">
                                                            <div class="d-flex gap-2">
                                                                <a href="#"
                                                                   class="btn btn-sm btn-outline-secondary px-2 py-1"
                                                                   onclick="openUpdateModal(${address.id}); return false;">
                                                                    <i class="bi bi-pencil"></i> Sửa
                                                                </a>
                                                                <a class="btn btn-sm btn-outline-danger px-2 py-1"
                                                                   href="address/delete?addressId=${address.id}"
                                                                   onclick="return confirm('Bạn chắc chắn muốn xóa địa chỉ #${address.id}?')">
                                                                    <i class="bi bi-trash"></i> Xóa
                                                                </a>
                                                            </div>

                                                            <c:if test="${not address.isDefault}">
                                                                <a class="btn btn-sm btn-link p-0 text-decoration-none text-primary mt-2 small"
                                                                   href="address/setDefault?addressId=${address.id}"
                                                                   onclick="return confirm('Bạn có muốn đặt làm địa chỉ mặc định #${address.id}?')">
                                                                    Thiết lập mặc định</a>
                                                            </c:if>
                                                        </div>

                                                    </div>
                                                </div>
                                            </c:forEach>

                                        </div>
                                    </div>
                                </c:when>

                                <c:otherwise>
                                    <div class="col-12">
                                        <div class="text-center py-5 bg-white shadow-sm rounded border">
                                            <p class="text-muted mb-0">Chưa thêm thông tin nhận hàng nào!</p>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="col-12 text-center py-5">
                        <p class="text-muted mb-3">Vui lòng đăng nhập để sử dụng trang này.</p>
                        <a href="${pageContext.request.contextPath}/signin" class="btn btn-primary px-4">Đăng nhập
                            ngay</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>
<jsp:include page="_footer.jsp"/>
<jsp:include page="../modals/addressModal.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    var API_BASE = '${pageContext.request.contextPath}';
    var GHN_API = API_BASE + '/api/ghn';
</script>
<script src="${pageContext.request.contextPath}/assets/js/addressBookScripts.js"></script>
</body>
</html>