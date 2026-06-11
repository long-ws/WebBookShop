package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import beans.*;
import beans.shipping.Address;
import beans.shipping.ShippingCalculationResult;
import beans.shipping.ShippingInfo;
import beans.vnpay.Payment;
import dao.*;
import dto.CategoryDTO;
import dto.CheckoutResult;
import dto.ProductDTO;
import servlet.vnpay.VNPConfig;
import utils.DBConnection;
import utils.ShippingStatus;

public class CheckoutService {

    private final CartDAO cartDAO = new CartDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final ShipmentDAO shipmentDAO = new ShipmentDAO();
    private final ShipmentTrackingDAO trackingDAO = new ShipmentTrackingDAO();
    private final ShippingCalculatorService shippingCalculator = new ShippingCalculatorService();
    private final VoucherDao voucherDao = new VoucherDao();
    private final PaymentService paymentService = new PaymentService();
    /**
     * Lấy cart với items và products
     */
    public Cart getCartWithItemsAndProducts(long userId) {
        Cart cart = cartDAO.getByUserId(userId);
        if (cart == null)
            return null;

        List<CartItem> items = cartItemDAO.getByCartId(cart.getId());
        for (CartItem ci : items) {
            Product p = productDAO.getById(ci.getProductId());
            ci.setProduct(p);
        }
        cart.setCartItems(items);
        return cart;
    }

