<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<fmt:setLocale value="vi_VN" /> 

<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Giỏ hàng</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
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
					<c:when test="${empty sessionScope.currentUser and empty cartItems}">
						<div class="col-12">
							<div class="alert alert-info">
								<i class="bi bi-info-circle me-2"></i> Vui lòng <a
									href="${pageContext.request.contextPath}/signin" class="alert-link">đăng nhập</a>
								để lưu giỏ hàng và theo dõi đơn hàng. Hoặc <a
									href="${pageContext.request.contextPath}/" class="alert-link">tiếp
									tục mua sắm</a> với giỏ hàng tạm thời.
							</div>
						</div>
					</c:when>

					<c:otherwise>
						<c:if test="${empty cartItems}">
							<div class="col-12">
								<div class="text-center py-5">
									<i class="bi bi-cart-x display-1 text-muted"></i>
									<h4 class="mt-3">Giỏ hàng của bạn đang trống</h4>
									<p class="text-muted">Hãy thêm một số sản phẩm vào giỏ
										hàng nhé!</p>
									<a href="${pageContext.request.contextPath}/"
										class="btn btn-primary"> <i
										class="bi bi-arrow-left me-2"></i>Mua sắm ngay
									</a>
								</div>
							</div>
						</c:if>

						<c:if test="${not empty cartItems}">
							<main class="col-lg-9 mb-lg-0 mb-3">
								<div class="card">
									<div
										class="card-header bg-white d-flex justify-content-between align-items-center">
										<h5 class="mb-0">
											<i class="bi bi-cart3 me-2"></i> Giỏ hàng của bạn <span
												class="badge bg-primary rounded-pill">${fn:length(cartItems)}
												sản phẩm</span>
										</h5>
										<div class="form-check">
											<input class="form-check-input" type="checkbox"
												id="selectAllCheckbox"> <label
												class="form-check-label fw-bold" for="selectAllCheckbox">Chọn
												tất cả</label>
										</div>
									</div>

									<form id="cartForm"
										action="${pageContext.request.contextPath}/cartItem" method="post">
										<input type="hidden" name="action" value="bulkUpdate" />

										<div class="table-responsive">
											<table id="cartTable" class="table align-middle mb-0">
												<thead class="table-light">
													<tr>
														<th width="5%"></th>
														<th width="35%">Sản phẩm</th>
														<th width="15%">Giá</th>
														<th width="15%">Số lượng</th>
														<th width="15%">Thành tiền</th>
														<th width="15%">Hành động</th>
													</tr>
												</thead>
												<tbody>
													<c:set var="tempTotal" value="0" />
													<c:set var="selectedTempTotal" value="0" />
													<c:set var="hasError" value="false" />
													<c:forEach var="item" items="${cartItems}"
														varStatus="status">
														<c:set var="itemKey" value="${not empty sessionScope.currentUser ? item.id : item.product.id}" />
														<c:set var="itemPrice"
															value="${item.product.discount > 0 ? item.product.price * (100 - item.product.discount) / 100 : item.product.price}" />
														<c:set var="tempTotal"
															value="${tempTotal + (itemPrice * item.quantity)}" />
														<c:if test="${item.selected}">
															<c:set var="selectedTempTotal"
																value="${selectedTempTotal + (itemPrice * item.quantity)}" />
														</c:if>
														<tr class="cart-item-row"
															data-item-id="${item.id}"
															data-product-id="${item.product.id}"
															data-selected="${item.selected}">
															<td><input type="checkbox"
																class="form-check-input item-select-checkbox"
																name="selected_${itemKey}"
																value="${item.selected ? 'true' : 'false'}"
																data-product-id="${item.product.id}"
																data-item-id="${item.id}"
																${item.selected ? 'checked' : ''}>
															</td>
															<td>
																<div class="d-flex align-items-center">
																	<c:choose>
																		<c:when test="${empty item.product.imageName}">
																			<img
																				src="${pageContext.request.contextPath}/img/placeholder.png"
																				width="60" height="60" class="me-3 rounded"
																				alt="Product" />
																		</c:when>
																		<c:otherwise>
																			<img
																				src="${pageContext.request.contextPath}/image/${item.product.imageName}"
																				width="60" height="60" class="me-3 rounded"
																				alt="${item.product.name}" />
																		</c:otherwise>
																	</c:choose>
																	<div>
																		<h6 class="mb-1">
																			<a
																				href="${pageContext.request.contextPath}/product?id=${item.product.id}"
																				class="text-decoration-none">${item.product.name}</a>
																		</h6>
																		<small class="text-muted">Tác giả:
																			${item.product.author}</small>
																		<c:if test="${item.product.quantity < item.quantity}">
																			<div class="text-danger small">
																				<i class="bi bi-exclamation-triangle"></i> Chỉ
																				còn ${item.product.quantity} sản phẩm trong kho
																			</div>
																			<c:set var="hasError" value="true" />
																		</c:if>
																	</div>
																</div>
															</td>

															<td class="item-price-col"
																data-product-id="${item.product.id}"
																data-price="${item.product.price}"
																data-discount="${item.product.discount}">
																<fmt:formatNumber
																	value="${item.product.discount > 0 ? item.product.price * (100 - item.product.discount) / 100 : item.product.price}"
																	pattern="#,##0" />₫ <c:if
																	test="${item.product.discount > 0}">
																	<br>
																	<small class="text-muted text-decoration-line-through">
																		<fmt:formatNumber value="${item.product.price}"
																			pattern="#,##0" />₫
																	</small>
																</c:if>
															</td>

															<td><input type="number"
																name="quantity_${itemKey}"
																value="${item.quantity}" min="1"
																max="${item.product.quantity}"
																class="form-control text-center quantity-input"
																style="width: 100px"
																data-product-id="${item.product.id}"
																data-item-id="${item.id}"></td>

															<td class="fw-bold item-total-col"><span
																class="item-total-value"> <fmt:formatNumber
																		value="${(item.product.discount > 0 ? item.product.price * (100 - item.product.discount) / 100 : item.product.price) * item.quantity}"
																		pattern="#,##0" />₫
															</span></td>

															<td>
																<button type="button"
																	class="btn btn-sm btn-outline-danger remove-item-btn"
																	data-product-id="${item.product.id}"
																	data-item-id="${item.id}"
																	data-product-name="${item.product.name}">
																	<i class="bi bi-trash"></i>
																</button>
																<c:choose>
																	<c:when test="${empty sessionScope.currentUser}">
																		<input type="hidden" class="remove-item-key" value="${item.product.id}">
																	</c:when>
																	<c:otherwise>
																		<input type="hidden" class="remove-item-key" value="${item.id}">
																	</c:otherwise>
																</c:choose>
															</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>

										<div class="card-body border-top bg-light">
											<div class="row align-items-center">
												<div class="col-md-6">
													<a href="${pageContext.request.contextPath}/"
														class="btn btn-light"> <i
														class="bi bi-arrow-left me-2"></i>Tiếp tục mua sắm
													</a>
													<button type="button" class="btn btn-outline-secondary"
														id="clearCartBtn">
														<i class="bi bi-trash me-2"></i>Xóa giỏ hàng
													</button>
												</div>
												<div class="col-md-6 text-end">
													<button type="submit" class="btn btn-primary"
														id="updateCartBtn">
														<i class="bi bi-arrow-clockwise me-2"></i>Cập nhật giỏ
														hàng
													</button>
												</div>
											</div>
										</div>
									</form>
								</div>
							</main>

							<aside class="col-lg-3">
								<div class="card mb-3">
									<div class="card-header bg-white">
										<h6 class="mb-0">
											<i class="bi bi-receipt me-2"></i>Thông tin thanh toán
										</h6>
									</div>
									<div class="card-body">
										<dl class="row mb-0">
											<dt class="col-6">Tạm tính:</dt>
											<dd class="col-6 text-end">
												<span id="subtotal"> <fmt:formatNumber
														value="${tempTotal}" pattern="#,##0" />₫
												</span>
											</dd>

											<dt class="col-6">Phí vận chuyển:</dt>
											<dd class="col-6 text-end">
												<span id="shippingFee">30,000₫</span>
											</dd>

											<dt class="col-6 border-top pt-2">Tổng cộng:</dt>
											<dd class="col-6 text-end border-top pt-2">
												<strong class="text-primary" id="totalAmount"> <fmt:formatNumber
														value="${tempTotal + 30000}" pattern="#,##0" />₫
												</strong>
											</dd>
										</dl>
									</div>
								</div>

								<div class="card">
									<div class="card-body">
										<form id="checkoutForm"
											action="${pageContext.request.contextPath}/cart" method="post">
											<input type="hidden" name="cartId" value="${cartId}" /> <input
												type="hidden" name="deliveryPrice" id="deliveryPrice"
												value="30000" />

											<h6 class="mb-3">Hình thức giao hàng</h6>
											<div class="form-check mb-2">
												<input class="form-check-input" type="radio"
													name="deliveryMethod" value="1" id="deliveryStandard"
													checked /> <label class="form-check-label"
													for="deliveryStandard"> Giao tiêu chuẩn (5-7 ngày) -
													30,000₫ </label>
											</div>
											<div class="form-check mb-3">
												<input class="form-check-input" type="radio"
													name="deliveryMethod" value="2" id="deliveryExpress" /> <label
													class="form-check-label" for="deliveryExpress"> Giao
													nhanh (2-3 ngày) - 50,000₫ </label>
											</div>

											<div id="selectedItemsInfo" class="alert alert-info py-2 mb-3"
												style="font-size: 0.85rem;">
												<i class="bi bi-info-circle me-1"></i> <span
													id="selectedCountText">Đã chọn
													${selectedCount > 0 ? selectedCount : '0'} sản phẩm</span>
											</div>

											<button type="button"
												class="btn btn-success w-100 ${hasError ? '' : ''}"
												id="checkoutBtn" data-bs-toggle="modal"
												data-bs-target="#checkoutConfirmModal"
												${selectedCount == 0 ? 'disabled' : ''}>
												<i class="bi bi-credit-card me-2"></i>Đặt hàng
											</button>

											<c:if test="${hasError}">
												<small class="text-danger d-block mt-2"> <i
													class="bi bi-exclamation-triangle"></i> Vui lòng cập nhật số
													lượng sản phẩm trước khi đặt hàng
												</small>
											</c:if>
										</form>
									</div>
								</div>
							</aside>
						</c:if>
					</c:otherwise>
				</c:choose>

			</div>
		</div>
	</section>

	<div class="toast-container position-fixed bottom-0 end-0 p-3"
		style="z-index: 9999;">
		<div id="successToast" class="toast align-items-center text-bg-success border-0"
			role="alert" aria-live="assertive" aria-atomic="true">
			<div class="d-flex">
				<div class="toast-body" id="successToastBody"></div>
				<button type="button" class="btn-close btn-close-white me-2 m-auto"
					data-bs-dismiss="toast" aria-label="Close"></button>
			</div>
		</div>
		<div id="errorToast" class="toast align-items-center text-bg-danger border-0"
			role="alert" aria-live="assertive" aria-atomic="true">
			<div class="d-flex">
				<div class="toast-body" id="errorToastBody"></div>
				<button type="button" class="btn-close btn-close-white me-2 m-auto"
					data-bs-dismiss="toast" aria-label="Close"></button>
			</div>
		</div>
	</div>

	<div class="modal fade" id="removeItemModal" tabindex="-1"
		aria-labelledby="removeItemModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="removeItemModalLabel">
						<i class="bi bi-exclamation-triangle text-danger me-2"></i>Xác
						nhận xóa
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					Bạn có chắc chắn muốn xóa "<strong id="removeItemName"></strong>"
					khỏi giỏ hàng?
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Hủy</button>
					<button type="button" class="btn btn-danger" id="confirmRemoveBtn">
						<i class="bi bi-trash me-1"></i>Xóa
					</button>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="clearCartModal" tabindex="-1"
		aria-labelledby="clearCartModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="clearCartModalLabel">
						<i class="bi bi-exclamation-triangle text-danger me-2"></i>Xác
						nhận xóa toàn bộ
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">Bạn có chắc chắn muốn xóa toàn bộ giỏ
					hàng?</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Hủy</button>
					<button type="button" class="btn btn-danger" id="confirmClearCartBtn">
						<i class="bi bi-trash me-1"></i>Xóa tất cả
					</button>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="checkoutConfirmModal" tabindex="-1"
		aria-labelledby="checkoutConfirmModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="checkoutConfirmModalLabel">
						<i class="bi bi-cart-check text-success me-2"></i>Xác nhận đặt hàng
					</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<p>Bạn có chắc chắn muốn đặt các sản phẩm đã chọn?</p>
					<div class="alert alert-light border">
						<div class="d-flex justify-content-between mb-1">
							<span>Tạm tính:</span> <span id="modalSubtotal">0₫</span>
						</div>
						<div class="d-flex justify-content-between mb-1">
							<span>Phí vận chuyển:</span> <span id="modalShipping">0₫</span>
						</div>
						<div class="d-flex justify-content-between fw-bold border-top pt-2 mt-2">
							<span>Tổng thanh toán:</span> <span class="text-danger"
								id="modalTotal">0₫</span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Hủy</button>
					<button type="submit" form="checkoutForm" class="btn btn-success">
						<i class="bi bi-check-circle me-1"></i>Xác nhận đặt hàng
					</button>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="_footer.jsp" />

	<script>
		document.addEventListener('DOMContentLoaded', function() {
			const deliveryStandard = document.getElementById('deliveryStandard');
			const deliveryExpress = document.getElementById('deliveryExpress');
			const deliveryPrice = document.getElementById('deliveryPrice');
			const shippingFee = document.getElementById('shippingFee');
			const totalAmount = document.getElementById('totalAmount');
			const checkoutBtn = document.getElementById('checkoutBtn');
			const selectAllCheckbox = document.getElementById('selectAllCheckbox');
			const itemCheckboxes = document.querySelectorAll('.item-select-checkbox');
			const cartForm = document.getElementById('cartForm');
			const clearCartBtn = document.getElementById('clearCartBtn');
			const successToast = document.getElementById('successToast');
			const errorToast = document.getElementById('errorToast');

			function showToast(type, message) {
				const toast = type === 'success' ? successToast : errorToast;
				const body = type === 'success' ? document.getElementById('successToastBody')
						: document.getElementById('errorToastBody');
				body.textContent = message;
				const bsToast = new bootstrap.Toast(toast);
				bsToast.show();
			}

			async function calculateSelectedTotal() {
				let selectedTotal = 0;
				let selectedCount = 0;
				const rows = document.querySelectorAll('.cart-item-row');

				for (const row of rows) {
					const checkbox = row.querySelector('.item-select-checkbox');
					if (checkbox && checkbox.checked) {
						selectedCount++;
						const priceCol = row.querySelector('.item-price-col');
						const quantityInput = row.querySelector('.quantity-input');
						if (priceCol && quantityInput) {
							const productId = priceCol.dataset.productId;
							const quantity = parseInt(quantityInput.value) || 0;
							if (quantity > 0) {
								try {
									const resp = await fetch('${pageContext.request.contextPath}/cartItem?action=calculatePrice&productId='
											+ productId + '&quantity=' + quantity);
									const data = await resp.json();
									if (!data.error) selectedTotal += data.totalPrice;
								} catch (err) {
								}
							}
						}
					}
				}

				document.getElementById('selectedCountText').textContent = 'Đã chọn '
						+ selectedCount + ' sản phẩm';
				checkoutBtn.disabled = selectedCount === 0;

				return {
					total : selectedTotal,
					count : selectedCount
				};
			}

			async function updateItemTotal(input) {
				const row = input.closest('tr');
				const priceCol = row.querySelector('.item-price-col');
				const totalCol = row.querySelector('.item-total-col');
				const checkbox = row.querySelector('.item-select-checkbox');

				if (!priceCol || !totalCol) return;

				const productId = priceCol.dataset.productId;
				const quantity = parseInt(input.value) || 0;

				if (quantity < 1) return;

				try {
					const resp = await fetch('${pageContext.request.contextPath}/cartItem?action=calculatePrice&productId='
							+ productId + '&quantity=' + quantity);
					const data = await resp.json();

					if (data.error) return;

					const formattedTotal = new Intl.NumberFormat('vi-VN').format(
							data.totalPrice)
							+ '₫';
					totalCol.querySelector('.item-total-value').textContent = formattedTotal;

					if (data.discount > 0) {
						const formattedUnitPrice = new Intl.NumberFormat('vi-VN')
								.format(data.unitPrice)
								+ '₫';
						const formattedBasePrice = new Intl.NumberFormat('vi-VN')
								.format(priceCol.dataset.price)
								+ '₫';
						priceCol.innerHTML = formattedUnitPrice
								+ '<br><small class="text-muted text-decoration-line-through">'
								+ formattedBasePrice + '</small>';
					}

					await updateCartTotals();
					if (checkbox && checkbox.checked) {
						await calculateSelectedTotal();
					}
				} catch (err) {
					console.error('Lỗi cập nhật giá:', err);
				}
			}

			async function updateCartTotals() {
				const rows = document.querySelectorAll('#cartTable tbody tr');
				let subtotal = 0;

				for (const row of rows) {
					const priceCol = row.querySelector('.item-price-col');
					const quantityInput = row.querySelector('.quantity-input');
					const totalCol = row.querySelector('.item-total-col');

					if (!priceCol || !quantityInput || !totalCol) continue;

					const productId = priceCol.dataset.productId;
					const quantity = parseInt(quantityInput.value) || 0;
					if (quantity < 1) continue;

					try {
						const resp = await fetch('${pageContext.request.contextPath}/cartItem?action=calculatePrice&productId='
								+ productId + '&quantity=' + quantity);
						const data = await resp.json();
						if (!data.error) subtotal += data.totalPrice;
					} catch (err) {
					}
				}

				const shipping = deliveryExpress.checked ? 50000 : 30000;
				const total = subtotal + shipping;

				document.getElementById('subtotal').textContent = new Intl.NumberFormat(
						'vi-VN').format(subtotal)
						+ '₫';
				shippingFee.textContent = new Intl.NumberFormat('vi-VN').format(shipping)
						+ '₫';
				totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(total)
						+ '₫';
			}

			document.querySelectorAll('.quantity-input').forEach(input => {
				let debounceTimer;
				input.addEventListener('input', function() {
					clearTimeout(debounceTimer);
					debounceTimer = setTimeout(() => updateItemTotal(this), 300);
				});
			});

			let pendingRemoveItemId = null;
			let pendingRemoveProductId = null;

			document.querySelectorAll('.remove-item-btn').forEach(btn => {
				btn.addEventListener('click', function() {
					pendingRemoveProductId = this.dataset.productId;
					pendingRemoveItemId = this.closest('tr').querySelector('.remove-item-key').value;
					document.getElementById('removeItemName').textContent = this.dataset.productName;
					new bootstrap.Modal(document.getElementById('removeItemModal')).show();
				});
			});

			document.getElementById('confirmRemoveBtn').addEventListener('click',
					function() {
						const form = document.createElement('form');
						form.method = 'POST';
						form.action = '${pageContext.request.contextPath}/cartItem';
						form.innerHTML = '<input type="hidden" name="action" value="delete">'
								+ '<input type="hidden" name="cartItemId" value="'
								+ pendingRemoveItemId + '">';
						document.body.appendChild(form);
						form.submit();
					});

			if (clearCartBtn) {
				clearCartBtn.addEventListener('click', function() {
					new bootstrap.Modal(document.getElementById('clearCartModal')).show();
				});
			}

			document.getElementById('confirmClearCartBtn').addEventListener('click',
					function() {
						const form = document.createElement('form');
						form.method = 'POST';
						form.action = '${pageContext.request.contextPath}/cartItem';
						form.innerHTML = '<input type="hidden" name="action" value="clear">';
						document.body.appendChild(form);
						form.submit();
					});

			itemCheckboxes.forEach(checkbox => {
				checkbox.addEventListener('change', async function() {
					const productId = this.dataset.productId;
					const itemId = this.dataset.itemId;
					const checked = this.checked;

					const row = this.closest('tr');
					if (checked) {
						row.setAttribute('data-selected', 'true');
					} else {
						row.setAttribute('data-selected', 'false');
					}

					const hiddenInput = row.querySelector('input[name^="selected_"]');
					if (hiddenInput) {
						hiddenInput.value = checked ? 'true' : 'false';
					}

					await calculateSelectedTotal();
				});
			});

			if (selectAllCheckbox) {
				selectAllCheckbox.addEventListener('change', async function() {
					const checked = this.checked;
					itemCheckboxes.forEach(checkbox => {
						checkbox.checked = checked;
						const row = checkbox.closest('tr');
						if (checked) {
							row.setAttribute('data-selected', 'true');
						} else {
							row.setAttribute('data-selected', 'false');
						}
						const hiddenInput = row.querySelector('input[name^="selected_"]');
						if (hiddenInput) {
							hiddenInput.value = checked ? 'true' : 'false';
						}
					});
					await calculateSelectedTotal();
				});
			}

			function updateSelectAllState() {
				if (!selectAllCheckbox || itemCheckboxes.length === 0) return;
				const allChecked = Array.from(itemCheckboxes).every(cb => cb.checked);
				const someChecked = Array.from(itemCheckboxes).some(cb => cb.checked);
				selectAllCheckbox.checked = allChecked;
				selectAllCheckbox.indeterminate = someChecked && !allChecked;
			}

			itemCheckboxes.forEach(cb => {
				cb.addEventListener('change', updateSelectAllState);
			});
			updateSelectAllState();

			if (deliveryStandard) {
				deliveryStandard.addEventListener('change', function() {
					deliveryPrice.value = '30000';
					updateCartTotals();
				});
			}
			if (deliveryExpress) {
				deliveryExpress.addEventListener('change', function() {
					deliveryPrice.value = '50000';
					updateCartTotals();
				});
			}

			document.getElementById('checkoutConfirmModal').addEventListener(
					'show.bs.modal',
					async function(event) {
						const selected = await calculateSelectedTotal();
						const shipping = deliveryExpress.checked ? 50000 : 30000;
						const total = selected.total + shipping;

						document.getElementById('modalSubtotal').textContent = new Intl.NumberFormat(
								'vi-VN').format(selected.total)
								+ '₫';
						document.getElementById('modalShipping').textContent = new Intl.NumberFormat(
								'vi-VN').format(shipping)
								+ '₫';
						document.getElementById('modalTotal').textContent = new Intl.NumberFormat(
								'vi-VN').format(total)
								+ '₫';
					});

			if (cartForm) {
				cartForm.addEventListener('submit', function(e) {
					let hasInvalid = false;
					document.querySelectorAll('.quantity-input').forEach(input => {
						if (parseInt(input.value) < 1) hasInvalid = true;
					});
					if (hasInvalid) {
						e.preventDefault();
						showToast('error',
								'Vui lòng kiểm tra lại số lượng sản phẩm');
					}
				});
			}

			const urlParams = new URLSearchParams(window.location.search);
			if (urlParams.get('added') === '1') {
				showToast('success', 'Đã thêm sản phẩm vào giỏ hàng');
			}
			if (urlParams.get('updated') === '1') {
				showToast('success', 'Đã cập nhật giỏ hàng');
			}
			if (urlParams.get('removed') === '1') {
				showToast('success', 'Đã xóa sản phẩm khỏi giỏ hàng');
			}
		});
	</script>
</body>

</html>
