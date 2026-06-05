<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="modal fade" id="addressModal" tabindex="-1" aria-labelledby="addressModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addressModalLabel">Thêm địa chỉ mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="addressModalBody">
                <div class="text-center py-4">
                    <div class="spinner-border spinner-border-sm text-secondary"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="button" class="btn btn-primary" id="btnSaveAddress" onclick="saveAddress()">Lưu</button>
            </div>
        </div>
    </div>
</div>