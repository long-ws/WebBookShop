<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Giỏ hàng</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/cart.css">
</head>

<body>
	<jsp:include page="_header.jsp" />

	<section class="section-pagetop bg-light" style="padding: 0.5rem 0">
		<div class="container">
			<h2 class="title-page">Giỏ hàng</h2>
		</div>
	</section>

	<section class="section-content padding-y">
		<div class="container">
			<div class="row">
				<c:choose>
					<c:when test="${empty sessionScope.currentUser}">
						<p>
							Vui lòng <a href="${pageContext.request.contextPath}/signin">đăng
								nhập</a> để sử dụng giỏ hàng.
						</p>
					</c:when>

					<c:otherwise>
						<c:if test="${empty cartItems}">
							<p>
								Giỏ hàng của bạn đang trống. <a
									href="${pageContext.request.contextPath}/">Mua sắm ngay</a>
							</p>
						</c:if>

						<c:if test="${not empty cartItems}">
							<main class="col-lg-9 mb-lg-0 mb-3">
								<div class="card">
									<table class="table align-middle">
										<thead>
											<tr>
												<th>Sản phẩm</th>
												<th>Giá</th>
												<th>Số lượng</th>
												<th>Thành tiền</th>
												<th>Hành động</th>
											</tr>
										</thead>
										<tbody>
											<c:set var="tempTotal" value="0" />
											<c:forEach var="item" items="${cartItems}">
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
														<form action="${pageContext.request.contextPath}/cartItem"
															method="post" class="d-inline">
															<input type="hidden" name="action" value="update" /> <input
																type="hidden" name="cartItemId" value="${item.id}" /> <input
																type="number" name="quantity" value="${item.quantity}"
																min="1" style="width: 70px" />
															<button type="submit" class="btn btn-sm btn-primary">Cập
																nhật</button>
														</form>
													</td>

													<td><fmt:formatNumber
															value="${item.product.price * item.quantity}"
															pattern="#,##0" /> ₫ <c:set var="tempTotal"
															value="${tempTotal + (item.product.price * item.quantity)}" />
													</td>

													<td>
														<form action="${pageContext.request.contextPath}/cartItem"
															method="post" class="d-inline">
															<input type="hidden" name="action" value="delete" /> <input
																type="hidden" name="cartItemId" value="${item.id}" />
															<button type="submit" class="btn btn-sm btn-danger">Xóa</button>
														</form>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>

									<div class="card-body border-top">
										<form action="${pageContext.request.contextPath}/cart"
											method="post">
											<input type="hidden" name="cartId" value="${cartId}" />
											<p>Hình thức giao hàng:</p>
											<div class="form-check">
												<input class="form-check-input" type="radio"
													name="deliveryMethod" value="1" checked /> <label
													class="form-check-label">Giao tiêu chuẩn</label>
											</div>
											<div class="form-check mb-2">
												<input class="form-check-input" type="radio"
													name="deliveryMethod" value="2" /> <label
													class="form-check-label">Giao nhanh</label>
											</div>
											<input type="hidden" name="deliveryPrice" value="30000" />
											<button type="submit" class="btn btn-primary float-end">Đặt
												hàng</button>
											<a href="${pageContext.request.contextPath}/"
												class="btn btn-light">Tiếp tục mua sắm</a>
										</form>
									</div>

								</div>
							</main>

							<aside class="col-lg-3">
								<div class="card mb-3">
									<div class="card-body">
										<p class="card-title">Thông tin thanh toán</p>
										<dl class="row mb-0">
											<dt class="col-6">Tạm tính:</dt>
											<dd class="col-6 text-end">
												<fmt:formatNumber value="${tempTotal}" pattern="#,##0" />
												₫
											</dd>

											<dt class="col-6">Phí vận chuyển:</dt>
											<dd class="col-6 text-end">
												<fmt:formatNumber value="30000" pattern="#,##0" />
												₫
											</dd>

											<dt class="col-6">Tổng cộng:</dt>
											<dd class="col-6 text-end">
												<strong> <fmt:formatNumber
														value="${tempTotal + 30000}" pattern="#,##0" />₫
												</strong>
											</dd>
										</dl>
									</div>
								</div>
							</aside>
						</c:if>
					</c:otherwise>
				</c:choose>

			</div>
		</div>
	</section>

	<jsp:include page="_footer.jsp" />
</body>

</html>