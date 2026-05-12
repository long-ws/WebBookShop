package service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import beans.Cart;
import beans.CartItem;
import beans.Order;
import beans.OrderItem;
import beans.Product;
import beans.vnpay.Payment;
import dao.CartDAO;
import dao.CartItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.ProductDAO;
import servlet.vnpay.VNPConfig;

public class CheckoutService {

	private final CartDAO cartDAO = new CartDAO();
	private final CartItemDAO cartItemDAO = new CartItemDAO();
	private final ProductDAO productDAO = new ProductDAO();
	private final OrderDAO orderDAO = new OrderDAO();
	private final OrderItemDAO orderItemDAO = new OrderItemDAO();

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

    public Payment checkoutFromCart(long userId, long cartId, int deliveryMethod, double deliveryPrice) {
        Cart cart = cartDAO.getById(cartId);
        if (cart == null) throw new RuntimeException("Giỏ hàng không tồn tại");

        List<CartItem> items = cartItemDAO.getByCartId(cartId);
        for (CartItem ci : items) {
            Product p = productDAO.getById(ci.getProductId());
            ci.setProduct(p);
        }
        cart.setCartItems(items);

        double totalPrice = cart.getTotalPrice() + deliveryPrice;

        Order o = new Order();
        o.setUserId(userId);
        o.setStatus(1);
        o.setDeliveryMethod(deliveryMethod);
        o.setDeliveryPrice(deliveryPrice);
        o.setTotalPrice(totalPrice);
        o.setCreatedAt(LocalDateTime.now());

        long orderId = orderDAO.insert(o);

        for (CartItem ci : items) {
            orderItemDAO.insert(new OrderItem(0L, orderId, ci.getProduct().getId(),
                    ci.getProduct().getPrice(), ci.getProduct().getDiscount(),
                    ci.getQuantity(), LocalDateTime.now(), null));
        }

        cartDAO.delete(cartId);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Payment p = new Payment();
        p.setOrderId(orderId);
        p.setUserId(userId);
        p.setStatus(0);
        p.setCreatedAt(now);
        p.setAmount(totalPrice);
        p.setVnpTxnRef(VNPConfig.getRandomCode(orderId, userId, now));

        return p;
    }
}
