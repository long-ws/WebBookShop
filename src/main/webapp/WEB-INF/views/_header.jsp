<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<link rel="stylesheet" href="${contextPath}/css/_headerSearchAutocomplete.css">

<!-- Header -->
<header class="section-header">
	<section class="header-main border-bottom">
		<div class="container">
			<div class="row align-items-center">
				<!-- Logo -->
				<div class="col-lg-3 py-3">
					<h3 class="m-0">
						<a class="text-body text-decoration-none" href="${contextPath}/">
							<i class="bi bi-house"></i> Shop Bán Sách
						</a>
					</h3>
				</div>

				<!-- Search -->
				<div
					class="col-lg-4 col-xl-5 ${empty sessionScope.currentUser ? 'mb-3 mb-lg-0' : ''}">
					<div class="search-autocomplete-wrapper">
						<form id="headerSearchForm" action="${contextPath}/search"
							method="post">
							<div class="input-group w-100">
								<input type="text" id="headerSearchInput" class="form-control"
									placeholder="Nhập từ khóa cần tìm ..." name="q"
									value="${requestScope.query}" autocomplete="off">
								<button class="btn btn-primary" type="submit">
									<i class="bi bi-search"></i>
								</button>
							</div>
						</form>
						<div id="searchDropdown" class="search-autocomplete-dropdown"></div>
					</div>
					<div class="text-end mt-1">
						<a href="${contextPath}/advancedSearch"
							class="small text-muted text-decoration-none">Tìm kiếm nâng
							cao</a>
					</div>
				</div>

				<!-- User / Cart -->
				<div class="col-lg-5 col-xl-4">
					<c:if test="${not empty sessionScope.currentUser}">
						<ul
							class="nav col-12 col-lg-auto my-2 my-lg-0 justify-content-center justify-content-lg-end text-small">
							<li><a href="${contextPath}/user" class="nav-link text-body"><i
									class="bi bi-person d-block text-center fs-3"></i> Tài khoản</a></li>
							<li><a href="${contextPath}/order"
								class="nav-link text-body"><i
									class="bi bi-list-check d-block text-center fs-3"></i> Đơn hàng</a></li>
							<li><a href="${contextPath}/cart"
								class="nav-link text-body position-relative"> <span
									id="total-cart-items-quantity"
									class="position-absolute top-0 end-0 mt-2 badge rounded-pill bg-primary">...</span>
									<i
									class="bi bi-cart d-block text-center fs-3 position-relative"></i>
									Giỏ hàng
							</a></li>
						</ul>
					</c:if>
				</div>

			</div>
		</div>
	</section>
</header>

<!-- Navbar / Danh mục -->
<nav class="navbar navbar-light border-bottom">
	<div class="container d-flex align-items-center">

		<!-- Menu Danh mục -->
		<details style="position: relative;">
			<summary
				style="cursor: pointer; padding: 5px 10px; background-color: #f8f9fa; border: 1px solid #ddd; border-radius: 4px;">
				<i class="bi bi-list"></i> Danh mục sản phẩm
			</summary>

			<ul
				style="position: absolute; top: 100%; left: 0; background-color: #e9ecef; border: 1px solid #ccc; list-style: none; padding: 10px; margin: 0; min-width: 200px; z-index: 100;">
				<c:forEach var="cat" items="${requestScope.categories}">
					<li><a class="dropdown-item"
						href="${contextPath}/category?id=${cat.id}">${cat.name}</a></li>
				</c:forEach>
			</ul>
		</details>

		<!-- Nút login / logout -->
		<div class="ms-auto">
			<c:choose>
				<c:when test="${not empty sessionScope.currentUser}">
					<span>Xin chào <strong>${sessionScope.currentUser.profile != null ? sessionScope.currentUser.profile.fullname : sessionScope.currentUser.username}</strong>!
					</span>
					<a class="btn btn-light ms-2" href="${contextPath}/signout">Đăng
						xuất</a>
				</c:when>
				<c:otherwise>
					<a class="btn btn-light me-2" href="${contextPath}/signup">Đăng
						ký</a>
					<a class="btn btn-primary" href="${contextPath}/signin">Đăng
						nhập</a>
				</c:otherwise>
			</c:choose>
		</div>

	</div>
</nav>

<script>
	var CONTEXT_PATH = '<c:out value="${contextPath}"/>';
</script>
<script src="${contextPath}/js/search-autocomplete.js"></script>
