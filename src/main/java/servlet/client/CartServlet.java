package servlet.client;

import java.io.IOException;
import java.time.LocalDateTime;

import beans.Cart;
import beans.CartItem;
import beans.User;
import beans.vnpay.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.CartService;
import service.CheckoutService;
import service.PaymentService;

@WebServlet(name = "CartServlet", value = "/cart")
public class CartServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final CheckoutService checkoutService = new CheckoutService();
	private final PaymentService  paymentService = new PaymentService();
	private final CartService cartService = new CartService();
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
				
				int cartCount = cartService.countCartItemQuantityByUserId(user.getId());
				session.setAttribute("cartCount", cartCount);
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

		System.out.println("========== [CartServlet] POST RECEIVED ==========");
		System.out.println("[CartServlet] Request URI: " + request.getRequestURI());
		System.out.println("[CartServlet] Context Path: " + request.getContextPath());
		System.out.println("[CartServlet] All parameters:");
		request.getParameterMap().forEach((key, value) -> {
			System.out.println("  " + key + " = " + String.join(", ", value));
		});

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");

		System.out.println("[CartServlet] User from session: " + (user != null ? user.getId() + " - " + user.getEmail() : "NULL"));

		if (user == null) {
			System.out.println("[CartServlet] User not logged in, redirecting to signin");
			response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		try {
			String cartIdStr = request.getParameter("cartId");
			String deliveryMethodStr = request.getParameter("deliveryMethod");
			String deliveryPriceStr = request.getParameter("deliveryPrice");
			String estimatedDaysStr = request.getParameter("estimatedDays");

			System.out.println("[CartServlet] cartId raw: '" + cartIdStr + "'");
			System.out.println("[CartServlet] deliveryMethod raw: '" + deliveryMethodStr + "'");
			System.out.println("[CartServlet] deliveryPrice raw: '" + deliveryPriceStr + "'");
			System.out.println("[CartServlet] estimatedDays raw: '" + estimatedDaysStr + "'");

			if (cartIdStr == null || cartIdStr.trim().isEmpty()) {
				System.out.println("[CartServlet] ERROR: cartId is null or empty!");
				throw new Exception("cartId is required");
			}

			long cartId = 0;
			try {
				cartId = Long.parseLong(cartIdStr.trim());
			} catch (NumberFormatException e) {
				System.out.println("[CartServlet] ERROR: cartId is not a valid number: '" + cartIdStr + "'");
				throw new Exception("cartId must be a valid number");
			}

			if (cartId <= 0) {
				System.out.println("[CartServlet] ERROR: cartId is not positive: " + cartId);
				throw new Exception("cartId must be positive");
			}

			System.out.println("[CartServlet] cartId parsed successfully: " + cartId);

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
			
			String receiverName = user.getProfile() != null ? user.getProfile().getFullname() : "Khach hang";
			String receiverPhone = user.getProfile() != null ? user.getProfile().getPhoneNumber() : "";
			String province = request.getParameter("provinceName");
			String district = request.getParameter("districtName");
			String ward = request.getParameter("wardName");
			String addressDetail = request.getParameter("addressDetailHidden");

			if (province == null || province.trim().isEmpty()) {
				province = request.getParameter("province");
			}
			if (district == null || district.trim().isEmpty()) {
				district = request.getParameter("district");
			}
			if (ward == null || ward.trim().isEmpty()) {
				ward = request.getParameter("ward");
			}
			if (addressDetail == null || addressDetail.trim().isEmpty()) {
				addressDetail = request.getParameter("addressDetail");
			}

			province = province != null ? province.trim() : "";
			district = district != null ? district.trim() : "";
			ward = ward != null ? ward.trim() : "";
			addressDetail = addressDetail != null ? addressDetail.trim() : "";

			System.out.println("[CartServlet] Final values:");
			System.out.println("  cartId: " + cartId);
			System.out.println("  deliveryMethod: " + deliveryMethod);
			System.out.println("  deliveryPrice: " + deliveryPrice);
			System.out.println("  estimatedDays: " + estimatedDays);
			System.out.println("  province: '" + province + "'");
			System.out.println("  district: '" + district + "'");
			System.out.println("  ward: '" + ward + "'");
			System.out.println("  addressDetail: '" + addressDetail + "'");

			System.out.println("[CartServlet] Calling checkoutService.checkoutFromCart...");
			Payment p = checkoutService.checkoutFromCart(user.getId(), cartId, deliveryMethod, deliveryPrice,
				receiverName, receiverPhone, province, district, ward, addressDetail, estimatedDays);
			System.out.println("[CartServlet] Order created - orderId: " + p.getOrderId() +
				", paymentRef: " + p.getVnpTxnRef());

            System.out.println("[CartServlet] Creating payment record...");
            boolean paymentCreated = paymentService.createPayment(p);
            System.out.println("[CartServlet] Payment created: " + paymentCreated);
			long cartId = Long.parseLong(request.getParameter("cartId"));
            if(checkoutService.hasEnoughQty(cartId)){
                session.setAttribute("errorMessage", "Đặt hàng thất bại, sản phấm hết hàng!");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
			int deliveryMethod = Integer.parseInt(request.getParameter("deliveryMethod"));
			double deliveryPrice = Double.parseDouble(request.getParameter("deliveryPrice"));

            session.setAttribute("latestPayment", p);
            session.setAttribute("latestOrderId", p.getOrderId());
            session.setAttribute("cartCount", 0);
            session.setAttribute("checkoutSuccess", true);

            System.out.println("[CartServlet] Redirecting to checkoutSuccess with orderId: " + p.getOrderId());
            System.out.println("========== [CartServlet] END ==========");

            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/checkoutSuccess?orderId=" + p.getOrderId()));
            return;

        } catch (NumberFormatException e) {
			System.out.println("[CartServlet] NumberFormatException: " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("errorMessage", "Dữ liệu không hợp lệ: " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/cart");
			return;
        } catch (Exception e) {
			System.out.println("[CartServlet] Exception: " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("errorMessage", "Đặt hàng thất bại: " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/cart");
			return;
		}
	}

	private int convertServiceTypeToMethodId(int serviceTypeId) {
		if (serviceTypeId == 2) {
			return 1; 
		} else if (serviceTypeId == 1) {
			return 2; 
		}
		return serviceTypeId; 
	}
}
