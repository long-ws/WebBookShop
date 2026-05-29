<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ page import="java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đơn hàng hoàn tất - Shop Bán Sách</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    fontFamily: { sans: ['Inter', 'sans-serif'] },
                    colors: {
                        primary: '#EE4D2D',
                        success: '#16A34A',
                        orange: '#F97316',
                        warning: '#F59E0B',
                    },
                    boxShadow: {
                        'card': '0 1px 3px rgba(0,0,0,0.05), 0 4px 12px rgba(0,0,0,0.03)',
                    },
                    borderRadius: { xl: '12px' }
                }
            }
        }
    </script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/checkoutSuccess.css">
</head>
<body class="bg-slate-100 min-h-screen">
    <header class="w-full relative overflow-hidden bg-success">
        <!-- Decorative blobs -->
        <div class="absolute top-0 right-0 w-80 h-80 bg-white/10 rounded-full -translate-y-1/2 translate-x-1/3 pointer-events-none"></div>
        <div class="absolute bottom-0 left-0 w-64 h-64 bg-white/10 rounded-full translate-y-1/3 -translate-x-1/3 pointer-events-none"></div>
        <div class="absolute top-1/2 left-1/3 w-32 h-32 bg-white/10 rounded-full -translate-y-1/2 pointer-events-none hidden md:block"></div>

        <div class="relative z-10 mx-auto px-6 md:px-12 lg:px-16 py-8 md:py-10" style="max-width: 1200px;">
            <div class="flex flex-col md:flex-row items-center md:items-center gap-6 md:gap-8">
                <!-- Left: Icon + Text + Badge -->
                <div class="flex-1 flex flex-col items-center md:items-start text-center md:text-left w-full">
                    <!-- Success Icon -->
                    <div class="flex items-center gap-5 mb-4 w-full">
                        <div class="flex-shrink-0 w-16 h-16 md:w-20 md:h-20 bg-white rounded-full flex items-center justify-center shadow-[0_4px_20px_rgba(0,0,0,0.10)]">
                            <i class="fas fa-check text-${requestScope.payment.status == 1 ? 'success' : 'warning'} text-3xl md:text-4xl"></i>
                        </div>
                        <div class="hidden md:block w-px h-12 bg-white/30"></div>
                        <!-- Order Badge -->
                        <div class="bg-white/90 backdrop-blur-sm px-4 py-2 rounded-full shadow-[0_2px_12px_rgba(0,0,0,0.08)] inline-flex items-center gap-2">
                            <span class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Mã đơn hàng</span>
                            <span class="text-xl md:text-2xl font-extrabold text-primary tracking-tight">#${requestScope.order.id}</span>
                        </div>
                    </div>

                    <!-- Title -->
                    <h1 class="text-3xl md:text-4xl lg:text-5xl font-extrabold text-slate-800 mb-2 tracking-tight leading-tight">
                        <c:choose>
                            <c:when test="${requestScope.payment.status == 1}">ĐẶT HÀNG THÀNH CÔNG</c:when>
                            <c:otherwise>CHỜ THANH TOÁN</c:otherwise>
                        </c:choose>
                    </h1>

                    <!-- Subtitle -->
                    <p class="text-base md:text-lg text-slate-600 max-w-lg leading-relaxed">
                        <c:choose>
                            <c:when test="${requestScope.payment.status == 1}">Cảm ơn bạn! Đơn hàng của bạn đã được ghi nhận thành công.</c:when>
                            <c:otherwise>Vui lòng hoàn tất thanh toán để đơn hàng được xử lý sớm nhất.</c:otherwise>
                        </c:choose>
                    </p>
                </div>

                <!-- Right: Illustration -->
                <div class="flex-shrink-0 w-full md:w-auto flex justify-center md:justify-end">
                    <div class="relative w-48 h-40 md:w-64 md:h-52">
                        <!-- Shipping Box Illustration -->
                        <svg viewBox="0 0 200 160" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-full drop-shadow-lg">
                            <!-- Box body -->
                            <rect x="40" y="60" width="120" height="80" rx="8" fill="#4ADE80" fill-opacity="0.9"/>
                            <rect x="40" y="60" width="120" height="80" rx="8" stroke="#16A34A" stroke-width="2" fill="none"/>
                            <!-- Box top flap left -->
                            <path d="M40 60 L100 35 L100 60 Z" fill="#22C55E" stroke="#16A34A" stroke-width="1.5"/>
                            <!-- Box top flap right -->
                            <path d="M160 60 L100 35 L100 60 Z" fill="#86EFAC" stroke="#16A34A" stroke-width="1.5"/>
                            <!-- Box tape -->
                            <rect x="90" y="35" width="20" height="25" rx="2" fill="#F59E0B" stroke="#D97706" stroke-width="1"/>
                            <rect x="88" y="60" width="24" height="80" rx="2" fill="#16A34A" fill-opacity="0.15"/>
                            <!-- Checkmark on box -->
                            <circle cx="130" cy="90" r="22" fill="white" stroke="#16A34A" stroke-width="2"/>
                            <path d="M120 90 L127 97 L141 83" stroke="#16A34A" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
                            <!-- Stars -->
                            <path d="M175 30 L178 38 L187 38 L180 43 L183 52 L175 47 L167 52 L170 43 L163 38 L172 38 Z" fill="#FCD34D" stroke="#F59E0B" stroke-width="1"/>
                            <path d="M20 50 L22 55 L27 55 L23 59 L25 65 L20 61 L15 65 L17 59 L13 55 L18 55 Z" fill="#FCD34D" stroke="#F59E0B" stroke-width="1" opacity="0.7"/>
                            <path d="M185 80 L186.5 84 L191 84 L187.7 86.5 L189.2 91 L185 88.5 L180.8 91 L182.3 86.5 L179 84 L183.5 84 Z" fill="#FCD34D" stroke="#F59E0B" stroke-width="1" opacity="0.5"/>
                            <!-- Sparkles -->
                            <circle cx="25" cy="30" r="3" fill="#FCD34D" opacity="0.8"/>
                            <circle cx="180" cy="55" r="2.5" fill="#FCD34D" opacity="0.6"/>
                            <circle cx="15" cy="90" r="2" fill="#86EFAC" opacity="0.7"/>
                            <!-- Motion lines -->
                            <line x1="5" y1="80" x2="25" y2="80" stroke="white" stroke-width="2" stroke-linecap="round" opacity="0.6"/>
                            <line x1="8" y1="90" x2="30" y2="90" stroke="white" stroke-width="2" stroke-linecap="round" opacity="0.4"/>
                            <line x1="5" y1="100" x2="25" y2="100" stroke="white" stroke-width="2" stroke-linecap="round" opacity="0.5"/>
                        </svg>
                    </div>
                </div>
            </div>
        </div>
    </header>

    <main class="container mx-auto px-4 py-8 max-w-6xl">
          
        <section class="bg-white rounded-xl shadow-card mb-6 overflow-hidden card-hover">
            <div class="bg-success px-6 py-4 flex items-center justify-between">
                <div class="flex items-center gap-3">
                    <i class="fas fa-bag-shopping text-white text-xl"></i>
                    <h2 class="text-white font-bold text-lg tracking-wide">SẢN PHẨM ĐÃ ĐẶT</h2>
                </div>
                <span class="bg-white/20 text-white text-sm font-semibold px-4 py-1.5 rounded-full">
                    ${requestScope.orderItems.size()} sản phẩm
                </span>
            </div>
            
            <div class="p-6">
                <c:forEach var="item" items="${requestScope.orderItems}">
                    <div class="flex flex-col md:flex-row gap-4 md:gap-6 pb-6 border-b border-slate-100">
                        <div class="w-full md:w-24 h-24 flex-shrink-0">
                            <c:choose>
                                <c:when test="${empty item.product.imageName}">
                                    <img src="${pageContext.request.contextPath}/img/280px.png" alt="${item.product.name}" class="w-full h-full object-cover rounded-lg border border-slate-100">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/image/${item.product.imageName}" alt="${item.product.name}" class="w-full h-full object-cover rounded-lg border border-slate-100">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="flex-1 min-w-0">
                            <h3 class="font-semibold text-slate-800 text-lg mb-2 line-clamp-2">${item.product.name}</h3>
                            <div class="flex flex-wrap items-center gap-3 text-sm text-slate-500">
                                <span class="bg-slate-100 px-2 py-0.5 rounded text-xs font-medium">SKU: ${item.product.id}</span>
                            </div>
                        </div>
                        <div class="flex md:flex-col items-center md:items-end gap-4 md:gap-2">
                            <span class="text-slate-600 font-medium">x<span class="font-bold">${item.quantity}</span></span>
                            <span class="text-slate-400 text-sm"><fmt:formatNumber pattern="#,##0" value="${item.price}" />₫</span>
                        </div>
                    </div>
                </c:forEach>
                
                <div class="flex flex-col sm:flex-row justify-end gap-4 pt-4">
                    <div class="flex items-center gap-3">
                        <span class="text-slate-600">Tạm tính:</span>
                        <span class="text-xl font-bold text-slate-800"><fmt:formatNumber pattern="#,##0" value="${requestScope.subtotal}" />₫</span>
                    </div>
                </div>
            </div>
        </section>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            
            <!-- Column 1: Receiver Info -->
            <section class="bg-white rounded-xl shadow-card overflow-hidden card-hover">
                <div class="px-5 py-4 border-b border-slate-100 flex items-center gap-3">
                    <i class="fas fa-user text-slate-700 text-lg"></i>
                    <h3 class="font-bold text-slate-800 tracking-wide">THÔNG TIN NGƯỜI NHẬN</h3>
                </div>
                <div class="p-5 space-y-4">
                    <div class="flex items-start gap-3">
                        <div class="w-8 h-8 bg-slate-100 rounded-lg flex items-center justify-center flex-shrink-0">
                            <i class="fas fa-id-card text-slate-500 text-sm"></i>
                        </div>
                        <div>
                            <span class="text-xs text-slate-400 uppercase tracking-wide block mb-0.5">Họ và tên</span>
                            <span class="font-medium text-slate-800">${requestScope.shipment.receiverName}</span>
                        </div>
                    </div>
                    <div class="flex items-start gap-3">
                        <div class="w-8 h-8 bg-slate-100 rounded-lg flex items-center justify-center flex-shrink-0">
                            <i class="fas fa-phone text-slate-500 text-sm"></i>
                        </div>
                        <div>
                            <span class="text-xs text-slate-400 uppercase tracking-wide block mb-0.5">Số điện thoại</span>
                            <span class="font-medium text-slate-800">${requestScope.shipment.receiverPhone}</span>
                        </div>
                    </div>
                    <div class="flex items-start gap-3">
                        <div class="w-8 h-8 bg-slate-100 rounded-lg flex items-center justify-center flex-shrink-0">
                            <i class="fas fa-location-dot text-slate-500 text-sm"></i>
                        </div>
                        <div>
                            <span class="text-xs text-slate-400 uppercase tracking-wide block mb-0.5">Địa chỉ giao hàng</span>
                            <span class="font-medium text-slate-800 leading-relaxed">
                                ${requestScope.shipment.addressDetail}<c:if test="${not empty requestScope.shipment.ward}">, ${requestScope.shipment.ward}</c:if><c:if test="${not empty requestScope.shipment.district}">, ${requestScope.shipment.district}</c:if><c:if test="${not empty requestScope.shipment.province}">, ${requestScope.shipment.province}</c:if>
                            </span>
                        </div>
                    </div>
                    <c:if test="${not empty requestScope.shipment.customerNote}">
                    <div class="flex items-start gap-3">
                        <div class="w-8 h-8 bg-amber-100 rounded-lg flex items-center justify-center flex-shrink-0">
                            <i class="fas fa-sticky-note text-amber-600 text-sm"></i>
                        </div>
                        <div>
                            <span class="text-xs text-slate-400 uppercase tracking-wide block mb-0.5">Ghi chú giao hàng</span>
                            <span class="font-medium text-slate-800 leading-relaxed">${requestScope.shipment.customerNote}</span>
                        </div>
                    </div>
                    </c:if>
                </div>
            </section>

            <!-- Column 2: Shipping Info -->
            <section class="bg-white rounded-xl shadow-card overflow-hidden card-hover">
                <div class="px-5 py-4 border-b border-slate-100 bg-light-green flex items-center gap-3">
                    <i class="fas fa-truck-fast text-success text-lg"></i>
                    <h3 class="font-bold text-slate-800 tracking-wide">THÔNG TIN VẬN CHUYỂN</h3>
                </div>
                <div class="p-5 space-y-4">
                    <div class="flex items-center gap-3 p-3 bg-slate-50 rounded-xl">
                        <div class="w-12 h-12 bg-success rounded-xl flex items-center justify-center flex-shrink-0">
                            <i class="fas fa-box text-white text-lg"></i>
                        </div>
                        <div>
                            <span class="font-bold text-slate-800 block">
                                <c:choose>
                                    <c:when test="${not empty requestScope.shipment and not empty requestScope.shipment.shippingMethod}">${requestScope.shipment.shippingMethod.name}</c:when>
                                    <c:otherwise>GHN Express</c:otherwise>
                                </c:choose>
                            </span>
                            <span class="text-xs text-slate-500">
                                <c:choose>
                                    <c:when test="${not empty requestScope.shipment and not empty requestScope.shipment.shippingMethod and not empty requestScope.shipment.shippingMethod.serviceTypeName}">${requestScope.shipment.shippingMethod.serviceTypeName}</c:when>
                                    <c:otherwise>Giao hàng nhanh chóng, an toàn</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                    
                    <div class="flex items-center justify-between">
                        <span class="text-slate-600">Phí vận chuyển</span>
                        <span class="font-semibold text-success">
                            <c:choose>
                                <c:when test="${not empty requestScope.shipment and requestScope.shipment.shippingFee == 0}">Miễn phí</c:when>
                                <c:when test="${not empty requestScope.shipment}"><fmt:formatNumber pattern="#,##0" value="${requestScope.shipment.shippingFee}" />₫</c:when>
                                <c:otherwise><fmt:formatNumber pattern="#,##0" value="${requestScope.order.deliveryPrice}" />₫</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    
                    <c:if test="${not empty requestScope.shipment and not empty requestScope.shipment.trackingCode}">
                        <div class="bg-light-green border border-green-200 rounded-xl p-3">
                            <span class="text-xs text-success/70 uppercase tracking-wide block mb-2">Tracking ID</span>
                            <div class="flex items-center justify-between gap-2">
                                <span class="font-bold text-slate-800 tracking-id">${requestScope.shipment.trackingCode}</span>
                                <button onclick="copyToClipboard('${requestScope.shipment.trackingCode}', this)" class="copy-btn w-8 h-8 bg-success/10 hover:bg-success/20 rounded-lg flex items-center justify-center">
                                    <i class="fas fa-copy text-success text-sm"></i>
                                </button>
                            </div>
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty requestScope.shipment and not empty requestScope.shipment.estimatedDeliveryDate}">
                        <div class="flex items-center gap-3">
                            <div class="w-8 h-8 bg-amber-100 rounded-lg flex items-center justify-center flex-shrink-0">
                                <i class="fas fa-calendar-check text-amber-600 text-sm"></i>
                            </div>
                            <div>
                                <span class="text-xs text-slate-400 uppercase tracking-wide block">Dự kiến giao hàng</span>
                                <span class="font-semibold text-slate-800">
                                    ${requestScope.shipment.estimatedDeliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
                                    <c:if test="${not empty requestScope.shipment.shippingMethod and requestScope.shipment.shippingMethod.estimatedDays > 0}">(${requestScope.shipment.shippingMethod.estimatedDays} ngày)</c:if>
                                </span>
                            </div>
                        </div>
                    </c:if>
                </div>
            </section>

            <!-- Column 3: Payment Info -->
            <section class="bg-white rounded-xl shadow-card overflow-hidden card-hover">
                <div class="px-5 py-4 border-b border-slate-100 bg-light-orange flex items-center gap-3">
                    <i class="fas fa-credit-card text-orange text-lg"></i>
                    <h3 class="font-bold text-slate-800 tracking-wide">THÔNG TIN THANH TOÁN</h3>
                </div>
                <div class="p-5 space-y-3">
                    <div class="flex items-center justify-between py-2">
                        <span class="text-slate-600">Tạm tính</span>
                        <span class="font-medium text-slate-800"><fmt:formatNumber pattern="#,##0" value="${requestScope.subtotal}" />₫</span>
                    </div>
                    <div class="flex items-center justify-between py-2 border-b border-slate-100">
                        <span class="text-slate-600">Phí vận chuyển</span>
                        <span class="font-semibold text-success">
                            <c:choose>
                                <c:when test="${requestScope.order.deliveryPrice == 0}">Miễn phí</c:when>
                                <c:otherwise><fmt:formatNumber pattern="#,##0" value="${requestScope.order.deliveryPrice}" />₫</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="flex items-center justify-between py-4 pt-4">
                        <span class="text-lg font-bold text-slate-800">Tổng cộng:</span>
                        <span class="text-2xl font-extrabold text-red-600"><fmt:formatNumber pattern="#,##0" value="${requestScope.order.totalPrice}" />₫</span>
                    </div>
                    
                    <div class="flex items-center justify-center gap-2 bg-indigo text-white px-4 py-3 rounded-xl font-semibold">
                        <i class="fas fa-wallet"></i>
                        <span>VNPAY</span>
                    </div>
                </div>
            </section>
        </div>

        <c:if test="${requestScope.payment.status == 0}">
            <section class="bg-white rounded-xl shadow-card p-6 mb-6 border-2 border-amber-200">
                <div class="flex flex-col lg:flex-row items-start lg:items-center gap-6">
                    <div class="flex items-start gap-4 flex-1">
                        <div class="w-14 h-14 bg-warning rounded-xl flex items-center justify-center flex-shrink-0 shadow-lg">
                            <i class="fas fa-clock text-white text-2xl"></i>
                        </div>
                        <div>
                            <h3 class="font-bold text-slate-800 text-lg mb-1">Chờ thanh toán qua VNPAY</h3>
                            <p class="text-slate-500 text-sm leading-relaxed">
                                Vui lòng hoàn tất thanh toán để đơn hàng được xử lý sớm nhất.
                            </p>
                            <div class="mt-3 inline-flex items-center gap-2 bg-slate-100 px-3 py-2 rounded-lg">
                                <span class="text-xs text-slate-500">Mã tham chiếu:</span>
                                <span class="font-mono font-bold text-slate-700 text-sm">${requestScope.payment.vnpTxnRef}</span>
                                <button onclick="copyToClipboard('${requestScope.payment.vnpTxnRef}', this)" class="copy-btn w-6 h-6 bg-slate-200 hover:bg-slate-300 rounded flex items-center justify-center">
                                    <i class="fas fa-copy text-slate-600 text-xs"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <a href="${pageContext.request.contextPath}/vnpay/checkout?vnpTxnRef=${requestScope.payment.vnpTxnRef}" class="w-full lg:w-auto flex-shrink-0">
                        <button class="w-full lg:w-auto bg-orange hover:bg-orange/90 text-white font-bold text-lg px-10 py-4 rounded-xl flex items-center justify-center gap-3 group">
                            <i class="fas fa-credit-card text-xl group-hover:scale-110 transition-transform"></i>
                            <span>THANH TOÁN NGAY QUA VNPAY</span>
                        </button>
                    </a>
                </div>
            </section>
        </c:if>

        <footer class="flex flex-col sm:flex-row items-center justify-center gap-3 py-6">
            <a href="${pageContext.request.contextPath}/tracking?order=${requestScope.order.id}" class="w-full sm:w-auto bg-primary hover:bg-primary/90 text-white font-semibold px-8 py-3.5 rounded-xl shadow-lg shadow-red-200 hover:shadow-xl hover:shadow-red-300 transition-all duration-300 flex items-center justify-center gap-2 group">
                <i class="fas fa-location-arrow group-hover:animate-bounce"></i>
                <span>Theo dõi đơn hàng</span>
                <span class="bg-white text-primary text-xs font-bold px-2 py-0.5 rounded">Mới</span>
            </a>
            
            <a href="${pageContext.request.contextPath}/invoice?id=${requestScope.order.id}" target="_blank" class="w-full sm:w-auto bg-white hover:bg-slate-50 text-slate-700 font-semibold px-6 py-3.5 rounded-xl border border-slate-200 hover:border-slate-300 transition-all duration-300 flex items-center justify-center gap-2 hover:shadow-md">
                <i class="fas fa-print text-slate-500"></i>
                <span>In hóa đơn</span>
            </a>
            
            <a href="${pageContext.request.contextPath}/orderDetail?id=${requestScope.order.id}" class="w-full sm:w-auto bg-white hover:bg-slate-50 text-slate-700 font-semibold px-6 py-3.5 rounded-xl border border-slate-200 hover:border-slate-300 transition-all duration-300 flex items-center justify-center gap-2 hover:shadow-md">
                <i class="fas fa-eye text-slate-500"></i>
                <span>Xem chi tiết</span>
            </a>
            
            <a href="${pageContext.request.contextPath}/" class="w-full sm:w-auto bg-light-green hover:bg-green-100 text-success font-semibold px-6 py-3.5 rounded-xl border border-green-200 transition-all duration-300 flex items-center justify-center gap-2 hover:shadow-md">
                <i class="fas fa-bag-shopping"></i>
                <span>Tiếp tục mua sắm</span>
            </a>
        </footer>
        
    </main>

    <!-- Toast Notification -->
    <div id="toast" class="fixed bottom-6 left-1/2 -translate-x-1/2 bg-slate-800 text-white px-6 py-3 rounded-xl shadow-xl flex items-center gap-3 opacity-0 translate-y-4 transition-all duration-300 pointer-events-none z-50">
        <i class="fas fa-check-circle text-green-400 text-xl"></i>
        <span id="toast-message">Đã sao chép!</span>
    </div>

    <script>
        function copyToClipboard(text, btn) {
            navigator.clipboard.writeText(text).then(() => {
                const toast = document.getElementById('toast');
                toast.classList.remove('opacity-0', 'translate-y-4');
                toast.classList.add('opacity-100', 'translate-y-0');
                
                const icon = btn.querySelector('i');
                icon.classList.remove('fa-copy');
                icon.classList.add('fa-check');
                btn.classList.add('bg-success', 'text-white');
                btn.classList.remove('bg-slate-200', 'bg-slate-300', 'bg-success/10', 'hover:bg-success/20');
                
                setTimeout(() => {
                    toast.classList.add('opacity-0', 'translate-y-4');
                    toast.classList.remove('opacity-100', 'translate-y-0');
                    icon.classList.add('fa-copy');
                    icon.classList.remove('fa-check');
                    btn.classList.remove('bg-success', 'text-white');
                    btn.classList.add('bg-slate-200', 'bg-slate-300', 'bg-success/10', 'hover:bg-success/20');
                }, 2000);
            });
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            <c:if test="${requestScope.payment.status == 1}">
            createConfetti();
            </c:if>
        });
        
        function createConfetti() {
            const colors = ['#16A34A', '#22C55E', '#4ADE80', '#86EFAC', '#F59E0B', '#FCD34D', '#EE4D2D'];
            const container = document.createElement('div');
            container.className = 'confetti';
            document.body.appendChild(container);
            
            for (let i = 0; i < 50; i++) {
                const confetti = document.createElement('div');
                confetti.style.cssText = `position:absolute;left:${Math.random()*100}%;top:-20px;width:${Math.random()*10+5}px;height:${Math.random()*10+5}px;background:${colors[Math.floor(Math.random()*colors.length)]};animation:confetti-fall ${Math.random()*2+2}s ease-out forwards;animation-delay:${Math.random()*1}s;border-radius:${Math.random()>0.5?'50%':'2px'};`;
                container.appendChild(confetti);
            }
            setTimeout(() => container.remove(), 4000);
        }
    </script>

</body>
</html>
