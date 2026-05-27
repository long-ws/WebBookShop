document.addEventListener('DOMContentLoaded', function() {
    loadProvinces();
    setupEventListeners();
    resetSubmitState();
    checkCheckoutReady();
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
    document.getElementById('province').addEventListener('change', handleProvinceChange);
    document.getElementById('district').addEventListener('change', handleDistrictChange);
    document.getElementById('ward').addEventListener('change', handleWardChange);
    document.getElementById('addressDetail').addEventListener('input', checkCheckoutReady);

    var checkboxes = document.querySelectorAll('input[name="selectedItems"]');
    for (var i = 0; i < checkboxes.length; i++) {
        checkboxes[i].addEventListener('change', updateSelectedCount);
    }
}

function loadProvinces() {
    fetch(GHN_API + '/provinces')
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if ((data.code === 200 || data.success) && data.data) {
                var select = document.getElementById('province');
                data.data.forEach(function(province) {
                    var option = document.createElement('option');
                    option.value = province.ProvinceID;
                    option.textContent = province.ProvinceName;
                    select.appendChild(option);
                });
            } else {
                console.error('GHN provinces error:', data);
            }
        })
        .catch(function(error) { console.error('Error loading provinces:', error); });
}

function handleProvinceChange() {
    var provinceId = this.value;
    var districtSelect = document.getElementById('district');
    var wardSelect = document.getElementById('ward');

    districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
    wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
    wardSelect.disabled = true;

    if (provinceId) {
        loadDistricts(provinceId);
        districtSelect.disabled = false;
    } else {
        districtSelect.disabled = true;
    }
    resetShippingOptions();
    checkCheckoutReady();
}

function loadDistricts(provinceId) {
    fetch(GHN_API + '/districts?province_id=' + provinceId)
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if ((data.code === 200 || data.success) && data.data) {
                var select = document.getElementById('district');
                data.data.forEach(function(district) {
                    var option = document.createElement('option');
                    option.value = district.DistrictID;
                    option.textContent = district.DistrictName;
                    select.appendChild(option);
                });
            } else {
                console.error('GHN districts error:', data);
            }
        })
        .catch(function(error) { console.error('Error loading districts:', error); });
}

function handleDistrictChange() {
    var districtId = this.value;
    var wardSelect = document.getElementById('ward');

    wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';

    if (districtId) {
        loadWards(districtId);
        wardSelect.disabled = false;
    } else {
        wardSelect.disabled = true;
    }
    resetShippingOptions();
    checkCheckoutReady();
}

function loadWards(districtId) {
    fetch(GHN_API + '/wards?district_id=' + districtId)
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if ((data.code === 200 || data.success) && data.data) {
                var select = document.getElementById('ward');
                data.data.forEach(function(ward) {
                    var option = document.createElement('option');
                    option.value = ward.WardCode;
                    option.textContent = ward.WardName;
                    select.appendChild(option);
                });
            } else {
                console.error('GHN wards error:', data);
            }
        })
        .catch(function(error) { console.error('Error loading wards:', error); });
}

function handleWardChange() {
    var districtId = document.getElementById('district').value;
    var wardCode = this.value;

    if (districtId && wardCode) {
        loadShippingOptions(districtId, wardCode);
    } else {
        resetShippingOptions();
    }
    checkCheckoutReady();
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
        var estimatedDate = calculateEstimatedDate(option.estimated_days);

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
                '<span><i class="bi bi-calendar-check"></i> Nhận hàng: <strong>' + estimatedDate + '</strong></span>' +
                '<span><i class="bi bi-clock"></i> ' + (option.estimated_days || 2) + ' ngày</span>' +
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
    document.getElementById('toDistrictId').value = document.getElementById('district').value;
    document.getElementById('toWardCode').value = document.getElementById('ward').value;

    updateShippingFee(option.fee);
    checkCheckoutReady();
}

function updateShippingFee(fee) {
    var feeDisplay = document.getElementById('shippingFeeDisplay');
    var totalDisplay = document.getElementById('totalDisplay');

    feeDisplay.textContent = formatCurrency(fee);
    feeDisplay.className = 'value';
    feeDisplay.style.color = '#ee4d2d';
    feeDisplay.style.fontWeight = '600';

    var total = SUBTOTAL + fee;
    totalDisplay.textContent = formatCurrency(total);
}

