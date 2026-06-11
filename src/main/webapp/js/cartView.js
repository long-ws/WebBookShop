document.addEventListener('DOMContentLoaded', function() {
    setupEventListeners();
    resetSubmitState();
    initVoucherFeatures();
    loadDefaultAddressShipping();
});

function resetSubmitState() {
    var btn = document.getElementById('btnPlaceOrder');
    if (btn) {
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-bag-check"></i> Đặt hàng';
    }
    isSubmitting = false;
}

function setupEventListeners() {
    var checkboxes = document.querySelectorAll('input[name="selectedItems"]');
    for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].addEventListener('change', updateSelectedCount);
    }
}
function loadDefaultAddressShipping() {
    var container = document.getElementById('defaultAddressContainer');

    if (!container) {
        showAddressMissing();
        return;
    }

    var districtId = container.dataset.districtId;
    var wardCode = container.dataset.wardCode;

    if (!districtId || !wardCode) {
        showAddressMissing();
        return;
    }

    document.getElementById('toDistrictId').value = districtId;
    document.getElementById('toWardCode').value = wardCode;

    loadShippingOptions(districtId, wardCode);
}
function showAddressMissing() {
    var btn = document.getElementById('btnPlaceOrder');

    if (btn) {
        btn.disabled = true;
    }

    var alertBox = document.getElementById('shippingAlert');
    var alertText = document.getElementById('shippingAlertText');

    alertBox.classList.add('show');
    alertText.textContent = 'Vui lòng chọn địa chỉ mặc định trước khi đặt hàng';
}

function loadShippingOptions(districtId, wardCode) {
    showLoading();

    var requestBody = {
        to_district_id: parseInt(districtId),
        to_ward_code: wardCode,
        weight: calculateTotalWeight(),
        length: calculateMaxDimension().length,
        width: calculateMaxDimension().width,
        height: calculateMaxDimension().height
    };

    fetch(GHN_API + '/shipping-options', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody)
    })
    .then(function(response) { return response.json(); })
    .then(function(data) {
        if (data.success && data.data && data.data.length > 0) {
            renderShippingOptions(data.data);
        } else {
            renderDefaultOptions();
        }
    })
    .catch(function(error) {
        console.error('Error loading shipping options:', error);
        renderDefaultOptions();
    })
    .finally(function() { hideLoading(); });
}

function calculateTotalWeight() {
    var totalWeight = 0;
    var items = document.querySelectorAll('.cart-item[data-product-id]');
    items.forEach(function(item) {
        var weightAttr = item.getAttribute('data-weight');
        var qtyInput = item.querySelector('.quantity-control input[type="number"]');
        var qty = qtyInput ? parseInt(qtyInput.value) || 1 : 1;
        var weight = weightAttr ? parseFloat(weightAttr) : 300;
        totalWeight += weight * qty;
    });
    return Math.max(totalWeight, 100);
}

function calculateMaxDimension() {
    var maxLength = 0, maxWidth = 0, maxHeight = 0;
    var items = document.querySelectorAll('.cart-item[data-product-id]');
    items.forEach(function(item) {
        var lengthAttr = parseInt(item.getAttribute('data-length')) || 20;
        var widthAttr = parseInt(item.getAttribute('data-width')) || 15;
        var heightAttr = parseInt(item.getAttribute('data-height')) || 10;
        if (lengthAttr > maxLength) maxLength = lengthAttr;
        if (widthAttr > maxWidth) maxWidth = widthAttr;
        if (heightAttr > maxHeight) maxHeight = heightAttr;
    });
    return {
        length: Math.max(maxLength, 10),
        width: Math.max(maxWidth, 10),
        height: Math.max(maxHeight, 10)
    };
}

