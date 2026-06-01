<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="_paramKeys.jsp" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="_meta.jsp" />
    <title>Tạo Vai trò mới - Admin</title>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
    <jsp:include page="_headerAdmin.jsp" />

    <main class="flex-fill">
        <section class="section-content padding-y py-4">
            <div class="container">
                
                <%-- Đường dẫn điều hướng (Breadcrumb) đồng bộ hệ thống --%>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin" class="text-decoration-none">Admin</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/role" class="text-decoration-none">Quản lý vai trò</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Tạo vai trò mới</li>
                    </ol>
                </nav>

                <%-- Tiêu đề trang trang trọng --%>
                <header class="section-heading my-3 text-center">
                    <h3 class="section-title fw-bold text-dark">
                        <i class="bi bi-shield-plus text-success me-2"></i>Tạo vai trò bảo mật mới
                    </h3>
                </header>

                <%-- Vùng hiển thị thông báo lỗi/thành công --%>
                <div class="row justify-content-center">
                    <div class="col-12">
                        <c:if test="${not empty sessionScope.successMessage}">
                            <div class="alert alert-success alert-dismissible fade show shadow-sm mb-3" role="alert">
                                <i class="bi bi-check-circle-fill me-2"></i><c:out value='${sessionScope.successMessage}' />
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                            <c:remove var="successMessage" scope="session" />
                        </c:if>
                        <c:if test="${not empty sessionScope.errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show shadow-sm mb-3" role="alert">
                                <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value='${sessionScope.errorMessage}' />
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                            <c:remove var="errorMessage" scope="session" />
                        </c:if>
                        <c:if test="${not empty requestScope[ATTR_ERRORS][ERR_GLOBAL]}">
                            <div class="alert alert-danger alert-dismissible fade show shadow-sm mb-3" role="alert">
                                <i class="bi bi-exclamation-octagon-fill me-2"></i> ${requestScope[ATTR_ERRORS][ERR_GLOBAL]}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                    </div>
                </div>

                <%-- Khung nội dung chính cấu trúc 2 cột song song như trang Edit --%>
                <div class="card shadow-sm border-0 rounded-3 mb-5">
                    <div class="card-body p-4">
                        <div class="row g-4">
                            
                            <%-- CỘT TRÁI: Form nhập liệu tạo mới --%>
                            <div class="col-md-7 border-end pe-md-4">
                                <div class="d-flex align-items-center justify-content-between mb-3 border-bottom pb-2">
                                    <h6 class="text-success mb-0 fw-bold">
                                        <i class="bi bi-file-earmark-medical-fill me-1"></i> Thông tin vai trò
                                    </h6>
                                    <small class="text-muted"><span class="text-danger">*</span> Trường bắt buộc</small>
                                </div>

                                <form action="${pageContext.request.contextPath}/admin/role/create" method="post" novalidate>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Mã vai trò <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-code-square text-muted"></i></span>
                                            <input type="text" name="${P_CODE}" class="form-control text-uppercase ${not empty requestScope[ATTR_ERRORS][P_CODE] ? 'is-invalid' : ''}" 
                                                   placeholder="VD: PRODUCT_MANAGER" 
                                                   value="<c:out value='${requestScope[ATTR_VALUES][P_CODE]}'/>" required
                                                   pattern="[A-Z][A-Z_]*" 
                                                   title="Bắt đầu bằng chữ hoa, chỉ chứa chữ hoa và gạch dưới">
                                            <c:if test="${not empty requestScope[ATTR_ERRORS][P_CODE]}">
                                                <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_CODE]}</div>
                                            </c:if>
                                        </div>
                                        <div class="form-text text-muted small mt-1">
                                            Quy tắc viết hoa cách nhau bằng dấu gạch dưới (SNAKE_CASE).
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Tên vai trò <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-tags-fill text-muted"></i></span>
                                            <input type="text" name="${P_NAME}" class="form-control ${not empty requestScope[ATTR_ERRORS][P_NAME] ? 'is-invalid' : ''}" 
                                                   placeholder="VD: Quản lý Sản phẩm" 
                                                   value="<c:out value='${requestScope[ATTR_VALUES][P_NAME]}'/>" required>
                                            <c:if test="${not empty requestScope[ATTR_ERRORS][P_NAME]}">
                                                <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_NAME]}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Mô tả chức năng</label>
                                        <textarea name="${P_DESCRIPTION}" class="form-control ${not empty requestScope[ATTR_ERRORS][P_DESCRIPTION] ? 'is-invalid' : ''}" rows="4"
                                                  placeholder="Mô tả chi tiết các đặc quyền hoặc phạm vi áp dụng của vai trò này..."><c:out value='${requestScope[ATTR_VALUES][P_DESCRIPTION]}'/></textarea>
                                        <c:if test="${not empty requestScope[ATTR_ERRORS][P_DESCRIPTION]}">
                                            <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_DESCRIPTION]}</div>
                                        </c:if>
                                    </div>
                                    
                                    <%-- Nhóm nút hành động đồng bộ --%>
                                    <div class="text-end pt-3 border-top mt-4">
                                        <button type="submit" class="btn btn-success px-4 shadow-sm">
                                            <i class="bi bi-check-lg me-1"></i> Tạo vai trò
                                        </button>
                                        <a href="${pageContext.request.contextPath}/admin/role" class="btn btn-danger px-4 ms-1 shadow-sm">
                                            <i class="bi bi-x-circle-fill me-1"></i> Hủy bỏ
                                        </a>
                                    </div>
                                </form>
                            </div>

                            <%-- CỘT PHẢI: Bảng tài liệu hướng dẫn nhanh --%>
                            <div class="col-md-5 ps-md-4 mt-4 mt-md-0">
                                <div class="d-flex align-items-center mb-3 border-bottom pb-2">
                                    <h6 class="text-info mb-0 fw-bold">
                                        <i class="bi bi-info-circle-fill me-1"></i> Quy chuẩn thiết lập chuẩn hệ thống
                                    </h6>
                                </div>
                                
                                <div class="alert alert-info border-0 shadow-sm p-3 mb-3 bg-opacity-10 text-dark">
                                    <p class="small mb-2">Tên định danh (Mã vai trò) được cấu trúc nghiêm ngặt theo quy định: <code>MODULE_ROLE</code></p>
                                    
                                    <table class="table table-sm table-bordered bg-white small mb-3 align-middle">
                                        <thead class="table-light">
                                            <tr>
                                                <th class="py-2 px-2">Thành phần cấu trúc</th>
                                                <th class="py-2 px-2">Giá trị phổ biến</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td class="fw-bold text-secondary px-2">MODULE</td>
                                                <td class="px-2"><code>USER</code>, <code>ROLE</code>, <code>PRODUCT</code>, <code>ORDER</code>, <code>REPORT</code></td>
                                            </tr>
                                            <tr>
                                                <td class="fw-bold text-secondary px-2">ROLE</td>
                                                <td class="px-2"><code>ADMIN</code>, <code>MANAGER</code>, <code>STAFF</code>, <code>VIEWER</code></td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    
                                    <div class="card border border-info border-opacity-20 rounded-3 bg-white p-2 mb-2">
                                        <div class="small fw-bold text-dark mb-1"><i class="bi bi-lightbulb-fill text-warning me-1"></i>Ví dụ thực tế:</div>
                                        <ul class="mb-0 ps-3 text-muted small">
                                            <li><code class="text-success">PRODUCT_MANAGER</code> — Quản lý kho hàng</li>
                                            <li><code class="text-success">ORDER_STAFF</code> — Nhân viên xử lý đơn</li>
                                        </ul>
                                    </div>
                                </div>

                                <blockquote class="blockquote blockquote-custom bg-white p-3 rounded-3 border-start border-warning border-3 shadow-sm mt-3">
                                    <div class="d-flex">
                                        <i class="bi bi-exclamation-triangle-fill text-warning fs-5 me-2 mt-1"></i>
                                        <div>
                                            <p class="small fw-bold text-dark mb-1">Phân biệt Vai trò và Quyền hạn</p>
                                            <p class="mb-0 text-muted small" style="font-size: 0.8rem;">
                                                Mã này dùng để tạo <strong>VAI TRÒ</strong> nhóm lớn. Sau khi tạo xong, bạn cần vào mục chỉnh sửa vai trò này để tiến hành gán các hành động <strong>QUYỀN CHI TIẾT</strong> dạng <code>module.action</code> (ví dụ: <code>product.create</code>).
                                            </p>
                                        </div>
                                    </div>
                                </blockquote>
                            </div>

                        </div>
                    </div>
                </div>

            </div>
        </section>
    </main>

    <jsp:include page="_footerAdmin.jsp" />
</body>
</html>