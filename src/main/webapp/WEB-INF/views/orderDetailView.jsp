<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Thong tin don hang #${requestScope.order.id}</title>
<style>
.order-status-timeline {
	list-style: none;
	padding: 0;
	margin: 0;
	position: relative;
}

.order-status-timeline::before {
	content: '';
	position: absolute;
	left: 12px;
	top: 0;
	bottom: 0;
	width: 3px;
	background: #e9ecef;
}

.order-status-item {
	position: relative;
	padding-left: 40px;
	padding-bottom: 20px;
}

.order-status-item::before {
	content: '';
	position: absolute;
	left: 5px;
	top: 4px;
	width: 17px;
	height: 17px;
	border-radius: 50%;
	background: #dee2e6;
	border: 3px solid #fff;
	z-index: 1;
}

.order-status-item.completed::before {
	background: #28a745;
	box-shadow: 0 0 0 4px rgba(40, 167, 69, 0.2);
}

.order-status-item.current::before {
	background: #0d6efd;
	box-shadow: 0 0 0 4px rgba(13, 110, 253, 0.2);
}

.order-status-time {
	font-size: 0.8rem;
	color: #6c757d;
}

.order-status-title {
	font-weight: 600;
	margin-bottom: 3px;
	font-size: 0.9rem;
}

.order-status-desc {
	font-size: 0.85rem;
	color: #6c757d;
	margin: 0;
}

