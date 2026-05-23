<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<c:set var="servletPath" scope="page" value="${requestScope['jakarta.servlet.forward.servlet_path']}" />

<header class="section-header border-bottom bg-white py-3">
	<div class="container">
		<div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
			<h3 class="mb-0">
				<a class="text-dark text-decoration-none fw-bold" href="${pageContext.request.contextPath}/admin">
					<i class="bi bi-book-half text-primary me-2"></i>Shop Bán Sách <span class="badge bg-secondary fs-6 align-middle">Admin</span>
				</a>
			</h3>

			<div class="d-flex align-items-center">
				<c:choose>
					<c:when test="${not empty sessionScope.currentUser}">
						<span class="text-muted">Xin chào, <strong class="text-dark">${sessionScope.currentUser.fullname}</strong>!</span>
						<a class="btn btn-outline-danger btn-sm ms-3" href="${pageContext.request.contextPath}/admin/signout">
							<i class="bi bi-box-arrow-right"></i> Đăng xuất
						</a>
					</c:when>
					<c:otherwise>
						<a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/admin/signin">
							<i class="bi bi-box-arrow-in-right"></i> Đăng nhập
						</a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</header>

<nav class="navbar navbar-expand-xl navbar-light bg-light border-bottom sticky-top shadow-sm py-1">
	<div class="container">
		<button class="navbar-toggler ms-auto" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar" aria-controls="adminNavbar" aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>

		<div class="collapse navbar-collapse" id="adminNavbar">
			<ul class="navbar-nav w-100 nav-fill gap-1 my-2 my-xl-0">
				
				<c:if test="${canViewUsers}">
					<li class="nav-item">
						<a class="nav-link rounded px-3 py-2 ${fn:startsWith(servletPath, '/admin/user') ? 'active bg-primary text-white' : 'text-dark'}" 
						   href="${pageContext.request.contextPath}/admin/user">
							<i class="bi bi-people me-1"></i> Người dùng
						</a>
					</li>
				</c:if>

				<c:if test="${canViewRoles}">
					<li class="nav-item">
						<a class="nav-link rounded px-3 py-2 ${fn:startsWith(servletPath, '/admin/role') ? 'active bg-primary text-white' : 'text-dark'}" 
						   href="${pageContext.request.contextPath}/admin/role">
							<i class="bi bi-shield-lock me-1"></i> Vai trò
						</a>
					</li>
				</c:if>

				<c:if test="${canViewPermissions}">
					<li class="nav-item">
						<a class="nav-link rounded px-3 py-2 ${fn:startsWith(servletPath, '/admin/permission') ? 'active bg-primary text-white' : 'text-dark'}" 
						   href="${pageContext.request.contextPath}/admin/permission">
							<i class="bi bi-key me-1"></i> Quyền hạn
						</a>
					</li>
				</c:if>

				<c:if test="${canViewCategories}">
					<li class="nav-item">
						<a class="nav-link rounded px-3 py-2 ${fn:startsWith(servletPath, '/admin/categoryManager') ? 'active bg-primary text-white' : 'text-dark'}" 
						   href="${pageContext.request.contextPath}/admin/categoryManager/view">
							<i class="bi bi-tags me-1"></i> Thể loại
						</a>
					</li>
				</c:if>

				<c:if test="${canViewProducts}">
					<li class="nav-item">
						<a class="nav-link rounded px-3 py-2 ${fn:startsWith(servletPath, '/admin/productManager') ? 'active bg-primary text-white' : 'text-dark'}" 
						   href="${pageContext.request.contextPath}/admin/productManager/view">
							<i class="bi bi-book me-1"></i> Sản phẩm
						</a>
					</li>
				</c:if>

				<c:if test="${canViewReviews}">
					<li class="nav-item">
						<a class="nav-link rounded px-3 py-2 ${fn:startsWith(servletPath, '/admin/reviewManager') ? 'active bg-primary text-white' : 'text-dark'}" 
						   href="${pageContext.request.contextPath}/admin/reviewManager/view">
							<i class="bi bi-star me-1"></i> Đánh giá
						</a>
					</li>
				</c:if>

				<c:if test="${canViewOrders}">
					<li class="nav-item">
						<a class="nav-link rounded px-3 py-2 ${fn:startsWith(servletPath, '/admin/orderManager') ? 'active bg-primary text-white' : 'text-dark'}" 
						   href="${pageContext.request.contextPath}/admin/orderManager/view">
							<i class="bi bi-inboxes"></i> Đơn hàng
						</a>
					</li>
				</c:if>

				<c:if test="${canViewVouchers}">
					<li class="nav-item">
						<a class="nav-link rounded px-3 py-2 ${fn:startsWith(servletPath, '/admin/voucherManager') ? 'active bg-primary text-white' : 'text-dark'}"
						   href="${pageContext.request.contextPath}/admin/voucherManager/view">
							<i class="bi bi-ticket"></i> Voucher
						</a>
					</li>
				</c:if>
				
			</ul>
		</div>
	</div>
</nav>