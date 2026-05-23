<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ include file="_paramKeys.jsp" %>

<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Trang chủ Admin</title>
</head>

<body class="d-flex flex-column min-vh-100">
	<jsp:include page="_headerAdmin.jsp" />

	<main class="flex-fill">
		<section class="section-content padding-y">
			<div class="container">
				<div class="card bg-light">
					<div class="card-body p-5">
						<h1 class="display-5 mb-5">Quản lý Shop Bán Sách</h1>
						<div class="row">
							<c:if test="${canViewUsers}">
								<div class="col-6 col-lg-3">
									<figure class="card">
										<div class="p-3">
											<h4 class="title">${requestScope[ATTR_TOTAL_USERS]}</h4>
											<span>người dùng</span>
										</div>
									</figure>
								</div>
							</c:if>
							<c:if test="${canViewCategories}">
							<div class="col-6 col-lg-3">
								<figure class="card">
									<div class="p-3">
										<h4 class="title">${requestScope[ATTR_TOTAL_CATEGORIES]}</h4>
										<span>thể loại sách</span>
									</div>
								</figure>
							</div>
							</c:if>
							<c:if test="${canViewProducts}">
							<div class="col-6 col-lg-3">
								<figure class="card">
									<div class="p-3">
										<h4 class="title">${requestScope[ATTR_TOTAL_PRODUCTS]}</h4>
										<span>sách</span>
									</div>
								</figure>
							</div>
							</c:if>
							<c:if test="${canViewOrders}">
							<div class="col-6 col-lg-3">
								<figure class="card">
									<div class="p-3">
										<h4 class="title">${requestScope[ATTR_TOTAL_ORDERS]}</h4>
										<span>đơn hàng</span>
									</div>
								</figure>
							</div>
							</c:if>
						</div>
					</div>
				</div>
				<!-- card.// -->
			</div>
			<!-- container.// -->
		</section>
	</main>

	<jsp:include page="_footerAdmin.jsp" />
</body>


</html>