    /**
     * Tính tổng trọng lượng và thể tích từ cart items
     */
    public ShippingInfo calculateShippingInfo(long cartId, long methodId, String provinceCode) {
        try {
            Cart cart = cartDAO.getById(cartId);
            if (cart == null) {
                return null;
            }

            List<CartItem> items = cartItemDAO.getByCartId(cartId);
            double totalWeight = 0;
            double totalVolume = 0;

            for (CartItem ci : items) {
                Product p = productDAO.getById(ci.getProductId());
                if (p != null) {
                    double itemWeight = p.getWeight() > 0 ? p.getWeight() : 0.3;
                    totalWeight += itemWeight * ci.getQuantity();
                    totalVolume += (itemWeight * 1000) * ci.getQuantity();
                }
            }

            ShippingCalculationResult calcResult =
                    shippingCalculator.calculateFee(methodId, provinceCode, totalWeight, totalVolume);

            if (calcResult != null) {
                ShippingInfo info = new ShippingInfo();
                info.setMethodId(methodId);
                info.setMethodName(calcResult.getMethodName());
                info.setIsExpress(calcResult.isIsExpress());
                info.setProvinceCode(provinceCode);
                info.setProvinceName(calcResult.getProvinceName());
                info.setZoneType(calcResult.getZoneType());
                info.setWeightKg(totalWeight);
                info.setVolumeCm3(totalVolume);
                info.setShippingFee(calcResult.getShippingFee());
                info.setEstimatedDaysMin(calcResult.getEstimatedDaysMin());
                info.setEstimatedDaysMax(calcResult.getEstimatedDaysMax());
                info.setFreeShipping(calcResult.isFreeShipping());
                info.setFreeShippingThreshold(calcResult.getFreeShippingThreshold());
                return info;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checkout đầy đủ tham số (signature mới - có customerNote)
     */
    public CheckoutResult checkoutFromCart(long userId, long cartId, Order order, String customerNote, Address address, int estimatedDays, Long finalVoucherId, Long finalShipVoucherId) throws SQLException {
        CheckoutResult result = null;

        Connection con = null;
        try{
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            Cart cart = cartDAO.getById(cartId);
            if (cart == null) {
                throw new RuntimeException("Giỏ hàng không tồn tại");
            }
            if (cart.getUserId() != userId) {
                throw new RuntimeException("Giỏ hàng không thuộc tài khoản hiện tại");
            }

            List<CartItem> items = cartItemDAO.getByCartId(cartId);
            if (items == null || items.isEmpty()) {
                throw new RuntimeException("Giỏ hàng trống, không thể đặt hàng");
            }
            double cartTotal = 0;
            double totalWeight = 0;
            Map<Long, Double> categoryTotalMap = new HashMap<>();
            Map<Long, Double> productTotalMap = new HashMap<>();
            for (CartItem ci : items) {
                Product p = productDAO.getById(ci.getProductId());
                if (p == null) {
                    throw new RuntimeException("Sản phẩm không tồn tại (ID: " + ci.getProductId() + ")");
                }
                ci.setProduct(p);
                double linePrice = p.getPrice();
                if (p.getDiscount() > 0) {
                    linePrice = p.getPrice() * (100 - p.getDiscount()) / 100.0;
                }
                double itemTotal = linePrice * ci.getQuantity();
                cartTotal += itemTotal;
                long categoryId = productDAO.getCategoryIdByProductId(p.getId());
                categoryTotalMap.put(categoryId, categoryTotalMap.getOrDefault(categoryId, 0.0) + itemTotal);
                productTotalMap.put(p.getId(), productTotalMap.getOrDefault(p.getId(), 0.0) + itemTotal);
                double weight = p.getWeight() > 0 ? p.getWeight() : 0.3;
                totalWeight += weight * ci.getQuantity();
            }
            cart.setCartItems(items);
            double discountOrderAmount = 0;
            Voucher discountVoucher = null;
            if (finalVoucherId != null && finalVoucherId > 0) {
                discountVoucher = voucherDao.getVoucherWithRelations(finalVoucherId);
                if (discountVoucher != null && discountVoucher.isActive() && cartTotal >= discountVoucher.getMinPurchase()) {
                    double baseAmountForDiscount = 0;
                    switch (discountVoucher.getApplyTo()) {
                        case 0:
                        case 3:
                            baseAmountForDiscount = cartTotal;
                            break;
                        case 1:
                            if (discountVoucher.getCategories() != null) {
                                for (CategoryDTO cat : discountVoucher.getCategories()) {
                                    baseAmountForDiscount += categoryTotalMap.getOrDefault(cat.getId(), 0.0);
                                }
                            }
                            break;
                        case 2:
                            if (discountVoucher.getProducts() != null) {
                                for (ProductDTO prod : discountVoucher.getProducts()) {
                                    baseAmountForDiscount += productTotalMap.getOrDefault(prod.getId(), 0.0);
                                }
                            }
                            break;
                    }
                    if (baseAmountForDiscount > 0) {
                        if (discountVoucher.getCalculationMethod() == 1) {
                            double calculated = (baseAmountForDiscount * discountVoucher.getValue()) / 100.0;
                            discountOrderAmount = Math.min(calculated, discountVoucher.getMaxDiscount() > 0 ? discountVoucher.getMaxDiscount() : Double.MAX_VALUE);
                        } else {
                            discountOrderAmount = Math.min(discountVoucher.getValue(), baseAmountForDiscount);
                        }
                    }
                }
            }

            double discountShipAmount = 0;
            Voucher shipVoucher = null;
            double deliveryPrice = order.getDeliveryPrice();
            if (finalShipVoucherId != null && finalShipVoucherId > 0) {
                shipVoucher = voucherDao.getVoucherWithRelations(finalShipVoucherId);
                if (shipVoucher != null && shipVoucher.isActive() && cartTotal >= shipVoucher.getMinPurchase()) {
                    if (shipVoucher.getCalculationMethod() == 1) {
                        discountShipAmount = (deliveryPrice * shipVoucher.getValue()) / 100.0;
                    } else {
                        discountShipAmount = shipVoucher.getValue();
                    }
                    discountShipAmount = Math.min(discountShipAmount, deliveryPrice);
                }
            }

            double finalDeliveryPrice = Math.max(0, deliveryPrice - discountShipAmount);
            double finalTotalPrice = cartTotal + finalDeliveryPrice - discountOrderAmount;
            if (finalTotalPrice < 0) finalTotalPrice = 0;

            order.setTotalProductPrice(cartTotal);
            order.setProductDiscount(discountOrderAmount);
            order.setShipDiscount(discountShipAmount);
            order.setTotalPrice(finalTotalPrice);
            long orderId = orderDAO.insert(con, order);
            if (orderId <= 0) {
                throw new RuntimeException("Tạo đơn hàng thất bại");
            }

            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem ci : items) {
                Product p = ci.getProduct();
                if (p == null) {
                    p = productDAO.getById(ci.getProductId());
                }
                if (p == null) {
                    throw new RuntimeException("Sản phẩm không tồn tại (ID: " + ci.getProductId() + ")");
                }
                OrderItem orderItem = new OrderItem(0L, orderId, p.getId(),
                        p.getPrice(), p.getDiscount(),
                        ci.getQuantity(), order.getCreatedAt(), null);
                orderItem.setProduct(p);
                orderItems.add(orderItem);

                orderItemDAO.insert(con, orderItem);
                try{
                    cartItemDAO.delete(con, ci.getId());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if (discountVoucher != null && discountOrderAmount > 0) {
                voucherDao.saveVoucherUsage(con, orderId, discountVoucher.getId(), userId, discountOrderAmount, 0);
                voucherDao.incrementUsedCount(con, discountVoucher.getId());
            }
            if (shipVoucher != null && discountShipAmount > 0) {
                voucherDao.saveVoucherUsage(con, orderId, shipVoucher.getId(), userId, discountShipAmount, 1);
                voucherDao.incrementUsedCount(con, shipVoucher.getId());
            }

            String trackingCode = "WEB" + String.format("%08d", orderId);
            LocalDateTime estimatedDelivery = LocalDateTime.now().plusDays(estimatedDays > 0 ? estimatedDays : 3);

            String receiverName = address.getFullname();
            String receiverPhone = address.getPhone();
            String province = address.getProvince();
            String district = address.getDistrict();
            String ward = address.getWard();
            String addressDetail = address.getAddressDetail();

            Shipment shipment = new Shipment();
            shipment.setOrderId(orderId);
            shipment.setShippingMethodId(order.getDeliveryMethod());
            shipment.setTrackingCode(trackingCode);
            shipment.setReceiverName(receiverName != null ? receiverName : "Khach hang");
            shipment.setReceiverPhone(receiverPhone != null ? receiverPhone : "");
            shipment.setProvince(province != null ? province : "");
            shipment.setDistrict(district != null ? district : "");
            shipment.setWard(ward != null ? ward : "");
            shipment.setAddressDetail(addressDetail != null ? addressDetail : "");
            shipment.setShippingFee(deliveryPrice);
            shipment.setShippingStatus(ShippingStatus.WAITING_PICKUP);
            shipment.setProviderType("GHN");
            shipment.setTotalWeight(totalWeight);
            shipment.setEstimatedDeliveryDate(estimatedDelivery);
            shipment.setCreatedAt(LocalDateTime.now());
            shipment.setCustomerNote(customerNote != null ? customerNote.trim() : "");

            try {
                long shipmentId = shipmentDAO.insert(con, shipment);
                shipment.setId(shipmentId);

                ShipmentTracking initialTracking = new ShipmentTracking();
                initialTracking.setShipmentId(shipmentId);
                initialTracking.setStatus(ShippingStatus.WAITING_PICKUP);
                initialTracking.setNote("Đơn hàng đã được tạo, đang chờ lấy hàng");
                initialTracking.setLocation("Kho hàng - Shop Bán Sách");
                initialTracking.setUpdatedBy("SYSTEM");
                initialTracking.setUpdatedAt(LocalDateTime.now());
                trackingDAO.insert(con, initialTracking);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Timestamp createdAt = Timestamp.valueOf(order.getCreatedAt());
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setUserId(userId);
            payment.setStatus(0);
            payment.setCreatedAt(createdAt);
            payment.setExpiredAt(VNPConfig.getExpireTime(createdAt));
            payment.setAmount(finalTotalPrice);
            payment.setVnpTxnRef(VNPConfig.getRandomCode(orderId, userId, createdAt));
            paymentService.createPayment(con, payment);

            con.commit();

            result = new CheckoutResult();
            order.setId(orderId);
            order.setOrderItems(orderItems);
            result.setOrder(order);
            result.setPayment(payment);
            result.setShipment(shipment);
        }catch(Exception e){
            if (con != null) con.rollback();
            throw new RuntimeException(e);
        }finally{
            if (con != null) {
                con.close();
            }
        }
        return result;
    }
    public boolean hasEnoughQty(long cartId) {
        Cart cart = cartDAO.getById(cartId);
        if (cart == null) throw new RuntimeException("Giỏ hàng không tồn tại");

        List<CartItem> items = cartItemDAO.getByCartId(cartId);
        if (items == null || items.isEmpty()) return true;

        List<Long> productIds = new ArrayList<>();
        for (CartItem ci : items) {
            productIds.add(ci.getProductId());
        }

        Map<Long, Integer> map = productDAO.getQty(productIds);
        for (CartItem ci : items) {
            long pId = ci.getProductId();
            int cartQty = ci.getQuantity();
            int productQty = map.getOrDefault(pId, 0);

            if (cartQty > productQty) {
                return false;
            }else{
                map.put(pId, cartQty);
            }
        }
        return productDAO.updateQty(map);
    }
}