function calculateEstimatedDate(days) {
    var today = new Date();
    today.setDate(today.getDate() + (days || 2));
    var opt = { day: 'numeric', month: 'short' };
    return today.toLocaleDateString('vi-VN', opt);
}

function resetShippingOptions() {
    var container = document.getElementById('shippingOptionsList');
    container.innerHTML =
        '<div class="alert-info">' +
            '<i class="bi bi-info-circle"></i> ' +
            'Vui lòng chọn địa chỉ giao hàng để xem các phương thức vận chuyển' +
        '</div>';

    document.getElementById('shippingFeeDisplay').textContent = '---';
    document.getElementById('shippingFeeDisplay').style.color = '';
    document.getElementById('shippingFeeDisplay').style.fontWeight = '';
    document.getElementById('totalDisplay').textContent = formatCurrency(SUBTOTAL);
    document.getElementById('selectedServiceId').value = '';
    document.getElementById('deliveryPrice').value = '0';
}

function checkCheckoutReady() {
    var province = document.getElementById('province').value;
    var district = document.getElementById('district').value;
    var ward = document.getElementById('ward').value;
    var addressDetail = document.getElementById('addressDetail').value.trim();
    var deliveryPrice = parseInt(document.getElementById('deliveryPrice').value) || 0;
    var provinceSelect = document.getElementById('province');
    var provincesLoaded = provinceSelect && provinceSelect.options.length > 1;

    var isAddressFilled = province && district && ward && addressDetail;
    var btn = document.getElementById('btnPlaceOrder');

    if (isAddressFilled && provincesLoaded) {
        btn.disabled = false;
    } else if (isAddressFilled && !provincesLoaded) {
        btn.disabled = true;
    } else {
        btn.disabled = true;
    }

    var alertBox = document.getElementById('shippingAlert');
    var alertText = document.getElementById('shippingAlertText');

    if (province && district && ward && !addressDetail) {
        alertBox.classList.add('show');
        alertText.textContent = 'Vui lòng nhập địa chỉ chi tiết (số nhà, tên đường)';
    } else if (isAddressFilled && !provincesLoaded) {
        alertBox.classList.add('show');
        alertText.textContent = 'Đang tải danh sách tỉnh/thành phố...';
    } else if (isAddressFilled && provincesLoaded && deliveryPrice === 0) {
        alertBox.classList.add('show');
        alertText.textContent = 'Đang tải phương thức vận chuyển...';
    } else if (isAddressFilled && provincesLoaded && deliveryPrice > 0) {
        alertBox.classList.remove('show');
    } else {
        alertBox.classList.remove('show');
    }

    console.log('[CheckReady] addr:', isAddressFilled, 'provLoaded:', provincesLoaded, 'price:', deliveryPrice, 'btnEnabled:', !btn.disabled);
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

    var province = document.getElementById('province');
    var district = document.getElementById('district');
    var ward = document.getElementById('ward');
    var addressDetail = document.getElementById('addressDetail').value.trim();

    if (!province.value) {
        alert('Vui lòng chọn Tỉnh/Thành phố!');
        return;
    }
    if (!district.value) {
        alert('Vui lòng chọn Quận/Huyện!');
        return;
    }
    if (!ward.value) {
        alert('Vui lòng chọn Phường/Xã!');
        return;
    }
    if (!addressDetail) {
        alert('Vui lòng nhập địa chỉ chi tiết!');
        return;
    }

    document.getElementById('provinceName').value = province.options[province.selectedIndex].text;
    document.getElementById('districtName').value = district.options[district.selectedIndex].text;
    document.getElementById('wardName').value = ward.options[ward.selectedIndex].text;
    document.getElementById('addressDetailHidden').value = addressDetail;
    document.getElementById('toDistrictId').value = district.value;
    document.getElementById('toWardCode').value = ward.value;

    var deliveryPrice = parseInt(document.getElementById('deliveryPrice').value, 10) || 0;
    if (deliveryPrice === 0) {
        document.getElementById('deliveryPrice').value = '25000';
    }
    var serviceId = document.getElementById('selectedServiceId').value;
    if (serviceId) {
        document.getElementById('deliveryMethod').value = serviceId;
    }

    isSubmitting = true;
    var btn = document.getElementById('btnPlaceOrder');
    btn.disabled = true;
    btn.innerHTML = '<i class="bi bi-hourglass-split"></i> Đang xử lý...';

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
