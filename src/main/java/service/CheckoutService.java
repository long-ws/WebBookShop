package service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import beans.Cart;
import beans.CartItem;
import beans.Order;
import beans.OrderItem;
import beans.Product;
import beans.Shipment;
import beans.ShipmentTracking;
import beans.shipping.ShippingCalculationResult;
import beans.shipping.ShippingInfo;
import beans.vnpay.Payment;
import dao.CartDAO;
import dao.CartItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.ProductDAO;
import dao.ShipmentDAO;
import dao.ShipmentTrackingDAO;
import servlet.vnpay.VNPConfig;
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
    public Payment checkoutFromCart(long userId, long cartId, int deliveryMethod, double deliveryPrice,
            String receiverName, String receiverPhone, String province, String district,
            String ward, String addressDetail, int estimatedDays, String customerNote) throws SQLException {

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

        double totalPrice = 0;
        double totalWeight = 0;
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
            totalPrice += linePrice * ci.getQuantity();
            double weight = p.getWeight() > 0 ? p.getWeight() : 0.3;
            totalWeight += weight * ci.getQuantity();
        }
        cart.setCartItems(items);

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(1);
        order.setDeliveryMethod(deliveryMethod);
        order.setDeliveryPrice(deliveryPrice);
        order.setTotalPrice(totalPrice + deliveryPrice);
        order.setCreatedAt(LocalDateTime.now());

        long orderId = orderDAO.insert(order);
        if (orderId <= 0) {
            throw new RuntimeException("Tạo đơn hàng thất bại");
        }

        for (CartItem ci : items) {
            Product p = ci.getProduct();
            if (p == null) {
                p = productDAO.getById(ci.getProductId());
            }
            if (p == null) {
                throw new RuntimeException("Sản phẩm không tồn tại (ID: " + ci.getProductId() + ")");
            }
            orderItemDAO.insert(new OrderItem(0L, orderId, p.getId(),
                    p.getPrice(), p.getDiscount(),
                    ci.getQuantity(), LocalDateTime.now(), null));
        }

        cartDAO.delete(cartId);

        String trackingCode = "WEB" + String.format("%08d", orderId);
        LocalDateTime estimatedDelivery = LocalDateTime.now().plusDays(estimatedDays > 0 ? estimatedDays : 3);

        Shipment shipment = new Shipment();
        shipment.setOrderId(orderId);
        shipment.setShippingMethodId(deliveryMethod);
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
            long shipmentId = shipmentDAO.insert(shipment);
            shipment.setId(shipmentId);

            ShipmentTracking initialTracking = new ShipmentTracking();
            initialTracking.setShipmentId(shipmentId);
            initialTracking.setStatus(ShippingStatus.WAITING_PICKUP);
            initialTracking.setNote("Đơn hàng đã được tạo, đang chờ lấy hàng");
            initialTracking.setLocation("Kho hàng - Shop Bán Sách");
            initialTracking.setUpdatedBy("SYSTEM");
            initialTracking.setUpdatedAt(LocalDateTime.now());
            trackingDAO.insert(initialTracking);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setStatus(0);
        payment.setCreatedAt(now);
        payment.setAmount(totalPrice + deliveryPrice);
        payment.setVnpTxnRef(VNPConfig.getRandomCode(orderId, userId, now));

        return payment;
    }

    /**
     * Checkout đầy đủ tham số (signature cũ - không có customerNote)
     */
    public Payment checkoutFromCart(long userId, long cartId, int deliveryMethod, double deliveryPrice,
            String receiverName, String receiverPhone, String province, String district,
            String ward, String addressDetail, int estimatedDays) throws SQLException {
        return checkoutFromCart(userId, cartId, deliveryMethod, deliveryPrice,
                receiverName, receiverPhone, province, district, ward, addressDetail, estimatedDays, null);
    }

    /**
     * Legacy method - tương thích với signature cũ
     */
    public Payment checkoutFromCart(long userId, long cartId, int deliveryMethod, double deliveryPrice) throws SQLException {
        return checkoutFromCart(userId, cartId, deliveryMethod, deliveryPrice, null, null, null, null, null, null, 3, null);
    }

    public Payment checkoutFromCart(long userId, long cartId, int deliveryMethod, double deliveryPrice,
            String receiverName, String receiverPhone, String province, String district,
            String ward, String addressDetail) throws SQLException {
        return checkoutFromCart(userId, cartId, deliveryMethod, deliveryPrice,
                receiverName, receiverPhone, province, district, ward, addressDetail, 3, null);
    }

    /**
     * Checkout với ShippingInfo (mới)
     */
    public Payment checkoutWithShippingInfo(long userId, long cartId, ShippingInfo shippingInfo,
            String receiverName, String receiverPhone, String province, String district,
            String ward, String addressDetail) throws SQLException {

        if (shippingInfo == null) {
            throw new RuntimeException("Thông tin vận chuyển không hợp lệ");
        }

        return checkoutFromCart(userId, cartId, (int)shippingInfo.getMethodId(), shippingInfo.getShippingFee(),
                receiverName, receiverPhone, province, district, ward, addressDetail,
                shippingInfo.getEstimatedDaysMax(), null);
    }
}
