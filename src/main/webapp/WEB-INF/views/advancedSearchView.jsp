<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Tìm kiếm nâng cao</title>
</head>

<body>
	<jsp:include page="_header.jsp" />

	<section class="section-pagetop bg-light" style="padding: 0.25rem 0">
		<div class="container">
			<h2 class="title-page">Tìm kiếm nâng cao</h2>
			<nav>
				<ol class="breadcrumb">
					<li class="breadcrumb-item"><a
						href="${pageContext.request.contextPath}/">Trang chủ</a></li>
					<li class="breadcrumb-item active">Tìm kiếm nâng cao</li>
				</ol>
			</nav>
		</div>
	</section>

	<section class="section-content padding-y">
		<div class="container">
			<div class="row">
				<aside class="col-md-4 col-lg-3 mb-md-0 mb-3">
					<div class="card">
						<form
							action="${pageContext.request.contextPath}/advancedSearch"
							method="get">
							<article class="filter-group">
								<header class="card-header my-1">
									<h6 class="title fw-bold">Từ khóa</h6>
								</header>
								<div class="card-body pt-0">
									<input type="text" class="form-control"
										name="q" placeholder="Tên sách, tác giả ..."
										value="${requestScope.keyword}">
								</div>
							</article>

							<article class="filter-group">
								<header class="card-header my-1">
									<h6 class="title fw-bold">Thể loại</h6>
								</header>
								<div class="card-body pt-0">
									<select class="form-select" name="categoryId">
										<option value="">-- Tất cả --</option>
										<c:forEach var="cat" items="${requestScope.categories}">
											<option value="${cat.id}"
												${requestScope.selectedCategoryId == cat.id ? 'selected' : ''}>
												${cat.name}</option>
										</c:forEach>
									</select>
								</div>
							</article>

							<article class="filter-group">
								<header class="card-header my-1">
									<h6 class="title fw-bold">Tác giả</h6>
								</header>
								<div class="card-body pt-0">
									<input type="text" class="form-control"
										name="author" placeholder="Tên tác giả"
										value="${requestScope.author}">
								</div>
							</article>

							<article class="filter-group">
								<header class="card-header my-1">
									<h6 class="title fw-bold">Nhà xuất bản</h6>
								</header>
								<div class="card-body pt-0">
									<input type="text" class="form-control"
										name="publisher" placeholder="Tên nhà xuất bản"
										value="${requestScope.publisher}">
								</div>
							</article>

							<article class="filter-group">
								<header class="card-header my-1">
									<h6 class="title fw-bold">Khoảng giá (VND)</h6>
								</header>
								<div class="card-body pt-0">
									<div class="row g-2">
										<div class="col-6">
											<input type="number" class="form-control"
												name="minPrice" placeholder="Từ"
												value="${requestScope.minPrice}" min="0">
										</div>
										<div class="col-6">
											<input type="number" class="form-control"
												name="maxPrice" placeholder="Đến"
												value="${requestScope.maxPrice}" min="0">
										</div>
									</div>
								</div>
							</article>

							<article class="filter-group">
								<header class="card-header my-1">
									<h6 class="title fw-bold">Năm xuất bản</h6>
								</header>
								<div class="card-body pt-0">
									<div class="row g-2">
										<div class="col-6">
											<input type="number" class="form-control"
												name="minYear" placeholder="Từ năm"
												value="${requestScope.minYear}" min="1900" max="2100">
										</div>
										<div class="col-6">
											<input type="number" class="form-control"
												name="maxYear" placeholder="Đến năm"
												value="${requestScope.maxYear}" min="1900" max="2100">
										</div>
									</div>
								</div>
							</article>

							<article class="filter-group">
								<header class="card-header my-1">
									<h6 class="title fw-bold">Sắp xếp theo</h6>
								</header>
								<div class="card-body pt-0">
									<select class="form-select" name="sort">
										<option value="totalBuy-DESC"
											${requestScope.sort == 'totalBuy-DESC' ? 'selected' : ''}>
											Bán chạy nhất</option>
										<option value="price-ASC"
											${requestScope.sort == 'price-ASC' ? 'selected' : ''}>
											Giá thấp đến cao</option>
										<option value="price-DESC"
											${requestScope.sort == 'price-DESC' ? 'selected' : ''}>
											Giá cao đến thấp</option>
										<option value="createdAt-DESC"
											${requestScope.sort == 'createdAt-DESC' ? 'selected' : ''}>
											Mới nhất</option>
										<option value="name-ASC"
											${requestScope.sort == 'name-ASC' ? 'selected' : ''}>
											Tên A - Z</option>
									</select>
								</div>
							</article>

							<article class="card-body">
								<button type="submit" class="btn btn-primary w-100">
									<i class="bi bi-search"></i> Tìm kiếm
								</button>
							</article>
						</form>
					</div>
				</aside>

				<main class="col-md-8 col-lg-9">

					<c:if test="${not empty requestScope.keyword || not empty requestScope.author || not empty requestScope.publisher || not empty requestScope.minPrice || not empty requestScope.maxPrice || not empty requestScope.minYear || not empty requestScope.maxYear || not empty requestScope.selectedCategoryId}">
						<header class="border-bottom mb-4 pb-3">
							<div class="form-inline d-flex justify-content-between align-items-center">
								<span>${requestScope.totalProducts} sản phẩm</span>
								<c:if test="${not empty requestScope.keyword}">
									<span class="text-muted">Từ khóa: "${requestScope.keyword}"</span>
								</c:if>
							</div>
						</header>
					</c:if>

					<c:choose>
						<c:when test="${not empty requestScope.products}">
							<div class="row item-grid">
								<c:forEach var="product" items="${requestScope.products}">
									<div class="col-xl-4 col-lg-6">
										<div class="card p-3 mb-4">
											<figure class="text-center mb-0">
												<a
													href="${pageContext.request.contextPath}/product?id=${product.id}"
													class="img-wrap"> <c:choose>
														<c:when test="${empty product.imageName}">
															<img width="200" height="200" class="img-fluid"
																src="${pageContext.request.contextPath}/img/280px.png"
																alt="280px.png">
														</c:when>
														<c:otherwise>
															<img width="200" height="200" class="img-fluid"
																src="${pageContext.request.contextPath}/image/${product.imageName}"
																alt="${product.imageName}">
														</c:otherwise>
													</c:choose>
												</a>
												<figcaption class="info-wrap mt-2">
													<a
														href="${pageContext.request.contextPath}/product?id=${product.id}"
														class="title">${product.name}</a>
													<div>
														<c:choose>
															<c:when test="${product.discount == 0}">
																<span class="price mt-1 fw-bold"> <fmt:formatNumber
																		pattern="#,##0" value="${product.price}" />₫
																</span>
															</c:when>
															<c:otherwise>
																<span class="price mt-1 fw-bold"> <fmt:formatNumber
																		pattern="#,##0"
																		value="${product.price * (100 - product.discount) / 100}" />₫
																</span>
																<span class="ms-2 text-muted text-decoration-line-through">
																	<fmt:formatNumber pattern="#,##0"
																		value="${product.price}" />₫
																</span>
																<span class="ms-2 badge bg-info"> - <fmt:formatNumber
																		pattern="#,##0" value="${product.discount}" />%
																</span>
															</c:otherwise>
														</c:choose>
													</div>
													<div class="small text-muted">${product.author}</div>
												</figcaption>
											</figure>
										</div>
									</div>
								</c:forEach>
							</div>

							<c:if test="${requestScope.totalPages != 0}">
								<nav class="mt-4">
									<ul class="pagination">
										<li
											class="page-item ${requestScope.page == 1 ? 'disabled' : ''}"><a
											class="page-link"
											href="${pageContext.request.contextPath}/advancedSearch?page=${requestScope.page - 1}${requestScope.baseQueryString}">
												Trang trước </a></li>

										<c:forEach begin="1" end="${requestScope.totalPages}" var="i">
											<c:choose>
												<c:when test="${requestScope.page == i}">
													<li class="page-item active"><a class="page-link">${i}</a></li>
												</c:when>
												<c:otherwise>
													<li class="page-item"><a class="page-link"
														href="${pageContext.request.contextPath}/advancedSearch?page=${i}${requestScope.baseQueryString}">
															${i} </a></li>
												</c:otherwise>
											</c:choose>
										</c:forEach>

										<li
											class="page-item ${requestScope.page == requestScope.totalPages ? 'disabled' : ''}"><a
											class="page-link"
											href="${pageContext.request.contextPath}/advancedSearch?page=${requestScope.page + 1}${requestScope.baseQueryString}">
												Trang sau </a></li>
									</ul>
								</nav>
							</c:if>
						</c:when>
						<c:otherwise>
							<div class="text-center py-5">
								<i class="bi bi-search fs-1 text-muted"></i>
								<h4 class="mt-3 text-muted">Không tìm thấy sản phẩm nào</h4>
								<p class="text-muted">Hãy thử thay đổi điều kiện tìm kiếm</p>
								<a class="btn btn-primary"
									href="${pageContext.request.contextPath}/advancedSearch">
									Xóa bộ lọc </a>
							</div>
						</c:otherwise>
					</c:choose>
				</main>
			</div>
		</div>
	</section>

	<jsp:include page="_footer.jsp" />
</body>

</html>