.shipper-avatar-placeholder {
	width: 45px;
	height: 45px;
	border-radius: 50%;
	background: linear-gradient(135deg, #4361ee, #7c3aed);
	color: white;
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: 700;
	font-size: 1.1rem;
}
</style>
</head>

<body>
<jsp:include page="_header.jsp" />

<section class="section-pagetop bg-light">
	<div class="container">
		<h2 class="title-page">Thong tin don hang #${requestScope.order.id}</h2>
	</div>
</section>

<section class="section-content padding-y">
	<div class="container">
		<div class="row">
			<c:choose>
				<c:when test="${not empty sessionScope.currentUser}">
					<jsp:include page="_navPanel.jsp">
						<jsp:param name="active" value="ORDER" />
					</jsp:include>

					<main class="col-md-9">
						<article class="card mb-4">

							<header class="card-header">
								<strong class="d-inline-block me-4">Ma don hang: ${requestScope.order.id}</strong>
								<span>Ngay mua: ${requestScope.createdAt}</span>
								<c:choose>
									<c:when test="${requestScope.order.status == 1}">
										<c:choose>
											<c:when test="${not empty requestScope.shipment && requestScope.shipment.shippingStatus == 'WAITING_PICKUP'}">
												<span class="badge bg-secondary float-end">Cho lay hang</span>
											</c:when>
											<c:when test="${not empty requestScope.shipment && requestScope.shipment.shippingStatus == 'PICKED_UP'}">
												<span class="badge bg-info float-end">Da lay hang</span>
											</c:when>
											<c:when test="${not empty requestScope.shipment && requestScope.shipment.shippingStatus == 'IN_TRANSIT'}">
												<span class="badge bg-primary float-end">Dang van chuyen</span>
											</c:when>
											<c:when test="${not empty requestScope.shipment && requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY'}">
												<span class="badge bg-warning text-dark float-end">Dang giao hang</span>
											</c:when>
											<c:when test="${not empty requestScope.shipment && requestScope.shipment.shippingStatus == 'DELIVERED'}">
												<span class="badge bg-success float-end">Da giao hang</span>
											</c:when>
											<c:when test="${not empty requestScope.shipment && requestScope.shipment.shippingStatus == 'FAILED'}">
												<span class="badge bg-danger float-end">Giao that bai</span>
											</c:when>
											<c:otherwise>
												<span class="badge bg-warning text-dark float-end">Dang xu ly</span>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:when test="${requestScope.order.status == 2}">
										<span class="badge bg-success float-end">Giao hang thanh cong</span>
									</c:when>
									<c:when test="${requestScope.order.status == 3}">
										<span class="badge bg-danger float-end">Huy don hang</span>
									</c:when>
								</c:choose>
							</header>

							<c:if test="${not empty requestScope.shipment}">
								<div class="mt-3 mx-3 p-3" style="background: linear-gradient(135deg, #4361ee, #7c3aed); border-radius: 10px; color: white;">
									<div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
										<div>
											<small style="opacity:0.8;">Ma van don</small>
											<div style="font-weight:700; font-size:1.1rem;">${requestScope.shipment.trackingCode}</div>
											<small style="opacity:0.8;">${requestScope.shipment.providerType}</small>
										</div>
										<c:choose>
											<c:when test="${requestScope.shipment.shippingStatus == 'WAITING_PICKUP'}">
												<span class="badge bg-light text-dark px-3 py-2">Cho lay hang</span>
											</c:when>
											<c:when test="${requestScope.shipment.shippingStatus == 'PICKED_UP'}">
												<span class="badge bg-info px-3 py-2">Da lay hang</span>
											</c:when>
											<c:when test="${requestScope.shipment.shippingStatus == 'IN_TRANSIT'}">
												<span class="badge bg-primary px-3 py-2">Dang van chuyen</span>
											</c:when>
											<c:when test="${requestScope.shipment.shippingStatus == 'OUT_FOR_DELIVERY'}">
												<span class="badge bg-warning text-dark px-3 py-2">Dang giao hang</span>
											</c:when>
											<c:when test="${requestScope.shipment.shippingStatus == 'DELIVERED'}">
												<span class="badge bg-success px-3 py-2">Da giao hang</span>
											</c:when>
											<c:when test="${requestScope.shipment.shippingStatus == 'FAILED'}">
												<span class="badge bg-danger px-3 py-2">Giao that bai</span>
											</c:when>
											<c:otherwise>
												<span class="badge bg-light text-dark px-3 py-2">Dang xu ly</span>
											</c:otherwise>
										</c:choose>
									</div>
								</div>
							</c:if>

							<div class="card-body pb-0">
								<div class="row">
									<div class="col-lg-8">
										<h6 class="text-muted">Dia chi nguoi nhan</h6>
										<p class="lh-lg">
											${sessionScope.currentUser.fullname}<br>
											So dien thoai: ${sessionScope.currentUser.phoneNumber}<br>
											Dia chi: ${sessionScope.currentUser.address}
										</p>
									</div>
									<div class="col-lg-4">
										<h6 class="text-muted">Hinh thuc thanh toan</h6>
										<span class="text-success">
											<i class="fab fa-lg fa-cc-visa"></i>
											${requestScope.order.deliveryMethod == 1 ? "Giao tieu chuan" : "Giao nhanh"}
										</span>
										<p class="lh-lg">
											Tam tinh: <fmt:formatNumber pattern="#,##0" value="${requestScope.tempPrice}" />d<br>
											Phi van chuyen: <fmt:formatNumber pattern="#,##0" value="${requestScope.order.deliveryPrice}" />d<br>
											<strong>Tong cong: <fmt:formatNumber pattern="#,##0" value="${requestScope.tempPrice + requestScope.order.deliveryPrice}" />d</strong>
										</p>
									</div>
								</div>
							</div>

							<hr class="m-0">

							<div class="table-responsive">
								<table class="cart-table table table-borderless">
									<thead class="text-muted">
										<tr class="small text-uppercase">
											<th scope="col" style="min-width: 280px;">San pham</th>
											<th scope="col" style="min-width: 150px;">Gia</th>
											<th scope="col" style="min-width: 150px;">So luong</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="orderItem" items="${requestScope.orderItems}">
											<tr>
												<td>
													<figure class="itemside">
														<div class="float-start me-3">
															<c:choose>
																<c:when test="${empty orderItem.product.imageName}">
															<img width="80" height="80" src="${pageContext.request.contextPath}/img/280px.png" alt="280px.png">
														</c:when>
														<c:otherwise>
															<img width="80" height="80" src="${pageContext.request.contextPath}/image/${orderItem.product.imageName}" alt="${orderItem.product.imageName}">
														</c:otherwise>
													</c:choose>
												</div>
														<figcaption class="info">
															<a href="${pageContext.request.contextPath}/product?id=${orderItem.product.id}" target="_blank">${orderItem.product.name}</a>
														</figcaption>
													</figure>
												</td>
												<td>
													<div class="price-wrap">
														<c:choose>
															<c:when test="${orderItem.discount == 0}">
																<span class="price"><fmt:formatNumber pattern="#,##0" value="${orderItem.price}" />d</span>
															</c:when>
															<c:otherwise>
																<div>
																	<span class="price"><fmt:formatNumber pattern="#,##0" value="${orderItem.price * (100 - orderItem.discount) / 100}" />d</span>
																	<span class="ms-2 badge bg-info"> - <fmt:formatNumber pattern="#,##0" value="${orderItem.discount}" />%</span>
																</div>
																<small class="text-muted text-decoration-line-through"><fmt:formatNumber pattern="#,##0" value="${orderItem.price}" />d</small>
															</c:otherwise>
														</c:choose>
													</div>
												</td>
												<td>${orderItem.quantity}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>

							<div class="card-footer py-3">
								<c:choose>
									<c:when test="${requestScope.order.status == 1}">
										<form action="${pageContext.request.contextPath}/orderDetail" method="post" class="d-inline">
											<input type="hidden" name="id" value="${requestScope.order.id}">
											<c:choose>
												<c:when test="${empty requestScope.confirmCancel}">
													<button type="submit" name="action" value="requestCancel" class="btn btn-danger me-2">Huy don hang</button>
												</c:when>
												<c:otherwise>
													<span>Ban co chac chan muon huy don?</span>
													<button type="submit" name="action" value="confirmCancel" class="btn btn-danger me-2">Xac nhan huy</button>
													<button type="submit" name="action" value="cancelCancel" class="btn btn-secondary">Huy thao tac</button>
												</c:otherwise>
											</c:choose>
										</form>
									</c:when>
									<c:otherwise>
										<form action="${pageContext.request.contextPath}/cartItem" method="post" class="d-inline">
											<input type="hidden" name="action" value="add">
											<input type="hidden" name="userId" value="${sessionScope.currentUser.id}">
											<input type="hidden" name="productId" value="${requestScope.orderItems[0].product.id}">
											<input type="number" name="quantity" class="form-control w-auto d-inline" value="1" min="1" max="${requestScope.orderItems[0].product.quantity}" step="1">
											<button type="submit" class="btn btn-primary">Mua lai</button>
										</form>
									</c:otherwise>
								</c:choose>
							</div>

						</article>

						<!-- Shipper Contact -->
						<c:if test="${not empty requestScope.shipment && not empty requestScope.shipment.shipperName}">
							<div class="card mt-3">
								<div class="card-header" style="background: #f8f9fa;">
									<strong><i class="bi bi-person-vcard me-2"></i>Thong tin tai xe giao hang</strong>
								</div>
								<div class="card-body">
									<div class="d-flex align-items-center gap-3">
										<c:choose>
											<c:when test="${not empty requestScope.shipment.shipperAvatar}">
												<img src="${requestScope.shipment.shipperAvatar}" class="rounded-circle" width="55" height="55" alt="Shipper">
											</c:when>
											<c:otherwise>
												<div class="shipper-avatar-placeholder">${fn:substring(requestScope.shipment.shipperName, 0, 1)}</div>
											</c:otherwise>
										</c:choose>
										<div class="flex-grow-1">
											<div class="fw-bold">${requestScope.shipment.shipperName}</div>
											<c:if test="${not empty requestScope.shipment.shipperPhone}">
												<div class="text-muted small"><i class="bi bi-telephone me-1"></i>${requestScope.shipment.shipperPhone}</div>
											</c:if>
										</div>
										<c:if test="${not empty requestScope.shipment.shipperPhone}">
											<a href="tel:${requestScope.shipment.shipperPhone}" class="btn btn-success"><i class="bi bi-telephone me-1"></i>Goi ngay</a>
										</c:if>
									</div>
								</div>
							</div>
						</c:if>

						<!-- Shipment Timeline -->
						<c:if test="${not empty requestScope.shipment && not empty requestScope.trackingHistory}">
							<div class="card mt-3">
								<div class="card-header d-flex justify-content-between align-items-center" style="background: #f8f9fa;">
									<strong><i class="bi bi-map me-2"></i>Lich su van chuyen</strong>
									<a href="${pageContext.request.contextPath}/orderTracking?orderId=${requestScope.order.id}" class="btn btn-sm btn-outline-primary"><i class="bi bi-box-arrow-up-right me-1"></i>Chi tiet</a>
								</div>
								<div class="card-body p-3">
									<ul class="order-status-timeline mb-0">
										<c:forEach var="track" items="${requestScope.trackingHistory}">
											<li class="order-status-item">
												<div class="order-status-time">${track.updatedAt.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))}</div>
												<div class="order-status-title">
													<c:choose>
														<c:when test="${track.status == 'WAITING_PICKUP'}">Cho lay hang</c:when>
														<c:when test="${track.status == 'PICKED_UP'}">Da lay hang</c:when>
														<c:when test="${track.status == 'IN_TRANSIT'}">Dang van chuyen</c:when>
														<c:when test="${track.status == 'OUT_FOR_DELIVERY'}">Dang giao hang</c:when>
														<c:when test="${track.status == 'DELIVERED'}">Da giao hang</c:when>
														<c:when test="${track.status == 'FAILED'}">Giao that bai</c:when>
														<c:otherwise>${track.status}</c:otherwise>
													</c:choose>
												</div>
												<c:if test="${not empty track.note}"><p class="order-status-desc">${fn:escapeXml(track.note)}</p></c:if>
												<c:if test="${not empty track.location}"><small class="text-muted"><i class="bi bi-geo-alt me-1"></i>${fn:escapeXml(track.location)}</small></c:if>
											</li>
										</c:forEach>
									</ul>
								</div>
							</div>
						</c:if>

						<!-- Order Notes -->
						<div class="card mt-3">
							<div class="card-header d-flex justify-content-between align-items-center" style="background: #f8f9fa;">
								<strong><i class="bi bi-chat-left-text me-2"></i>Ghi chu don hang voi Admin</strong>
								<c:if test="${requestScope.unreadNoteCount > 0}">
									<span class="badge bg-primary">${requestScope.unreadNoteCount} chua doc</span>
								</c:if>
							</div>
							<div class="card-body p-0">
								<div style="max-height: 300px; overflow-y: auto;">
									<c:forEach var="note" items="${requestScope.orderNotes}">
										<div class="p-3 border-bottom ${note.noteType == 'ADMIN' ? 'bg-light' : ''}">
											<div class="d-flex justify-content-between align-items-start">
												<div>
													<strong class="${note.noteType == 'ADMIN' ? 'text-danger' : note.noteType == 'SYSTEM' ? 'text-secondary' : 'text-primary'}">
														${note.noteType == 'ADMIN' ? 'Quan tri vien' : note.noteType == 'SYSTEM' ? 'He thong' : sessionScope.currentUser.fullname}
													</strong>
													<c:if test="${not empty note.senderName}">
														<span class="text-muted small ms-1">(${note.senderName})</span>
													</c:if>
												</div>
												<small class="text-muted">${note.createdAt.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))}</small>
											</div>
											<p class="mb-0 mt-1">${fn:escapeXml(note.content)}</p>
											<c:if test="${note.isRead}"><small class="text-success"><i class="bi bi-check-circle"></i> Da xem</small></c:if>
										</div>
									</c:forEach>
									<c:if test="${empty requestScope.orderNotes}">
										<div class="p-4 text-center text-muted"><i class="bi bi-chat-dots fs-3 opacity-25"></i><p class="mb-0 mt-2">Chua co ghi chu nao</p></div>
									</c:if>
								</div>
								<div class="p-3" style="background: #f8f9fa;">
									<form action="${pageContext.request.contextPath}/orderNote" method="post">
										<input type="hidden" name="action" value="addNote">
										<input type="hidden" name="orderId" value="${requestScope.order.id}">
										<input type="hidden" name="noteType" value="CUSTOMER">
										<div class="input-group">
											<input type="text" name="content" class="form-control" placeholder="Nhap cau hoi, yeu cau ho tro cho don hang..." required>
											<button type="submit" class="btn btn-primary"><i class="bi bi-send"></i> Gui</button>
										</div>
									</form>
								</div>
							</div>
						</div>

						<!-- Product Review Section -->
						<c:if test="${requestScope.order.status == 2 && not empty requestScope.orderItems}">
							<div class="card mt-3" id="reviews">
								<div class="card-header" style="background: #f8f9fa;">
									<strong><i class="bi bi-star me-2"></i>Danh gia san pham</strong>
								</div>
								<div class="card-body">
									<c:forEach var="item" items="${requestScope.orderItems}">
										<div class="d-flex align-items-center gap-3 mb-3 pb-3 border-bottom">
											<img src="${pageContext.request.contextPath}/image/${item.product.imageName}" width="55" height="55" class="rounded object-fit-cover" alt="${item.product.name}" onerror="this.src='${pageContext.request.contextPath}/img/280px.png'">
											<div class="flex-grow-1">
												<div class="fw-semibold">${item.product.name}</div>
												<c:choose>
													<c:when test="${not empty requestScope.existingReviews[item.product.id]}">
														<div class="text-success small mt-1">
															<i class="bi bi-check-circle"></i> Da danh gia:
															<c:forEach begin="1" end="${requestScope.existingReviews[item.product.id].ratingScore}"><i class="bi bi-star-fill text-warning"></i></c:forEach>
															<c:forEach begin="${requestScope.existingReviews[item.product.id].ratingScore + 1}" end="5"><i class="bi bi-star text-warning"></i></c:forEach>
															<a href="${pageContext.request.contextPath}/submitReview?productId=${item.product.id}&amp;orderId=${requestScope.order.id}" class="ms-2 text-primary">Chinh sua</a>
														</div>
													</c:when>
													<c:otherwise>
														<a href="${pageContext.request.contextPath}/submitReview?productId=${item.product.id}&amp;orderId=${requestScope.order.id}" class="btn btn-outline-primary btn-sm mt-1"><i class="bi bi-star"></i> Danh gia ngay</a>
													</c:otherwise>
												</c:choose>
											</div>
										</div>
									</c:forEach>
								</div>
							</div>
						</c:if>

					</main>
				</c:when>
				<c:otherwise>
					<p>Vui long <a href="${pageContext.request.contextPath}/signin">dang nhap</a> de su dung trang nay.</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</section>

<jsp:include page="_footer.jsp" />
</body>
</html>
