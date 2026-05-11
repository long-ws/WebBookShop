<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="../_meta.jsp"/>
    <title>${title}</title>
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
            <h3 class="section-title">${title}</h3>
        </header>

        <main class="card shadow-sm mb-5">
            <div class="card-body">
                <form id="voucherForm" class="needs-validation" novalidate
                      action="${pageContext.request.contextPath}/admin/voucherManager/${mode == 'add' ? 'create' : 'update'}"
                      method="POST">

                    <c:if test="${mode != 'add'}">
                        <input type="hidden" name="id" value="${voucher.id}">
                    </c:if>

                    <div class="row g-3">
                        <div class="col-md-4">
                            <div class="form-floating">
                                <input type="text" name="code" class="form-control" id="floatingCode" placeholder="Mã Voucher"
                                       value="${voucher.code}" pattern="[A-Z0-9]{3,20}"
                                ${mode == 'view' ? 'disabled' : 'required'}>
                                <label for="floatingCode">Mã Voucher</label>
                                <div class="invalid-feedback">Mã (3-20 ký tự in hoa/số).</div>
                            </div>
                        </div>

                        <div class="col-md-8">
                            <div class="form-floating">
                                <input type="text" name="name" class="form-control" id="floatingName" placeholder="Tên Voucher"
                                       value="${voucher.name}" ${mode == 'view' ? 'disabled' : 'required'}>
                                <label for="floatingName">Tên Voucher</label>
                                <div class="invalid-feedback">Vui lòng nhập tên voucher.</div>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="form-floating">
                                <select name="applyTo" id="applyTo" class="form-select" ${mode == 'view' ? 'disabled' : ''}>
                                    <option value="ORDER" ${voucher.applyTo == 'ORDER' ? 'selected' : ''}>Đơn hàng</option>
                                    <option value="SHIPPING" ${voucher.applyTo == 'SHIPPING' ? 'selected' : ''}>Ship</option>
                                </select>
                                <label for="calculationMethod">Sử dụng</label>
                            </div>
                        </div>
                        <div class="col-md-8">
                            <div class="form-floating">
                        <textarea name="description" class="form-control" id="floatingDesc" placeholder="Mô tả"
                                  style="height: 100px" ${mode == 'view' ? 'disabled' : ''}>${voucher.description}</textarea>
                                <label for="floatingDesc">Mô tả</label>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="form-floating">
                                <select name="calculationMethod" id="calculationMethod" class="form-select" ${mode == 'view' ? 'disabled' : ''}>
                                    <option value="PERCENT" ${voucher.calculationMethod == 'PERCENT' ? 'selected' : ''}>% (Phần trăm)</option>
                                    <option value="FIXED" ${voucher.calculationMethod == 'FIXED' ? 'selected' : ''}>Cố định (Số tiền)</option>
                                </select>
                                <label for="calculationMethod">Cách tính</label>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="form-floating">
                                <input type="number" name="value" id="voucherValue" class="form-control" placeholder="Giá trị"
                                       value="${voucher.value}" min="1" step="any" ${mode == 'view' ? 'disabled' : 'required'}>
                                <label for="voucherValue">Giá trị</label>
                                <div id="valueFeedback" class="invalid-feedback">Giá trị phải lớn hơn 0.</div>
                            </div>
                        </div>

                        <div class="col-md-4 d-flex align-items-center justify-content-center">
                            <div class="form-check form-switch p-0 m-0">
                                <input class="form-check-input ms-0" type="checkbox" name="isActive" id="flexSwitchCheckDefault"
                                ${voucher.isActive ? 'checked' : ''} ${mode == 'view' ? 'disabled' : ''}>
                                <label class="form-check-label ms-2 fw-bold" for="flexSwitchCheckDefault">Kích hoạt</label>
                            </div>
                        </div>

                        <div class="col-md-3">
                            <div class="form-floating">
                                <input type="number" name="minPurchase" class="form-control" id="floatingMin" placeholder="Đơn tối thiểu"
                                       min="0" value="${voucher.minPurchase}" ${mode == 'view' ? 'disabled' : ''}>
                                <label for="floatingMin">Đơn tối thiểu</label>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="form-floating">
                                <input type="number" name="maxDiscount" class="form-control" id="floatingMax" placeholder="Giảm tối đa"
                                       min="0" value="${voucher.maxDiscount}" ${mode == 'view' ? 'disabled' : ''}>
                                <label for="floatingMax">Giảm tối đa</label>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="form-floating">
                                <input type="number" name="usageLimit" class="form-control" id="floatingUsage" placeholder="Tổng lượt dùng"
                                       min="1" value="${voucher.usageLimit}" ${mode == 'view' ? 'disabled' : ''}>
                                <label for="floatingUsage">Tổng lượt dùng</label>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="form-floating">
                                <input type="number" name="perUserLimit" class="form-control" id="floatingPer" placeholder="Lượt/Người"
                                       min="1" value="${voucher.perUserLimit}" ${mode == 'view' ? 'disabled' : ''}>
                                <label for="floatingPer">Lượt/Người dùng</label>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="datetime-local" name="startDate" id="startDate" class="form-control"
                                       value="${voucher.startDate}" required ${mode == 'view' ? 'disabled' : ''}>
                                <label for="startDate">Ngày bắt đầu</label>
                                <div class="invalid-feedback">Vui lòng chọn ngày bắt đầu.</div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-floating">
                                <input type="datetime-local" name="endDate" id="endDate" class="form-control"
                                       value="${voucher.endDate}" required ${mode == 'view' ? 'disabled' : ''}>
                                <label for="endDate">Ngày kết thúc</label>
                                <div class="invalid-feedback">Ngày kết thúc phải sau ngày bắt đầu.</div>
                            </div>
                        </div>

                        <div class="row g-4 mt-2">
                            <div class="col-md-6">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <label class="form-label fw-bold text-success mb-0">Danh mục áp dụng</label>
                                    <c:if test="${mode != 'view'}">
                                        <button type="button" class="btn btn-outline-success btn-sm"
                                                data-bs-toggle="modal" data-bs-target="#selectionModal"
                                                onclick="prepareModal('category')">
                                            <i class="bi bi-plus-lg"></i>
                                        </button>
                                    </c:if>
                                </div>
                                <ul class="list-group list-group-flush border rounded p-2 bg-light" id="selected-categories" style="min-height: 100px;">
                                </ul>
                            </div>

                            <div class="col-md-6">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <label class="form-label fw-bold text-primary mb-0">Sản phẩm áp dụng</label>
                                    <c:if test="${mode != 'view'}">
                                        <button type="button" class="btn btn-outline-primary btn-sm"
                                                data-bs-toggle="modal" data-bs-target="#selectionModal"
                                                onclick="prepareModal('product')">
                                            <i class="bi bi-plus-lg"></i>
                                        </button>
                                    </c:if>
                                </div>
                                <ul class="list-group list-group-flush border rounded p-2 bg-light" id="selected-products" style="min-height: 100px;">
                                    <c:if test="${empty selectedProducts}">
                                        <li class="text-muted small text-center mt-4">Chưa có sản phẩm nào được chọn</li>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <hr class="my-4">

                    <div class="d-flex justify-content-end gap-2 mb-5">
                        <a href="${pageContext.request.contextPath}/admin/vouchers" class="btn btn-secondary">Quay lại</a>
                        <c:if test="${mode != 'view'}">
                            <button type="submit" class="btn btn-primary px-4">
                                    ${mode == 'add' ? 'Tạo mới' : 'Lưu thay đổi'}
                            </button>
                        </c:if>
                        <c:if test="${mode == 'view'}">
                            <a href="${pageContext.request.contextPath}/admin/vouchers/edit?id=${voucher.id}" class="btn btn-warning">Chỉnh sửa</a>
                        </c:if>
                    </div>
                </form>
            </div>
        </main>
    </div>
</section>

<div class="modal fade" id="selectionModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="selectionModalLabel">Chọn dữ liệu</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="input-group mb-3">
                    <span class="input-group-text"><i class="bi bi-search"></i></span>
                    <input type="text" id="searchInput" class="form-control" placeholder="Nhập tên để tìm kiếm...">
                </div>

                <div class="list-group" id="searchResultList" style="max-height: 400px;">
                    <div class="text-center py-5">
                        <div class="spinner-border text-secondary" role="status"></div>
                        <p class="mt-2 text-muted">Đang tải dữ liệu...</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<jsp:include page="../_footerAdmin.jsp"/>
<script>
    const contextPath = "${pageContext.request.contextPath}";
</script>
<script src="${pageContext.request.contextPath}/assets/js/voucherDetailScripts.js"></script>
</body>

</html>