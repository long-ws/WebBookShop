<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Quản lý người dùng</title>
</head>

<body class="d-flex flex-column min-vh-100">
	<jsp:include page="_headerAdmin.jsp" />

	<section class="section-content flex-fill">
		<div class="container">
			<c:if test="${not empty sessionScope.successMessage}">
				<div class="alert alert-success mb-0 mt-4" role="alert">${sessionScope.successMessage}</div>
			</c:if>
			<c:if test="${not empty sessionScope.errorMessage}">
				<div class="alert alert-danger mb-0 mt-4" role="alert">${sessionScope.errorMessage}</div>
			</c:if>
			<c:remove var="successMessage" scope="session" />
			<c:remove var="errorMessage" scope="session" />

			<header class="section-heading py-4 d-flex justify-content-between">
				<h3 class="section-title">Quản lý người dùng</h3>
				<a class="btn btn-primary"
					href="${pageContext.request.contextPath}/admin/userManager/create"
					role="button" style="height: fit-content;"> Thêm người dùng </a>
			</header>
			<!-- section-heading.// -->

			<main class="table-responsive-xl mb-5">
				<table
					class="table table-bordered table-striped table-hover align-middle">
					<thead>
						<tr>
							<th scope="col">#</th>
							<th scope="col">ID</th>
							<th scope="col">Tên đăng nhập</th>
							<th scope="col">Họ và tên</th>
							<th scope="col">Email</th>
							<th scope="col">Số điện thoại</th>
							<th scope="col">Giới tính</th>
							<th scope="col">Quyền</th>
							<th scope="col" style="width: 225px;">Thao tác</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="user" varStatus="loop"
							items="${requestScope.users}">
							<tr>
								<th scope="row">${loop.index + 1}</th>
								<td>${user.id}</td>
								<td>${user.username}</td>
								<td>${user.fullname}</td>
								<td>${user.email}</td>
								<td>${user.phoneNumber}</td>
								<td>${user.gender == 0 ? 'Nam' : 'Nữ'}</td>
								<td><c:choose>
										<c:when test="${user.role == 'ADMIN'}">Quản trị viên</c:when>
										<c:when test="${user.role == 'EMPLOYEE'}">Nhân viên</c:when>
										<c:when test="${user.role == 'CUSTOMER'}">Khách hàng</c:when>
									</c:choose></td>
								<td class="text-center text-nowrap">
									<!-- Nút Xem --> <a class="btn btn-primary me-2"
									href="${pageContext.request.contextPath}/admin/userManager/detail?id=${user.id}"
									role="button">Xem</a> <!-- Nút Sửa --> <a
									class="btn btn-success me-2"
									href="${pageContext.request.contextPath}/admin/userManager/update?id=${user.id}"
									role="button">Sửa</a> <!-- Bước 1: Nút Xóa --> <c:if
										test="${param.confirmId ne user.id}">
										<form
											action="${pageContext.request.contextPath}/admin/userManager"
											method="get" style="display: inline">
											<input type="hidden" name="confirmId" value="${user.id}" />
											<input type="hidden" name="page" value="${requestScope.page}" />
											<button type="submit" class="btn btn-danger">Xóa</button>
										</form>

									</c:if> <!-- Bước 2: Xác nhận --> <c:if
										test="${param.confirmId eq user.id}">
										<span class="badge bg-warning text-dark">Bạn có chắc
											muốn xóa?</span>
										<form
											action="${pageContext.request.contextPath}/admin/userManager"
											method="post" style="display: inline">
											<input type="hidden" name="action" value="delete" /> <input
												type="hidden" name="id" value="${user.id}" /> <input
												type="hidden" name="page" value="${requestScope.page}" />
											<button type="submit" class="btn btn-danger btn-sm">Xác
												nhận</button>
										</form>

										<a class="btn btn-secondary btn-sm"
											href="${pageContext.request.contextPath}/admin/userManager">Hủy</a>
									</c:if>
								</td>

							</tr>
						</c:forEach>
					</tbody>
				</table>
			</main>


		</div>
		<!-- container.// -->
	</section>
	<!-- section-content.// -->

	<c:if test="${requestScope.totalPages != 0}">
		<nav class="mt-3 mb-5">
			<ul class="pagination justify-content-center">
				<li class="page-item ${requestScope.page == 1 ? 'disabled' : ''}"><a
					class="page-link"
					href="${pageContext.request.contextPath}/admin/userManager?page=${requestScope.page - 1}">
						Trang trước </a></li>

				<c:forEach begin="1" end="${requestScope.totalPages}" var="i">
					<c:choose>
						<c:when test="${requestScope.page == i}">
							<li class="page-item active"><a class="page-link">${i}</a></li>
						</c:when>
						<c:otherwise>
							<li class="page-item"><a class="page-link"
								href="${pageContext.request.contextPath}/admin/userManager?page=${i}">
									${i} </a></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>

				<li
					class="page-item ${requestScope.page == requestScope.totalPages ? 'disabled' : ''}"><a
					class="page-link"
					href="${pageContext.request.contextPath}/admin/userManager?page=${requestScope.page + 1}">
						Trang sau </a></li>
			</ul>
		</nav>
	</c:if>

	<jsp:include page="_footerAdmin.jsp" />
</body>

</html>