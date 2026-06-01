<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Thêm thể loại</title>
</head>

<body>
	<jsp:include page="_headerAdmin.jsp" />

	<section class="section-content">
		<div class="container">
			<header class="section-heading py-4">
				<h3 class="section-title">Thêm thể loại</h3>
			</header>

			<main class="row mb-5">
				<form class="col-lg-6" method="POST"
					action="${pageContext.request.contextPath}/admin/categoryManager/create"
					enctype="multipart/form-data">
					<!-- Hiển thị thông báo thành công -->
					<c:if test="${not empty requestScope.successMessage}">
						<div class="alert alert-success mb-3">${requestScope.successMessage}</div>
					</c:if>

					<!-- Hiển thị thông báo lỗi -->
					<c:if test="${not empty requestScope.errorMessage}">
						<div class="alert alert-danger mb-3">${requestScope.errorMessage}</div>
					</c:if>

					<!-- Tên thể loại -->
					<div class="mb-3">
						<label for="category-name" class="form-label">Tên thể loại
							<span class="text-danger">*</span>
						</label> <input type="text"
							class="form-control
                               ${not empty requestScope.violations.nameViolations ? 'is-invalid' : (not empty requestScope.category.name ? 'is-valid' : '')}"
							id="category-name" name="name"
							value="${requestScope.category.name}" required>
						<c:if test="${not empty requestScope.violations.nameViolations}">
							<div class="invalid-feedback">
								<ul class="list-unstyled">
									<c:forEach var="violation"
										items="${requestScope.violations.nameViolations}">
										<li>${violation}</li>
									</c:forEach>
								</ul>
							</div>
						</c:if>
					</div>

					<!-- Mô tả thể loại -->
					<div class="mb-3">
						<label for="category-description" class="form-label">Mô tả
							thể loại</label>
						<textarea
							class="form-control
                                  ${not empty requestScope.violations.descriptionViolations ? 'is-invalid' : (not empty requestScope.category.description ? 'is-valid' : '')}"
							id="category-description" rows="5" name="description">${requestScope.category.description}</textarea>
						<c:if
							test="${not empty requestScope.violations.descriptionViolations}">
							<div class="invalid-feedback">
								<ul class="list-unstyled">
									<c:forEach var="violation"
										items="${requestScope.violations.descriptionViolations}">
										<li>${violation}</li>
									</c:forEach>
								</ul>
							</div>
						</c:if>
					</div>

					<!-- Hình ảnh -->
					<div class="mb-3">
						<label for="category-imageName" class="form-label">Hình
							thể loại</label> <input type="file" class="form-control"
							id="category-imageName" name="image" accept="image/*">
					</div>

					<!-- Nút thao tác -->
					<div class="mb-3 d-flex gap-2">
						<button type="submit" class="btn btn-primary">Thêm</button>
						<button type="reset" class="btn btn-warning">Mặc định</button>
						<a class="btn btn-danger"
							href="${pageContext.request.contextPath}/admin/categoryManager">Hủy</a>
					</div>
				</form>
			</main>
		</div>
	</section>

	<jsp:include page="_footerAdmin.jsp" />
</body>

</html>
