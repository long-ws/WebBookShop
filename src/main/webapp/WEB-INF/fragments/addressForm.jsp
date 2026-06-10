<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<form id="addressForm" method="POST" class="needs-validation" novalidate>

    <input type="hidden" id="addressId" name="addressId" value="${address.id}">

    <div class="row g-3">
        <div class="col-12"><small class="text-muted">Thông tin người nhận</small></div>

        <div class="col-6">
            <div class="form-floating">
                <input type="text" class="form-control" id="fullname" name="fullname"
                       placeholder="Họ và tên" value="${address.fullname}"
                       oninput="validateField(this, this.value.trim() !== '')">
                <label for="fullname">Họ và tên</label>
                <div class="invalid-feedback">Vui lòng nhập họ và tên.</div>
            </div>
        </div>

        <div class="col-6">
            <div class="form-floating">
                <input type="text" class="form-control" id="phone" name="phone"
                       placeholder="Số điện thoại" value="${address.phone}"
                       oninput="this.value = this.value.replace(/[^0-9]/g, ''); validatePhone(this);">
                <label for="phone">Số điện thoại</label>
                <div class="invalid-feedback">Số điện thoại phải có 0 ở đầu và từ 9 đến 11 chữ số.</div>
            </div>
        </div>

        <div class="col-12">
            <hr class="my-0">
            <small class="text-muted">Địa chỉ nhận hàng</small></div>

        <div class="col-md-4">
            <select class="form-select" id="province" name="provinceId">
                <option value="">Tỉnh / Thành phố</option>
            </select>
            <div class="invalid-feedback">Vui lòng chọn Tỉnh/Thành phố.</div>
            <input type="hidden" id="provinceName" name="provinceName" value="${address.province}">
        </div>

        <div class="col-md-4">
            <select class="form-select" id="district" name="districtId" disabled>
                <option value="">Quận / Huyện</option>
            </select>
            <div class="invalid-feedback">Vui lòng chọn Quận/Huyện.</div>
            <input type="hidden" id="districtName" name="districtName" value="${address.district}">
        </div>

        <div class="col-md-4">
            <select class="form-select" id="ward" name="wardCode" disabled>
                <option value="">Phường / Xã</option>
            </select>
            <div class="invalid-feedback">Vui lòng chọn Phường/Xã.</div>
            <input type="hidden" id="wardName" name="wardName" value="${address.ward}">
        </div>

        <div class="col-12">
            <label class="form-label">Địa chỉ chi tiết (số nhà, tên đường)</label>
            <input type="text" class="form-control" id="detail" name="detail"
                   placeholder="Ví dụ: 123 Nguyễn Huệ, P.Bến Nghé, Q.1" value="${address.addressDetail}"
                   oninput="validateField(this, this.value.trim() !== '')">
            <div class="invalid-feedback">Vui lòng nhập số nhà, tên đường chi tiết.</div>
        </div>
    </div>
</form>

<script>
    const oldProvinceId = "${address != null ? address.provinceId : ''}";
    const oldDistrictId = "${address != null ? address.districtId : ''}";
    const oldWardCode = "${address != null ? address.wardCode : ''}";
</script>