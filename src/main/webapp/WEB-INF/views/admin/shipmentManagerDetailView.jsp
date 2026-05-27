<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
<head>
<jsp:include page="../_meta.jsp" />
<title>Chi tiết vận đơn #${requestScope.shipment.id}</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/shipmentManagerDetailView.css">
<style>
@media print {
	body { background: #fff !important; }
	body > * { display: none !important; }
	.print-only { display: block !important; background: #F5F5F5 !important; padding: 40px !important; width: 100% !important; }
}
</style>
</head>
<body class="d-flex flex-column min-vh-100">
	<jsp:include page="../_headerAdmin.jsp" />

	<section class="section-content">
		<div class="container">
			<c:if test="${not empty sessionScope.successMessage}">
				<div class="alert-soft alert-soft-success" role="alert">
					<i class="bi bi-check-circle-fill"></i> <span>${sessionScope.successMessage}</span>
				</div>
			</c:if>
			<c:if test="${not empty sessionScope.errorMessage}">
				<div class="alert-soft alert-soft-danger" role="alert">
					<i class="bi bi-exclamation-triangle-fill"></i> <span>${sessionScope.errorMessage}</span>
				</div>
			</c:if>
			<c:remove var="successMessage" scope="session" />
			<c:remove var="errorMessage" scope="session" />

			<nav class="breadcrumb-nav">
				<a href="${pageContext.request.contextPath}/admin"><i
					class="bi bi-house"></i> Trang chủ</a> <span class="separator">/</span>
				<a href="${pageContext.request.contextPath}/admin/shipmentManager">Quản
					lý vận đơn</a> <span class="separator">/</span> <span>Vận đơn
					#${requestScope.shipment.id}</span>
			</nav>

			<c:if test="${not empty requestScope.shipment}">
				<c:set var="s" value="${requestScope.shipment}" />

				<div class="shipment-hero">
					<div
						style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px;">
						<div>
							<h1>
								Vận đơn #${s.id}
								<code class="tracking-code">${s.trackingCode}</code>
							</h1>
							<div style="margin-top: 12px;">
								<a
									href="${pageContext.request.contextPath}/admin/shipmentManager"
									class="btn-back"> <i class="bi bi-arrow-left"></i> Quay lại
								</a>
								<button onclick="window.print()" class="btn-print"
									style="margin-left: 8px;">
									<i class="bi bi-printer"></i> In
								</button>
							</div>
						</div>
						<div>
							<c:choose>
								<c:when test="${s.shippingStatus == 'WAITING_PICKUP'}">
									<span class="status-badge status-waiting"><span
										class="dot"></span>Chờ lấy hàng</span>
								</c:when>
								<c:when test="${s.shippingStatus == 'PICKED_UP'}">
									<span class="status-badge status-picked"><span
										class="dot"></span>Đã lấy hàng</span>
								</c:when>
								<c:when test="${s.shippingStatus == 'IN_TRANSIT'}">
									<span class="status-badge status-transit"><span
										class="dot"></span>Đang vận chuyển</span>
								</c:when>
								<c:when test="${s.shippingStatus == 'OUT_FOR_DELIVERY'}">
									<span class="status-badge status-out"><span class="dot"></span>Đang
										giao hàng</span>
								</c:when>
								<c:when test="${s.shippingStatus == 'DELIVERED'}">
									<span class="status-badge status-delivered"><span
										class="dot"></span>Đã giao hàng</span>
								</c:when>
								<c:when test="${s.shippingStatus == 'FAILED'}">
									<span class="status-badge status-failed"><span
										class="dot"></span>Giao thất bại</span>
								</c:when>
								<c:otherwise>
									<span class="status-badge"
										style="background: var(--gray-100); color: var(--gray-500);"><span
										class="dot" style="background: var(--gray-400);"></span>${s.shippingStatus}</span>
								</c:otherwise>
							</c:choose>
						</div>
					</div>

					<div class="shipment-progress-wrapper">
						<input type="hidden" id="shipment-status-val"
							value="${s.shippingStatus}">
						<div class="shipment-progress-header">
							<h5 class="shipment-progress-title">
								<i class="bi bi-geo-alt-fill"></i> Tiến trình giao hàng
							</h5>
							<c:if test="${not empty s.estimatedDeliveryDate}">
								<div class="shipment-progress-eta">
									<i class="bi bi-calendar-event"></i> Dự kiến giao: <strong>${s.estimatedDeliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</strong>
								</div>
							</c:if>
						</div>

						<div class="shipment-progress-bar-container">
							<div class="shipment-progress-track">
								<div class="shipment-progress-fill" id="progress-fill"
									style="width: 0%"></div>

								<c:set var="isWaiting"
									value="${s.shippingStatus == 'WAITING_PICKUP'}" />
								<c:set var="isPicked" value="${s.shippingStatus == 'PICKED_UP'}" />
								<c:set var="isTransit"
									value="${s.shippingStatus == 'IN_TRANSIT'}" />
								<c:set var="isOut"
									value="${s.shippingStatus == 'OUT_FOR_DELIVERY'}" />
								<c:set var="isDelivered"
									value="${s.shippingStatus == 'DELIVERED'}" />
								<c:set var="isFailed"
									value="${s.shippingStatus == 'FAILED' || s.shippingStatus == 'RETURNED' || s.shippingStatus == 'CANCELLED'}" />
								<c:set var="isActive1"
									value="${isWaiting && empty requestScope.timeline}" />
								<c:set var="isActive2"
									value="${isPicked || (isWaiting && not empty requestScope.timeline)}" />
								<c:set var="isActive3" value="${isTransit}" />
								<c:set var="isActive4" value="${isOut}" />
								<c:set var="isActive5" value="${isDelivered}" />
								<c:set var="isDone" value="${isDelivered || isFailed}" />

								<c:set var="step1Class"
									value="${isFailed ? 'failed' : (isActive1 ? 'active' : (isPicked || isTransit || isOut || isDelivered ? 'completed' : ''))}" />
								<c:set var="step2Class"
									value="${isFailed ? 'failed' : (isActive2 ? 'active' : (isTransit || isOut || isDelivered ? 'completed' : ''))}" />
								<c:set var="step3Class"
									value="${isFailed ? 'failed' : (isActive3 ? 'active' : (isOut || isDelivered ? 'completed' : ''))}" />
								<c:set var="step4Class"
									value="${isFailed ? 'failed' : (isActive4 ? 'active' : (isDelivered ? 'completed' : ''))}" />
								<c:set var="step5Class"
									value="${isFailed ? 'failed' : (isDelivered ? 'completed' : '')}" />

								<c:set var="time1" value="" />
								<c:set var="time2" value="" />
								<c:set var="time3" value="" />
								<c:set var="time4" value="" />
								<c:set var="time5" value="" />

								<c:if test="${not empty s.createdAt}">
									<c:set var="time1"
										value="${s.createdAt.format(DateTimeFormatter.ofPattern('dd/MM HH:mm'))}" />
								</c:if>
								<c:if test="${not empty s.shippedAt}">
									<c:set var="time2"
										value="${s.shippedAt.format(DateTimeFormatter.ofPattern('dd/MM HH:mm'))}" />
								</c:if>
								<c:if test="${not empty s.deliveredAt}">
									<c:set var="time4"
										value="${s.deliveredAt.format(DateTimeFormatter.ofPattern('dd/MM HH:mm'))}" />
									<c:set var="time5"
										value="${s.deliveredAt.format(DateTimeFormatter.ofPattern('dd/MM HH:mm'))}" />
								</c:if>

								<!-- Step 1: Chờ lấy hàng -->
								<div class="shipment-progress-step ${step1Class}">
									<div class="shipment-progress-icon-wrap">
										<i class="bi bi-clock-fill step-icon"></i> <i
											class="bi bi-check step-check"></i> <i
											class="bi bi-clock step-active"></i> <i
											class="bi bi-x-lg step-failed"></i>
									</div>
									<div class="shipment-progress-step-label">
										<div class="shipment-progress-step-name">Chờ lấy</div>
										<div class="shipment-progress-step-time">${time1}</div>
									</div>
								</div>

								<!-- Step 2: Đã lấy hàng -->
								<div class="shipment-progress-step ${step2Class}">
									<div class="shipment-progress-icon-wrap">
										<i class="bi bi-box-seam-fill step-icon"></i> <i
											class="bi bi-check step-check"></i> <i
											class="bi bi-box-seam step-active"></i> <i
											class="bi bi-x-lg step-failed"></i>
									</div>
									<div class="shipment-progress-step-label">
										<div class="shipment-progress-step-name">Đã lấy</div>
										<div class="shipment-progress-step-time">${time2}</div>
									</div>
								</div>

								<!-- Step 3: Đang vận chuyển -->
								<div class="shipment-progress-step ${step3Class}">
									<div class="shipment-progress-icon-wrap">
										<i class="bi bi-truck step-icon"></i> <i
											class="bi bi-check step-check"></i> <i
											class="bi bi-truck step-active"></i> <i
											class="bi bi-x-lg step-failed"></i>
									</div>
									<div class="shipment-progress-step-label">
										<div class="shipment-progress-step-name">Vận chuyển</div>
										<div class="shipment-progress-step-time">${time3}</div>
									</div>
								</div>

								<!-- Step 4: Đang giao -->
								<div class="shipment-progress-step ${step4Class}">
									<div class="shipment-progress-icon-wrap">
										<i class="bi bi-geo-alt-fill step-icon"></i> <i
											class="bi bi-check step-check"></i> <i
											class="bi bi-geo-alt step-active"></i> <i
											class="bi bi-x-lg step-failed"></i>
									</div>
									<div class="shipment-progress-step-label">
										<div class="shipment-progress-step-name">Đang giao</div>
										<div class="shipment-progress-step-time">${time4}</div>
									</div>
								</div>

								<!-- Step 5: Đã giao -->
								<div class="shipment-progress-step ${step5Class}">
									<div class="shipment-progress-icon-wrap">
										<i class="bi bi-check-circle-fill step-icon"></i> <i
											class="bi bi-check step-check"></i> <i
											class="bi bi-check-circle step-active"></i> <i
											class="bi bi-x-lg step-failed"></i>
									</div>
									<div class="shipment-progress-step-label">
										<div class="shipment-progress-step-name">Đã giao</div>
										<div class="shipment-progress-step-time">${time5}</div>
									</div>
								</div>
							</div>
						</div>

						<!-- Mini dot progress -->
						<div class="shipment-progress-mini">
							<div class="shipment-progress-mini-dot ${step1Class}"></div>
							<div class="shipment-progress-mini-dot ${step2Class}"></div>
							<div class="shipment-progress-mini-dot ${step3Class}"></div>
							<div class="shipment-progress-mini-dot ${step4Class}"></div>
							<div class="shipment-progress-mini-dot ${step5Class}"></div>
						</div>

						<!-- Status row -->
						<div class="shipment-progress-status-row">
							<div class="shipment-progress-current-status">
								<span class="status-label">Trạng thái:</span> <span
									class="status-text ${step1Class != '' ? step1Class : ''}">
									<c:choose>
										<c:when test="${s.shippingStatus == 'WAITING_PICKUP'}">Chờ lấy hàng</c:when>
										<c:when test="${s.shippingStatus == 'PICKED_UP'}">Đã lấy hàng</c:when>
										<c:when test="${s.shippingStatus == 'IN_TRANSIT'}">Đang vận chuyển</c:when>
										<c:when test="${s.shippingStatus == 'OUT_FOR_DELIVERY'}">Đang giao hàng</c:when>
										<c:when test="${s.shippingStatus == 'DELIVERED'}">Đã giao hàng thành công</c:when>
										<c:when test="${s.shippingStatus == 'FAILED'}">Giao hàng thất bại</c:when>
										<c:when test="${s.shippingStatus == 'RETURNED'}">Đã trả hàng</c:when>
										<c:when test="${s.shippingStatus == 'CANCELLED'}">Đã hủy</c:when>
										<c:otherwise>${s.shippingStatus}</c:otherwise>
									</c:choose>
								</span>
							</div>
							<div class="shipment-progress-eta"
								style="border: none; background: transparent; padding: 0;">
								<i class="bi bi-hash" style="color: var(--gray-400);"></i> Mã
								vận đơn: <strong>${s.trackingCode}</strong>
							</div>
						</div>
					</div>

					<div class="hero-stats">
						<div class="hero-stat">
							<div class="stat-label">Phí vận chuyển</div>
							<div class="stat-value">
								<fmt:formatNumber pattern="#,##0" value="${s.shippingFee}" />
								₫
							</div>
						</div>
						<div class="hero-stat">
							<div class="stat-label">Trọng lượng</div>
							<div class="stat-value">${s.totalWeight}kg</div>
						</div>
						<div class="hero-stat">
							<div class="stat-label">Ngày tạo</div>
							<div class="stat-value">
								<c:if test="${not empty s.createdAt}">${s.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</c:if>
							</div>
						</div>
						<div class="hero-stat">
							<div class="stat-label">Dự kiến giao</div>
							<div class="stat-value">
								<c:if test="${not empty s.estimatedDeliveryDate}">${s.estimatedDeliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</c:if>
							</div>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-md-6">
						<div class="info-card">
							<div class="card-header">
								<i class="bi bi-person-circle"></i> Thông tin người nhận
							</div>
							<div class="card-body">
								<div class="info-row">
									<div class="info-icon blue">
										<i class="bi bi-person"></i>
									</div>
									<div>
										<div class="info-label">Họ tên</div>
										<div class="info-value">${s.receiverName}</div>
									</div>
								</div>
								<div class="info-row">
									<div class="info-icon green">
										<i class="bi bi-telephone"></i>
									</div>
									<div>
										<div class="info-label">Số điện thoại</div>
										<div class="info-value">${s.receiverPhone}</div>
									</div>
								</div>
								<div class="info-row">
									<div class="info-icon orange">
										<i class="bi bi-house"></i>
									</div>
									<div>
										<div class="info-label">Địa chỉ giao hàng</div>
										<div class="info-value">${s.addressDetail}<c:if
												test="${not empty s.ward}">, ${s.ward}</c:if>
											<c:if test="${not empty s.district}">, ${s.district}</c:if>
											<c:if test="${not empty s.province}">, ${s.province}</c:if>
										</div>
									</div>
								</div>
								<c:if test="${not empty s.customerNote}">
									<div class="info-row">
										<div class="info-icon purple">
											<i class="bi bi-sticky"></i>
										</div>
										<div>
											<div class="info-label">Ghi chú khách hàng</div>
											<div class="info-value">${s.customerNote}</div>
										</div>
									</div>
								</c:if>
							</div>
						</div>
					</div>

					<div class="col-md-6">
						<div class="info-card">
							<div class="card-header">
								<i class="bi bi-box-seam"></i> Thông tin đơn hàng
							</div>
							<div class="card-body">
								<div class="info-row">
									<div class="info-icon blue">
										<i class="bi bi-hash"></i>
									</div>
									<div>
										<div class="info-label">Mã đơn hàng</div>
										<div class="info-value">
											<a
												href="${pageContext.request.contextPath}/admin/orderManager/detail?id=${s.orderId}"
												class="text-primary text-decoration-none"> #${s.orderId}
											</a>
										</div>
									</div>
								</div>
								<div class="info-row">
									<div class="info-icon purple">
										<i class="bi bi-truck"></i>
									</div>
									<div>
										<div class="info-label">Đơn vị vận chuyển</div>
										<div class="info-value">
											<c:choose>
												<c:when test="${not empty requestScope.shippingMethod.name}">${requestScope.shippingMethod.name}</c:when>
												<c:otherwise>GHN - Giao Hàng Nhanh</c:otherwise>
											</c:choose>
										</div>
									</div>
								</div>
								<c:if test="${not empty s.providerOrderCode}">
									<div class="info-row">
										<div class="info-icon green">
											<i class="bi bi-qr-code"></i>
										</div>
										<div>
											<div class="info-label">Mã vận đơn GHN</div>
											<div class="info-value code">${s.providerOrderCode}</div>
										</div>
									</div>
								</c:if>
								<div class="info-row">
									<div class="info-icon red">
										<i class="bi bi-currency-dollar"></i>
									</div>
									<div>
										<div class="info-label">Phí vận chuyển</div>
										<div class="info-value"
											style="color: var(--primary); font-weight: 700;">
											<fmt:formatNumber pattern="#,##0" value="${s.shippingFee}" />
											₫
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-lg-8">
						<div class="timeline-section">
							<div class="timeline-header">
								<i class="bi bi-activity"></i> Lịch sử vận chuyển
							</div>

							<c:choose>
								<c:when test="${not empty requestScope.timeline}">
									<div class="detail-timeline">
										<c:forEach var="t" items="${requestScope.timeline}"
											varStatus="loop">
											<c:set var="tlClass" value="completed" />
											<c:if
												test="${loop.first && s.shippingStatus != 'DELIVERED' && s.shippingStatus != 'FAILED' && s.shippingStatus != 'CANCELLED' && s.shippingStatus != 'RETURNED'}">
												<c:set var="tlClass" value="current" />
											</c:if>
											<c:if
												test="${t.status == 'FAILED' || t.status == 'CANCELLED'}">
												<c:set var="tlClass" value="failed" />
											</c:if>
											<c:if test="${t.status == 'DELIVERED'}">
												<c:set var="tlClass" value="delivered" />
											</c:if>
											<div class="detail-timeline-item ${tlClass}">
												<div class="detail-timeline-dot">
													<c:if test="${tlClass == 'current'}">
														<i class="bi bi-arrow-right"></i>
													</c:if>
													<c:if test="${tlClass == 'completed'}">
														<i class="bi bi-check"></i>
													</c:if>
													<c:if test="${tlClass == 'failed'}">
														<i class="bi bi-x"></i>
													</c:if>
													<c:if test="${tlClass == 'delivered'}">
														<i class="bi bi-check-lg"></i>
													</c:if>
												</div>
												<div class="detail-timeline-card">
													<div class="detail-timeline-status">
														<c:if test="${tlClass == 'current'}">
															<span class="status-dot"></span>
														</c:if>
														<c:if test="${tlClass != 'current'}">
															<span class="status-dot"
																style="animation: none; background: var(--success)"></span>
														</c:if>
														${t.status == 'WAITING_PICKUP' ? 'Chờ lấy hàng' :
                                                      t.status == 'PICKED_UP' ? 'Đã lấy hàng' :
                                                      t.status == 'IN_TRANSIT' ? 'Đang vận chuyển' :
                                                      t.status == 'OUT_FOR_DELIVERY' ? 'Đang giao hàng' :
                                                      t.status == 'DELIVERED' ? 'Đã giao hàng' :
                                                      t.status == 'FAILED' ? 'Giao hàng thất bại' :
                                                      t.status == 'RETURNED' ? 'Đã trả hàng' :
                                                      t.status == 'CANCELLED' ? 'Đã hủy' :
                                                      t.status}
													</div>
													<div class="detail-timeline-meta">
														<span><i class="bi bi-clock"></i>
															${t.updatedAt.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))}</span>
														<c:if test="${not empty t.location}">
															<span><i class="bi bi-geo-alt"></i> ${t.location}</span>
														</c:if>
														<c:if test="${not empty t.updatedBy}">
															<span><i class="bi bi-person"></i> ${t.updatedBy}</span>
														</c:if>
													</div>
													<c:if test="${not empty t.note}">
														<div class="detail-timeline-note">"${t.note}"</div>
													</c:if>
												</div>
											</div>
										</c:forEach>
									</div>
								</c:when>
								<c:otherwise>
									<div class="text-center py-4 text-muted">
										<i class="bi bi-clock-history" style="font-size: 2rem;"></i>
										<p class="mt-2 mb-0">Chưa có lịch sử vận chuyển</p>
									</div>
								</c:otherwise>
							</c:choose>

							<c:if
								test="${s.shippingStatus != 'DELIVERED' && s.shippingStatus != 'CANCELLED' && s.shippingStatus != 'RETURNED'}">
								<div class="update-form">
									<h6>
										<i class="bi bi-arrow-repeat"></i> Cập nhật trạng thái vận
										chuyển
									</h6>
									<form
										action="${pageContext.request.contextPath}/admin/shipmentManager"
										method="post">
										<input type="hidden" name="action" value="addTracking" /> <input
											type="hidden" name="id" value="${s.id}" />

										<div class="row g-3">
											<div class="col-md-6">
												<label class="form-label">Trạng thái mới</label> <select
													class="form-select" name="trackingStatus" required>
													<option value="">-- Chọn trạng thái --</option>
													<option value="WAITING_PICKUP">Chờ lấy hàng</option>
													<option value="PICKED_UP">Đã lấy hàng</option>
													<option value="IN_TRANSIT">Đang vận chuyển</option>
													<option value="OUT_FOR_DELIVERY">Đang giao hàng</option>
													<option value="DELIVERED">Đã giao hàng</option>
													<option value="FAILED">Giao thất bại</option>
												</select>
											</div>
											<div class="col-md-6">
												<label class="form-label">Người cập nhật</label> <input
													type="text" class="form-control" name="updatedBy"
													value="Admin" />
											</div>
											<div class="col-12">
												<label class="form-label">Địa điểm</label> <input
													type="text" class="form-control" name="trackingLocation"
													placeholder="VD: Tp.HCM, Quận Thủ Đức" />
											</div>
											<div class="col-12">
												<label class="form-label">Ghi chú</label>
												<textarea class="form-control" name="trackingNote" rows="2"
													placeholder="VD: Shipper đang giao hàng cho người nhận"></textarea>
											</div>
										</div>

										<div class="mt-3">
											<button type="submit" class="btn-update">
												<i class="bi bi-check-circle"></i> Cập nhật trạng thái
											</button>
										</div>
									</form>
								</div>
							</c:if>
						</div>
					</div>

					<div class="col-lg-4">
						<div class="info-card">
							<div class="card-header">
								<i class="bi bi-clock-history"></i> Thông tin thời gian
							</div>
							<div class="card-body">
								<div class="info-row">
									<div class="info-icon blue">
										<i class="bi bi-calendar-plus"></i>
									</div>
									<div>
										<div class="info-label">Ngày tạo</div>
										<div class="info-value">
											<c:if test="${not empty s.createdAt}">${s.createdAt.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))}</c:if>
										</div>
									</div>
								</div>
								<div class="info-row">
									<div class="info-icon orange">
										<i class="bi bi-calendar-event"></i>
									</div>
									<div>
										<div class="info-label">Dự kiến giao</div>
										<div class="info-value">
											<c:if test="${not empty s.estimatedDeliveryDate}">${s.estimatedDeliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</c:if>
										</div>
									</div>
								</div>
								<c:if test="${not empty s.shippedAt}">
									<div class="info-row">
										<div class="info-icon green">
											<i class="bi bi-box-seam"></i>
										</div>
										<div>
											<div class="info-label">Ngày lấy hàng</div>
											<div class="info-value">${s.shippedAt.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))}</div>
										</div>
									</div>
								</c:if>
								<c:if test="${not empty s.deliveredAt}">
									<div class="info-row">
										<div class="info-icon purple">
											<i class="bi bi-check-circle"></i>
										</div>
										<div>
											<div class="info-label">Ngày giao hàng</div>
											<div class="info-value">${s.deliveredAt.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"))}</div>
										</div>
									</div>
								</c:if>
							</div>
						</div>

						<c:if test="${not empty s.shipperName}">
							<div class="info-card">
								<div class="card-header">
									<i class="bi bi-motorcycle"></i> Thông tin shipper
								</div>
								<div class="card-body">
									<div class="info-row">
										<div class="info-icon blue">
											<i class="bi bi-person"></i>
										</div>
										<div>
											<div class="info-label">Tên shipper</div>
											<div class="info-value">${s.shipperName}</div>
										</div>
									</div>
									<div class="info-row">
										<div class="info-icon green">
											<i class="bi bi-telephone"></i>
										</div>
										<div>
											<div class="info-label">Số điện thoại</div>
											<div class="info-value">${s.shipperPhone}</div>
										</div>
									</div>
								</div>
							</div>
						</c:if>
					</div>
				</div>
			</c:if>

			<c:if test="${empty requestScope.shipment}">
			<div class="info-card">
				<div class="card-body text-center" style="padding: 60px;">
					<i class="bi bi-inbox"
						style="font-size: 4rem; color: var(--gray-300);"></i>
					<h5 style="color: var(--gray-400); margin: 20px 0 10px;">Không
						tìm thấy vận đơn</h5>
					<a href="${pageContext.request.contextPath}/admin/shipmentManager"
						class="btn btn-primary" style="border-radius: 8px;"> <i
						class="bi bi-arrow-left"></i> Quay lại danh sách
					</a>
				</div>
			</div>
		</c:if>
	</div>
</section>

<!-- Hóa đơn chỉ hiển thị khi in -->
<c:if test="${not empty requestScope.shipment}">
<div class="print-only" style="display: none; font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif; background: #F5F5F5; padding: 40px;">
	<div style="max-width: 800px; margin: 0 auto; background: #fff; padding: 40px; border: 1px solid #ddd;">
		<!-- Header -->
		<div style="display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 3px solid #0F2C59; padding-bottom: 20px; margin-bottom: 30px;">
			<div style="display: flex; align-items: center; gap: 15px;">
				<div style="width: 50px; height: 50px; background: #0F2C59; display: flex; align-items: center; justify-content: center; color: #fff; font-weight: bold; font-size: 24px;">B</div>
				<div>
					<div style="font-size: 18px; font-weight: 700; color: #0F2C59;">BookShop</div>
					<div style="font-size: 12px; color: #666;">123 Nguyen Hue, District 1, HCMC<br>Hotline: 0901 234 567</div>
				</div>
			</div>
			<div style="text-align: right;">
				<div style="font-size: 20px; font-weight: 700; color: #0F2C59;">PHIẾU VẬN CHUYỂN</div>
				<div style="font-size: 14px; color: #333; margin-top: 5px;">Mã đơn hàng: <strong>#${s.id}</strong></div>
				<c:if test="${not empty s.trackingCode}">
					<div style="font-size: 12px; color: #666;">Mã vận đơn: ${s.trackingCode}</div>
				</c:if>
				<c:if test="${not empty s.createdAt}">
					<div style="font-size: 12px; color: #666;">Ngày tạo: ${s.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</div>
				</c:if>
				<div style="margin-top: 8px;">
					<c:choose>
						<c:when test="${s.shippingStatus == 'WAITING_PICKUP'}"><span style="background: #FFF3CD; color: #856404; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600;">Chờ lấy hàng</span></c:when>
						<c:when test="${s.shippingStatus == 'PICKED_UP'}"><span style="background: #D4EDDA; color: #155724; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600;">Đã lấy hàng</span></c:when>
						<c:when test="${s.shippingStatus == 'IN_TRANSIT'}"><span style="background: #FFF3CD; color: #856404; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600;">Đang vận chuyển</span></c:when>
						<c:when test="${s.shippingStatus == 'OUT_FOR_DELIVERY'}"><span style="background: #CCE5FF; color: #004085; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600;">Đang giao hàng</span></c:when>
						<c:when test="${s.shippingStatus == 'DELIVERED'}"><span style="background: #D4EDDA; color: #155724; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600;">Đã giao hàng</span></c:when>
						<c:when test="${s.shippingStatus == 'FAILED'}"><span style="background: #F8D7DA; color: #721C24; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600;">Giao thất bại</span></c:when>
						<c:when test="${s.shippingStatus == 'RETURNED'}"><span style="background: #E2E3E5; color: #383D41; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600;">Đã trả hàng</span></c:when>
						<c:when test="${s.shippingStatus == 'CANCELLED'}"><span style="background: #F8D7DA; color: #721C24; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600;">Đã hủy</span></c:when>
						<c:otherwise><span style="background: #E2E3E5; color: #383D41; padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: 600;">${s.shippingStatus}</span></c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>

		<!-- Shipping Info Section -->
		<div style="display: flex; gap: 30px; margin-bottom: 30px;">
			<div style="flex: 1; border: 1px solid #ddd; padding: 20px;">
				<div style="font-size: 14px; font-weight: 700; color: #0F2C59; border-bottom: 2px solid #0F2C59; padding-bottom: 8px; margin-bottom: 15px;">THÔNG TIN NGƯỜI NHẬN</div>
				<div style="margin-bottom: 10px;">
					<div style="font-size: 12px; color: #666;">Họ tên</div>
					<div style="font-size: 14px; font-weight: 600; color: #333;">${s.receiverName}</div>
				</div>
				<div style="margin-bottom: 10px;">
					<div style="font-size: 12px; color: #666;">Điện thoại</div>
					<div style="font-size: 14px; color: #333;">${s.receiverPhone}</div>
				</div>
				<div>
					<div style="font-size: 12px; color: #666;">Địa chỉ</div>
					<div style="font-size: 14px; color: #333;">${s.addressDetail}<c:if test="${not empty s.ward}">, ${s.ward}</c:if><c:if test="${not empty s.district}">, ${s.district}</c:if><c:if test="${not empty s.province}">, ${s.province}</c:if></div>
				</div>
			</div>
			<div style="flex: 1; border: 1px solid #ddd; padding: 20px;">
				<div style="font-size: 14px; font-weight: 700; color: #0F2C59; border-bottom: 2px solid #0F2C59; padding-bottom: 8px; margin-bottom: 15px;">THÔNG TIN VẬN CHUYỂN</div>
				<div style="margin-bottom: 10px;">
					<div style="font-size: 12px; color: #666;">Mã vận đơn</div>
					<div style="font-size: 14px; font-weight: 600; color: #0F2C59;">${s.trackingCode}</div>
				</div>
				<div style="margin-bottom: 10px;">
					<div style="font-size: 12px; color: #666;">Đơn vị vận chuyển</div>
					<div style="font-size: 14px; color: #333;">
						<c:choose>
							<c:when test="${not empty requestScope.shippingMethod.name}">${requestScope.shippingMethod.name}</c:when>
							<c:otherwise>GHN - Giao Hàng Nhanh</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div>
					<div style="font-size: 12px; color: #666;">Phí vận chuyển</div>
					<div style="font-size: 16px; font-weight: 700; color: #0F2C59;"><fmt:formatNumber pattern="#,##0" value="${s.shippingFee}" />₫</div>
				</div>
			</div>
		</div>

		<!-- Order Details Table -->
		<c:if test="${not empty requestScope.orderItems}">
			<div style="margin-bottom: 30px;">
				<div style="font-size: 14px; font-weight: 700; color: #0F2C59; border-bottom: 2px solid #0F2C59; padding-bottom: 8px; margin-bottom: 15px;">CHI TIẾT ĐƠN HÀNG</div>
				<table style="width: 100%; border-collapse: collapse; font-size: 13px;">
					<thead>
						<tr style="background: #F5F5F5;">
							<th style="padding: 12px; text-align: left; border-bottom: 2px solid #ddd; color: #0F2C59; font-weight: 600;">STT</th>
							<th style="padding: 12px; text-align: left; border-bottom: 2px solid #ddd; color: #0F2C59; font-weight: 600;">Tên sản phẩm</th>
							<th style="padding: 12px; text-align: center; border-bottom: 2px solid #ddd; color: #0F2C59; font-weight: 600;">SL</th>
							<th style="padding: 12px; text-align: right; border-bottom: 2px solid #ddd; color: #0F2C59; font-weight: 600;">Đơn giá</th>
							<th style="padding: 12px; text-align: right; border-bottom: 2px solid #ddd; color: #0F2C59; font-weight: 600;">Thành tiền</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="item" items="${requestScope.orderItems}" varStatus="loop">
							<tr>
								<td style="padding: 12px; border-bottom: 1px solid #eee;">${loop.index + 1}</td>
								<td style="padding: 12px; border-bottom: 1px solid #eee;">${not empty item.product ? item.product.name : '—'}</td>
								<td style="padding: 12px; border-bottom: 1px solid #eee; text-align: center;">${item.quantity}</td>
								<td style="padding: 12px; border-bottom: 1px solid #eee; text-align: right;"><fmt:formatNumber pattern="#,##0" value="${item.discount > 0 ? item.price * (100 - item.discount) / 100 : item.price}" />₫</td>
								<td style="padding: 12px; border-bottom: 1px solid #eee; text-align: right; font-weight: 600;"><fmt:formatNumber pattern="#,##0" value="${(item.discount > 0 ? item.price * (100 - item.discount) / 100 : item.price) * item.quantity}" />₫</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:if>

		<!-- Signature Lines -->
		<div style="display: flex; justify-content: space-around; margin: 50px 0 30px;">
			<div style="text-align: center;">
				<div style="width: 150px; border-bottom: 1px solid #333; margin-bottom: 8px; height: 40px;"></div>
				<div style="font-size: 12px; color: #666;">Người giao hàng</div>
			</div>
			<div style="text-align: center;">
				<div style="width: 150px; border-bottom: 1px solid #333; margin-bottom: 8px; height: 40px;"></div>
				<div style="font-size: 12px; color: #666;">Người nhận</div>
			</div>
		</div>

		<!-- Footer -->
		<div style="text-align: center; border-top: 1px solid #ddd; padding-top: 20px;">
			<div style="font-size: 14px; color: #0F2C59; font-weight: 600;">Cảm ơn quý khách đã đặt hàng tại BookShop</div>
		</div>
	</div>
</div>
</c:if>

	<jsp:include page="../_footerAdmin.jsp" />
	<script>
		(function() {
			var status = document.getElementById('shipment-status-val');
			if (!status)
				return;
			var stepMap = {
				'WAITING_PICKUP' : 1,
				'PICKED_UP' : 2,
				'IN_TRANSIT' : 3,
				'OUT_FOR_DELIVERY' : 4,
				'DELIVERED' : 5,
				'FAILED' : 1,
				'RETURNED' : 1,
				'CANCELLED' : 1
			};
			var currentStep = stepMap[status.value] || 0;
			var maxStep = 5;
			var fillPercent = ((currentStep - 1) / (maxStep - 1)) * 100;
			var fill = document.getElementById('progress-fill');
			if (fill) {
				setTimeout(function() {
					fill.style.width = fillPercent + '%';
				}, 200);
			}
		})();
	</script>
</body>
</html>
