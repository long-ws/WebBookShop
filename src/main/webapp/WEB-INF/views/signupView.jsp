<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="_paramKeys.jsp" %>
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Đăng ký</title>
</head>

<body>
	<jsp:include page="_header.jsp" />

	<section class="section-content" style="margin: 100px 0;">
		<div class="card mx-auto" style="max-width: 380px">
			<div class="card-body">
				<c:if test="${not empty requestScope.successMessage}">
					<div class="alert alert-success" role="alert">${requestScope.successMessage}</div>
				</c:if>
				<c:if test="${not empty requestScope.errorMessage}">
					<div class="alert alert-danger" role="alert">${requestScope.errorMessage}</div>
				</c:if>
				<h4 class="card-title mb-4">Đăng ký</h4>
				<form action="${pageContext.request.contextPath}/signup"
					method="post">
					<div class="mb-3">
						<label for="inputUsername" class="form-label">Tên đăng
							nhập</label> <input type="text"
							class="form-control ${not empty requestScope[ATTR_ERRORS][P_USERNAME] ? 'is-invalid' : (not empty requestScope[ATTR_VALUES][P_USERNAME] ? 'is-valid' : '')}"
							id="inputUsername" name="${P_USERNAME}"
							value="${requestScope[ATTR_VALUES][P_USERNAME]}">
						<c:if test="${not empty requestScope[ATTR_ERRORS][P_USERNAME]}">
							<div class="invalid-feedback">
								${requestScope[ATTR_ERRORS][P_USERNAME]}
							</div>
						</c:if>
					</div>
					<div class="mb-3">
						<label for="inputPassword" class="form-label">Mật khẩu</label> <input
							type="password"
							class="form-control ${not empty requestScope[ATTR_ERRORS][P_PASSWORD] ? 'is-invalid' : ''}"
							id="inputPassword" name="${P_PASSWORD}"
							value="">
						<c:if test="${not empty requestScope[ATTR_ERRORS][P_PASSWORD]}">
							<div class="invalid-feedback">
								${requestScope[ATTR_ERRORS][P_PASSWORD]}
							</div>
						</c:if>
					</div>
					<div class="mb-3">
						<label for="inputFullname" class="form-label">Họ và tên</label> <input
							type="text"
							class="form-control ${not empty requestScope[ATTR_ERRORS][P_FULLNAME] ? 'is-invalid' : (not empty requestScope[ATTR_VALUES][P_FULLNAME] ? 'is-valid' : '')}"
							id="inputFullname" name="${P_FULLNAME}"
							value="${requestScope[ATTR_VALUES][P_FULLNAME]}">
						<c:if test="${not empty requestScope[ATTR_ERRORS][P_FULLNAME]}">
							<div class="invalid-feedback">
								${requestScope[ATTR_ERRORS][P_FULLNAME]}
							</div>
						</c:if>
					</div>
					<div class="mb-3">
						<label for="inputEmail" class="form-label">Email</label> <input
							type="email"
							class="form-control ${not empty requestScope[ATTR_ERRORS][P_EMAIL] ? 'is-invalid' : (not empty requestScope[ATTR_VALUES][P_EMAIL] ? 'is-valid' : '')}"
							id="inputEmail" name="${P_EMAIL}" value="${requestScope[ATTR_VALUES][P_EMAIL]}">
						<c:if test="${not empty requestScope[ATTR_ERRORS][P_EMAIL]}">
							<div class="invalid-feedback">
								${requestScope[ATTR_ERRORS][P_EMAIL]}
							</div>
						</c:if>
					</div>
					<button type="submit" class="btn btn-primary w-100">Đăng
						ký</button>
				</form>
			</div>
			<!-- card-body.// -->
		</div>
		<!-- card.// -->
		<p class="text-center mt-4">
			Đã có tài khoản? <a href="${pageContext.request.contextPath}/signin">Đăng
				nhập</a>
		</p>
	</section>
	<!-- section-content.// -->

	<jsp:include page="_footer.jsp" />
</body>

</html>
