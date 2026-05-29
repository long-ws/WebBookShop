<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN"/>

<style>
    .toggle-btn::after { content: 'Thu gọn'; }
    .toggle-btn.collapsed::after { content: 'Xem thêm'; }
</style>

<c:forEach var="group" items="${['discount','shipping']}">
    <c:set var="isDisc" value="${group == 'discount'}"/>
    <c:set var="mainColor" value="${isDisc ? 'primary' : 'success'}"/>
    <c:set var="voucherType" value="${isDisc ? 'Mã giảm giá sản phẩm' : 'Mã miễn phí vận chuyển'}"/>

    <div class="${isDisc ? 'mb-4' : ''}">

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold m-0">
                <i class="bi ${isDisc ? 'bi-tag-fill' : 'bi-truck'} text-${mainColor} me-2"></i>${voucherType}
            </h6>
            <button class="btn btn-sm btn-link text-decoration-none fw-semibold p-0 text-${mainColor} toggle-btn ${isDisc ? '' : 'collapsed'}"
                    type="button" data-bs-toggle="collapse" data-bs-target="#collapse${group}"></button>
        </div>

        <div class="collapse ${isDisc ? 'show' : ''}" id="collapse${group}">
            <div class="d-flex flex-column gap-3">

                <c:set var="hasItem" value="false"/>
                <c:forEach var="entry" items="${usableVouchers}">
                    <c:set var="v" value="${entry.key}"/>
                    <c:set var="canUse" value="${entry.value}"/>
                    <c:set var="showVoucher" value="${(isDisc && (v.applyTo == 0 || v.applyTo == 1 || v.applyTo == 2)) || (!isDisc && v.applyTo == 3)}"/>

                    <c:if test="${showVoucher}">
                        <c:set var="hasItem" value="true"/>

                        <label class="card border-0 shadow-sm overflow-hidden m-0 ${!canUse ? 'bg-light text-muted' : ''}"
                               style="cursor: ${canUse ? 'pointer' : 'not-allowed'};">
                            <div class="d-flex align-items-stretch">

                                <div class="${canUse ? 'bg-'.concat(mainColor) : 'bg-secondary'}" style="width:6px;"></div>

                                <div class="flex-grow-1 px-3 py-2">
                                    <div class="d-flex justify-content-between align-items-start">

                                        <div>
                                            <span class="badge ${canUse ? 'bg-'.concat(mainColor).concat('-subtle text-').concat(mainColor) : 'bg-secondary-subtle text-secondary'} mb-2">
                                                <c:choose>
                                                    <c:when test="${v.applyTo == 0}">Toàn bộ sản phẩm</c:when>
                                                    <c:when test="${v.applyTo == 1}">Một số danh mục</c:when>
                                                    <c:when test="${v.applyTo == 2}">Một số sản phẩm</c:when>
                                                    <c:otherwise>Vận chuyển</c:otherwise>
                                                </c:choose>
                                            </span>

                                            <div class="fw-bold">${v.code}</div>

                                            <div class="small fw-semibold mt-1 ${canUse ? 'text-'.concat(mainColor) : 'text-secondary'}">
                                                <c:choose>
                                                    <c:when test="${v.calculationMethod == 0}">
                                                        Giảm ${v.value}% <c:if test="${v.maxDiscount > 0}">(tối đa <fmt:formatNumber value="${v.maxDiscount}" pattern="#,##0"/>₫)</c:if>
                                                    </c:when>
                                                    <c:otherwise>
                                                        Giảm <fmt:formatNumber value="${v.value}" pattern="#,##0"/>₫
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div class="small text-muted">
                                                Đơn tối thiểu <fmt:formatNumber value="${v.minPurchase}" pattern="#,##0"/>₫
                                                <c:if test="${!canUse}"> • Chưa đủ điều kiện</c:if>
                                            </div>

                                        </div>

                                        <input type="radio"
                                               name="${isDisc ? 'radDiscountVoucher' : 'radShipVoucher'}"
                                               value="${v.id}"
                                               class="form-check-input mt-2"
                                               data-min-purchase="${v.minPurchase}"
                                               data-discount-type="${v.calculationMethod == 0 ? 'PERCENT' : 'FIXED'}"
                                               data-discount-value="${v.value}"
                                               data-max-discount="${v.maxDiscount > 0 ? v.maxDiscount : 0}"
                                               onchange="reCalculateOrder()"
                                            ${!canUse ? 'disabled' : ''}>

                                    </div>
                                </div>
                            </div>
                        </label>
                    </c:if>
                </c:forEach>
                <c:if test="${!hasItem}">
                    <div class="text-center p-3 text-muted small bg-white border rounded">Không có mã khả dụng</div>
                </c:if>

            </div>
        </div>
    </div>
</c:forEach>