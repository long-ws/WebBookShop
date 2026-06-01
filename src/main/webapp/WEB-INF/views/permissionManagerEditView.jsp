<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="_paramKeys.jsp" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="_meta.jsp" />
    <title>Chỉnh sửa Quyền - Admin</title>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
    <jsp:include page="_headerAdmin.jsp" />

    <main class="flex-fill">
        <section class="section-content padding-y py-4">
            <div class="container">
                
                <%-- Đường dẫn điều hiện hướng (Breadcrumb) --%>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin" class="text-decoration-none">Admin</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/permission" class="text-decoration-none">Quản lý quyền</a></li>
                        <li class="breadcrumb-item active" aria-current="page">Chỉnh sửa quyền</li>
                    </ol>
                </nav>

                <%-- Hệ thống thông báo trạng thái đồng bộ --%>
                <div class="mb-3">
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success alert-dismissible fade show shadow-sm border-0" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i><c:out value="${sessionScope.successMessage}" />
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                        <c:remove var="successMessage" scope="session"/>
                    </c:if>
                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show shadow-sm border-0" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${sessionScope.errorMessage}" />
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                        <c:remove var="errorMessage" scope="session"/>
                    </c:if>
                    <c:if test="${not empty requestScope[ATTR_ERRORS][ERR_GLOBAL]}">
                        <div class="alert alert-danger alert-dismissible fade show shadow-sm border-0" role="alert">
                            <i class="bi bi-exclamation-octagon-fill me-2"></i> ${requestScope[ATTR_ERRORS][ERR_GLOBAL]}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                </div>

                <%-- Tiêu đề trang trang trọng --%>
                <header class="section-heading my-3 text-center">
                    <h3 class="section-title fw-bold text-dark">
                        <i class="bi bi-shield-exclamation text-primary me-2"></i>Cập nhật thông tin quyền truy cập
                    </h3>
                </header>

                <%-- Khung bọc dữ liệu chính cấu trúc chia đôi 2 cột song song giống form Tạo mới --%>
                <div class="card shadow-sm border-0 rounded-3 mb-5">
                    <div class="card-body p-4">
                        <div class="row g-4">
                            
                            <%-- CỘT TRÁI: Form nhập liệu cập nhật thay đổi --%>
                            <div class="col-md-7 border-end p-md-4 pt-md-2">
                                <div class="d-flex align-items-center justify-content-between mb-3 border-bottom pb-2">
                                    <h6 class="text-primary mb-0 fw-bold">
                                        <i class="bi bi-pencil-square me-1"></i> Form điều chỉnh cấu hình
                                    </h6>
                                    <small class="text-muted"><span class="text-danger">*</span> Trường bắt buộc</small>
                                </div>

                                <%-- Cảnh báo nếu là Quyền lõi hệ thống --%>
                                <c:if test="${requestScope[ATTR_PERMISSION].isSystem}">
                                    <div class="alert alert-warning border-0 shadow-sm p-3 bg-opacity-10 text-dark mb-3 small d-flex align-items-center">
                                        <i class="bi bi-exclamation-triangle-fill text-warning fs-5 me-2"></i>
                                        <span><strong>Chú ý:</strong> Đây là phân quyền lõi thuộc lõi bảo mật hệ thống (S-Core). Bạn không được quyền thay đổi bất kỳ trường dữ liệu nào.</span>
                                    </div>
                                </c:if>

                                <form action="${pageContext.request.contextPath}/admin/permission/update" method="post" novalidate>
                                    <input type="hidden" name="${P_ID}" value="${requestScope[ATTR_PERMISSION].id}">
                                    <input type="hidden" name="${P_CODE}" value="${requestScope[ATTR_PERMISSION].code}">
                                    
                                    <%-- Mã định danh (Chỉ đọc) --%>
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold text-secondary">Mã định danh quyền (Cố định)</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-light"><i class="bi bi-lock-fill text-muted"></i></span>
                                            <input type="text" class="form-control bg-light text-uppercase fw-bold text-secondary" 
                                                   value="${requestScope[ATTR_PERMISSION].code}" disabled>
                                        </div>
                                        <div class="form-text text-muted small mt-1">
                                            Chuỗi định danh bảo mật cấp thấp không được phép sửa đổi sau khi tạo.
                                        </div>
                                    </div>
                                    
                                    <%-- Tên quyền hiển thị --%>
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Tên quyền hiển thị <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-tags-fill text-muted"></i></span>
                                            <input type="text" name="${P_NAME}" 
                                                   class="form-control ${not empty requestScope[ATTR_ERRORS][P_NAME] ? 'is-invalid' : ''}" 
                                                   value="<c:out value='${requestScope[ATTR_PERMISSION].name}'/>" required
                                                   ${requestScope[ATTR_PERMISSION].isSystem ? 'disabled' : ''}>
                                            <c:if test="${not empty requestScope[ATTR_ERRORS][P_NAME]}">
                                                <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_NAME]}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                    
                                    <%-- Phân loại hệ thống (Module) --%>
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Phân loại hệ thống (Module)</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-grid-fill text-muted"></i></span>
                                            <select name="${P_MODULE}" class="form-select ${not empty requestScope[ATTR_ERRORS][P_MODULE] ? 'is-invalid' : ''}"
                                                    ${requestScope[ATTR_PERMISSION].isSystem ? 'disabled' : ''}>
                                                <option value="" ${empty requestScope[ATTR_PERMISSION].module ? 'selected' : ''} disabled>-- Chọn phân loại module --</option>
                                                <c:forEach var="moduleItem" items="${requestScope[ATTR_MODULES]}">
                                                    <option value="${moduleItem}" ${requestScope[ATTR_PERMISSION].module eq moduleItem ? 'selected="selected"' : ''}>
                                                        ${moduleItem}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty requestScope[ATTR_ERRORS][P_MODULE]}">
                                                <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_MODULE]}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                    
                                    <%-- Mô tả tác vụ chi tiết --%>
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Mô tả tác vụ chi tiết</label>
                                        <textarea name="${P_DESCRIPTION}" class="form-control ${not empty requestScope[ATTR_ERRORS][P_DESCRIPTION] ? 'is-invalid' : ''}" rows="3"
                                                  placeholder="Giải trình cụ thể về phạm vi cho phép thao tác..."
                                                  ${requestScope[ATTR_PERMISSION].isSystem ? 'disabled' : ''}><c:out value='${requestScope[ATTR_PERMISSION].description}'/></textarea>
                                        <c:if test="${not empty requestScope[ATTR_ERRORS][P_DESCRIPTION]}">
                                            <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_DESCRIPTION]}</div>
                                        </c:if>
                                    </div>

                                    <%-- Trạng thái kích hoạt --%>
                                    <div class="mb-3 p-3 bg-light rounded-3 border border-light-subtle">
                                        <div class="form-check form-switch m-0 d-flex align-items-center">
                                            <input type="checkbox" name="${P_IS_ACTIVE}" class="form-check-input me-2 style-switch" id="activeCheck" 
                                                   style="transform: scale(1.15); cursor: pointer;"
                                                   ${requestScope[ATTR_PERMISSION].isActive ? 'checked="checked"' : ''}
                                                   ${requestScope[ATTR_PERMISSION].isSystem ? 'disabled' : ''}>
                                            <label class="form-check-label fw-semibold text-dark p-0 ms-1" style="cursor: pointer;" for="activeCheck">
                                                Cho phép hoạt động trong hệ thống
                                            </label>
                                        </div>
                                    </div>
                                    
                                    <%-- Nhóm nút bấm hành động cuối trang --%>
                                    <div class="text-end pt-3 border-top mt-4">
                                        <button type="submit" class="btn btn-primary px-4 shadow-sm"
                                                ${requestScope[ATTR_PERMISSION].isSystem ? 'disabled' : ''}>
                                            <i class="bi bi-save-fill me-1"></i> Lưu thay đổi
                                        </button>
                                        <a href="${pageContext.request.contextPath}/admin/permission" class="btn btn-danger px-4 ms-1 shadow-sm">
                                            <i class="bi bi-x-circle-fill me-1"></i> Hủy bỏ
                                        </a>
                                    </div>
                                </form>
                            </div>

                            <%-- CỘT PHẢI: Metadata và Chỉ mục cấu trúc kỹ thuật hệ thống --%>
                            <div class="col-md-5 p-md-4 pt-md-2 mt-4 mt-md-0">
                                <div class="d-flex align-items-center mb-3 border-bottom pb-2">
                                    <h6 class="text-info mb-0 fw-bold">
                                        <i class="bi bi-info-circle-fill me-1"></i> Thông tin Metadata / Nhật ký hệ thống
                                    </h6>
                                </div>
                                
                                <div class="alert alert-info border-0 shadow-sm p-3 bg-opacity-10 text-dark mb-0">
                                    <p class="small fw-bold mb-2">Thông tin kỹ thuật cấu trúc vật lý của Quyền:</p>
                                    
                                    <table class="table table-sm table-bordered bg-white small mb-3 align-middle">
                                        <tbody>
                                            <tr>
                                                <td class="fw-bold text-secondary px-2 py-2" style="width: 35%;">Mã ID bản ghi</td>
                                                <td class="px-2 text-dark font-monospace">${requestScope[ATTR_PERMISSION].id}</td>
                                            </tr>
                                            <tr>
                                                <td class="fw-bold text-secondary px-2 py-2">Loại định dạng</td>
                                                <td class="px-2">
                                                    <c:choose>
                                                        <c:when test="${requestScope[ATTR_PERMISSION].isSystem}">
                                                            <span class="badge bg-danger-subtle text-danger border border-danger border-opacity-10"><i class="bi bi-shield-lock-fill"></i> Hệ thống (Cố định)</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary-subtle text-secondary border"><i class="bi bi-gear-wide-connected"></i> Tùy biến (Custom)</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="fw-bold text-secondary px-2 py-2">Thời gian tạo</td>
                                                <td class="px-2 text-muted" style="font-size: 0.8rem;">${requestScope[ATTR_PERMISSION].createdAt}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    
                                    <div class="card border border-info border-opacity-20 rounded-3 bg-white p-3 mb-2">
                                        <div class="small fw-bold text-dark mb-1"><i class="bi bi-lightbulb-fill text-warning me-1"></i>Ràng buộc ứng dụng:</div>
                                        <p class="mb-0 text-muted small" style="font-size: 0.8rem; line-height: 1.4;">
                                            Nếu tắt trạng thái kích hoạt, tất cả các nhóm **Vai trò (Role)** chứa quyền này ngay lập tức bị đình chỉ quyền hạn tương ứng trên hệ thống trong thời gian thực.
                                        </p>
                                    </div>
                                </div>

                                <blockquote class="blockquote blockquote-custom bg-white p-3 rounded-3 border-start border-warning border-3 shadow-sm mt-3">
                                    <div class="d-flex">
                                        <i class="bi bi-exclamation-triangle-fill text-warning fs-5 me-2 mt-1"></i>
                                        <div>
                                            <p class="small fw-bold text-dark mb-1">Kiến trúc phân cấp phân quyền</p>
                                            <p class="mb-0 text-muted small" style="font-size: 0.8rem;">
                                                Mã <strong>QUYỀN HẠN</strong> chi tiết này sau khi cập nhật sẽ lập tức phân phối đến cấu trúc của trang <strong>Vai trò (Role)</strong>. Bạn không cần thực hiện gán lại thủ công nếu quyền hạn đã được tích chọn trước đó.
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