<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Thông tin tài khoản</title>
</head>

<body>
	<jsp:include page="_header.jsp" />
	
	<section class="section-content padding-y">
		<div class="container">
			<div class="row">
				<c:choose>
					<c:when test="${empty sessionScope.currentUser}">
						<div class="col-12">
							<div class="alert alert-warning">
								Vui lòng <a href="${pageContext.request.contextPath}/signin">đăng nhập</a> để xem thông tin tài khoản.
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<jsp:include page="_navPanel.jsp">
							<jsp:param name="active" value="USER" />
						</jsp:include>

						<main class="col-md-9">
							<!-- Profile Header Card -->
							<article class="card mb-4">
								<div class="card-body">
									<c:if test="${not empty sessionScope.successMessage}">
										<div class="alert alert-success">${sessionScope.successMessage}</div>
										<c:remove var="successMessage" scope="session"/>
									</c:if>
									<c:if test="${not empty requestScope.successMessage}">
										<div class="alert alert-success">${requestScope.successMessage}</div>
									</c:if>
									<c:if test="${not empty requestScope.errorMessage}">
										<div class="alert alert-danger">${requestScope.errorMessage}</div>
									</c:if>
									
									<div class="row align-items-center">
										<div class="col-lg-3 text-center mb-3">
											<c:url var="defaultAvatarUrl" value="/images/default-avatar.svg"/>
											<c:set var="avatarSrc" value="${not empty sessionScope.currentUser.avatarUrl ? sessionScope.currentUser.avatarUrl : defaultAvatarUrl}"/>
											<img src="${avatarSrc}" alt="Avatar" class="rounded-circle" style="width: 150px; height: 150px; object-fit: cover; border: 3px solid #dee2e6;">
											<p class="small text-muted mt-2 mb-0">Ảnh đại diện</p>
										</div>
										<div class="col-lg-9">
											<h4 class="mb-1">${sessionScope.currentUser.fullname != null ? sessionScope.currentUser.fullname : sessionScope.currentUser.username}</h4>
											<p class="text-muted mb-2">
												<i class="bi bi-envelope me-2"></i>${sessionScope.currentUser.email}
											</p>
										</div>
									</div>
								</div>
							</article>

							<!-- Edit Profile Form -->
							<article class="card mb-4">
								<div class="card-header bg-white">
									<h5 class="card-title mb-0">Chỉnh sửa thông tin</h5>
								</div>
								<div class="card-body">
									<c:if test="${not empty requestScope.errors}">
										<div class="alert alert-danger">
											<ul>
												<c:forEach var="error" items="${requestScope.errors}">
													<li>${error.value}</li>
												</c:forEach>
											</ul>
										</div>
									</c:if>
									<form id="profileForm" action="${pageContext.request.contextPath}/user" method="post">
										<div class="row">
											<div class="col-md-6 mb-3">
												<label for="username" class="form-label">Tên tài khoản</label>
												<input type="text" class="form-control" id="username" name="username" 
													value="${sessionScope.currentUser.username}" readonly>
												<div class="form-text">Tên tài khoản không thể thay đổi</div>
											</div>
											<div class="col-md-6 mb-3">
												<label for="fullname" class="form-label">Họ và tên <span class="text-danger">*</span></label>
												<input type="text" class="form-control ${not empty requestScope.errors.fullname ? 'is-invalid' : ''}" id="fullname" name="fullname" 
													value="${requestScope.user != null ? requestScope.user.fullname : sessionScope.currentUser.fullname}" required>
												<c:if test="${not empty requestScope.errors.fullname}">
													<div class="invalid-feedback">${requestScope.errors.fullname}</div>
												</c:if>
											</div>
										</div>
										<div class="row">
											<div class="col-md-6 mb-3">
												<label for="email" class="form-label">Email <span class="text-danger">*</span></label>
												<input type="email" class="form-control ${not empty requestScope.errors.email ? 'is-invalid' : ''}" id="email" name="email" 
													value="${requestScope.user != null ? requestScope.user.email : sessionScope.currentUser.email}" required>
												<c:if test="${not empty requestScope.errors.email}">
													<div class="invalid-feedback">${requestScope.errors.email}</div>
												</c:if>
											</div>
											<div class="col-md-6 mb-3">
												<label for="phoneNumber" class="form-label">Số điện thoại</label>
												<input type="tel" class="form-control" id="phoneNumber" name="phoneNumber" 
													value="${requestScope.user != null ? requestScope.user.phoneNumber : sessionScope.currentUser.phoneNumber}">
											</div>
										</div>
										<div class="row">
											<div class="col-md-6 mb-3">
												<label class="form-label">Ngôn ngữ ưa thích</label>
												<select class="form-select" name="preferredLanguage">
													<option value="1" ${(sessionScope.currentUser.preferredLanguage == null or sessionScope.currentUser.preferredLanguage.id == 1) ? 'selected' : ''}>Tiếng Việt</option>
													<option value="2" ${sessionScope.currentUser.preferredLanguage != null and sessionScope.currentUser.preferredLanguage.id == 2 ? 'selected' : ''}>English</option>
												</select>
											</div>
											<div class="col-md-6 mb-3">
												<label class="form-label d-block">Giới tính</label>
												<div class="form-check form-check-inline">
													<input class="form-check-input" type="radio" name="gender" id="genderMale" value="0" 
													${sessionScope.currentUser.gender != null and sessionScope.currentUser.gender.id == 0 ? 'checked' : ''}>
													<label class="form-check-label" for="genderMale">Nam</label>
												</div>
												<div class="form-check form-check-inline">
													<input class="form-check-input" type="radio" name="gender" id="genderFemale" value="1" 
													${sessionScope.currentUser.gender != null and sessionScope.currentUser.gender.id == 1 ? 'checked' : ''}>
													<label class="form-check-label" for="genderFemale">Nữ</label>
												</div>
											</div>
										</div>
										<button type="submit" class="btn btn-primary">
											<i class="bi bi-save me-2"></i>Lưu thay đổi
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
