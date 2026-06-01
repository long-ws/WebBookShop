<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Bảo mật và thông báo</title>
</head>

<body>
	<jsp:include page="_header.jsp" />

	<section class="section-content padding-y">
		<div class="container">
			<div class="row">
				<c:choose>
					<c:when test="${empty sessionScope.currentUser}">
						<p>
							Vui lòng <a href="${pageContext.request.contextPath}/signin">đăng
								nhập</a> để sử dụng chức năng thiết đặt.
						</p>
					</c:when>
					<c:otherwise>
						<jsp:include page="_navPanel.jsp">
							<jsp:param name="active" value="SECURITY" />
						</jsp:include>

						<main class="col-md-9">
							<!-- Alert Messages -->
							<c:if test="${not empty requestScope.successMessage}">
								<div class="alert alert-success">${requestScope.successMessage}</div>
							</c:if>
							<c:if test="${not empty requestScope.errorMessage}">
								<div class="alert alert-danger">${requestScope.errorMessage}</div>
							</c:if>

							<!-- Change Password Card -->
							<article class="card mb-4">
								<div class="card-header bg-white">
									<h5 class="card-title mb-0">Đổi mật khẩu</h5>
								</div>
								<div class="card-body">
									<form action="${pageContext.request.contextPath}/security" method="post">
										<div class="mb-3">
											<label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
											<input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
										</div>
										<div class="mb-3">
											<label for="newPassword" class="form-label">Mật khẩu mới</label>
											<input type="password" class="form-control" id="newPassword" name="newPassword" required>
										</div>
										<div class="mb-3">
											<label for="newPasswordAgain" class="form-label">Nhập lại mật khẩu mới</label>
											<input type="password" class="form-control" id="newPasswordAgain" name="newPasswordAgain" required>
										</div>
										<button type="submit" class="btn btn-primary">
											<i class="bi bi-key me-2"></i>Đổi mật khẩu
										</button>
									</form>
								</div>
							</article>

							<!-- Notification Settings Card -->
							<article class="card">
								<div class="card-header bg-white">
									<h5 class="card-title mb-0">Cài đặt thông báo</h5>
								</div>
								<div class="card-body">
									<div class="form-check mb-3">
										<input class="form-check-input" type="checkbox" id="emailNotifications" checked>
										<label class="form-check-label" for="emailNotifications">
											Nhận thông báo qua email
										</label>
									</div>
									<div class="form-check mb-3">
										<input class="form-check-input" type="checkbox" id="orderNotifications" checked>
										<label class="form-check-label" for="orderNotifications">
											Thông báo về đơn hàng
										</label>
									</div>
									<div class="form-check">
										<input class="form-check-input" type="checkbox" id="promoNotifications">
										<label class="form-check-label" for="promoNotifications">
											Thông báo khuyến mãi
										</label>
									</div>
								</div>
							</article>
						</main>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</section>

	<jsp:include page="_footer.jsp" />
</body>

</html>
