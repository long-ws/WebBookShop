<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<footer class="section-footer">
	<section class="footer-top py-2 bg-light">
		<div class="container">
			<div class="row">
				<aside class="col-sm-6 col-lg-3">
					<h6 class="pb-1 mb-1">Giới thiệu</h6>
					<ul class="list-unstyled mb-0">
						<li class="mb-1"><a href="#"> Về Shop </a></li>
						<li class="mb-1"><a href="#"> Tuyển dụng </a></li>
						<li class="mb-1"><a href="#"> Chính sách thanh toán </a></li>
						<li class="mb-1"><a href="#"> Chính sách bảo mật </a></li>
						<li class="mb-1"><a href="#"> Giải quyết khiếu nại </a></li>
						<li class="mb-1"><a href="#"> Hợp tác </a></li>
					</ul>
				</aside>
				<aside class="col-sm-6 col-lg-3">
					<h6 class="pb-1 mb-1">Hỗ trợ khách hàng</h6>
					<ul class="list-unstyled mb-0">
						<li class="mb-1">Hotline: 1900-80xx</li>
						<li class="mb-1"><a href="#"> Câu hỏi thường gặp </a></li>
						<li class="mb-1"><a href="#"> Hướng dẫn đặt hàng </a></li>
						<li class="mb-1"><a href="#"> Phương thức vận chuyển </a></li>
						<li class="mb-1"><a href="#"> Chính sách đổi trả </a></li>
					</ul>
				</aside>
			</div>
		</div>
	</section>
	<section class="footer-bottom text-center bg-light border-top py-1">
		<div class="container-fluid">© 2025 — Shop Bán Sách</div>
	</section>
</footer>

<c:if test="${not empty sessionScope.successMessage}">
	<div class="position-fixed bottom-0 end-0 p-3" style="z-index: 9999">
		<div class="toast align-items-center text-bg-success border-0 show"
			role="alert" aria-live="assertive" aria-atomic="true">
			<div class="d-flex">
				<div class="toast-body">${fn:escapeXml(sessionScope.successMessage)}</div>
				<button type="button" class="btn-close btn-close-white me-2 m-auto"
					data-bs-dismiss="toast" aria-label="Close"></button>
			</div>
		</div>
	</div>
	<c:remove var="successMessage" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.errorMessage}">
	<div class="position-fixed bottom-0 end-0 p-3" style="z-index: 9999">
		<div class="toast align-items-center text-bg-danger border-0 show"
			role="alert" aria-live="assertive" aria-atomic="true">
			<div class="d-flex">
				<div class="toast-body">${fn:escapeXml(sessionScope.errorMessage)}</div>
				<button type="button" class="btn-close btn-close-white me-2 m-auto"
					data-bs-dismiss="toast" aria-label="Close"></button>
			</div>
		</div>
	</div>
	<c:remove var="errorMessage" scope="session" />
</c:if>

<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
	document.addEventListener('DOMContentLoaded', function() {
		var toastElList = [].slice.call(document.querySelectorAll('.toast'));
		var toastList = toastElList.map(function(toastEl) {
			return new bootstrap.Toast(toastEl, {
				delay : 3000
			});
		});
		toastList.forEach(function(toast) {
			toast.show();
		});
	});
</script>
