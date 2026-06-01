<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ include file="_paramKeys.jsp" %>
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
							<c:if test="${not empty sessionScope[SSN_SUCCESS_MESSAGE]}">
								<div class="alert alert-success alert-dismissible fade show shadow-sm mb-3" role="alert">
									<i class="bi bi-check-circle-fill me-2"></i>${sessionScope[SSN_SUCCESS_MESSAGE]}
									<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
								</div>
								<c:remove var="successMessage" scope="session" />
							</c:if>
							
							<!-- Global Error -->
							<c:if test="${not empty requestScope[ATTR_ERRORS][ERR_GLOBAL]}">
								<div class="alert alert-danger alert-dismissible fade show shadow-sm mb-3" role="alert">
									<i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope[ATTR_ERRORS][ERR_GLOBAL]}
									<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
								</div>
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
											<input type="password" class="form-control ${not empty requestScope[ATTR_ERRORS][P_CURRENT_PASSWORD] ? 'is-invalid' : ''}" 
												id="currentPassword" name="${P_CURRENT_PASSWORD}" 
												value="" required>
											<c:if test="${not empty requestScope[ATTR_ERRORS][P_CURRENT_PASSWORD]}">
												<div class="invalid-feedback">
													${requestScope[ATTR_ERRORS][P_CURRENT_PASSWORD]}
												</div>
											</c:if>
										</div>
										<div class="mb-3">
											<label for="newPassword" class="form-label">Mật khẩu mới</label>
											<input type="password" class="form-control ${not empty requestScope[ATTR_ERRORS][P_NEW_PASSWORD] ? 'is-invalid' : ''}" 
												id="newPassword" name="${P_NEW_PASSWORD}" 
												value="" required>
											<c:if test="${not empty requestScope[ATTR_ERRORS][P_NEW_PASSWORD]}">
												<div class="invalid-feedback">
													${requestScope[ATTR_ERRORS][P_NEW_PASSWORD]}
												</div>
											</c:if>
										</div>
										<div class="mb-3">
											<label for="newPasswordAgain" class="form-label">Nhập lại mật khẩu mới</label>
											<input type="password" class="form-control ${not empty requestScope[ATTR_ERRORS][P_CONFIRM_PASSWORD] ? 'is-invalid' : ''}" 
												id="newPasswordAgain" name="${P_CONFIRM_PASSWORD}" 
												value="" required>
											<c:if test="${not empty requestScope[ATTR_ERRORS][P_CONFIRM_PASSWORD]}">
												<div class="invalid-feedback">
													${requestScope[ATTR_ERRORS][P_CONFIRM_PASSWORD]}
												</div>
											</c:if>
										</div>
										<button type="submit" class="btn btn-primary">
											<i class="bi bi-key me-2"></i>Đổi mật khẩu
										</button>
									</form>
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
