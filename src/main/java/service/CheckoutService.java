package service;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import beans.Cart;
import beans.CartItem;
import beans.Order;
import beans.OrderItem;
import beans.Product;
import beans.Shipment;
import beans.ShippingMethod;
import beans.User;
import dao.CartDAO;
import dao.CartItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.ProductDAO;
import dao.ShipmentDAO;
import dao.ShipmentTrackingDAO;
import dao.ShippingMethodDAO;
import dao.UserDAO;
import dto.ProviderFeeResponse;
import utils.DBConnection;
import utils.ShippingStatus;

public class CheckoutService {

	private final CartDAO cartDAO = new CartDAO();
	private final CartItemDAO cartItemDAO = new CartItemDAO();
	private final ProductDAO productDAO = new ProductDAO();
	private final OrderDAO orderDAO = new OrderDAO();
	private final OrderItemDAO orderItemDAO = new OrderItemDAO();
	private final ShippingMethodDAO shippingMethodDAO = new ShippingMethodDAO();
	private final ShipmentDAO shipmentDAO = new ShipmentDAO();
	private final ShipmentTrackingDAO trackingDAO = new ShipmentTrackingDAO();
	private final UserDAO userDAO = new UserDAO();

	private final HybridShippingService hybridShippingService = new HybridShippingService();

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

	public Cart getCartWithSelectedItemsAndProducts(long userId) {
		Cart cart = cartDAO.getByUserId(userId);
		if (cart == null)
			return null;

		List<CartItem> items = cartItemDAO.getSelectedByCartId(cart.getId());
		for (CartItem ci : items) {
			Product p = productDAO.getById(ci.getProductId());
			ci.setProduct(p);
		}
		cart.setCartItems(items);
		return cart;
	}

	public long checkoutFromCart(long userId, long cartId, int deliveryMethod, double deliveryPrice) {
		try (Connection conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false);

			Order order = new Order(0L, userId, 1, deliveryMethod, deliveryPrice, LocalDateTime.now(), null);
			long orderId = orderDAO.insert(conn, order);

			List<CartItem> items = cartItemDAO.getSelectedByCartId(cartId);

			for (CartItem ci : items) {
				Product p = productDAO.getById(conn, ci.getProductId());

				orderItemDAO.insert(conn, new OrderItem(0L, orderId, p.getId(), p.getPrice(), p.getDiscount(),
						ci.getQuantity(), LocalDateTime.now(), null));
			}

			for (CartItem ci : items) {
				cartItemDAO.delete(conn, ci.getId());
			}

			conn.commit();

			// Auto-create shipment record after order is committed
			createShipmentForOrder(orderId, userId, deliveryMethod, deliveryPrice);

			return orderId;
		} catch (Exception e) {
			throw new RuntimeException("Checkout failed", e);
		}
	}

	private void createShipmentForOrder(long orderId, long userId, int deliveryMethod, double deliveryPrice) {
		try {
			User user = userDAO.getById(userId);
			if (user == null) return;

			Shipment shipment = new Shipment();
			shipment.setOrderId(orderId);
			shipment.setShippingMethodId(0L);
			shipment.setTrackingCode("WEB" + String.format("%010d", orderId));
			shipment.setReceiverName(user.getFullname() != null ? user.getFullname() : user.getUsername());
			shipment.setReceiverPhone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
			shipment.setProvince("");
			shipment.setDistrict("");
			shipment.setWard("");
			shipment.setAddressDetail(user.getAddress() != null ? user.getAddress() : "");
			shipment.setTotalWeight(0.5);
			shipment.setTotalVolume(0.0);
			shipment.setShippingFee(deliveryPrice);
			shipment.setShippingStatus(ShippingStatus.WAITING_PICKUP);
			shipment.setSellerNote("");
			shipment.setCustomerNote("");
			shipment.setShipperContact("");
			shipment.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(deliveryMethod == 1 ? 5 : 2));
			shipment.setShippedAt(null);
			shipment.setDeliveredAt(null);
			shipment.setCreatedAt(LocalDateTime.now());
			shipment.setUpdatedAt(LocalDateTime.now());
			shipment.setProviderType("INTERNAL");
			shipment.setProviderOrderCode("");

			long shipmentId = shipmentDAO.insert(shipment);

			// Add initial tracking event
			beans.ShipmentTracking tracking = new beans.ShipmentTracking();
			tracking.setShipmentId(shipmentId);
			tracking.setStatus(ShippingStatus.WAITING_PICKUP);
			tracking.setNote("Don hang da duoc tao, dang cho xac nhan");
			tracking.setLocation("He thong WebBookShop");
			tracking.setUpdatedBy("SYSTEM");
			tracking.setUpdatedAt(LocalDateTime.now());
			trackingDAO.insert(tracking);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long getLastOrderId() {
		try {
			List<Order> orders = orderDAO.getOrderedPart(1, 0, "id", "DESC");
			return orders.isEmpty() ? 0 : orders.get(0).getId();
		} catch (Exception e) {
			return 0;
		}
	}

	public List<ProviderFeeResponse> getAvailableShippingFees(double weight, double volume,
			String province, String district, String ward) {
		return hybridShippingService.getAvailableFees(weight, volume, province, district, ward);
	}

	public ShippingMethod getShippingMethodById(long id) {
		return shippingMethodDAO.getById(id);
	}

	public List<ShippingMethod> getActiveShippingMethods() {
		return shippingMethodDAO.getAllActive();
	}

	public double getShippingFeeEstimate(long methodId, double weight, double volume,
			String province, String district, String ward) {
		ShippingMethod method = shippingMethodDAO.getById(methodId);
		if (method == null) {
			return 0;
		}

		List<ProviderFeeResponse> fees = hybridShippingService.getAvailableFees(
				weight, volume, province, district, ward);

		for (ProviderFeeResponse fee : fees) {
			if (fee.getProviderName().equalsIgnoreCase(method.getProviderType())) {
				return fee.getFee();
			}
		}

		return method.getPricePerKg() * weight;
	}
}
