<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Quản lý đơn hàng</title>
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

			<header class="section-heading py-4">
				<h3 class="section-title">Quản lý đơn hàng</h3>
			</header>
			<!-- section-heading.// -->

			<main class="table-responsive-xl mb-5">
				<table
					class="table table-bordered table-striped table-hover align-middle">
					<thead>
						<tr>
							<th scope="col">#</th>
							<th scope="col">Mã</th>
							<th scope="col">Người dùng</th>
							<th scope="col">Ngày tạo</th>
							<th scope="col">Ngày cập nhật</th>
							<th scope="col">Số sản phẩm</th>
							<th scope="col">Tổng tiền</th>
							<th scope="col">Trạng thái</th>
							<th scope="col" style="width: 250px;">Thao tác</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="order" varStatus="loop"
							items="${requestScope.orders}">
							<tr>
								<th scope="row">${loop.index + 1}</th>
								<td>${order.id}</td>
								<td><a
									href="${pageContext.request.contextPath}/admin/userManager/detail?id=${order.user.id}">
										${order.user.username} </a> (${order.user.fullname})</td>
								<td>${order.createdAt.format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"))}</td>
								<td>${order.updatedAt.format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"))}</td>
								<td>${order.orderItems.size()}</td>
								<td class="text-end"><c:choose>
										<c:when test="${order.deliveryMethod == 2}">
											<i class="bi bi-truck me-1" title="Giao nhanh"></i>
										</c:when>
									</c:choose> <fmt:formatNumber pattern="#,##0" value="${order.totalPrice}" />₫</td>
								<td><c:choose>
										<c:when test="${order.status == 1}">
											<span class="badge bg-warning text-dark">Đang giao
												hàng</span>
										</c:when>
										<c:when test="${order.status == 2}">
											<span class="badge bg-success">Giao hàng thành công</span>
										</c:when>
										<c:when test="${order.status == 3}">
											<span class="badge bg-danger">Hủy đơn hàng</span>
										</c:when>
									</c:choose></td>
								<td class="text-center text-nowrap"><a
									class="btn btn-primary me-2"
									href="${pageContext.request.contextPath}/admin/orderManager/detail?id=${order.id}"
									role="button"> Xem </a>
									<button type="submit"
										class="btn ${order.status == 2 || order.status == 3 ? 'btn-secondary' : 'btn-success'} me-2"
										form="update-confirm-${order.id}"
										${order.status==2 || order.status==3 ? 'disabled' : '' }>
										<i class="bi bi-check-circle"></i>
									</button>
									<button type="submit"
										class="btn  ${order.status == 2 || order.status == 3 ? 'btn-secondary' : 'btn-danger'} me-2"
										form="update-cancel-${order.id}"
										${order.status==2 || order.status==3 ? 'disabled' : '' }>
										<i class="bi bi-x-circle"></i>
									</button>
									<button type="submit"
										class="btn  ${order.status == 1 ? 'btn-secondary' : 'btn-warning'}"
										form="update-reset-${order.id}"
										${order.status==1 ? 'disabled' : '' }>
										<i class="bi bi-arrow-clockwise"></i>
									</button>
									<form
										action="${pageContext.request.contextPath}/admin/orderManager"
										method="post" id="update-confirm-${order.id}">
										<input type="hidden" name="id" value="${order.id}"> <input
											type="hidden" name="action" value="CONFIRM"> <input
											type="hidden" name="page" value="${requestScope.page}">
									</form>

									<form
										action="${pageContext.request.contextPath}/admin/orderManager"
										method="post" id="update-cancel-${order.id}">
										<input type="hidden" name="id" value="${order.id}"> <input
											type="hidden" name="action" value="CANCEL"> <input
											type="hidden" name="page" value="${requestScope.page}">
									</form>

									<form
										action="${pageContext.request.contextPath}/admin/orderManager"
										method="post" id="update-reset-${order.id}">
										<input type="hidden" name="id" value="${order.id}"> <input
											type="hidden" name="action" value="RESET"> <input
											type="hidden" name="page" value="${requestScope.page}">
									</form></td>
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
					href="${pageContext.request.contextPath}/admin/orderManager?page=${requestScope.page - 1}">
						Trang trước </a></li>

				<c:forEach begin="1" end="${requestScope.totalPages}" var="i">
					<c:choose>
						<c:when test="${requestScope.page == i}">
							<li class="page-item active"><a class="page-link">${i}</a></li>
						</c:when>
						<c:otherwise>
							<li class="page-item"><a class="page-link"
								href="${pageContext.request.contextPath}/admin/orderManager?page=${i}">
									${i} </a></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>

				<li
					class="page-item ${requestScope.page == requestScope.totalPages ? 'disabled' : ''}"><a
					class="page-link"
					href="${pageContext.request.contextPath}/admin/orderManager?page=${requestScope.page + 1}">
						Trang sau </a></li>
			</ul>
		</nav>
	</c:if>

	<jsp:include page="_footerAdmin.jsp" />
</body>

</html>