<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Yêu thích</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/cart.css">
</head>

<body>
	<jsp:include page="_header.jsp" />

	<section class="section-pagetop bg-light">
		<div class="container">
			<h2 class="title-page">Danh sách sản phẩm yêu thích</h2>
		</div>
	</section>

	<section class="section-content padding-y">
		<div class="container">
			<div class="row">
				<c:choose>
					<c:when test="${empty sessionScope.currentUser}">
						<p>
							Vui lòng <a href="${pageContext.request.contextPath}/signin">đăng
								nhập</a> để xem danh sách yêu thích.
						</p>
					</c:when>

					<c:otherwise>
						<c:if test="${empty wishlistItems}">
							<p>
								Bạn chưa thêm sản phẩm nào vào danh sách yêu thích. <a
									href="${pageContext.request.contextPath}/">Mua sắm ngay</a>
							</p>
						</c:if>

						<c:if test="${not empty wishlistItems}">
							<main class="col-lg-12">
								<div class="card">
									<table class="table align-middle">
										<thead>
											<tr>
												<th>Sản phẩm</th>
												<th>Giá</th>
												<th>Hành động</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="item" items="${wishlistItems}">
												<tr>
													<td>
														<div class="d-flex align-items-center">
															<c:choose>
																<c:when test="${empty item.product.imageName}">
																	<img
																		src="${pageContext.request.contextPath}/img/placeholder.png"
																		width="60" height="60" class="me-2" />
																</c:when>
																<c:otherwise>
																	<img
																		src="${pageContext.request.contextPath}/image/${item.product.imageName}"
																		width="60" height="60" class="me-2" />
																</c:otherwise>
															</c:choose>
															<a
																href="${pageContext.request.contextPath}/product?id=${item.product.id}">${item.product.name}</a>
														</div>
													</td>
													<td><fmt:formatNumber value="${item.product.price}"
															pattern="#,##0" />₫</td>
													<td>
														<form action="${pageContext.request.contextPath}/wishlist"
															method="post">
															<input type="hidden" name="action" value="delete" /> <input
																type="hidden" name="id" value="${item.id}" />
															<button type="submit" class="btn btn-sm btn-danger">Xóa</button>
														</form>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</main>
						</c:if>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</section>

	<jsp:include page="_footer.jsp" />
</body>

</html>