<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="_paramKeys.jsp"%>
<!DOCTYPE html>
<html lang="vi">

<head>
	<jsp:include page="_meta.jsp" />
	<title>Xác thực email</title>
</head>

<body class="d-flex flex-column min-vh-100 bg-light">
	<jsp:include page="_header.jsp" />

	<main class="flex-fill d-flex align-items-center py-5">
		<section class="container">
			<div class="row">
				<div class="col-10 col-sm-8 col-md-6 col-lg-5 mx-auto">

					<c:if test="${not empty sessionScope[SSN_SUCCESS_MESSAGE]}">
						<div class="alert alert-success alert-dismissible fade show shadow-sm mb-3" role="alert">
							<i class="bi bi-check-circle-fill me-2"></i>${sessionScope[SSN_SUCCESS_MESSAGE]}
							<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
						</div>
						<c:remove var="successMessage" scope="session" />
					</c:if>

					<c:if test="${not empty sessionScope[SSN_ERROR_MESSAGE]}">
						<div class="alert alert-danger alert-dismissible fade show shadow-sm mb-3" role="alert">
							<i class="bi bi-exclamation-triangle-fill me-2"></i>${sessionScope[SSN_ERROR_MESSAGE]}
							<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
						</div>
						<c:remove var="errorMessage" scope="session" />
					</c:if>

					<div class="card shadow-sm border-0 rounded-3">
						<div class="card-body p-4">
							<h4 class="card-title fw-bold text-center mb-3 text-dark">Xác thực email</h4>
							<p class="text-muted mb-4">
								Vui lòng kiểm tra hộp thư của bạn và nhấn vào liên kết xác thực được gửi tới:
								<strong>${requestScope.email}</strong>
							</p>

							<form action="${pageContext.request.contextPath}/verify-email/resend" method="post" class="mb-3">
								<button type="submit" class="btn btn-outline-primary w-100 py-2 shadow-sm fw-semibold">
									<i class="bi bi-envelope-arrow-up me-1"></i> Gửi lại email xác thực
								</button>
							</form>
						</div>
					</div>

				</div>
			</div>
		</section>
	</main>

	<jsp:include page="_footer.jsp" />
</body>

</html>
