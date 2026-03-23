package service;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import beans.Cart;
import beans.CartItem;
import beans.Order;
import beans.OrderItem;
import beans.Product;
import dao.CartDAO;
import dao.CartItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.ProductDAO;
import utils.DBConnection;

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

	public void checkoutFromCart(long userId, long cartId, int deliveryMethod, double deliveryPrice) {
		try (Connection conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false);

			long orderId = orderDAO.insert(conn,
					new Order(0L, userId, 1, deliveryMethod, deliveryPrice, LocalDateTime.now(), null));

			List<CartItem> items = cartItemDAO.getByCartId(conn, cartId);

			for (CartItem ci : items) {
				Product p = productDAO.getById(conn, ci.getProductId());

				orderItemDAO.insert(conn, new OrderItem(0L, orderId, p.getId(), p.getPrice(), p.getDiscount(),
						ci.getQuantity(), LocalDateTime.now(), null));
			}

			cartDAO.delete(conn, cartId);

			conn.commit();
		} catch (Exception e) {
			throw new RuntimeException("Checkout failed", e);
		}
	}
}