function renderShippingOptions(options) {
    var container = document.getElementById('shippingOptionsList');
    container.innerHTML = '';
    options.forEach(function(option, index) {
        var isFast = option.service_type_id === 2;
        var deliveryTime = option.estimated_days || '2-3';

        var div = document.createElement('div');
        div.className = 'shipping-option' + (index === 0 ? ' selected' : '');
        div.innerHTML =
            '<div class="shipping-option-header">' +
                '<div class="shipping-option-info">' +
                    '<div class="shipping-option-logo"><i class="bi bi-truck"></i></div>' +
                    '<div>' +
                        '<div class="shipping-option-name">' +
                            'GHN - Giao hàng ' + (isFast ? 'nhanh' : 'tiêu chuẩn') +
                            (isFast ? ' <span class="badge-fast">Giao Nhanh</span>' : '') +
                        '</div>' +
                        '<div class="shipping-option-type">' + (option.service_name || 'Vận chuyển qua GHN') + '</div>' +
                    '</div>' +
                '</div>' +
                '<div class="shipping-option-price">' +
                    '<div class="shipping-option-fee">' + formatCurrency(option.fee) + '</div>' +
                '</div>' +
            '</div>' +
            '<div class="shipping-option-detail">' +
            '<span><i class="bi bi-calendar-check"></i> Nhận hàng: <strong>' + deliveryTime + ' ngày</strong></span>' +
            '</div>';

        div.addEventListener('click', (function(opt) {
            return function() { selectShippingOption(this, opt); };
        })(option));

        container.appendChild(div);
    });

    if (options.length > 0) {
        selectShippingOption(container.querySelector('.shipping-option'), options[0]);
    }
}

function renderDefaultOptions() {
    var container = document.getElementById('shippingOptionsList');
    var estimatedDate = calculateEstimatedDate(3);

    container.innerHTML =
        '<div class="shipping-option selected">' +
            '<div class="shipping-option-header">' +
                '<div class="shipping-option-info">' +
                    '<div class="shipping-option-logo"><i class="bi bi-truck"></i></div>' +
                    '<div>' +
                        '<div class="shipping-option-name">' +
                            'GHN - Giao hàng nhanh <span class="badge-fast">Giao Nhanh</span>' +
                        '</div>' +
                        '<div class="shipping-option-type">Vận chuyển qua Giao Hàng Nhanh</div>' +
                    '</div>' +
                '</div>' +
                '<div class="shipping-option-price">' +
                    '<div class="shipping-option-fee">25.000₫</div>' +
                '</div>' +
            '</div>' +
            '<div class="shipping-option-detail">' +
                '<span><i class="bi bi-calendar-check"></i> Nhận hàng: <strong>' + estimatedDate + '</strong></span>' +
                '<span><i class="bi bi-clock"></i> 2-3 ngày</span>' +
            '</div>' +
        '</div>';

    var option = {
        service_id: 2,
        service_type_id: 2,
        short_name: 'GHN',
        service_name: 'Giao hàng nhanh',
        fee: 25000,
        estimated_days: 3
    };

    container.querySelector('.shipping-option').addEventListener('click', function() {
        selectShippingOption(this, option);
    });

    selectShippingOption(container.querySelector('.shipping-option'), option);
}

function selectShippingOption(element, option) {
    var items = document.querySelectorAll('.shipping-option');
    for (var i = 0; i < items.length; i++) {
        items[i].classList.remove('selected');
    }
    element.classList.add('selected');

    document.getElementById('selectedServiceId').value = option.service_type_id;
    document.getElementById('deliveryMethod').value = option.service_type_id || 2;
    document.getElementById('deliveryPrice').value = option.fee;
    document.getElementById('estimatedDays').value = option.estimated_days || 2;

    var addressContainer = document.getElementById('defaultAddressContainer');

    if (addressContainer) {
        document.getElementById('toDistrictId').value =
            addressContainer.dataset.districtId;

        document.getElementById('toWardCode').value =
            addressContainer.dataset.wardCode;
    }
    updateShippingFee(option.fee);
    checkCheckoutReady();
}

function updateShippingFee(fee) {
    var feeDisplay = document.getElementById('shippingFeeDisplay');
    feeDisplay.textContent = formatCurrency(fee);
    feeDisplay.className = 'value';
    feeDisplay.style.color = '#ee4d2d';
    feeDisplay.style.fontWeight = '600';
    renderOrderSummary();
}

function calculateEstimatedDate(days) {
    var today = new Date();
    today.setDate(today.getDate() + (days || 2));
    var opt = { day: 'numeric', month: 'short' };
    return today.toLocaleDateString('vi-VN', opt);
}

