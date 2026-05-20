package service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Timestamp now =  new Timestamp(System.currentTimeMillis());

        Order o = new Order();
        o.setUserId(userId);
        o.setStatus(1);
        o.setDeliveryMethod(deliveryMethod);
        o.setDeliveryPrice(deliveryPrice);
        o.setTotalPrice(totalPrice);
        o.setCreatedAt(now.toLocalDateTime());

        long orderId = orderDAO.insert(o);

        for (CartItem ci : items) {
            orderItemDAO.insert(new OrderItem(0L, orderId, ci.getProduct().getId(),
                    ci.getProduct().getPrice(), ci.getProduct().getDiscount(),
                    ci.getQuantity(), LocalDateTime.now(), null));
        }

        cartDAO.delete(cartId);

        Payment p = new Payment();
        p.setOrderId(orderId);
        p.setUserId(userId);
        p.setStatus(0);
        p.setCreatedAt(now);
        p.setExpiredAt(VNPConfig.getExpireTime(now));
        p.setAmount(totalPrice);
        p.setVnpTxnRef(VNPConfig.getRandomCode(orderId, userId, now));

        return p;
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
