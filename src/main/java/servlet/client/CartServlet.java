package servlet.client;

import java.io.IOException;
import java.time.LocalDateTime;

import beans.Cart;
import beans.Order;
import beans.User;
import beans.shipping.Address;
import constants.SessionConstants;
import dto.CheckoutResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AddressService;
import service.CartService;
import service.CheckoutService;
import service.PaymentService;

@WebServlet(name = "CartServlet", value = "/cart")
public class CartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final CheckoutService checkoutService = new CheckoutService();
	private final PaymentService paymentService = new PaymentService();
	private final CartService cartService = new CartService();
    private final AddressService addressService = new AddressService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(SessionConstants.CURRENT_USER);

		try {
			if (user != null) {
				Cart cart = checkoutService.getCartWithItemsAndProducts(user.getId());
				if (cart != null) {
					request.setAttribute("cartItems", cart.getCartItems());
					request.setAttribute("cartId", cart.getId());
				}

				// Cap nhat cartCount vao session
				int cartCount = cartService.countCartItemQuantityByUserId(user.getId());
				session.setAttribute("cartCount", cartCount);
                Address defaultAddress =  addressService.getDefaultAddress(user.getId());
                session.setAttribute("defaultAddress", defaultAddress);
                request.setAttribute("defaultAddress", defaultAddress);
			}
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

		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(SessionConstants.CURRENT_USER);

		if (user == null) {
            response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		try {
			String cartIdStr = request.getParameter("cartId");
			String deliveryMethodStr = request.getParameter("deliveryMethod");
			String deliveryPriceStr = request.getParameter("deliveryPrice");
			String estimatedDaysStr = request.getParameter("estimatedDays");

			if (cartIdStr == null || cartIdStr.trim().isEmpty()) {
                throw new Exception("cartId is required");
			}

			long cartId = 0;
			try {
				cartId = Long.parseLong(cartIdStr.trim());
			} catch (NumberFormatException e) {
                throw new Exception("cartId must be a valid number");
			}

			if (cartId <= 0) {
                throw new Exception("cartId must be positive");
			}

            if (!checkoutService.hasEnoughQty(cartId)) {
				session.setAttribute("errorMessage", "Đặt hàng thất bại, sản phấm hết hàng!");
				response.sendRedirect(request.getContextPath() + "/cart");
				return;
			}
			int deliveryMethod = 2;
			if (deliveryMethodStr != null && !deliveryMethodStr.trim().isEmpty()) {
				deliveryMethod = convertServiceTypeToMethodId(Integer.parseInt(deliveryMethodStr.trim()));
			} else {
				String selectedServiceId = request.getParameter("selectedServiceId");
				if (selectedServiceId != null && !selectedServiceId.trim().isEmpty()) {
					deliveryMethod = convertServiceTypeToMethodId(Integer.parseInt(selectedServiceId.trim()));
				}
			}

			double deliveryPrice = 0;
			if (deliveryPriceStr != null && !deliveryPriceStr.trim().isEmpty()) {
				try {
					deliveryPrice = Double.parseDouble(deliveryPriceStr.trim());
				} catch (NumberFormatException e) {
					deliveryPrice = 0;
				}
			}

			int estimatedDays = 3;
			if (estimatedDaysStr != null && !estimatedDaysStr.trim().isEmpty()) {
				try {
					estimatedDays = Integer.parseInt(estimatedDaysStr.trim());
				} catch (NumberFormatException e) {
					estimatedDays = 3;
				}
			}

			String finalVoucherIdStr = request.getParameter("finalVoucherId");
			String finalShipVoucherIdStr = request.getParameter("finalShipVoucherId");
			Long finalVoucherId = null;
			Long finalShipVoucherId = null;
			if (finalVoucherIdStr != null && !finalVoucherIdStr.trim().isEmpty()) {
				try {
					finalVoucherId = Long.parseLong(finalVoucherIdStr.trim());
				} catch (NumberFormatException e) {
					finalVoucherId = null;
				}
			}
			if (finalShipVoucherIdStr != null && !finalShipVoucherIdStr.trim().isEmpty()) {
				try {
					finalShipVoucherId = Long.parseLong(finalShipVoucherIdStr.trim());
				} catch (NumberFormatException e) {
					finalShipVoucherId = null;
				}
			}
            long shippingAddressId = Long.parseLong(request.getParameter("shippingAddressId"));

            LocalDateTime now = LocalDateTime.now();
            long userId = user.getId();

            Order order = new Order();
            order.setUserId(userId);
            order.setStatus(1);
            order.setDeliveryMethod(deliveryMethod);
            order.setDeliveryPrice(deliveryPrice);
            order.setCreatedAt(now);
            order.setShippingAddressId(shippingAddressId);

            String customerNote = request.getParameter("customerNote");
            Address address = addressService.getAddressById(userId, shippingAddressId);

            CheckoutResult result = checkoutService.checkoutFromCart(userId, cartId, order, customerNote, address, estimatedDays, finalVoucherId, finalShipVoucherId);

			session.setAttribute("result", result);
			session.setAttribute("cartCount", 0);

			response.sendRedirect(request.getContextPath() + "/checkoutSuccess");
		} catch (NumberFormatException e) {
			System.out.println("[CartServlet] NumberFormatException: " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("errorMessage", "Dữ liệu không hợp lệ: " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/cart");
		} catch (Exception e) {
			System.out.println("[CartServlet] Exception: " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("errorMessage", "Đặt hàng thất bại: " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/cart");
		}
	}

	/**
	 * Convert GHN service_type_id sang shipping_method_id trong database GHN:
	 * service_type_id = 2 (nhanh), service_type_id = 1 (tieu chuan) DB:
	 * shipping_method_id = 1 (nhanh), shipping_method_id = 2 (tieu chuan)
	 */
	private int convertServiceTypeToMethodId(int serviceTypeId) {
		if (serviceTypeId == 2) {
			return 1; // GHN nhanh -> DB method 1 (nhanh)
		} else if (serviceTypeId == 1) {
			return 2; // GHN tieu chuan -> DB method 2 (tieu chuan)
		}
		return serviceTypeId; // fallback
	}
}
