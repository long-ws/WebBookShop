<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<c:set var="servletPath" scope="page"
	value="${requestScope['javax.servlet.forward.servlet_path']}" />

<!-- Header -->
<header class="section-header border-bottom">
	<div class="container">
		<div class="row align-items-center">
			<!-- Logo -->
			<div class="col py-3">
				<h3>
					<a class="text-body text-decoration-none"
						href="${pageContext.request.contextPath}/admin"> Shop Bán Sách
					</a>
				</h3>
			</div>
		</div>
	</div>

	<!-- Login / Logout -->
	<div class="container">
		<div class="ms-auto" align="right">
			<c:choose>
				<c:when test="${not empty sessionScope.currentUser}">
					<span>Xin chào <strong>${sessionScope.currentUser.fullname}</strong>!
					</span>
					<a class="btn btn-light ms-2"
						href="${pageContext.request.contextPath}/admin/signout">Đăng
						xuất</a>
				</c:when>
				<c:otherwise>
					<a class="btn btn-primary"
						href="${pageContext.request.contextPath}/admin/signin">Đăng
						nhập</a>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</header>

<!-- Navbar -->
<nav class="navbar navbar-main border-bottom">
	<div class="container">
		<table
			style="border-collapse: separate; border-spacing: 5px; width: 100%;">
			<tr>
				<!-- Quản lý người dùng -->
				<c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
					<td
						bgcolor="${fn:startsWith(servletPath, '/admin/userManager') ? '#0d6efd' : ''}"
						align="center"><a
						href="${pageContext.request.contextPath}/admin/userManager"
						style="text-decoration:none;
                  color:${fn:startsWith(servletPath, '/admin/userManager') ? 'white' : 'black'};">
							<i class="bi bi-people"></i> Quản lý người dùng
					</a></td>
				</c:if>

				<!-- Quản lý thể loại -->
				<td
					bgcolor="${fn:startsWith(servletPath, '/admin/categoryManager') ? '#0d6efd' : ''}"
					align="center"><a
					href="${pageContext.request.contextPath}/admin/categoryManager"
					style="text-decoration:none; color:${fn:startsWith(servletPath, '/admin/categoryManager') ? 'white' : 'black'};">
						<i class="bi bi-tags"></i> Quản lý thể loại
				</a></td>

				<!-- Quản lý sản phẩm -->
				<td
					bgcolor="${fn:startsWith(servletPath, '/admin/productManager') ? '#0d6efd' : ''}"
					align="center"><a
					href="${pageContext.request.contextPath}/admin/productManager"
					style="text-decoration:none; color:${fn:startsWith(servletPath, '/admin/productManager') ? 'white' : 'black'};">
						<i class="bi bi-book"></i> Quản lý sản phẩm
				</a></td>

				<!-- Quản lý đánh giá -->
				<td
					bgcolor="${fn:startsWith(servletPath, '/admin/reviewManager') ? '#0d6efd' : ''}"
					align="center"><a
					href="${pageContext.request.contextPath}/admin/reviewManager"
					style="text-decoration:none; color:${fn:startsWith(servletPath, '/admin/reviewManager') ? 'white' : 'black'};">
						<i class="bi bi-star"></i> Quản lý đánh giá
				</a></td>

				<!-- Quản lý đơn hàng -->
				<td
					bgcolor="${fn:startsWith(servletPath, '/admin/orderManager') ? '#0d6efd' : ''}"
					align="center"><a
					href="${pageContext.request.contextPath}/admin/orderManager"
					style="text-decoration:none; color:${fn:startsWith(servletPath, '/admin/orderManager') ? 'white' : 'black'};">
						<i class="bi bi-inboxes"></i> Quản lý đơn hàng
				</a></td>
			</tr>
		</table>



	</div>
</nav>