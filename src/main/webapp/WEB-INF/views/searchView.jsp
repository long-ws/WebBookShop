<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Kết quả tìm kiếm cho "${requestScope.query}"</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/searchView.css">
</head>

<body>
	<jsp:include page="_header.jsp" />

	<section class="section-content mb-5">
		<div class="container">
			<div class="search-info">
				<h4 class="mb-3">
					<i class="bi bi-search me-2"></i> Kết quả tìm kiếm
				</h4>
				<p class="mb-2">
					Tìm thấy <strong>${requestScope.totalProducts}</strong> sản phẩm
					cho từ khóa: <strong>"${requestScope.query}"</strong>
				</p>
				<div class="mt-3">
					<small class="d-block mb-2 opacity-75">Tìm kiếm trong các
						trường:</small> <span class="search-keywords">Tên sản phẩm</span> <span
						class="search-keywords">Tác giả</span> <span
						class="search-keywords">Nhà xuất bản</span> <span
						class="search-keywords">Mô tả</span>
				</div>
			</div>

			<c:if test="${requestScope.totalProducts == 0}">
				<div class="no-results">
					<i class="bi bi-search"></i>
					<h4>Không tìm thấy sản phẩm nào</h4>
					<p>Không có sản phẩm nào khớp với từ khóa tìm kiếm của bạn.</p>
					<div class="search-tips">
						<h6>
							<i class="bi bi-lightbulb me-2"></i>Gợi ý tìm kiếm:
						</h6>
						<ul class="mb-0">
							<li>Thử tìm kiếm với từ khóa khác</li>
							<li>Sử dụng từ khóa ngắn gọn hơn</li>
							<li>Kiểm tra lỗi chính tả</li>
							<li>Tìm kiếm theo tên tác giả hoặc nhà xuất bản</li>
						</ul>
					</div>
					<a href="${pageContext.request.contextPath}/"
						class="btn btn-primary"> <i class="bi bi-house me-2"></i>Về
						trang chủ
					</a>
				</div>
			</c:if>

			<c:if test="${requestScope.totalProducts > 0}">
				<div class="row item-grid">
					<c:forEach var="product" items="${requestScope.products}">
						<div class="col-xl-3 col-lg-4 col-md-6">
							<div class="card p-3 mb-4">
								<figure class="text-center">
									<a
										href="${pageContext.request.contextPath}/product?id=${product.id}">
										<c:choose>
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
										<div class="text-muted small mb-2">
											<i class="bi bi-person me-1"></i>${product.author}
										</div>
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
														<fmt:formatNumber pattern="#,##0" value="${product.price}" />₫
													</span>
													<span class="ms-2 badge bg-info"> -<fmt:formatNumber
															pattern="#,##0" value="${product.discount}" />%
													</span>
												</c:otherwise>
											</c:choose>
										</div>
									</figcaption>
								</figure>
							</div>
						</div>
					</c:forEach>
				</div>
			</c:if>

			<c:if test="${requestScope.totalPages > 0}">
				<nav class="mt-4">
					<ul class="pagination">
						<c:choose>
							<c:when test="${requestScope.page == 1}">
								<li class="page-item disabled"><a class="page-link"
									href="#">Trang trước</a></li>
							</c:when>
							<c:otherwise>
								<li class="page-item"><a class="page-link"
									href="${pageContext.request.contextPath}/search?q=${requestScope.query}&page=${requestScope.page - 1}">
										Trang trước </a></li>
							</c:otherwise>
						</c:choose>

						<c:forEach begin="1" end="${requestScope.totalPages}" var="i">
							<c:choose>
								<c:when test="${requestScope.page == i}">
									<li class="page-item active"><a class="page-link">${i}</a>
									</li>
								</c:when>
								<c:otherwise>
									<li class="page-item"><a class="page-link"
										href="${pageContext.request.contextPath}/search?q=${requestScope.query}&page=${i}">
											${i} </a></li>
								</c:otherwise>
							</c:choose>
						</c:forEach>

						<c:choose>
							<c:when test="${requestScope.page == requestScope.totalPages}">
								<li class="page-item disabled"><a class="page-link"
									href="#">Trang sau</a></li>
							</c:when>
							<c:otherwise>
								<li class="page-item"><a class="page-link"
									href="${pageContext.request.contextPath}/search?q=${requestScope.query}&page=${requestScope.page + 1}">
										Trang sau </a></li>
							</c:otherwise>
						</c:choose>
					</ul>
				</nav>
			</c:if>
		</div>
	</section>

	<jsp:include page="_footer.jsp" />
</body>

</html>
