<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ include file="_paramKeys.jsp" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="_meta.jsp" />
    <title>Sửa người dùng #${requestScope[ATTR_VALUES][P_ID]}</title>
</head>

<body class="bg-light">
    <jsp:include page="_headerAdmin.jsp" />

    <section class="section-content">
        <div class="container py-4">

            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin" class="text-decoration-none">Admin</a></li>
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/user" class="text-decoration-none">Quản lý người dùng</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Cập nhật #${requestScope[ATTR_VALUES][P_ID]}</li>
                </ol>
            </nav>

            <header class="section-heading my-3 text-center">
                <h3 class="section-title fw-bold text-dark">
                    <i class="bi bi-pencil-square text-primary me-2"></i>Cập nhật thông tin người dùng #${requestScope[ATTR_VALUES][P_ID]}
                </h3>
            </header>

            <main class="row mb-5">
                <form class="col-lg-10 offset-lg-1" method="POST" action="${pageContext.request.contextPath}/admin/user/update">

                    <input type="hidden" name="${P_ID}" value="${requestScope[ATTR_VALUES][P_ID]}">

                    <div class="mb-3">
                        <c:if test="${not empty sessionScope.successMessage}">
                            <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                                <i class="bi bi-check-circle-fill me-2"></i>${sessionScope.successMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                            <c:remove var="successMessage" scope="session" />
                        </c:if>

                        <c:if test="${not empty sessionScope.errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                                <i class="bi bi-exclamation-triangle-fill me-2"></i>${sessionScope.errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                            <c:remove var="errorMessage" scope="session" />
                        </c:if>

                        <c:if test="${not empty requestScope[ATTR_ERRORS][ERR_GLOBAL]}">
                            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                                <i class="bi bi-exclamation-octagon-fill me-2"></i>${requestScope[ATTR_ERRORS][ERR_GLOBAL]}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                    </div>

                    <div class="card shadow-sm border-0 rounded-3">
                        <div class="card-body p-4">
                            
                            <c:if test="${requestScope[ATTR_IS_SYSTEM_USER]}">
                                <div class="alert alert-warning alert-dismissible fade show shadow-sm mb-4" role="alert">
                                    <i class="bi bi-exclamation-triangle-fill me-2"></i> Không thể chỉnh sửa người dùng có vai trò hệ thống!
                                </div>
                            </c:if>
                            
                            <div class="row g-4">

                                <div class="col-md-6">
                                    <div class="card h-100 border bg-white rounded-3">
                                        <div class="card-header bg-light border-bottom fw-bold py-3 text-secondary">
                                            <i class="bi bi-person-badge-fill me-2 text-primary"></i>Thông tin tài khoản
                                        </div>
                                        <div class="card-body p-3">

                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Tên đăng nhập <span class="text-danger">*</span></label>
                                                <div class="input-group">
                                                    <span class="input-group-text bg-light"><i class="bi bi-person-fill text-muted"></i></span>
                                                    <input type="text" class="form-control bg-light" name="${P_USERNAME}" value="${requestScope[ATTR_VALUES][P_USERNAME]}" readonly>
                                                </div>
                                                <div class="form-text text-warning"><i class="bi bi-info-circle"></i> Tên tài khoản không thể thay đổi</div>
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Mật khẩu mới</label>
                                                <div class="input-group">
                                                    <span class="input-group-text"><i class="bi bi-key-fill text-muted"></i></span>
                                                    <input type="password" class="form-control ${not empty requestScope[ATTR_ERRORS][P_PASSWORD] ? 'is-invalid' : ''}" 
                                                           name="${P_PASSWORD}" placeholder="Bỏ trống nếu giữ nguyên"
                                                           ${requestScope[ATTR_IS_SYSTEM_USER] ? 'disabled' : ''}>
                                                    <c:if test="${not empty requestScope[ATTR_ERRORS][P_PASSWORD]}">
                                                        <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_PASSWORD]}</div>
                                                    </c:if>
                                                </div>
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Vai trò hệ thống <span class="text-danger">*</span></label>
                                                <select class="form-select ${not empty requestScope[ATTR_ERRORS][P_ROLE] ? 'is-invalid' : ''}" name="${P_ROLE}" required
                                                        ${requestScope[ATTR_IS_SYSTEM_USER] ? 'disabled' : ''}>
                                                    <c:if test="${empty requestScope[ATTR_VALUES][P_ROLE]}">
                                                        <option value="">--- Chọn quyền ---</option>
                                                    </c:if>
                                                    <c:forEach var="role" items="${requestScope[ATTR_ALL_ROLES]}">
                                                        <option value="${role.code}" ${not empty requestScope[ATTR_VALUES][P_ROLE] and requestScope[ATTR_VALUES][P_ROLE] == role.code ? 'selected' : ''}>
                                                            ${role.name} (${role.code})
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                <c:if test="${not empty requestScope[ATTR_ERRORS][P_ROLE]}">
                                                    <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_ROLE]}</div>
                                                </c:if>
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Ngôn ngữ ưa thích</label>
                                                <select class="form-select ${not empty requestScope[ATTR_ERRORS][P_PREFERRED_LANGUAGE_ID] ? 'is-invalid' : ''}" name="${P_PREFERRED_LANGUAGE_ID}"
                                                        ${requestScope[ATTR_IS_SYSTEM_USER] ? 'disabled' : ''}>
                                                    <c:forEach var="lang" items="${requestScope[ATTR_LANGUAGES]}">
                                                        <option value="${lang.id}" ${(requestScope[ATTR_VALUES][P_PREFERRED_LANGUAGE_ID] == null and lang.id == 1) or (requestScope[ATTR_VALUES][P_PREFERRED_LANGUAGE_ID] != null and requestScope[ATTR_VALUES][P_PREFERRED_LANGUAGE_ID] == lang.id) ? 'selected' : ''}>
                                                            ${lang.name} (${lang.code})
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                <c:if test="${not empty requestScope[ATTR_ERRORS][P_PREFERRED_LANGUAGE_ID]}">
                                                    <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_PREFERRED_LANGUAGE_ID]}</div>
                                                </c:if>
                                            </div>

                                        </div>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <div class="card h-100 border bg-white rounded-3">
                                        <div class="card-header bg-light border-bottom fw-bold py-3 text-secondary">
                                            <i class="bi bi-card-list me-2 text-primary"></i>Thông tin cá nhân
                                        </div>
                                        <div class="card-body p-3">

                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Họ và tên <span class="text-danger">*</span></label>
                                                <input type="text" class="form-control ${not empty requestScope[ATTR_ERRORS][P_FULLNAME] ? 'is-invalid' : ''}" 
                                                       name="${P_FULLNAME}" value="${requestScope[ATTR_VALUES][P_FULLNAME]}" required
                                                       ${requestScope[ATTR_IS_SYSTEM_USER] ? 'disabled' : ''}>
                                                <c:if test="${not empty requestScope[ATTR_ERRORS][P_FULLNAME]}">
                                                    <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_FULLNAME]}</div>
                                                </c:if>
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Email liên hệ <span class="text-danger">*</span></label>
                                                <div class="input-group">
                                                    <span class="input-group-text"><i class="bi bi-envelope-fill text-muted"></i></span>
                                                    <input type="email" class="form-control ${not empty requestScope[ATTR_ERRORS][P_EMAIL] ? 'is-invalid' : ''}" 
                                                           name="${P_EMAIL}" value="${requestScope[ATTR_VALUES][P_EMAIL]}" required
                                                           ${requestScope[ATTR_IS_SYSTEM_USER] ? 'disabled' : ''}>
                                                    <c:if test="${not empty requestScope[ATTR_ERRORS][P_EMAIL]}">
                                                        <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_EMAIL]}</div>
                                                    </c:if>
                                                </div>
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label fw-semibold">Số điện thoại</label>
                                                <div class="input-group">
                                                    <span class="input-group-text"><i class="bi bi-telephone-fill text-muted"></i></span>
                                                    <input type="text" class="form-control ${not empty requestScope[ATTR_ERRORS][P_PHONE_NUMBER] ? 'is-invalid' : ''}" 
                                                           name="${P_PHONE_NUMBER}" value="${requestScope[ATTR_VALUES][P_PHONE_NUMBER]}"
                                                           ${requestScope[ATTR_IS_SYSTEM_USER] ? 'disabled' : ''}>
                                                    <c:if test="${not empty requestScope[ATTR_ERRORS][P_PHONE_NUMBER]}">
                                                        <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_PHONE_NUMBER]}</div>
                                                    </c:if>
                                                </div>
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label d-block fw-semibold">Giới tính</label>
                                                <div class="d-flex gap-4 mt-2">
                                                    <div class="form-check text-dark">
                                                        <input class="form-check-input" type="radio" name="${P_GENDER}" value="0" ${requestScope[ATTR_VALUES][P_GENDER] != null and requestScope[ATTR_VALUES][P_GENDER]=='0' ? 'checked' : ''}
                                                               ${requestScope[ATTR_IS_SYSTEM_USER] ? 'disabled' : ''}>
                                                        <label class="form-check-label"><i class="bi bi-gender-male text-primary me-1"></i>Nam</label>
                                                    </div>
                                                    <div class="form-check text-dark">
                                                        <input class="form-check-input" type="radio" name="${P_GENDER}" value="1" ${requestScope[ATTR_VALUES][P_GENDER] != null and requestScope[ATTR_VALUES][P_GENDER]=='1' ? 'checked' : ''}
                                                               ${requestScope[ATTR_IS_SYSTEM_USER] ? 'disabled' : ''}>
                                                        <label class="form-check-label"><i class="bi bi-gender-female text-danger me-1"></i>Nữ</label>
                                                    </div>
                                                </div>
                                                <c:if test="${not empty requestScope[ATTR_ERRORS][P_GENDER]}">
                                                    <div class="text-danger small mt-1"><i class="bi bi-exclamation-circle"></i> ${requestScope[ATTR_ERRORS][P_GENDER]}</div>
                                                </c:if>
                                            </div>

                                        </div>
                                    </div>
                                </div>

                            </div>

                            <div class="text-end mt-4 pt-3 border-top">
                                <button type="submit" class="btn btn-primary px-4 shadow-sm"
                                        ${requestScope[ATTR_IS_SYSTEM_USER] ? 'disabled' : ''}>
                                    <i class="bi bi-save2-fill me-1"></i> Lưu thay đổi
                                </button>

                                <a href="${pageContext.request.contextPath}/admin/user/update?${P_ID}=${requestScope[ATTR_VALUES][P_ID]}" class="btn btn-outline-secondary px-3 ms-1">
                                    <i class="bi bi-arrow-counterclockwise"></i> Làm mới
                                </a>

                                <a class="btn btn-danger px-4 ms-1 shadow-sm" href="${pageContext.request.contextPath}/admin/user">
                                    <i class="bi bi-x-circle-fill me-1"></i> Hủy
                                </a>
                            </div>

                        </div>
                    </div>

                </form>
            </main>

        </div>
    </section>

    <jsp:include page="_footerAdmin.jsp" />
</body>
</html>