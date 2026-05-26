<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="modal fade" id="voucherModal" tabindex="-1" aria-labelledby="voucherModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header py-3">
                <h5 class="modal-title fw-bold text-dark" id="voucherModalLabel">Chọn Ưu Đãi</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body bg-light p-3">

                <div class="mb-4">
                    <h6 class="fw-bold text-dark mb-2">
                        <i class="bi bi-tag-fill me-2 text-primary"></i>Mã Giảm Giá Sản Phẩm
                    </h6>
                    <div class="d-flex flex-column gap-2" id="discountVoucherList"></div>
                    <div class="text-center mt-2 d-none" id="divMoreDiscount">
                        <a href="javascript:void(0)" class="small fw-bold text-decoration-none" id="btnMoreDiscount">Xem thêm mã sản phẩm</a>
                    </div>
                </div>

                <div>
                    <h6 class="fw-bold text-dark mb-2">
                        <i class="bi bi-truck me-2 text-success"></i>Mã Miễn Phí Vận Chuyển
                    </h6>
                    <div class="d-flex flex-column gap-2" id="shipVoucherList"></div>
                    <div class="text-center mt-2 d-none" id="divMoreShip">
                        <a href="javascript:void(0)" class="small fw-bold text-success text-decoration-none" id="btnMoreShip">Xem thêm mã vận chuyển</a>
                    </div>
                </div>

            </div>
            <div class="modal-footer py-2">
                <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Đóng</button>
                <button type="button" class="btn btn-primary btn-sm px-4" id="btnConfirmVoucher">Xác nhận</button>
            </div>
        </div>
    </div>
</div>