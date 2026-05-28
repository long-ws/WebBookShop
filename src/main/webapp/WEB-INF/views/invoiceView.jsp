<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hóa Đơn #${requestScope.order.id} - Shop bán sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/invoice.css">
</head>
<body>

    <div class="print-btn-container">
        <button class="btn-print" onclick="window.print()">In Hóa Đơn</button>
    </div>

    <div class="invoice-container">
        
        <!-- HEADER -->
        <div class="invoice-header">
            <div class="logo-area">
                <svg viewBox="0 0 24 24" fill="#d63384">
                    <path d="M21 5c-1.11-.35-2.33-.5-3.5-.5-1.95 0-4.05.4-5.5 1.5-1.45-1.1-3.55-1.5-5.5-1.5S2.45 4.9 1 6v14.65c0 .25.25.5.5.5.1 0 .15-.05.25-.5C3.1 20.45 5.05 21 6.5 21c1.95 0 4.05-.4 5.5-1.5 1.35-.85 3.8-1.5 5.5-1.5 1.65 0 3.35.3 4.75 1.05.1.05.15.05.25.05.25 0 .5-.25.5-.5V6c-.6-.45-1.25-.75-2-1zm0 13.5c-1.1-.35-2.3-.5-3.5-.5-1.7 0-4.15.65-5.5 1.5V8c1.35-.85 3.8-1.5 5.5-1.5 1.2 0 2.4.15 3.5.5v11.5z"/>
                </svg>
                <div>
                    <div class="shop-name">BOOK STORE</div>
                    <div class="shop-tagline">Chuyên sách uy tín</div>
                </div>
            </div>
            <div class="invoice-title">
                <div class="invoice-label">HÓA ĐƠN</div>
                <div class="invoice-number">#${requestScope.order.id}</div>
                <div class="invoice-date"><fmt:formatDate value="${requestScope.orderCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></div>
            </div>
        </div>
        
        <!-- LIÊN HỆ -->
        <div class="shop-contact">
            <span>
                <svg class="contact-icon" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/></svg>
                123 Đường ABC, Quận 1, TP.HCM
            </span>
            <span>
                <svg class="contact-icon" viewBox="0 0 24 24" fill="currentColor"><path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/></svg>
                Hotline: 1900 1234
            </span>
            <span>
                <svg class="contact-icon" viewBox="0 0 24 24" fill="currentColor"><path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/></svg>
                contact@bookstore.vn
            </span>
        </div>

        <!-- THÔNG TIN -->
        <div class="info-grid">
            <!-- Khách hàng -->
                <div class="info-box">
                <div class="info-box-header customer">
                    <svg viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                    Thông Tin Khách Hàng
                </div>
                <div class="info-box-content">
                    <div class="info-row">
                        <div class="info-label">Họ tên:</div>
                        <div class="info-value">
                        <c:choose>
                            <c:when test="${not empty requestScope.shipment and not empty requestScope.shipment.receiverName}">${requestScope.shipment.receiverName}</c:when>
                            <c:otherwise>${sessionScope.currentUser.profile.fullname}</c:otherwise>
                        </c:choose>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Số điện thoại:</div>
                        <div class="info-value">
                        <c:choose>
                                <c:when test="${not empty requestScope.shipment and not empty requestScope.shipment.receiverPhone}">${requestScope.shipment.receiverPhone}</c:when>
                                <c:otherwise>${sessionScope.currentUser.profile.phoneNumber}</c:otherwise>
                        </c:choose>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Email:</div>
                        <div class="info-value">${sessionScope.currentUser.email}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Địa chỉ:</div>
                        <div class="info-value">
                        <c:choose>
                            <c:when test="${not empty requestScope.shipment and not empty requestScope.shipment.addressDetail}">
                                    ${requestScope.shipment.addressDetail}<c:if test="${not empty requestScope.shipment.ward}">, ${requestScope.shipment.ward}</c:if><c:if test="${not empty requestScope.shipment.district}">, ${requestScope.shipment.district}</c:if><c:if test="${not empty requestScope.shipment.province}">, ${requestScope.shipment.province}</c:if>
                            </c:when>
                                <c:otherwise>Chưa có thông tin địa chỉ</c:otherwise>
                        </c:choose>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Đơn hàng -->
                <div class="info-box">
                <div class="info-box-header order">
                    <svg viewBox="0 0 24 24"><path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg>
                    Thông Tin Đơn Hàng
                </div>
                <div class="info-box-content">
                    <div class="info-row">
                        <div class="info-label">Mã đơn hàng:</div>
                        <div class="info-value">#${requestScope.order.id}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Ngày đặt:</div>
                        <div class="info-value"><fmt:formatDate value="${requestScope.orderCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Giao hàng:</div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${not empty requestScope.shipment.shippingMethod.name}">${requestScope.shipment.shippingMethod.name}</c:when>
                                <c:otherwise>Chưa có thông tin</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Phí vận chuyển:</div>
                        <div class="info-value price"><fmt:formatNumber pattern="#,##0" value="${requestScope.order.deliveryPrice}" /> VND</div>
                    </div>
                </div>
            </div>
            
            <!-- Vận chuyển -->
                <div class="info-box">
                <div class="info-box-header shipping">
                    <svg viewBox="0 0 24 24"><path d="M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4z"/></svg>
                    Vận Chuyển
                </div>
                <div class="info-box-content">
                    <div class="info-row">
                        <div class="info-label">Đơn vị:</div>
                        <div class="info-value">
                            <c:choose>
                                <c:when test="${not empty requestScope.shipment.shippingMethod.name}">${requestScope.shipment.shippingMethod.name}</c:when>
                                <c:otherwise>Chưa có thông tin</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <c:if test="${not empty requestScope.shipment and not empty requestScope.shipment.trackingCode}">
                        <div class="info-row">
                            <div class="info-label">Mã vận đơn:</div>
                            <div class="info-value tracking">${requestScope.shipment.trackingCode}</div>
                        </div>
                    </c:if>
                    <c:if test="${not empty requestScope.shipment.totalWeight}">
                        <div class="info-row">
                            <div class="info-label">Trọng lượng:</div>
                            <div class="info-value">${requestScope.shipment.totalWeight} kg</div>
                        </div>
                    </c:if>
                    <c:if test="${not empty requestScope.shipment.customerNote}">
                        <div class="info-row note-row">
                            <div class="info-label">Ghi chú:</div>
                            <div class="info-value note-value">${requestScope.shipment.customerNote}</div>
                        </div>
                    </c:if>
                    <div class="info-row">
                        <div class="info-label">Thanh toán:</div>
                        <div class="info-value">
                            <span class="status-badge ${requestScope.payment.status == 1 ? 'paid' : 'pending'}">
                                <c:choose>
                                    <c:when test="${requestScope.payment.status == 1}">Đã thanh toán</c:when>
                                    <c:when test="${requestScope.payment.status == 0}">Chờ thanh toán</c:when>
                                    <c:otherwise>Thất bại</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- BẢNG SẢN PHẨM -->
        <div class="section-title">
            <span style="display:flex;align-items:center;gap:5px;">
                <svg viewBox="0 0 24 24"><path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z"/></svg>
                Chi Tiết Sản Phẩm
            </span>
            <span class="item-count">${requestScope.orderItems.size()} sản phẩm</span>
            </div>

        <table class="invoice-table">
                <thead>
                    <tr>
                    <th class="col-stt text-center">STT</th>
                    <th class="col-img">Ảnh</th>
                    <th class="col-name">Tên sản phẩm</th>
                    <th class="col-price text-right">Đơn giá</th>
                    <th class="col-qty text-center">SL</th>
                    <th class="col-total text-right">Thành tiền</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${requestScope.orderItems}" varStatus="loop">
                        <tr>
                        <td class="col-stt text-center">${loop.index + 1}</td>
                        <td class="col-img">
                                <c:choose>
                                    <c:when test="${empty item.product.imageName}">
                                    <img src="${pageContext.request.contextPath}/img/280px.png" class="product-thumb" alt="Product">
                                    </c:when>
                                    <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/images/${item.product.imageName}" class="product-thumb" alt="${item.product.name}">
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        <td class="col-name">
                            <div class="product-name">${item.product.name}</div>
                            <div class="product-author">Tác giả: ${item.product.author}</div>
                            <c:if test="${item.discount > 0}">
                                <span class="discount-tag">-${item.discount}%</span>
                            </c:if>
                            </td>
                        <td class="col-price text-right">
                            <fmt:formatNumber pattern="#,##0" value="${item.discount > 0 ? item.price * (100 - item.discount) / 100 : item.price}" /> VND
                            </td>
                        <td class="col-qty text-center">${item.quantity}</td>
                        <td class="col-total text-right">
                            <span class="item-total"><fmt:formatNumber pattern="#,##0" value="${item.discount > 0 ? item.price * (100 - item.discount) / 100 * item.quantity : item.price * item.quantity}" /> VND</span>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

        <!-- TỔNG KẾT -->
        <div class="summary-section">
            <div class="payment-info">
                <div class="payment-title">
                    <svg viewBox="0 0 24 24"><path d="M20 4H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V6c0-1.11-.89-2-2-2zm0 14H4v-6h16v6zm0-10H4V6h16v2z"/></svg>
                    Thông Tin Thanh Toán
                </div>
                <c:choose>
                    <c:when test="${not empty requestScope.payment}">
                        <div class="payment-row">
                            <span class="label">Mã tham chiếu:</span>
                            <span class="value">${requestScope.payment.vnpTxnRef}</span>
                        </div>
                        <div class="payment-row">
                            <span class="label">Ngày tạo:</span>
                            <span class="value"><fmt:formatDate value="${requestScope.paymentCreatedAt}" pattern="dd/MM/yyyy HH:mm" /></span>
                        </div>
                        <c:if test="${not empty requestScope.payment.vnpTransactionNo}">
                            <div class="payment-row">
                                <span class="label">Mã GD VNPAY:</span>
                                <span class="value">${requestScope.payment.vnpTransactionNo}</span>
                            </div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <div style="color:#888;font-style:italic;">Chưa có thông tin thanh toán</div>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="totals-box">
                <div class="total-row">
                    <span>Tạm tính</span>
                    <span><fmt:formatNumber pattern="#,##0" value="${requestScope.subtotal}" /> VND</span>
                </div>
                <div class="total-row">
                    <span>Phí vận chuyển</span>
                    <span><fmt:formatNumber pattern="#,##0" value="${requestScope.order.deliveryPrice}" /> VND</span>
                </div>
                <div class="total-row grand">
                    <span>TỔNG CỘNG</span>
                    <span class="grand-amount"><fmt:formatNumber pattern="#,##0" value="${requestScope.totalOrderPrice}" /> VND</span>
                </div>
            </div>
        </div>

        <!-- FOOTER -->
        <div class="invoice-footer">
            <div class="thank-you">
                <svg viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
                Cảm ơn quý khách đã mua sắm tại Shop bán sách!
            </div>
            <div class="footer-note">Chúc quý khách một ngày tốt lành</div>
            <div class="footer-contact">
                <span>Hotline: 1900 1234</span>
                <span class="divider">|</span>
                <span>Email: contact@bookstore.vn</span>
                <span class="divider">|</span>
                <span>Website: bookstore.vn</span>
            </div>
        </div>
    </div>

</body>
</html>
