<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ include file="_paramKeys.jsp"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<jsp:include page="_meta.jsp" />
<title>Quản lý quyền</title>
<script src="${pageContext.request.contextPath}/js/selectAll.js" defer></script>
<script src="${pageContext.request.contextPath}/js/dynamicFilter.js"
	defer></script>
</head>
<body class="d-flex flex-column min-vh-100">

	<jsp:include page="_headerAdmin.jsp" />

	<main class="flex-fill">
		<section class="section-content padding-y py-4">
			<div class="container">

				<%-- Alerts (Theo cấu trúc tối giản của Trang 2) --%>
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
				<c:if test="${not empty requestScope[ATTR_ERRORS][ERR_GLOBAL]}">
					<div class="alert alert-danger py-2 mb-3" role="alert">
						${requestScope[ATTR_ERRORS][ERR_GLOBAL]}</div>
				</c:if>

				<%-- Tiêu đề & Thanh công cụ (Theo cấu trúc tối giản của Trang 2) --%>
				<div
					class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3 pb-3 border-bottom">
					<div>
						<h5 class="text-primary mb-0 fw-bold">
							<i class="bi bi-shield-lock-fill me-2"></i>Quản lý quyền hệ thống
						</h5>
					</div>

					<div class="d-flex align-items-center flex-wrap gap-3">
						<div class="d-flex align-items-center gap-2 flex-wrap"
							id="filterControls">
							<span class="text-muted fw-bold small">Lọc:</span>

							<div class="form-check form-switch m-0">
								<input type="checkbox" class="form-check-input"
									id="filterToggle"> <label
									class="form-check-label small" for="filterToggle">
									OR </label>
							</div>
						</div>

						<%-- Nút hành động thêm / xóa nhanh --%>
						<div class="d-flex gap-2">
							<c:if test="${requestScope[ATTR_HAS_PERMISSION_CREATE]}">
								<a class="btn btn-sm btn-primary"
									href="${pageContext.request.contextPath}/admin/permission/create">
									<i class="bi bi-plus-circle me-1"></i> Thêm quyền
								</a>
							</c:if>

							<c:if test="${requestScope[ATTR_HAS_PERMISSION_DELETE]}">
								<button type="submit" form="batchDeleteForm"
									class="btn btn-sm btn-danger">
									<i class="bi bi-trash me-1"></i> Xóa đã chọn
								</button>
							</c:if>
						</div>
					</div>
				</div>

				<%-- Form và Bảng dữ liệu chính (Bảng có viền theo Trang 2) --%>
				<form id="batchDeleteForm"
					action="${pageContext.request.contextPath}/admin/permission/delete"
					method="post">
					<div class="table-responsive"
						style="max-height: 600px; overflow-y: auto;">
						<table
							class="table table-bordered table-sm table-hover align-middle filter-table mb-0">

							<thead class="table-light sticky-top">
								<tr id="header-row">
									<c:if test="${requestScope[ATTR_HAS_PERMISSION_DELETE]}">
										<th class="no-filter text-center" style="width: 40px;"><input
											type="checkbox" id="selectAllCheckbox"
											class="form-check-input"></th>
									</c:if>
									<th data-name="filterModule">Module</th>
									<th data-name="filterId" style="width: 80px;">ID</th>
									<th data-name="filterCode">Mã định danh</th>
									<th data-name="filterName">Tên hiển thị</th>
									<th data-name="filterDescription">Mô tả chức năng</th>
									<th data-name="filterSystem" style="width: 120px;"
										class="text-center">Hệ thống</th>
									<th data-name="filterStatus" style="width: 130px;"
										class="text-center">Trạng thái</th>
								</tr>
								<%-- Hàng này dành cho bộ lọc động inject tự động từ JS --%>
								<tr id="filter-row"></tr>
							</thead>

							<tbody>
								<c:forEach var="entry"
									items="${requestScope[ATTR_PERMISSIONS_BY_MODULE]}">
									<c:forEach var="perm" items="${entry.value}">
										<tr>
											<%-- Cột Checkbox xóa hàng loạt --%>
											<c:if test="${requestScope[ATTR_HAS_PERMISSION_DELETE]}">
												<td class="text-center"><c:choose>
														<c:when test="${perm.system}">
															<input type="checkbox" name="${P_PERMISSION_IDS}"
																value="${perm.id}" class="form-check-input" disabled>
														</c:when>
														<c:otherwise>
															<input type="checkbox" name="${P_PERMISSION_IDS}"
																value="${perm.id}" class="form-check-input">
														</c:otherwise>
													</c:choose></td>
											</c:if>

											<%-- Thiết lập biến kiểm tra quyền được chỉnh sửa --%>
											<c:set var="isEditable"
												value="${requestScope[ATTR_HAS_PERMISSION_EDIT] and not perm.system}" />

											<%-- Module --%>
											<td><c:choose>
													<c:when test="${isEditable}">
														<a
															class="text-decoration-none fw-bold text-primary d-block p-2"
															href="${pageContext.request.contextPath}/admin/permission/update?${P_ID}=${perm.id}"><c:out
																value="${perm.module}" /></a>
													</c:when>
													<c:otherwise>
														<span class="p-2 d-block"><span
															class="badge bg-secondary"><c:out
																	value="${perm.module}" /></span></span>
													</c:otherwise>
												</c:choose></td>

											<%-- ID --%>
											<td><c:choose>
													<c:when test="${isEditable}">
														<a
															class="text-decoration-none text-secondary font-monospace d-block p-2"
															href="${pageContext.request.contextPath}/admin/permission/update?${P_ID}=${perm.id}">#<c:out
																value="${perm.id}" /></a>
													</c:when>
													<c:otherwise>
														<span class="text-secondary font-monospace p-2 d-block">#<c:out
																value="${perm.id}" /></span>
													</c:otherwise>
												</c:choose></td>

											<%-- Code --%>
											<td><c:choose>
													<c:when test="${isEditable}">
														<a class="text-decoration-none d-block p-2"
															href="${pageContext.request.contextPath}/admin/permission/update?${P_ID}=${perm.id}"><code>
																<c:out value="${perm.code}" />
															</code></a>
													</c:when>
													<c:otherwise>
														<span class="p-2 d-block"><code>
																<c:out value="${perm.code}" />
															</code></span>
													</c:otherwise>
												</c:choose></td>

											<%-- Name --%>
											<td><c:choose>
													<c:when test="${isEditable}">
														<a
															class="text-decoration-none text-dark fw-bold d-block p-2"
															href="${pageContext.request.contextPath}/admin/permission/update?${P_ID}=${perm.id}"><c:out
																value="${perm.name}" /></a>
													</c:when>
													<c:otherwise>
														<span class="fw-bold text-dark p-2 d-block"><c:out
																value="${perm.name}" /></span>
													</c:otherwise>
												</c:choose></td>

											<%-- Description --%>
											<td><c:choose>
													<c:when test="${isEditable}">
														<a
															class="text-decoration-none text-muted small d-block p-2"
															href="${pageContext.request.contextPath}/admin/permission/update?${P_ID}=${perm.id}"><c:out
																value="${perm.description}" /></a>
													</c:when>
													<c:otherwise>
														<span class="text-muted small p-2 d-block"><c:out
																value="${perm.description}" /></span>
													</c:otherwise>
												</c:choose></td>

											<%-- System (GIỮ NGUYÊN GIAO DIỆN HIỆN ĐẠI CỦA TRANG 1) --%>
											<td class="text-center"><c:choose>
													<c:when test="${isEditable}">
														<a class="text-decoration-none d-block p-2"
															href="${pageContext.request.contextPath}/admin/permission/update?${P_ID}=${perm.id}">
															<span class="badge bg-light text-secondary border">Không</span>
														</a>
													</c:when>
													<c:otherwise>
														<span class="p-2 d-block"> <c:choose>
																<c:when test="${perm.system}">
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

											<%-- Status (GIỮ NGUYÊN GIAO DIỆN HIỆN ĐẠI CỦA TRANG 1) --%>
											<td class="text-center"><c:choose>
													<c:when test="${isEditable}">
														<a class="text-decoration-none d-block p-2"
															href="${pageContext.request.contextPath}/admin/permission/update?${P_ID}=${perm.id}">
															<c:choose>
																<c:when test="${perm.active}">
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
																<c:when test="${perm.active}">
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