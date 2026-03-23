package servlet.client;

import java.io.IOException;

import beans.Cart;
import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.CheckoutService;

@WebServlet(name = "CartServlet", value = "/cart")
public class CartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final CheckoutService checkoutService = new CheckoutService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");

		try {
			if (user != null) {
				Cart cart = checkoutService.getCartWithItemsAndProducts(user.getId());
				if (cart != null) {
					request.setAttribute("cartItems", cart.getCartItems());
					request.setAttribute("cartId", cart.getId());
				}
			}
			request.getRequestDispatcher("/WEB-INF/views/cartView.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", "Không thể tải giỏ hàng");
			request.getRequestDispatcher("/WEB-INF/views/cartView.jsp").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		try {
			long cartId = Long.parseLong(request.getParameter("cartId"));
			int deliveryMethod = Integer.parseInt(request.getParameter("deliveryMethod"));
			double deliveryPrice = Double.parseDouble(request.getParameter("deliveryPrice"));

			checkoutService.checkoutFromCart(user.getId(), cartId, deliveryMethod, deliveryPrice);

			response.sendRedirect(request.getContextPath() + "/order?success=1");

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("errorMessage", "Đặt hàng thất bại, vui lòng thử lại");
			response.sendRedirect(request.getContextPath() + "/cart");
		}
	}
}
