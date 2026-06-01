<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Quản lý sản phẩm</title>
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
				<h3 class="section-title">Quản lý sản phẩm</h3>
				<a class="btn btn-primary"
					href="${pageContext.request.contextPath}/admin/productManager/create"
					role="button" style="height: fit-content;"> Thêm sản phẩm </a>
			</header>
			<!-- section-heading.// -->

			<main class="table-responsive-xl mb-5">
				<table
					class="table table-bordered table-striped table-hover align-middle">
					<thead>
						<tr>
							<th scope="col">#</th>
							<th scope="col">ID</th>
							<th scope="col">Hình</th>
							<th scope="col">Tên sản phẩm</th>
							<th scope="col">Giá gốc</th>
							<th scope="col">Khuyến mãi</th>
							<th scope="col">Giá bán</th>
							<th scope="col">Tồn kho</th>
							<th scope="col">Lượt mua</th>
							<th scope="col" style="width: 225px;">Thao tác</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="product" varStatus="loop"
							items="${requestScope.products}">
							<tr>
								<th scope="row">${loop.index + 1}</th>
								<td>${product.id}</td>
								<td class="text-center"><c:choose>
										<c:when test="${empty product.imageName}">
											<img width="38"
												src="${pageContext.request.contextPath}/img/280px.png"
												alt="280px.png">
										</c:when>
										<c:otherwise>
											<img width="38"
												src="${pageContext.request.contextPath}/image/${product.imageName}"
												alt="${product.imageName}">
										</c:otherwise>
									</c:choose></td>
								<td><a
									href="${pageContext.request.contextPath}/product?id=${product.id}"
									target="_blank">${product.name}</a></td>
								<td><fmt:formatNumber pattern="#,##0"
										value="${product.price}" />₫</td>
								<td><fmt:formatNumber pattern="#,##0"
										value="${product.discount}" />%</td>
								<td><c:choose>
										<c:when test="${product.discount == 0}">
											<fmt:formatNumber pattern="#,##0" value="${product.price}" />₫
													</c:when>
										<c:otherwise>
											<fmt:formatNumber pattern="#,##0"
												value="${product.price * (100 - product.discount) / 100}" />
														₫
													</c:otherwise>
									</c:choose></td>
								<td>${product.quantity}</td>
								<td>${product.totalBuy}</td>
								<!-- Nút thao tác -->
								<td class="text-center text-nowrap">
									<!-- Nút Xem --> <a class="btn btn-primary me-2"
									href="${pageContext.request.contextPath}/admin/productManager/detail?id=${product.id}"
									role="button">Xem</a> <!-- Nút Sửa --> <a
									class="btn btn-success me-2"
									href="${pageContext.request.contextPath}/admin/productManager/update?id=${product.id}"
									role="button">Sửa</a> <!-- Bước 1: Nút Xóa --> <c:set var="pid"
										value="${product.id}" /> <c:if
										test="${param.confirmId != pid}">
										<form
											action="${pageContext.request.contextPath}/admin/productManager"
											method="get" style="display: inline">
											<input type="hidden" name="confirmId" value="${product.id}" />
											<input type="hidden" name="page" value="${requestScope.page}" />
											<button type="submit" class="btn btn-danger">Xóa</button>
										</form>

									</c:if> <!-- Bước 2: Xác nhận --> <c:if
										test="${param.confirmId == pid}">
										<span class="badge bg-warning text-dark">Bạn có chắc
											muốn xóa?</span>
										<form
											action="${pageContext.request.contextPath}/admin/productManager"
											method="post" style="display: inline">
											<input type="hidden" name="id" value="${product.id}" /> <input
												type="hidden" name="action" value="delete" /> <input
												type="hidden" name="page" value="${requestScope.page}" />
											<button type="submit" class="btn btn-danger btn-sm">Xác
												nhận</button>
										</form>

										<a class="btn btn-secondary btn-sm"
											href="${pageContext.request.contextPath}/admin/productManager">Hủy</a>
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
					href="${pageContext.request.contextPath}/admin/productManager?page=${requestScope.page - 1}">
						Trang trước </a></li>

				<c:forEach begin="1" end="${requestScope.totalPages}" var="i">
					<c:choose>
						<c:when test="${requestScope.page == i}">
							<li class="page-item active"><a class="page-link">${i}</a></li>
						</c:when>
						<c:otherwise>
							<li class="page-item"><a class="page-link"
								href="${pageContext.request.contextPath}/admin/productManager?page=${i}">
									${i} </a></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>

				<li
					class="page-item ${requestScope.page == requestScope.totalPages ? 'disabled' : ''}"><a
					class="page-link"
					href="${pageContext.request.contextPath}/admin/productManager?page=${requestScope.page + 1}">
						Trang sau </a></li>
			</ul>
		</nav>
	</c:if>

	<jsp:include page="_footerAdmin.jsp" />
</body>

</html>