<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="_paramKeys.jsp"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<jsp:include page="_meta.jsp" />
<title>Chỉnh sửa Vai trò - Admin</title>
<script
    src="<c:out value='${pageContext.request.contextPath}'/>/js/selectAll.js"
    defer></script>
</head>
<body class="d-flex flex-column min-vh-100">
    <jsp:include page="_headerAdmin.jsp" />

    <main class="flex-fill">
        <section class="section-content padding-y">
            <div class="container">
                <div class="row">
                    <%-- 1. Role Info Card - Xếp theo chiều ngang --%>
                    <div class="col-12 mb-4">
                        <div class="card shadow-sm">
                            <div class="card-body">
                                <c:if test="${not empty sessionScope.successMessage}">
                                    <div class="alert alert-success alert-dismissible fade show py-2">
                                        <i class="bi bi-check-circle-fill me-2"></i><c:out value='${sessionScope.successMessage}' />
                                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                    </div>
                                    <c:remove var="successMessage" scope="session" />
                                </c:if>
                                <c:if test="${not empty sessionScope.errorMessage}">
                                    <div class="alert alert-danger alert-dismissible fade show py-2">
                                        <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value='${sessionScope.errorMessage}' />
                                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                    </div>
                                    <c:remove var="errorMessage" scope="session" />
                                </c:if>
                                <c:if test="${not empty requestScope[ATTR_ERRORS][ERR_GLOBAL]}">
                                    <div class="alert alert-danger alert-dismissible fade show mb-3">
                                        <i class="bi bi-exclamation-triangle"></i> ${requestScope[ATTR_ERRORS][ERR_GLOBAL]}
                                    </div>
                                </c:if>

                                <form
                                    action="<c:out value='${pageContext.request.contextPath}'/>/admin/role/update"
                                    method="post">
                                    <input type="hidden" name="${P_ID}"
                                        value="<c:out value='${requestScope[ATTR_ROLE].id}'/>">
                                    <input type="hidden" name="${P_CODE}"
                                        value="<c:out value='${requestScope[ATTR_ROLE].code}'/>">

                                    <div class="row align-items-end">
                                        <div class="col-md-2 mb-3">
                                            <label class="form-label fw-bold">Mã vai trò</label> <input
                                                type="text" class="form-control"
                                                value="<c:out value='${requestScope[ATTR_ROLE].code}'/>"
                                                disabled>
                                        </div>

                                        <div class="col-md-3 mb-3">
                                            <label class="form-label fw-bold">Tên vai trò</label> <input
                                                type="text" name="${P_NAME}" class="form-control ${not empty requestScope[ATTR_ERRORS][P_NAME] ? 'is-invalid' : ''}"
                                                value="<c:out value='${requestScope[ATTR_ROLE].name}'/>"
                                                ${requestScope[ATTR_ROLE].isSystem ? 'disabled' : ''}
                                                required>
                                            <c:if test="${not empty requestScope[ATTR_ERRORS][P_NAME]}">
                                                <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_NAME]}</div>
                                            </c:if>
                                        </div>

                                        <div class="col-md-3 mb-3">
                                            <label class="form-label fw-bold">Mô tả</label> <input
                                                type="text" name="${P_DESCRIPTION}" class="form-control ${not empty requestScope[ATTR_ERRORS][P_DESCRIPTION] ? 'is-invalid' : ''}"
                                                value="<c:out value='${requestScope[ATTR_ROLE].description}'/>"
                                                ${requestScope[ATTR_ROLE].isSystem ? 'disabled' : ''}>
                                            <c:if test="${not empty requestScope[ATTR_ERRORS][P_DESCRIPTION]}">
                                                <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_DESCRIPTION]}</div>
                                            </c:if>
                                        </div>

                                        <div class="col-md-2 mb-3">
                                            <div class="form-check pb-2">
                                                <c:choose>
                                                    <c:when test="${requestScope[ATTR_ROLE].isActive}">
                                                        <input type="checkbox" name="${P_IS_ACTIVE}"
                                                            class="form-check-input" id="activeCheck"
                                                            checked="checked"
                                                            ${requestScope[ATTR_ROLE].isSystem ? 'disabled' : ''}>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input type="checkbox" name="${P_IS_ACTIVE}"
                                                            class="form-check-input" id="activeCheck"
                                                            ${requestScope[ATTR_ROLE].isSystem ? 'disabled' : ''}>
                                                    </c:otherwise>
                                                </c:choose>
                                                <label class="form-check-label fw-bold" for="activeCheck">Hoạt động</label>
                                            </div>
                                        </div>

                                        <div class="col-md-2 mb-3 ms-auto">
                                            <div class="d-flex justify-content-end">
                                                <button type="submit" class="btn btn-primary w-100"
                                                    ${requestScope[ATTR_ROLE].isSystem ? 'disabled' : ''}>
                                                    <i class="bi bi-save"></i> Lưu
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <%-- 2. Permission Assignment Card - 2 bảng xếp song song ngang nhau --%>
                    <div class="col-12">
                        <div class="card shadow-sm">
                            <div class="card-body">
                                <div class="row">

                                    <%-- LEFT COLUMN: Thêm quyền mới --%>
                                    <div class="col-md-6 border-end pe-md-4">
                                        <div class="d-flex align-items-center justify-content-between mb-3 border-bottom pb-2">
                                            <h6 class="text-primary mb-0 fw-bold">
                                                <i class="bi bi-plus-circle-fill me-1"></i> Gán quyền
                                            </h6>
                                        </div>

                                        <form
                                            action="<c:out value='${pageContext.request.contextPath}'/>/admin/role/batchAssignPermission"
                                            method="post"
                                            ${requestScope[ATTR_ROLE].isSystem ? 'style="pointer-events: none; opacity: 0.6;"' : ''}>
                                            <input type="hidden" name="${P_ROLE_ID}"
                                                value="<c:out value='${requestScope[ATTR_ROLE].id}'/>">

                                            <%-- Lọc các quyền chưa gán --%>
                                            <c:set var="unassignedPermissions" value="" />
                                            <c:forEach var="perm" items="${requestScope[ATTR_ALL_PERMISSIONS]}">
                                                <c:set var="alreadyAssigned" value="false" />
                                                <c:forEach var="assigned" items="${requestScope[ATTR_ROLE_PERMISSIONS]}">
                                                    <c:if test="${assigned.id == perm.id}">
                                                        <c:set var="alreadyAssigned" value="true" />
                                                    </c:if>
                                                </c:forEach>
                                                <c:if test="${not alreadyAssigned}">
                                                    <c:set var="unassignedPermissions" value="${unassignedPermissions},${perm.id}" scope="request" />
                                                </c:if>
                                            </c:forEach>

                                            <c:set var="hasUnassigned" value="false" />
                                            <c:forEach var="perm" items="${requestScope[ATTR_ALL_PERMISSIONS]}">
                                                <c:set var="alreadyAssigned" value="false" />
                                                <c:forEach var="assigned" items="${requestScope[ATTR_ROLE_PERMISSIONS]}">
                                                    <c:if test="${assigned.id == perm.id}">
                                                        <c:set var="alreadyAssigned" value="true" />
                                                    </c:if>
                                                </c:forEach>
                                                <c:if test="${not alreadyAssigned}">
                                                    <c:set var="hasUnassigned" value="true" />
                                                </c:if>
                                            </c:forEach>

                                            <c:if test="${requestScope[ATTR_ROLE].isSystem}">
                                                <div class="alert alert-warning py-3">
                                                    <i class="bi bi-exclamation-triangle"></i> Không thể chỉnh sửa quyền cho vai trò hệ thống!
                                                </div>
                                            </c:if>
                                            <c:if test="${not hasUnassigned and not requestScope[ATTR_ROLE].isSystem}">
                                                <div class="alert alert-light text-muted fst-italic py-3">
                                                    Tất cả quyền hiện có đã được gán hoàn tất.
                                                </div>
                                            </c:if>
                                            
                                            <c:if test="${hasUnassigned and not requestScope[ATTR_ROLE].isSystem}">
                                                <div class="mb-3 d-flex align-items-center justify-content-between">
                                                    <small class="text-muted">Chọn quyền rồi nhấn nút thêm:</small>
                                                    <button type="submit" class="btn btn-sm btn-success">
                                                        <i class="bi bi-plus me-1"></i> Thêm mục đã chọn
                                                    </button>
                                                </div>
                                                <div class="table-responsive" style="max-height: 400px; overflow-y: auto;">
                                                    <table class="table table-bordered table-sm table-hover align-middle">
                                                        <thead class="table-light sticky-top">
                                                            <tr>
                                                                <th style="width: 40px; text-align: center;">
                                                                    <input type="checkbox" id="selectAllAddCheckbox" class="form-check-input">
                                                                </th>
                                                                <th>Module</th>
                                                                <th>Mã quyền</th>
                                                                <th>Tên quyền</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="perm" items="${requestScope[ATTR_ALL_PERMISSIONS]}">
                                                                <c:set var="alreadyAssigned" value="false" />
                                                                <c:forEach var="assigned" items="${requestScope[ATTR_ROLE_PERMISSIONS]}">
                                                                    <c:if test="${assigned.id == perm.id}">
                                                                        <c:set var="alreadyAssigned" value="true" />
                                                                    </c:if>
                                                                </c:forEach>
                                                                <c:if test="${not alreadyAssigned}">
                                                                    <tr>
                                                                        <td class="text-center">
                                                                            <input class="form-check-input" type="checkbox" value="${perm.id}" name="${P_PERMISSION_IDS}">
                                                                        </td>
                                                                        <td>
                                                                            <span class="badge bg-secondary">
                                                                                <c:out value='${perm.module}' />
                                                                            </span>
                                                                        </td>
                                                                        <td>
                                                                            <code><c:out value='${perm.code}' /></code>
                                                                        </td>
                                                                        <td>
                                                                            <div class="fw-bold">
                                                                                <c:out value='${perm.name}' />
                                                                            </div>
                                                                            <small class="text-muted d-block" style="font-size: 0.75rem;">
                                                                                <c:out value='${perm.description}' />
                                                                            </small>
                                                                        </td>
                                                                    </tr>
                                                                </c:if>
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </c:if>
                                        </form>
                                    </div>

                                    <%-- RIGHT COLUMN: Quyền đã gán --%>
                                    <div class="col-md-6 ps-md-4 mt-4 mt-md-0">
                                        <div class="d-flex align-items-center justify-content-between mb-3 border-bottom pb-2">
                                            <h6 class="text-danger mb-0 fw-bold">
                                                <i class="bi bi-shield-check me-1"></i> Gỡ quyền
                                            </h6>
                                        </div>

                                        <form
                                            action="<c:out value='${pageContext.request.contextPath}'/>/admin/role/batchRemovePermission"
                                            method="post"
                                            ${requestScope[ATTR_ROLE].isSystem ? 'style="pointer-events: none; opacity: 0.6;"' : ''}>
                                            <input type="hidden" name="${P_ROLE_ID}"
                                                value="<c:out value='${requestScope[ATTR_ROLE].id}'/>">

                                            <c:if test="${empty requestScope[ATTR_ROLE_PERMISSIONS] and not requestScope[ATTR_ROLE].isSystem}">
                                                <div class="alert alert-light text-muted fst-italic py-3">
                                                    Vai trò này hiện chưa được gán bất kỳ quyền nào.
                                                </div>
                                            </c:if>
                                            
                                            <c:if test="${not empty requestScope[ATTR_ROLE_PERMISSIONS] and not requestScope[ATTR_ROLE].isSystem}">
                                                <div class="mb-3 d-flex align-items-center justify-content-between">
                                                    <small class="text-muted">Chọn quyền cần loại bỏ rồi nhấn nút xóa:</small>
                                                    <button type="submit" class="btn btn-sm btn-danger">
                                                        <i class="bi bi-trash me-1"></i> Xóa mục đã chọn
                                                    </button>
                                                </div>
                                                <div class="table-responsive" style="max-height: 400px; overflow-y: auto;">
                                                    <table class="table table-bordered table-sm table-hover align-middle">
                                                        <thead class="table-light sticky-top">
                                                            <tr>
                                                                <th style="width: 40px; text-align: center;">
                                                                    <input type="checkbox" id="selectAllCheckbox" class="form-check-input">
                                                                </th>
                                                                <th>Module</th>
                                                                <th>Mã quyền</th>
                                                                <th>Tên quyền</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="perm" items="${requestScope[ATTR_ROLE_PERMISSIONS]}">
                                                                <tr>
                                                                    <td class="text-center">
                                                                        <input class="form-check-input" type="checkbox" value="${perm.id}" name="${P_PERMISSION_IDS}">
                                                                    </td>
                                                                    <td>
                                                                        <span class="badge bg-secondary">
                                                                            <c:out value='${perm.module}' />
                                                                        </span>
                                                                    </td>
                                                                    <td>
                                                                        <code><c:out value='${perm.code}' /></code>
                                                                    </td>
                                                                    <td>
                                                                        <div class="fw-bold">
                                                                            <c:out value='${perm.name}' />
                                                                        </div>
                                                                        <small class="text-muted d-block" style="font-size: 0.75rem;">
                                                                            <c:out value='${perm.description}' />
                                                                        </small>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </c:if>
                                        </form>
                                    </div>

                                </div>
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