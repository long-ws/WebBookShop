<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<aside class="col-md-3 mb-md-0 mb-3">
	<nav class="list-group">
		<a class="list-group-item ${param.active == 'USER' ? 'active' : ''}"
			href="${pageContext.request.contextPath}/user" role="button">
			<i class="bi bi-person me-2"></i>Thông tin tài khoản
		</a>
		<a class="list-group-item ${param.active == 'ORDER' ? 'active' : ''}"
			href="${pageContext.request.contextPath}/order" role="button">
			<i class="bi bi-bag me-2"></i>Đơn hàng của tôi
		</a>
        <a class="list-group-item ${param.active == 'VOUCHER' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/vouchers" role="button">
            <i class="bi bi-bag me-2"></i>Voucher của tôi
        </a>
		<a class="list-group-item ${param.active == 'WISHLIST' ? 'active' : ''}"
			href="${pageContext.request.contextPath}/wishlist">
			<i class="bi bi-heart me-2"></i>Sản phẩm yêu thích
		</a>
		<a class="list-group-item ${param.active == 'SECURITY' ? 'active' : ''}"
			href="${pageContext.request.contextPath}/security" role="button">
			<i class="bi bi-shield-lock me-2"></i>Bảo mật
		</a>
		<a class="list-group-item"
			href="${pageContext.request.contextPath}/signout" role="button">
			<i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
		</a>
	</nav>
</aside>
