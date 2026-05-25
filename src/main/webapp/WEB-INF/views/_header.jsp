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
									class="position-absolute top-0 end-0 mt-2 badge rounded-pill ${sessionScope.cartCount > 0 ? 'bg-primary' : 'bg-secondary'}">${sessionScope.cartCount > 0 ? (sessionScope.cartCount > 99 ? '99+' : sessionScope.cartCount) : ''}</span>
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

	function updateCartBadge(cartCount, animate) {
		var badge = document.getElementById('total-cart-items-quantity');
		if (badge) {
			cartCount = parseInt(cartCount) || 0;

			if (animate !== false) {
				badge.classList.add('badge-updating');
			}

			var displayCount = cartCount > 99 ? '99+' : cartCount;
			badge.textContent = cartCount > 0 ? displayCount : '';

			if (cartCount > 0) {
				badge.className = 'position-absolute top-0 end-0 mt-2 badge rounded-pill bg-primary';
			} else {
				badge.className = 'position-absolute top-0 end-0 mt-2 badge rounded-pill bg-secondary';
			}

			if (animate !== false) {
				setTimeout(function() {
					badge.classList.remove('badge-updating');
				}, 300);
			}
		}
	}

	function refreshCartBadge(async) {
		async = (async !== false);
		try {
			var xhr = new XMLHttpRequest();
			xhr.open('GET', CONTEXT_PATH + '/cartItem?action=getCartBadge', async);
			xhr.onreadystatechange = function() {
				if (xhr.readyState === 4 && xhr.status === 200) {
					try {
						var data = JSON.parse(xhr.responseText);
						updateCartBadge(data.cartCount || 0);
					} catch (e) {}
				}
			};
			xhr.send(null);
		} catch (e) {}
	}

	function hideMiniCart() {
		var popup = document.getElementById('mini-cart-popup');
		if (popup) popup.remove();
	}

	function showMiniCart(productName, productPrice, quantity, cartCount) {
		updateCartBadge(cartCount);

		var existing = document.getElementById('mini-cart-popup');
		if (existing) existing.remove();

		var popup = document.createElement('div');
		popup.id = 'mini-cart-popup';
		var priceFormatted = new Intl.NumberFormat('vi-VN').format(productPrice);
		popup.innerHTML = '<div class="mini-cart-overlay" onclick="hideMiniCart()"></div>' +
			'<div class="mini-cart-content">' +
				'<div class="mini-cart-header">' +
					'<h6 class="mb-0"><i class="bi bi-cart-check"></i> Đã thêm vào giỏ hàng!</h6>' +
					'<button type="button" class="btn-close btn-close-white" onclick="hideMiniCart()"></button>' +
				'</div>' +
				'<div class="mini-cart-body">' +
					'<div class="d-flex align-items-center gap-3 p-3 bg-light rounded mx-3 mt-3">' +
						'<div class="flex-grow-1">' +
							'<div class="fw-medium">' + productName + '</div>' +
							'<div class="text-muted small">' + priceFormatted + ' x ' + quantity + '</div>' +
						'</div>' +
					'</div>' +
					'<div class="p-3 border-top mx-3">' +
						'<div class="d-flex justify-content-between mb-2">' +
							'<span>Tổng số giỏ hàng:</span>' +
							'<span class="badge bg-primary">' + cartCount + ' sản phẩm</span>' +
						'</div>' +
					'</div>' +
				'</div>' +
				'<div class="mini-cart-footer">' +
					'<a href="' + CONTEXT_PATH + '/cart" class="btn btn-outline-secondary btn-sm flex-grow-1">' +
						'<i class="bi bi-cart3"></i> Xem giỏ hàng' +
					'</a>' +
					'<button onclick="hideMiniCart()" class="btn btn-primary btn-sm flex-grow-1">' +
						'<i class="bi bi-bag-plus"></i> Tiếp tục mua' +
					'</button>' +
				'</div>' +
			'</div>';
		popup.style.cssText = 'position: fixed; top: 0; left: 0; right: 0; bottom: 0; z-index: 9999;';

		var style = document.createElement('style');
		style.textContent = '.mini-cart-overlay{position:absolute;top:0;left:0;right:0;bottom:0;background:rgba(0,0,0,0.3);}' +
			'.mini-cart-content{position:absolute;top:50%;left:50%;transform:translate(-50%,-50%);background:white;border-radius:12px;width:380px;max-width:90vw;box-shadow:0 10px 40px rgba(0,0,0,0.2);animation:miniCartSlideIn 0.3s ease-out;}' +
			'@keyframes miniCartSlideIn{from{opacity:0;transform:translate(-50%,-45%);}to{opacity:1;transform:translate(-50%,-50%);}}' +
			'.mini-cart-header{display:flex;justify-content:space-between;align-items:center;padding:16px 20px;border-bottom:1px solid #eee;background:linear-gradient(135deg,#28a745 0%,#20c997 100%);color:white;border-radius:12px 12px 0 0;}' +
			'.mini-cart-body{padding:0;}' +
			'.mini-cart-footer{display:flex;gap:10px;padding:16px 20px;border-top:1px solid #eee;border-radius:0 0 12px 12px;}';
		document.head.appendChild(style);
		document.body.appendChild(popup);

		document.addEventListener('keydown', function closeHandler(e) {
			if (e.key === 'Escape') {
				hideMiniCart();
				document.removeEventListener('keydown', closeHandler);
			}
		});
	}

	window.updateCartBadge = updateCartBadge;
	window.refreshCartBadge = refreshCartBadge;
	window.showMiniCart = showMiniCart;
	window.hideMiniCart = hideMiniCart;
	document.addEventListener('DOMContentLoaded', function() {
		refreshCartBadge();
	});
</script>
<style>
.badge-updating { animation: badgePulse 0.3s ease-out; }
@keyframes badgePulse {
	0% { transform: scale(1); }
	50% { transform: scale(1.3); }
	100% { transform: scale(1); }
}
</style>
<script src="${contextPath}/js/search-autocomplete.js"></script>
