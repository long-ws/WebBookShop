<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>403 - Truy cập bị từ chối</title>
</head>

<body>
	<jsp:include page="_headerAdmin.jsp" />

	<section class="section-content padding-y">
		<div class="container">
			<div class="card">
				<div class="card-body text-center py-5">
					<i class="bi bi-exclamation-triangle-fill text-warning" style="font-size: 5rem;"></i>
					<h2 class="mt-3">403 - Truy cập bị từ chối</h2>
					<p class="text-muted">Bạn không có quyền truy cập chức năng này. Vui lòng liên hệ quản trị viên nếu bạn cho rằng đây là lỗi.</p>
					<a href="${pageContext.request.contextPath}/admin" class="btn btn-primary mt-3">
						<i class="bi bi-house"></i> Quay về trang chủ
					</a>
				</div>
			</div>
		</div>
	</section>

	<jsp:include page="_footerAdmin.jsp" />
</body>

</html>
