<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>${requestScope.product.name}</title>
</head>

<body>
	<jsp:include page="_header.jsp" />

	<section class="section-pagetop-2 bg-light" style="padding: 0.25rem 0">
		<div class="container">
			<nav>
				<ol class="breadcrumb">
					<li class="breadcrumb-item"><a
						href="${pageContext.request.contextPath}/">Trang chủ</a></li>
					<li class="breadcrumb-item"><a
						href="${pageContext.request.contextPath}/category?id=${requestScope.category.id}">${requestScope.category.name}</a>
					</li>
					<li class="breadcrumb-item active">${requestScope.product.name}</li>
				</ol>
			</nav>
		</div>
	</section>

	<section class="section-content padding-y">
		<div class="container">
			<div class="row">

				<!-- Ảnh sản phẩm -->
				<aside
					class="col-md-5 mb-md-0 mb-4 d-flex justify-content-center align-items-center">
					<c:choose>
						<c:when test="${empty requestScope.product.imageName}">
							<img width="280" height="280" class="img-fluid"
								src="${pageContext.request.contextPath}/img/280px.png"
								alt="280px.png">
						</c:when>
						<c:otherwise>
							<img width="280" height="280" class="img-fluid"
								src="${pageContext.request.contextPath}/image/${requestScope.product.imageName}"
								alt="${requestScope.product.imageName}">
						</c:otherwise>
					</c:choose>
				</aside>

				<!-- Thông tin sản phẩm -->
				<main class="col-md-7">
					<h2 class="title">${requestScope.product.name}</h2>

					<!-- Rating -->
					<div class="rating-wrap my-3">
						<span class="rating-stars me-2"> <c:forEach begin="1"
								end="5" var="i">
								<i
									class="bi bi-star-fill ${i <= requestScope.averageRatingScore ? 'active' : ''}"></i>
							</c:forEach>
						</span> <small class="label-rating text-muted me-2">${requestScope.totalProductReviews}
							đánh giá</small> <small class="label-rating text-success"><i
							class="bi bi-bag-check-fill"></i>
							${requestScope.product.totalBuy} đã mua</small>
					</div>

					<!-- Giá sản phẩm -->
					<div class="mb-4">
						<c:choose>
							<c:when test="${requestScope.product.discount == 0}">
								<span class="price h4"> <fmt:formatNumber pattern="#,##0"
										value="${requestScope.product.price}" />₫
								</span>
							</c:when>
							<c:otherwise>
								<span class="price h4"> <fmt:formatNumber pattern="#,##0"
										value="${requestScope.product.price * (100 - requestScope.product.discount) / 100}" />₫
								</span>
								<span class="ms-2 text-muted text-decoration-line-through">
									<fmt:formatNumber pattern="#,##0"
										value="${requestScope.product.price}" />₫
								</span>
								<span class="ms-2 badge bg-info">- <fmt:formatNumber
										pattern="#,##0" value="${requestScope.product.discount}" />%
								</span>
							</c:otherwise>
						</c:choose>
					</div>

					<!-- Thông số sản phẩm -->
					<dl class="row mb-4">
						<dt class="col-xl-4 col-sm-5 col-6">Tác giả</dt>
						<dd class="col-xl-8 col-sm-7 col-6">${requestScope.product.author}</dd>

						<dt class="col-xl-4 col-sm-5 col-6">Số trang</dt>
						<dd class="col-xl-8 col-sm-7 col-6">${requestScope.product.pages}</dd>

						<dt class="col-xl-4 col-sm-5 col-6">Nhà xuất bản</dt>
						<dd class="col-xl-8 col-sm-7 col-6">${requestScope.product.publisher}</dd>

						<dt class="col-xl-4 col-sm-5 col-6">Năm xuất bản</dt>
						<dd class="col-xl-8 col-sm-7 col-6">${requestScope.product.yearPublishing}</dd>

					</dl>

					<!-- Nút Yêu thích + Số lượng + Thêm vào giỏ hàng cùng hàng -->
					<div class="d-flex align-items-center gap-2 mb-4">

						<!-- Nút Yêu thích -->
						<form action="${pageContext.request.contextPath}/wishlist"
							method="post" class="d-inline">
							<input type="hidden" name="productId"
								value="${requestScope.product.id}">
							<c:set var="isInWishlist" value="false" />
							<c:forEach var="item" items="${wishlistItems}">
								<c:if test="${item.productId == requestScope.product.id}">
									<c:set var="isInWishlist" value="true" />
								</c:if>
							</c:forEach>

							<c:choose>
								<c:when test="${isInWishlist}">
									<input type="hidden" name="action" value="delete">
									<button type="submit" class="btn btn-outline-danger"
										title="Xóa khỏi yêu thích">
										<i class="bi bi-heart-fill"></i>
									</button>
								</c:when>
								<c:otherwise>
									<input type="hidden" name="action" value="add">
									<button type="submit" class="btn btn-light"
										title="Thêm vào yêu thích">
										<i class="bi bi-heart"></i>
									</button>
								</c:otherwise>
							</c:choose>
						</form>

						<!-- Form Thêm vào giỏ hàng + số lượng -->
						<form action="${pageContext.request.contextPath}/cartItem"
							method="post" class="d-flex align-items-center gap-2">
							<input type="hidden" name="action" value="add"> <input
								type="hidden" name="userId"
								value="${sessionScope.currentUser.id}"> <input
								type="hidden" name="productId"
								value="${requestScope.product.id}">

							<!-- Input số lượng -->
							<input type="number" name="quantity" class="form-control w-auto"
								value="1" min="1" max="${requestScope.product.quantity}"
								step="1">

							<!-- Nút Thêm vào giỏ hàng -->
							<button type="submit" class="btn btn-primary">Thêm vào
								giỏ hàng</button>
						</form>

					</div>

				</main>
			</div>
		</div>
	</section>

	<!-- Mô tả sản phẩm -->
	<section class="section-content mb-4">
		<div class="container">
			<h3 class="pb-2">Mô tả sản phẩm</h3>
			<div>${requestScope.product.description}</div>
		</div>
	</section>

	<!-- Đánh giá sản phẩm -->
	<section class="section-content mb-5">
		<div class="container">
			<h3 id="review" class="pb-2">${requestScope.totalProductReviews}
				đánh giá</h3>
			<c:forEach var="productReview" items="${requestScope.productReviews}">
				<div class="sin-rattings mb-4">
					<div class="star-author-all mb-2 clearfix">
						<div class="ratting-author float-start">
							<h5 class="float-start me-3">${productReview.user.fullname}</h5>
							<span> <fmt:parseDate value="${productReview.createdAt}"
									pattern="yyyy-MM-dd'T'HH:mm" var="parsedCreatedAt" type="both" />
								<fmt:formatDate pattern="HH:mm dd/MM/yyyy"
									value="${parsedCreatedAt}" />
							</span>
						</div>
						<div class="ratting-star float-end">
							<span class="rating-stars me-2"> <c:forEach begin="1"
									end="5" var="i">
									<i
										class="bi bi-star-fill ${i <= productReview.ratingScore ? 'active' : ''}"></i>
								</c:forEach>
							</span> <span>(${productReview.ratingScore})</span>
						</div>
					</div>
					<div>
						<c:choose>
							<c:when test="${productReview.isShow == 1}">${productReview.content}</c:when>
							<c:otherwise>
								<em>Nội dung đánh giá đã được ẩn bởi quản trị viên</em>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</c:forEach>

			<!-- Thêm đánh giá -->
			<h3 id="review-form" class="pb-2">Thêm đánh giá</h3>
			<c:choose>
				<c:when test="${not empty sessionScope.currentUser}">
					<form action="${pageContext.request.contextPath}/addProductReview"
						method="post">
						<div class="row mb-3">
							<div class="col-md-3 mb-3">
								<select class="form-select" name="ratingScore">
									<c:forEach var="i" begin="1" end="5">
										<option selected value="${i}">${i}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="row mb-3">
							<div class="col">
								<textarea class="form-control" name="content"
									placeholder="Nội dung đánh giá" rows="3"></textarea>
							</div>
						</div>
						<input type="hidden" name="userId"
							value="${sessionScope.currentUser.id}"> <input
							type="hidden" name="productId" value="${requestScope.product.id}">
						<button type="submit" class="btn btn-primary">Gửi đánh
							giá</button>
					</form>
				</c:when>
				<c:otherwise>
					<p>
						Vui lòng <a href="${pageContext.request.contextPath}/signin">đăng
							nhập</a> để đánh giá sản phẩm.
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</section>

	<!-- Sản phẩm liên quan -->
	<section class="section-content mb-5">
		<div class="container">
			<h3 class="pb-2">Sản phẩm liên quan</h3>
			<div class="row item-grid">
				<c:forEach var="relatedProduct"
					items="${requestScope.relatedProducts}">
					<div class="col-xl-3 col-lg-4 col-md-6">
						<div class="card p-3 mb-4">
							<figure class="text-center">
								<a
									href="${pageContext.request.contextPath}/product?id=${relatedProduct.id}"
									class="img-wrap"> <c:choose>
										<c:when test="${empty relatedProduct.imageName}">
											<img width="200" height="200" class="img-fluid"
												src="${pageContext.request.contextPath}/img/280px.png" />
										</c:when>
										<c:otherwise>
											<img width="200" height="200" class="img-fluid"
												src="${pageContext.request.contextPath}/image/${relatedProduct.imageName}" />
										</c:otherwise>
									</c:choose>
								</a>
								<figcaption class="info-wrap mt-2">
									<a
										href="${pageContext.request.contextPath}/product?id=${relatedProduct.id}"
										class="title">${relatedProduct.name}</a> ...
								</figcaption>
							</figure>
						</div>

					</div>
				</c:forEach>
			</div>
		</div>
	</section>

	<jsp:include page="_footer.jsp" />
</body>

</html>