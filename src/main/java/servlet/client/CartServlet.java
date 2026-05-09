package servlet.client;

import java.io.IOException;
import java.util.List;

import beans.Cart;
import beans.CartItem;
import beans.Product;
import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.CartItemService;
import service.CartService;
import service.CheckoutService;
import service.ProductService;
import utils.CartUtils;

@WebServlet(name = "CartServlet", value = "/cart")
public class CartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final CheckoutService checkoutService = new CheckoutService();
	private final CartService cartService = new CartService();
	private final CartItemService cartItemService = new CartItemService();
	private final ProductService productService = new ProductService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");

		try {
			List<CartItem> cartItems = null;
			Long cartId = null;
			int selectedCount = 0;

			if (user != null) {
				Cart cart = checkoutService.getCartWithItemsAndProducts(user.getId());
				if (cart != null) {
					cartItems = cart.getCartItems();
					cartId = cart.getId();
					selectedCount = cartItemService.getSelectedCountByCartId(cart.getId());
				}
			} else {
				cartItems = CartUtils.getGuestCart(session);
				for (CartItem item : cartItems) {
					Product product = productService.getById(item.getProductId());
					if (product != null) {
						item.setProduct(product);
					}
					if (item.isSelected()) selectedCount++;
				}
			}

			request.setAttribute("cartItems", cartItems);
			request.setAttribute("cartId", cartId);
			request.setAttribute("selectedCount", selectedCount);
			request.getRequestDispatcher("/WEB-INF/views/cartView.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", "Khong the tai gio hang");
			request.getRequestDispatcher("/WEB-INF/views/cartView.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");

		if (user == null) {
			session.setAttribute("redirectAfterLogin", "/checkout");
			response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		try {
			CartUtils.mergeGuestCartToDb(session);
			
			long cartId = Long.parseLong(request.getParameter("cartId"));
			int deliveryMethod = Integer.parseInt(request.getParameter("deliveryMethod"));
			double deliveryPrice = Double.parseDouble(request.getParameter("deliveryPrice"));

			checkoutService.checkoutFromCart(user.getId(), cartId, deliveryMethod, deliveryPrice);

			response.sendRedirect(request.getContextPath() + "/invoice?orderId=" + checkoutService.getLastOrderId());

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("errorMessage", "Đặt hàng thất bại, vui lòng thử lại");
			response.sendRedirect(request.getContextPath() + "/cart");
		}
	}
}
