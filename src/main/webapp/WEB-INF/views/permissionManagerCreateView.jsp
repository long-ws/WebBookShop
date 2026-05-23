<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="_paramKeys.jsp" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="_meta.jsp" />
    <title>Tạo Quyền mới - Admin</title>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
    <jsp:include page="_headerAdmin.jsp" />

    <main class="flex-fill">
        <section class="section-content padding-y py-4">
            <div class="container">
                
                <%-- Đường dẫn điều hướng (Breadcrumb) --%>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin" class="text-decoration-none">Admin</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/permission" class="text-decoration-none">Quản lý quyền</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Tạo quyền mới</li>
                    </ol>
                </nav>

                <%-- Tiêu đề trang trang trọng --%>
                <header class="section-heading my-3 text-center">
                    <h3 class="section-title fw-bold text-dark">
                        <i class="bi bi-shield-plus text-success me-2"></i>Tạo phân quyền truy cập mới
                    </h3>
                </header>

                <%-- Khung bọc dữ liệu chính cấu trúc chia đôi 2 cột song song --%>
                <div class="card shadow-sm border-0 rounded-3 mb-5">
                    <div class="card-body p-4">
                        <div class="row g-4">
                            
                            <%-- CỘT TRÁI: Form nhập liệu tạo mới --%>
                            <div class="col-md-7 border-end p-md-4 pt-md-2">
                                <div class="d-flex align-items-center justify-content-between mb-3 border-bottom pb-2">
                                    <h6 class="text-success mb-0 fw-bold">
                                        <i class="bi bi-file-earmark-medical-fill me-1"></i> Thông tin cấu hình quyền
                                    </h6>
                                    <small class="text-muted"><span class="text-danger">*</span> Trường bắt buộc</small>
                                </div>

                                <form action="${pageContext.request.contextPath}/admin/permission/create" method="post" novalidate>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Mã định danh quyền <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-code-square text-muted"></i></span>
                                            <input type="text" name="${P_CODE}" class="form-control text-uppercase ${not empty requestScope[ATTR_ERRORS][P_CODE] ? 'is-invalid' : ''}" 
                                                   placeholder="VD: PRODUCT_CREATE" 
                                                   value="<c:out value='${requestScope[ATTR_VALUES][P_CODE]}'/>" required
                                                   pattern="^[A-Z][A-Z_]*$" 
                                                   title="Format: chữ hoa, dùng gạch dưới (VD: PRODUCT_CREATE)">
                                            <c:if test="${not empty requestScope[ATTR_ERRORS][P_CODE]}">
                                                <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_CODE]}</div>
                                            </c:if>
                                        </div>
                                        <div class="form-text text-muted small mt-1">
                                            Bắt buộc viết hoa cách nhau bằng dấu gạch dưới (Snake_case).
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Tên quyền hiển thị <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-tags-fill text-muted"></i></span>
                                            <input type="text" name="${P_NAME}" class="form-control ${not empty requestScope[ATTR_ERRORS][P_NAME] ? 'is-invalid' : ''}" 
                                                   placeholder="VD: Tạo sản phẩm mới" 
                                                   value="<c:out value='${requestScope[ATTR_VALUES][P_NAME]}'/>" required>
                                            <c:if test="${not empty requestScope[ATTR_ERRORS][P_NAME]}">
                                                <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_NAME]}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Phân loại hệ thống (Module) <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-grid-fill text-muted"></i></span>
                                            <select name="${P_MODULE}" class="form-select ${not empty requestScope[ATTR_ERRORS][P_MODULE] ? 'is-invalid' : ''}" required>
                                                <option value="" ${empty requestScope[ATTR_VALUES][P_MODULE] ? 'selected' : ''} disabled>-- Chọn phân loại module --</option>
                                                <c:forEach var="moduleItem" items="${requestScope[ATTR_MODULES]}">
                                                    <option value="${moduleItem}" ${requestScope[ATTR_VALUES][P_MODULE] == moduleItem ? 'selected' : ''}>${moduleItem}</option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty requestScope[ATTR_ERRORS][P_MODULE]}">
                                                <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_MODULE]}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Mô tả tác vụ chi tiết</label>
                                        <textarea name="${P_DESCRIPTION}" class="form-control ${not empty requestScope[ATTR_ERRORS][P_DESCRIPTION] ? 'is-invalid' : ''}" rows="4"
                                                  placeholder="Giải trình cụ thể về phạm vi cho phép thao tác của quyền này..."><c:out value='${requestScope[ATTR_VALUES][P_DESCRIPTION]}'/></textarea>
                                        <c:if test="${not empty requestScope[ATTR_ERRORS][P_DESCRIPTION]}">
                                            <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_DESCRIPTION]}</div>
                                        </c:if>
                                    </div>
                                    
                                    <%-- Nhóm nút hành động đồng bộ dưới đáy form --%>
                                    <div class="text-end pt-3 border-top mt-4">
                                        <button type="submit" class="btn btn-success px-4 shadow-sm">
                                            <i class="bi bi-check-lg me-1"></i> Tạo quyền mới
                                        </button>
                                        <a href="${pageContext.request.contextPath}/admin/permission" class="btn btn-danger px-4 ms-1 shadow-sm">
                                            <i class="bi bi-x-circle-fill me-1"></i> Hủy bỏ
                                        </a>
                                    </div>
                                </form>
                            </div>

                            <%-- CỘT PHẢI: Bảng tài liệu và Hướng dẫn đặt tên --%>
                            <div class="col-md-5 p-md-4 pt-md-2 mt-4 mt-md-0">
                                <div class="d-flex align-items-center mb-3 border-bottom pb-2">
                                    <h6 class="text-info mb-0 fw-bold">
                                        <i class="bi bi-info-circle-fill me-1"></i> Quy định chuẩn hóa định danh (Permission)
                                    </h6>
                                </div>
                                
                                <div class="alert alert-info border-0 shadow-sm p-3 bg-opacity-10 text-dark mb-0">
                                    <p class="small mb-2">Quyền (Permission) được định danh kiểm soát logic mã hóa chặt chẽ theo format: <code>MODULE_ACTION</code></p>
                                    
                                    <table class="table table-sm table-bordered bg-white small mb-3 align-middle">
                                        <thead class="table-light">
                                            <tr>
                                                <th class="py-2 px-2">Cấu trúc</th>
                                                <th class="py-2 px-2">Ý nghĩa hành động phổ biến</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td class="fw-bold text-secondary px-2">MODULE</td>
                                                <td class="px-2"><code>USER</code>, <code>ROLE</code>, <code>PRODUCT</code>, <code>CATEGORY</code>, <code>ORDER</code>, <code>REVIEW</code></td>
                                            </tr>
                                            <tr>
                                                <td class="fw-bold text-secondary px-2">ACTION</td>
                                                <td class="px-2"><code>VIEW</code>, <code>CREATE</code>, <code>EDIT</code>, <code>DELETE</code>, <code>MANAGE</code></td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    
                                    <div class="card border border-info border-opacity-20 rounded-3 bg-white p-3 mb-2">
                                        <div class="small fw-bold text-dark mb-1"><i class="bi bi-lightbulb-fill text-warning me-1"></i>Ví dụ thực tế chuẩn chỉ:</div>
                                        <ul class="mb-0 ps-3 text-muted small">
                                            <li><code class="text-success">PRODUCT_CREATE</code> — Thêm sản phẩm mới vào kho</li>
                                            <li><code class="text-success">USER_DELETE</code> — Xóa vĩnh viễn tài khoản người dùng</li>
                                            <li><code class="text-success">ORDER_MANAGE</code> — Quản lý toàn quyền trạng thái đơn</li>
                                        </ul>
                                    </div>
                                </div>

                                <blockquote class="blockquote blockquote-custom bg-white p-3 rounded-3 border-start border-warning border-3 shadow-sm mt-3">
                                    <div class="d-flex">
                                        <i class="bi bi-exclamation-triangle-fill text-warning fs-5 me-2 mt-1"></i>
                                        <div>
                                            <p class="small fw-bold text-dark mb-1">Kiến trúc phân cấp phân quyền</p>
                                            <p class="mb-0 text-muted small" style="font-size: 0.8rem;">
                                                Mã <strong>QUYỀN HẠN</strong> chi tiết này sau khi tạo ra sẽ nằm trong danh mục tổng. Bạn cần quay lại cấu trúc của trang <strong>Vai trò (Role)</strong> để thực hiện tích chọn gán quyền này vào các nhóm phòng ban tương ứng.
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