function checkCheckoutReady() {
    var addressContainer = document.getElementById('defaultAddressContainer');
    var deliveryPrice =
        parseInt(document.getElementById('deliveryPrice').value) || 0;

    var btn = document.getElementById('btnPlaceOrder');

    if (addressContainer && deliveryPrice > 0) {
        btn.disabled = false;
    } else {
        btn.disabled = true;
    }

    var alertBox = document.getElementById('shippingAlert');
    var alertText = document.getElementById('shippingAlertText');

    if (!addressContainer) {
        alertBox.classList.add('show');
        alertText.textContent =
            'Vui lòng chọn địa chỉ mặc định trước khi đặt hàng';
    } else if (deliveryPrice <= 0) {
        alertBox.classList.add('show');
        alertText.textContent =
            'Đang tải phương thức vận chuyển...';
    } else {
        alertBox.classList.remove('show');
    }
}
function updateSelectedCount() {
    var checked = document.querySelectorAll('input[name="selectedItems"]:checked').length;
    document.getElementById('selectedCount').textContent = checked;
}

function updateQuantity(cartItemId, currentQty, delta) {
    var input = document.getElementById('qty-' + cartItemId);
    if (!input) {
        console.error('Input not found: qty-' + cartItemId);
        return;
    }
    var value = parseInt(input.value) || 1;
    var max = parseInt(input.max) || 99;
    value = Math.max(1, Math.min(max, value + delta));
    input.value = value;
    updateCartItemQuantity(cartItemId, value);
}

function updateQuantityDirect(cartItemId) {
    var input = document.getElementById('qty-' + cartItemId);
    if (!input) return;
    updateCartItemQuantity(cartItemId, parseInt(input.value) || 1);
}

function updateCartItemQuantity(cartItemId, quantity) {
    var url = API_BASE + '/cartItem';
    var params = 'action=update&cartItemId=' + cartItemId + '&quantity=' + quantity;

    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200 || xhr.status === 302) {
                window.location.reload();
            } else {
                console.error('Error: ' + xhr.status);
                alert('Có lỗi xảy ra, vui lòng thử lại!');
            }
        }
    };
    xhr.send(params);
}

var isSubmitting = false;

function submitOrder() {
    if (isSubmitting) return;

    var addressContainer =
        document.getElementById('defaultAddressContainer');

    if (!addressContainer) {
        alert('Vui lòng chọn địa chỉ mặc định!');
        return;
    }

    document.getElementById('toDistrictId').value =
        addressContainer.dataset.districtId;

    document.getElementById('toWardCode').value =
        addressContainer.dataset.wardCode;

    var deliveryPrice =
        parseInt(document.getElementById('deliveryPrice').value) || 0;

    if (deliveryPrice <= 0) {
        alert('Chưa tải được phí vận chuyển!');
        return;
    }

    isSubmitting = true;

    var btn = document.getElementById('btnPlaceOrder');
    btn.disabled = true;
    btn.innerHTML =
        '<i class="bi bi-hourglass-split"></i> Đang xử lý...';

    document.getElementById('checkoutForm').submit();
}
function deleteCartItem(cartItemId) {
    if (!confirm('Bạn có muốn xóa sản phẩm này?')) return;

    var url = API_BASE + '/cartItem';
    var params = 'action=delete&cartItemId=' + cartItemId;

    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200 || xhr.status === 302) {
                window.location.reload();
            } else {
                console.error('Error: ' + xhr.status);
                alert('Có lỗi xảy ra, vui lòng thử lại!');
            }
        }
    };
    xhr.send(params);
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN').format(amount) + '₫';
}

function showLoading() {
    document.getElementById('loadingOverlay').classList.add('show');
}

function hideLoading() {
    document.getElementById('loadingOverlay').classList.remove('show');
}

const voucherState = { discountVoucher: null, shipVoucher: null };

function initVoucherFeatures() {
    const voucherModal = document.getElementById('voucherModal');
    if (!voucherModal) return;

    voucherModal.addEventListener('show.bs.modal', loadVoucherList);
    document.getElementById('btnApplyVoucher')?.addEventListener('click', () => {
        applyVoucher();
        bootstrap.Modal.getInstance(voucherModal)?.hide();
    });
}

