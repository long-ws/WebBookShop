function loadProvinces() {
    return fetch(GHN_API + '/provinces')
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            if ((data.code === 200 || data.success) && data.data) {
                var select = document.getElementById('province');
                data.data.forEach(function (province) {
                    var option = document.createElement('option');
                    option.value = province.ProvinceID;
                    option.textContent = province.ProvinceName;
                    select.appendChild(option);
                });
            } else {
                console.error('GHN provinces error:', data);
            }
        })
        .catch(function (error) {
            console.error('Error loading provinces:', error);
        });
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
}

function loadDistricts(provinceId) {
    return fetch(GHN_API + '/districts?province_id=' + provinceId)
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            if ((data.code === 200 || data.success) && data.data) {
                var select = document.getElementById('district');
                data.data.forEach(function (district) {
                    var option = document.createElement('option');
                    option.value = district.DistrictID;
                    option.textContent = district.DistrictName;
                    select.appendChild(option);
                });
            } else {
                console.error('GHN districts error:', data);
            }
        })
        .catch(function (error) {
            console.error('Error loading districts:', error);
        });
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
}

function loadWards(districtId) {
    return fetch(GHN_API + '/wards?district_id=' + districtId)
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            if ((data.code === 200 || data.success) && data.data) {
                var select = document.getElementById('ward');
                data.data.forEach(function (ward) {
                    var option = document.createElement('option');
                    option.value = ward.WardCode;
                    option.textContent = ward.WardName;
                    select.appendChild(option);
                });
            } else {
                console.error('GHN wards error:', data);
            }
        })
        .catch(function (error) {
            console.error('Error loading wards:', error);
        });
}

function handleWardChange() {
    var districtId = document.getElementById('district').value;
    var wardCode = this.value;

    if (districtId && wardCode) {
        loadShippingOptions(districtId, wardCode);
    } else {
        resetShippingOptions();
    }
}

var addressModal = null;

function openCreateModal() {
    addressModal = new bootstrap.Modal(document.getElementById('addressModal'));
    document.getElementById('addressModalLabel').textContent = "Thêm mới địa chỉ";
    fetch(API_BASE + "/address/create")
        .then(res => res.text())
        .then(html => {
            document.getElementById('addressModalBody').innerHTML = html;
            initModalForm(html);
            loadProvinces();
            addressModal.show();
        });
}

function initModalForm(html) {
    document.getElementById('addressModalBody').innerHTML = html;

    document.getElementById('province').addEventListener('change', function () {
        handleProvinceChange.call(this);
        validateField(this, this.value !== '');
    });
    document.getElementById('district').addEventListener('change', function () {
        handleDistrictChange.call(this);
        validateField(this, this.value !== '');
    });
    document.getElementById('ward').addEventListener('change', function () {
        handleWardChange.call(this);
        validateField(this, this.value !== '');
    });

    document.getElementById('fullname').addEventListener('input', function () {
        validateField(this, this.value.trim() !== '');
    });
    document.getElementById('phone').addEventListener('input', function () {
        this.value = this.value.replace(/[^0-9]/g, '');
        var phoneRegex = /^0\d{8,10}$/;
        validateField(this, phoneRegex.test(this.value.trim()));
    });
    document.getElementById('detail').addEventListener('input', function () {
        validateField(this, this.value.trim() !== '');
    });
}

function checkFormValidity() {
    var fullname = document.getElementById('fullname');
    var phone = document.getElementById('phone');
    var province = document.getElementById('province');
    var district = document.getElementById('district');
    var ward = document.getElementById('ward');
    var detail = document.getElementById('detail');

    var phoneRegex = /^0\d{8,10}$/;

    var isFullnameValid = validateField(fullname, fullname?.value.trim() !== '');
    var isPhoneValid = validateField(phone, phoneRegex.test(phone?.value.trim()));
    var isProvinceValid = validateField(province, province?.value !== '');
    var isDistrictValid = validateField(district, district?.value !== '');
    var isWardValid = validateField(ward, ward?.value !== '');
    var isDetailValid = validateField(detail, detail?.value.trim() !== '');

    return isFullnameValid && isPhoneValid && isProvinceValid && isDistrictValid && isWardValid && isDetailValid;
}

function validateField(element, isValidCondition) {
    if (!element) return isValidCondition;
    if (isValidCondition) {
        element.classList.remove('is-invalid');
    } else {
        element.classList.add('is-invalid');
    }
    return isValidCondition;
}

function saveAddress() {
    var formEl = document.getElementById('addressForm');
    var btnSave = document.getElementById('btnSaveAddress');
    if (!formEl) return;
    var isFormValid = checkFormValidity();
    if (!isFormValid) {
        return;
    }
    var addressIdEl = document.getElementById('addressId');
    var addressId = addressIdEl ? addressIdEl.value : '';

    var isUpdate = addressId && addressId.trim() !== '';
    var confirmMessage = isUpdate ? "Bạn có chắc chắn muốn cập nhật địa chỉ này không?" : "Bạn có chắc chắn muốn thêm địa chỉ mới này không?";
    if (!confirm(confirmMessage)) {
        return;
    }
    if (btnSave) {
        btnSave.disabled = true;
        btnSave.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang lưu...';
    }
    var provinceSelect = document.getElementById('province');
    var districtSelect = document.getElementById('district');
    var wardSelect = document.getElementById('ward');

    document.getElementById('provinceName').value = provinceSelect.options[provinceSelect.selectedIndex]?.text || '';
    document.getElementById('districtName').value = districtSelect.options[districtSelect.selectedIndex]?.text || '';
    document.getElementById('wardName').value = wardSelect.options[wardSelect.selectedIndex]?.text || '';

    if (isUpdate) {
        updateAddress(formEl);
    } else {
        createAddress(formEl);
    }
}

function createAddress(formEl) {
    formEl.action = API_BASE + "/address/create";
    formEl.submit();
}

function updateAddress(formEl) {
    formEl.action = API_BASE + "/address/update";
    formEl.submit();
}

function openUpdateModal(addressId) {
    addressModal = new bootstrap.Modal(document.getElementById('addressModal'));
    document.getElementById('addressModalLabel').textContent = "Chỉnh sửa địa chỉ";
    fetch(API_BASE + "/address/update?addressId=" + addressId)
        .then(res => res.text())
        .then(html => {
            initModalForm(html);

            const oldProvince = document.getElementById('provinceName').value;
            const oldDistrict = document.getElementById('districtName').value;
            const oldWard = document.getElementById('wardName').value;

            loadProvinces().then(() => {
                var pId = selectOptionByText('province', oldProvince);
                if (pId) {
                    return loadDistricts(pId).then(() => {
                        var dId = selectOptionByText('district', oldDistrict);
                        if (dId) {
                            return loadWards(dId).then(() => {
                                selectOptionByText('ward', oldWard);
                            });
                        }
                    });
                }
            });
            addressModal.show();
        });
}

function selectOptionByText(selectId, targetText) {
    var select = document.getElementById(selectId);
    if (!select || !targetText) return null;

    for (var i = 0; i < select.options.length; i++) {
        if (select.options[i].textContent.trim() === targetText.trim()) {
            select.selectedIndex = i;
            select.disabled = false;
            return select.options[i].value;
        }
    }
    return null;
}