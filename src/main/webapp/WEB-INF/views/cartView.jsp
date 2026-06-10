<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="_meta.jsp" />
    <title>Giỏ hàng - Shop Bán Sách</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cartView.css">
</head>
<body>
<jsp:include page="_header.jsp" />

<div class="loading-overlay" id="loadingOverlay">
    <div class="loading-spinner"></div>
</div>

<section class="section-pagetop bg-light">
    <div class="container">
        <h2 class="title-page">Giỏ hàng của bạn</h2>
    </div>
</section>

<section class="section-content padding-y">
    <div class="container">
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${sessionScope.errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        <c:choose>
            <c:when test="${empty sessionScope.currentUser}">
                <div class="col-12 text-center py-5">
                    <p class="text-muted mb-3">Vui lòng đăng nhập để sử dụng trang này.</p>
                    <a href="${pageContext.request.contextPath}/signin" class="btn btn-primary px-4">Đăng nhập
                        ngay</a>
                </div>
            </c:when>

            <c:when test="${empty cartItems}">
                <div class="empty-cart">
                    <i class="bi bi-cart-x"></i>
                    <h5>Giỏ hàng trống</h5>
                    <p>Hãy thêm sản phẩm vào giỏ hàng để bắt đầu mua sắm</p>
                    <a href="${pageContext.request.contextPath}/" class="btn-shop">Bắt đầu mua sắm</a>
                </div>
            </c:when>

            <c:otherwise>
                <form id="checkoutForm" action="${pageContext.request.contextPath}/cart" method="post" data-submitting="false">
                    <input type="hidden" name="cartId" value="${cartId}" />
                    <input type="hidden" id="shippingAddressId" name="shippingAddressId" value="${defaultAddress != null ? defaultAddress.id : ''}" />
                    <input type="hidden" id="selectedServiceId" name="selectedServiceId" value="2" />
                    <input type="hidden" id="deliveryPrice" name="deliveryPrice" value="0" />
                    <input type="hidden" id="estimatedDays" name="estimatedDays" value="2" />
                    <input type="hidden" id="toDistrictId" name="toDistrictId" value="" />
                    <input type="hidden" id="toWardCode" name="toWardCode" value="" />
                    <input type="hidden" id="addressDetailHidden" name="addressDetailHidden" value="" />
                    <input type="hidden" id="provinceName" name="provinceName" value="" />
                    <input type="hidden" id="districtName" name="districtName" value="" />
                    <input type="hidden" id="wardName" name="wardName" value="" />
                    <input type="hidden" id="deliveryMethod" name="deliveryMethod" value="2" />

                    <input type="hidden" name="selectedVoucherId" id="inputVoucherId" value="" />
                    <input type="hidden" name="selectedShipVoucherId" id="inputShipVoucherId" value="" />
                    <input type="hidden" id="finalVoucherId" name="finalVoucherId" value="">
                    <input type="hidden" id="finalShipVoucherId" name="finalShipVoucherId" value="">
                    <div class="row">
                        <div class="col-lg-8">
                            <div class="cart-main">
                                <div class="cart-header">
                                    <div class="col-product">Sản phẩm</div>
                                    <div class="col-price">Đơn giá</div>
                                    <div class="col-quantity">Số lượng</div>
                                    <div class="col-total">Thành tiền</div>
                                    <div class="col-action"></div>
                                </div>

                                <div class="cart-shop">
                                    <div class="cart-shop-name">
                                        <i class="bi bi-shop"></i>
                                        Shop Bán Sách
                                    </div>
                                </div>

                                <c:set var="tempTotal" value="0" />
                                <c:forEach var="item" items="${cartItems}">
                                    <c:set var="itemTotal" value="${item.product.price * item.quantity}" />
                                    <c:set var="tempTotal" value="${tempTotal + itemTotal}" />

                                    <div class="cart-item" data-product-id="${item.productId}" data-weight="${item.product.weight > 0 ? item.product.weight : 300}" data-length="${item.product.length > 0 ? item.product.length : 20}" data-width="${item.product.width > 0 ? item.product.width : 15}" data-height="${item.product.height > 0 ? item.product.height : 10}">
                                        <div class="item-checkbox">
                                            <input type="checkbox" name="selectedItems" value="${item.id}" checked />
                                        </div>

                                        <div class="item-product">
                                            <c:choose>
                                                <c:when test="${empty item.product.imageName}">
                                                    <img src="${pageContext.request.contextPath}/img/280px.png" class="item-image" alt="${item.product.name}" />
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}/image/${item.product.imageName}" class="item-image" alt="${item.product.name}" />
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="item-info">
                                                <a href="${pageContext.request.contextPath}/product?id=${item.product.id}" class="item-name">${item.product.name}</a>
                                                <small class="text-muted d-block mt-1">Tác giả: ${item.product.author}</small>
                                            </div>
                                        </div>

                                        <div class="item-price">
                                            <fmt:formatNumber value="${item.product.price}" pattern="#,##0" />₫
                                        </div>

                                        <div class="item-quantity">
                                            <div class="quantity-control">
                                                <button type="button" class="qty-btn" onclick="updateQuantity(${item.id}, ${item.quantity}, -1)">-</button>
                                                <input type="number" id="qty-${item.id}" value="${item.quantity}" min="1" max="${item.product.quantity > 0 ? item.product.quantity : 1}" onchange="updateQuantityDirect(${item.id})" />
                                                <button type="button" class="qty-btn" onclick="updateQuantity(${item.id}, ${item.quantity}, 1)">+</button>
                                            </div>
                                        </div>

                                        <div class="item-total">
                                            <fmt:formatNumber value="${itemTotal}" pattern="#,##0" />₫
                                        </div>

                                        <div class="item-action">
                                            <button type="button" class="btn-delete" onclick="deleteCartItem(${item.id})" title="Xóa sản phẩm">
                                                <i class="bi bi-trash3"></i>
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                                <input type="hidden" id="inputSubTotal" value="${tempTotal}">
                            </div>

                            <div class="shipping-section">
                                <div class="shipping-header">
                                    <i class="bi bi-geo-alt-fill"></i>
                                    <span>Thông tin nhận hàng</span>
                                </div>
                                <div class="shipping-body">
                                    <c:choose>
                                        <c:when test="${not empty defaultAddress}">
                                            <div class="col-12" id="defaultAddressContainer"
                                                 data-district-id="${defaultAddress.districtId}"
                                                 data-ward-code="${defaultAddress.wardCode}">
                                                <div class="p-3 rounded border d-flex justify-content-between align-items-center shadow-sm border-warning bg-warning-subtle">
                                                    <div>
                                                        <div class="d-flex align-items-center mb-2">
                                                            <span class="fw-bold me-2">${defaultAddress.fullname}</span>
                                                            <span class="text-muted border-start ps-2">${defaultAddress.phone}</span>
                                                        </div>
                                                        <p class="mb-1 text-secondary small">${defaultAddress.addressDetail}</p>
                                                        <p class="mb-2 text-secondary small">${defaultAddress.fullAddress}</p>
                                                    </div>
                                                    <a href="${pageContext.request.contextPath}/addressBook" class="btn btn-outline-warning btn-sm ms-3">
                                                        Thay đổi
                                                    </a>
                                                </div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="col-12">
                                                <div class="p-3 rounded border d-flex justify-content-between align-items-center shadow-sm border-secondary-subtle bg-light">
                                                    <span class="text-secondary">Chưa chọn thông tin nhận hàng mặc định!</span>
                                                    <a href="${pageContext.request.contextPath}/addressBook" class="btn btn-outline-primary btn-sm ms-3">
                                                        Chọn địa chỉ
                                                    </a>
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>

                                    <div class="shipping-options" id="shippingOptions">
                                        <div class="shipping-options-title">
                                            <i class="bi bi-truck"></i>
                                            <span>Phương thức vận chuyển</span>
                                        </div>
                                        <div id="shippingOptionsList">
                                            <div class="alert-info">
                                                <i class="bi bi-info-circle"></i>
                                                Vui lòng chọn địa chỉ giao hàng để xem các phương thức vận chuyển
                                            </div>
                                        </div>
                                    </div>

                                    <div class="shipping-note">
                                        <label class="form-label">
                                            <i class="bi bi-sticky"></i>
                                            <span>Ghi chú giao hàng (tùy chọn)</span>
                                        </label>
                                        <textarea class="form-control" id="customerNote" name="customerNote" rows="2" placeholder="Ví dụ: Gọi điện trước khi giao, giao giờ hành chính..."></textarea>
                                    </div>

                                    <div class="alert-warning" id="shippingAlert">
                                        <i class="bi bi-exclamation-triangle"></i>
                                        <span id="shippingAlertText"></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-4">
                            <div class="order-summary">
                                <div class="summary-section">
                                    <div class="summary-row">
                                        <span>Tạm tính (<span id="selectedCount">${cartItems.size()}</span> sản phẩm)</span>
                                        <span class="value" id="subtotalDisplay">
                                            <fmt:formatNumber value="${tempTotal}" pattern="#,##0" />₫
                                        </span>
                                    </div>
                                    <div class="summary-row">
                                        <span>Phí vận chuyển</span>
                                        <span class="value" id="shippingFeeDisplay">---</span>
                                    </div>
                                    <div class="summary-row text-danger">
                                        <span>Giảm giá đơn hàng</span>
                                        <span class="value" id="discountDisplay">0₫</span>
                                    </div>

                                    <div class="summary-row text-danger">
                                        <span>Giảm phí vận chuyển</span>
                                        <span class="value" id="shipDiscountDisplay">0₫</span>
                                    </div>
                                </div>

                                <div class="summary-section border-top pt-2 mb-2">
                                    <div class="p-2 d-flex align-items-center justify-content-between rounded"
                                         style="background-color: #fff5f3; border: 1px dashed #ee4d2d; cursor: pointer;"
                                         data-bs-toggle="modal" data-bs-target="#voucherModal">
                                        <div class="d-flex align-items-center" style="color: #ee4d2d;">
                                            <i class="bi bi-tag fs-5 me-2"></i>
                                            <span class="small fw-bold" id="lblVoucherStatus">Chọn voucher</span>
                                        </div>
                                        <i class="bi bi-chevron-right text-muted small"></i>
                                    </div>
                                </div>

                                <div class="summary-section">
                                    <div class="summary-row total">
                                        <span>Tổng cộng</span>
                                        <span class="value" id="totalDisplay">
                    <fmt:formatNumber value="${tempTotal}" pattern="#,##0" />₫
                </span>
                                    </div>
                                </div>

                                <button type="button" class="btn-place-order" id="btnPlaceOrder" disabled onclick="submitOrder()">
                                    <i class="bi bi-bag-check"></i>
                                    Đặt hàng
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<jsp:include page="../modals/selectVoucher.jsp" />

<jsp:include page="_footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    var API_BASE = '${pageContext.request.contextPath}';
    var GHN_API = API_BASE + '/api/ghn';
    var SUBTOTAL = ${empty tempTotal ? 0 : tempTotal};
    var CART_ID = '${cartId}';
</script>
<script src="${pageContext.request.contextPath}/js/cartView.js"></script>
</body>
</html>