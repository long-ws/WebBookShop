<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ include file="_paramKeys.jsp"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<jsp:include page="_meta.jsp" />
<title>Quản lý vai trò</title>
<script src="${pageContext.request.contextPath}/js/selectAll.js" defer></script>
<script src="${pageContext.request.contextPath}/js/dynamicFilter.js"
	defer></script>
</head>
<body class="d-flex flex-column min-vh-100">

	<jsp:include page="_headerAdmin.jsp" />

	<main class="flex-fill">
		<section class="section-content padding-y py-4">
			<div class="container">

				<%-- Alerts --%>
				<c:if test="${not empty sessionScope.successMessage}">
					<div class="alert alert-success py-2 mb-3" role="alert">
						<c:out value="${sessionScope.successMessage}" />
					</div>
					<c:remove var="successMessage" scope="session" />
				</c:if>
				<c:if test="${not empty sessionScope.errorMessage}">
					<div class="alert alert-danger py-2 mb-3" role="alert">
						<c:out value="${sessionScope.errorMessage}" />
					</div>
					<c:remove var="errorMessage" scope="session" />
				</c:if>

				<%-- Tiêu đề & Thanh công cụ (Bộ lọc + Nút hành động) --%>
				<div
					class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3 pb-3 border-bottom">
					<div>
						<h5 class="text-primary mb-0 fw-bold">
							<i class="bi bi-person-badge-fill me-2"></i>Quản lý vai trò
						</h5>
					</div>

					<div class="d-flex align-items-center flex-wrap gap-3">
						<%-- Bộ lọc JS gắn kết --%>
						<div class="d-flex align-items-center gap-2 flex-wrap"
							id="filterControls">
							<span class="text-muted fw-bold small">Lọc:</span>

							<div class="form-check form-switch m-0">
								<input type="checkbox" class="form-check-input"
									id="filterToggle"> <label
									class="form-check-label small" for="filterToggle"> OR </label>
							</div>
						</div>

						<%-- Nút hành động thêm / xóa nhanh --%>
						<div class="d-flex gap-2">
							<c:if test="${requestScope[ATTR_HAS_ROLE_CREATE]}">
								<a class="btn btn-sm btn-primary"
									href="${pageContext.request.contextPath}/admin/role/create">
									<i class="bi bi-plus-circle me-1"></i> Thêm vai trò
								</a>
							</c:if>

							<c:if test="${requestScope[ATTR_HAS_ROLE_DELETE]}">
								<button type="submit" form="batchDeleteForm"
									class="btn btn-sm btn-danger">
									<i class="bi bi-trash me-1"></i> Xóa đã chọn
								</button>
							</c:if>
						</div>
					</div>
				</div>

				<%-- Form và Bảng dữ liệu chính --%>
				<form id="batchDeleteForm"
					action="${pageContext.request.contextPath}/admin/role/delete"
					method="post">
					<div class="table-responsive"
						style="max-height: 600px; overflow-y: auto;">
						<table
							class="table table-bordered table-sm table-hover align-middle filter-table mb-0">

							<thead class="table-light sticky-top">
								<tr id="header-row">
									<c:if test="${requestScope[ATTR_HAS_ROLE_DELETE]}">
										<th class="no-filter text-center" style="width: 40px;"><input
											type="checkbox" id="selectAllCheckbox"
											class="form-check-input"></th>
									</c:if>
									<th data-name="filterId" style="width: 80px;">ID</th>
									<th data-name="filterCode">Mã</th>
									<th data-name="filterName">Tên</th>
									<th data-name="filterDescription">Mô tả</th>
									<th data-name="filterSystem" style="width: 120px;"
										class="text-center">Hệ thống</th>
									<th data-name="filterStatus" style="width: 130px;"
										class="text-center">Trạng thái</th>
								</tr>
								<%-- Hàng này dành cho bộ lọc động inject tự động từ JS --%>
								<tr id="filter-row"></tr>
							</thead>

							<tbody>
								<c:forEach var="role" items="${requestScope[ATTR_ROLES]}">
									<tr>
										<%-- Cột Checkbox xóa hàng loạt --%>
										<c:if test="${requestScope[ATTR_HAS_ROLE_DELETE]}">
											<td class="text-center"><c:choose>
													<c:when test="${role.isSystem}">
														<input type="checkbox" name="${P_ROLE_IDS}"
															value="${role.id}" class="form-check-input" disabled>
													</c:when>
													<c:otherwise>
														<input type="checkbox" name="${P_ROLE_IDS}"
															value="${role.id}" class="form-check-input">
													</c:otherwise>
												</c:choose></td>
										</c:if>

										<%-- Thiết lập biến kiểm tra quyền được chỉnh sửa --%>
										<c:set var="isEditable"
											value="${requestScope[ATTR_HAS_ROLE_EDIT] and not role.isSystem}" />

										<%-- ID --%>
										<td><c:choose>
												<c:when test="${isEditable}">
													<a class="text-decoration-none text-secondary d-block p-2"
														href="${pageContext.request.contextPath}/admin/role/update?${P_ID}=${role.id}">#<c:out
															value="${role.id}" /></a>
												</c:when>
												<c:otherwise>
													<span class="text-secondary p-2 d-block">#<c:out
															value="${role.id}" /></span>
												</c:otherwise>
											</c:choose></td>

										<%-- Mã --%>
										<td><c:choose>
												<c:when test="${isEditable}">
													<a class="text-decoration-none d-block p-2"
														href="${pageContext.request.contextPath}/admin/role/update?${P_ID}=${role.id}"><code>
															<c:out value="${role.code}" />
														</code></a>
												</c:when>
												<c:otherwise>
													<span class="d-block p-2"><code>
															<c:out value="${role.code}" />
														</code></span>
												</c:otherwise>
											</c:choose></td>

										<%-- Tên --%>
										<td><c:choose>
												<c:when test="${isEditable}">
													<a
														class="text-decoration-none text-dark fw-bold d-block p-2"
														href="${pageContext.request.contextPath}/admin/role/update?${P_ID}=${role.id}"><c:out
															value="${role.name}" /></a>
												</c:when>
												<c:otherwise>
													<span class="fw-bold text-dark p-2 d-block"><c:out
															value="${role.name}" /></span>
												</c:otherwise>
											</c:choose></td>

										<%-- Mô tả --%>
										<td><c:choose>
												<c:when test="${isEditable}">
													<a
														class="text-decoration-none text-muted small d-block p-2"
														href="${pageContext.request.contextPath}/admin/role/update?${P_ID}=${role.id}"><c:out
															value="${role.description}" /></a>
												</c:when>
												<c:otherwise>
													<span class="text-muted small p-2 d-block"><c:out
															value="${role.description}" /></span>
												</c:otherwise>
											</c:choose></td>

										<%-- Hệ thống (Đã đồng bộ giống Trang 1) --%>
										<td class="text-center"><c:choose>
												<c:when test="${isEditable}">
													<a class="text-decoration-none d-block p-2"
														href="${pageContext.request.contextPath}/admin/role/update?${P_ID}=${role.id}">
														<span class="badge bg-light text-secondary border">Không</span>
													</a>
												</c:when>
												<c:otherwise>
													<span class="p-2 d-block"> <c:choose>
															<c:when test="${role.isSystem}">
																<span
																	class="badge bg-info-subtle text-info border border-info border-opacity-20"><i
																	class="bi bi-shield-lock"></i> Hệ thống</span>
															</c:when>
															<c:otherwise>
																<span class="badge bg-light text-secondary border">Không</span>
															</c:otherwise>
														</c:choose>
													</span>
												</c:otherwise>
											</c:choose></td>

										<%-- Trạng thái (Đã đồng bộ giống Trang 1) --%>
										<td class="text-center"><c:choose>
												<c:when test="${isEditable}">
													<a class="text-decoration-none d-block p-2"
														href="${pageContext.request.contextPath}/admin/role/update?${P_ID}=${role.id}">
														<c:choose>
															<c:when test="${role.isActive}">
																<span
																	class="badge bg-success-subtle text-success border border-success border-opacity-20">Hoạt
																	động</span>
															</c:when>
															<c:otherwise>
																<span
																	class="badge bg-secondary-subtle text-secondary border">Tắt</span>
															</c:otherwise>
														</c:choose>
													</a>
												</c:when>
												<c:otherwise>
													<span class="p-2 d-block"> <c:choose>
															<c:when test="${role.isActive}">
																<span
																	class="badge bg-success-subtle text-success border border-success border-opacity-20">Hoạt
																	động</span>
															</c:when>
															<c:otherwise>
																<span
																	class="badge bg-secondary-subtle text-secondary border">Tắt</span>
															</c:otherwise>
														</c:choose>
													</span>
												</c:otherwise>
											</c:choose></td>
									</tr>
								</c:forEach>
							</tbody>

						</table>
					</div>
				</form>

			</div>
		</section>
	</main>

	<jsp:include page="_footerAdmin.jsp" />

</body>
</html>