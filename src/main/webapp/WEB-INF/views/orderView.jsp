<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp"/>
<title>Đơn hàng của tôi</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderStatusCheck.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>

<body>
	<jsp:include page="_header.jsp"/>

	<section class="section-pagetop bg-light">
		<div class="container">
			<div class="d-flex justify-content-between align-items-center">
				<div>
					<h2 class="title-page mb-1">
						<i class="bi bi-bag-check me-2"></i>Đơn hàng của tôi
					</h2>
					<p class="text-muted mb-0">Theo dõi và quản lý đơn hàng của bạn</p>
				</div>
				<div class="realtime-indicator">
					<span class="pulse-dot"></span>
					<span>Cập nhật tự động</span>
				</div>
			</div>
		</div>
	</section>

	<section class="section-content padding-y">
		<div class="container">
			<div class="row">
				<c:choose>
					<c:when test="${not empty sessionScope.currentUser}">
						<jsp:include page="_navPanel.jsp">
							<jsp:param name="active" value="ORDER"/>
						</jsp:include>

						<main class="col-md-9">
							<c:choose>
								<c:when test="${not empty requestScope.orders}">
									<div class="table-responsive-xxl">
										<table class="table table-bordered table-striped table-hover align-middle bg-white rounded overflow-hidden">
											<thead class="table-light">
												<tr>
													<th scope="col" style="min-width: 120px;">Mã đơn hàng</th>
													<th scope="col" style="min-width: 100px;">Ngày mua</th>
													<th scope="col" style="min-width: 280px;">Sản phẩm</th>
													<th scope="col" style="min-width: 120px;">Tổng tiền</th>
													<th scope="col" style="min-width: 150px;">Trạng thái</th>
													<th scope="col" class="text-center">Thao tác</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="order" items="${requestScope.orders}">
													<tr data-order-id="${order.id}">
														<th scope="row" class="fw-bold">#${order.id}</th>
														<td>
															<i class="bi bi-calendar3 me-1 text-muted"></i>
															${order.createdAt}
														</td>
														<td>
															<span class="text-truncate d-inline-block" style="max-width: 250px;">
																${order.name}
															</span>
														</td>
														<td class="fw-bold text-danger">
															<fmt:formatNumber pattern="#,##0" value="${order.total}"/>₫
														</td>
													<td class="order-status-cell">
														<c:choose>
															<c:when test="${order.status == 1}">
																<span class="badge bg-warning text-dark px-2 py-1">
																	<i class="bi bi-bag-check me-1"></i> Đã đặt hàng
																</span>
															</c:when>
															<c:when test="${order.status == 2}">
																<span class="badge bg-primary px-2 py-1">
																	<i class="bi bi-check2-all me-1"></i> Đã xác nhận
																</span>
															</c:when>
															<c:when test="${order.status == 3}">
																<span class="badge bg-primary px-2 py-1">
																	<i class="bi bi-box-seam me-1"></i> Đã lấy hàng
																</span>
															</c:when>
															<c:when test="${order.status == 4}">
																<span class="badge bg-primary px-2 py-1">
																	<i class="bi bi-truck me-1"></i> Đang vận chuyển
																</span>
															</c:when>
															<c:when test="${order.status == 5}">
																<span class="badge bg-info px-2 py-1">
																	<i class="bi bi-geo-alt me-1"></i> Đang giao hàng
																</span>
															</c:when>
															<c:when test="${order.status == 6}">
																<span class="badge bg-success px-2 py-1">
																	<i class="bi bi-check-circle me-1"></i> Đã giao thành công
																</span>
															</c:when>
															<c:when test="${order.status == 7}">
																<span class="badge bg-danger px-2 py-1">
																	<i class="bi bi-x-circle me-1"></i> Đã hủy
																</span>
															</c:when>
															<c:otherwise>
																<span class="badge bg-secondary px-2 py-1">
																	<i class="bi bi-question-circle me-1"></i> Không xác định
																</span>
															</c:otherwise>
														</c:choose>
													</td>
														<td class="text-center">
															<a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/orderDetail?id=${order.id}" role="button">
																<i class="bi bi-eye me-1"></i> Chi tiết
															</a>
														</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>

									<c:if test="${requestScope.totalPages > 1}">
										<nav class="mt-4">
											<ul class="pagination justify-content-center">
												<li class="page-item ${requestScope.page == 1 ? 'disabled' : ''}">
													<a class="page-link" href="${pageContext.request.contextPath}/order?page=${requestScope.page - 1}">
														<i class="bi bi-chevron-left"></i> Trang trước
													</a>
												</li>

												<c:forEach begin="1" end="${requestScope.totalPages}" var="i">
													<li class="page-item ${requestScope.page == i ? 'active' : ''}">
														<c:choose>
															<c:when test="${requestScope.page == i}">
																<span class="page-link">${i}</span>
															</c:when>
															<c:otherwise>
																<a class="page-link" href="${pageContext.request.contextPath}/order?page=${i}">${i}</a>
															</c:otherwise>
														</c:choose>
													</li>
												</c:forEach>

												<li class="page-item ${requestScope.page == requestScope.totalPages ? 'disabled' : ''}">
													<a class="page-link" href="${pageContext.request.contextPath}/order?page=${requestScope.page + 1}">
														Trang sau <i class="bi bi-chevron-right"></i>
													</a>
												</li>
											</ul>
										</nav>
									</c:if>
								</c:when>
								<c:otherwise>
									<div class="card order-card-enhanced">
										<div class="card-body text-center py-5">
											<div class="mb-4">
												<i class="bi bi-bag text-muted" style="font-size: 64px;"></i>
											</div>
											<h4 class="text-muted mb-3">Chưa có đơn hàng nào</h4>
											<p class="text-muted mb-4">Hãy bắt đầu mua sắm để tạo đơn hàng đầu tiên của bạn</p>
											<a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
												<i class="bi bi-shop me-2"></i>Khám phá cửa hàng
											</a>
										</div>
									</div>
								</c:otherwise>
							</c:choose>
						</main>
					</c:when>
					<c:otherwise>
						<div class="col-12">
							<div class="alert alert-warning text-center py-4">
								<i class="bi bi-exclamation-triangle me-2"></i>
								Vui lòng <a href="${pageContext.request.contextPath}/signin" class="alert-link">đăng nhập</a> để xem thông tin đơn hàng.
							</div>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</section>

	<jsp:include page="_footer.jsp"/>

	<script>
	(function() {
		var contextPath = '${pageContext.request.contextPath}';
		var statusIcons = {
			1: 'bag-check',
			2: 'check2-all',
			3: 'box-seam',
			4: 'truck',
			5: 'geo-alt',
			6: 'check-circle',
			7: 'x-circle'
		};
		var statusLabels = {
			1: 'Đã đặt hàng',
			2: 'Đã xác nhận',
			3: 'Đã lấy hàng',
			4: 'Đang vận chuyển',
			5: 'Đang giao hàng',
			6: 'Đã giao thành công',
			7: 'Đã hủy'
		};
		var statusBadgeClasses = {
			1: 'bg-warning text-dark',
			2: 'bg-primary',
			3: 'bg-primary',
			4: 'bg-primary',
			5: 'bg-info',
			6: 'bg-success',
			7: 'bg-danger'
		};

			function getBadgeClass(status) {
				return statusBadgeClasses[status] || 'bg-secondary';
			}

			function getIcon(status) {
				return statusIcons[status] || 'question-circle';
			}

			function getLabel(status) {
				return statusLabels[status] || 'Không xác định';
			}

			function fetchOrderStatuses() {
				var rows = document.querySelectorAll('tr[data-order-id]');
				if (rows.length === 0) return;

				rows.forEach(function(row) {
					var orderId = row.getAttribute('data-order-id');
					var statusCell = row.querySelector('.order-status-cell');

					if (orderId && statusCell) {
						var url = contextPath + '/api/order-status?id=' + orderId + '&t=' + new Date().getTime();

						fetch(url)
							.then(function(response) {
								if (!response.ok) return null;
								return response.json();
							})
							.then(function(data) {
								if (!data || data.error) return;

								var currentBadge = statusCell.querySelector('.badge');
								var currentText = currentBadge ? currentBadge.textContent.trim() : '';

								var newText = getLabel(data.status);

								if (currentText.indexOf(newText) === -1) {
									statusCell.innerHTML = '<span class="badge ' + getBadgeClass(data.status) + ' px-2 py-1">' +
										'<i class="bi bi-' + getIcon(data.status) + ' me-1"></i> ' + newText +
										'</span>';
								}
							})
							.catch(function() {});
					}
				});
			}

			setInterval(fetchOrderStatuses, 10000);
		})();
	</script>
</body>
</html>