function loadVoucherList() {
    const container = document.getElementById('voucherContainer');
    container.innerHTML = `
        <div class="text-center p-3 text-muted small">
            <span class="spinner-border spinner-border-sm me-2"></span> Đang tải danh sách...
        </div>`;

    fetch(`${API_BASE}/loadVoucher?cartId=${CART_ID}`)
        .then(res => { if (!res.ok) throw new Error(); return res.text(); })
        .then(html => { container.innerHTML = html; })
        .catch(() => {
            container.innerHTML = `<div class="text-center p-3 text-danger small">Lỗi tải danh sách mã giảm giá.</div>`;
        });
}

function applyVoucher() {
    const subtotal = getSubtotal();
    voucherState.discountVoucher = getSelectedVoucher('radDiscountVoucher');
    voucherState.shipVoucher = getSelectedVoucher('radShipVoucher');

    validateVoucher(voucherState.discountVoucher, subtotal, 'Đơn hàng không đủ điều kiện áp dụng mã giảm giá!');
    validateVoucher(voucherState.shipVoucher, subtotal, 'Đơn hàng không đủ điều kiện áp dụng mã freeship!');

    updateVoucherInputs();
    renderOrderSummary();
    updateVoucherLabel();
}

function getSelectedVoucher(name) {
    return document.querySelector(`input[name="${name}"]:checked`);
}

function validateVoucher(voucher, subtotal, message) {
    if (!voucher) return;
    const minPurchase = parseFloat(voucher.dataset.minPurchase || 0);

    if (subtotal < minPurchase) {
        voucher.checked = false;
        if (voucher.name === 'radDiscountVoucher') voucherState.discountVoucher = null;
        else voucherState.shipVoucher = null;
        alert(message);
    }
}

function calculateDiscount(voucher, amount) {
    if (!voucher) return 0;
    const type = voucher.dataset.discountType;
    const value = parseFloat(voucher.dataset.discountValue || 0);
    if (type === 'PERCENT') {
        const maxDiscount = parseFloat(voucher.dataset.maxDiscount || Infinity);
        return Math.min((amount * value) / 100, maxDiscount, amount);
    }
    return Math.min(value, amount);
}

function renderOrderSummary() {
    const subtotal = getSubtotal();
    const shippingFee = getShippingFee();
    const hasShipping = shippingFee > 0;
    const orderDiscount = calculateDiscount(voucherState.discountVoucher, subtotal);
    const shipDiscount = hasShipping ? calculateDiscount(voucherState.shipVoucher, shippingFee) : 0;
    const finalTotal = subtotal + shippingFee - orderDiscount - shipDiscount;

    setText('discountDisplay', orderDiscount > 0 ? '-' + formatCurrency(orderDiscount) : '0₫');
    setText('shipDiscountDisplay', shipDiscount > 0 ? '-' + formatCurrency(shipDiscount) : '0₫');
    setText('shippingFeeDisplay', !hasShipping ? '---' : formatCurrency(shippingFee));
    setText('totalDisplay', formatCurrency(Math.max(0, finalTotal)));
}

function updateVoucherInputs() {
    document.getElementById('finalVoucherId').value = voucherState.discountVoucher?.value || '';
    document.getElementById('finalShipVoucherId').value = voucherState.shipVoucher?.value || '';
}

function updateVoucherLabel() {
    const hasDiscount = !!voucherState.discountVoucher;
    const hasShip = !!voucherState.shipVoucher;
    let text = 'Chọn hoặc nhập mã';

    if (hasDiscount && hasShip) text = 'Áp dụng: 2 mã';
    else if (hasDiscount) text = 'Áp dụng: Giảm đơn';
    else if (hasShip) text = 'Áp dụng: Giảm ship';

    setText('lblVoucherStatus', text);
}

function getSubtotal() {
    return parseFloat(document.getElementById('inputSubTotal')?.value || 0);
}
function getShippingFee() {
    return parseFloat(document.getElementById('deliveryPrice')?.value || 0);
}
function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = value;
}
