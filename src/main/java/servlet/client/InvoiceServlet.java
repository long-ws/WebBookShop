package servlet.client;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beans.Order;
import beans.OrderItem;
import beans.Product;
import beans.Shipment;
import beans.User;
import beans.vnpay.Payment;
import constants.SessionConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.OrderItemService;
import service.OrderService;
import service.PaymentService;
import service.ProductService;
import service.ShipmentService;

@WebServlet(name = "InvoiceServlet", value = "/invoice")
public class InvoiceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final OrderService orderService = new OrderService();
	private final OrderItemService orderItemService = new OrderItemService();
	private final ProductService productService = new ProductService();
	private final PaymentService paymentService = new PaymentService();
	private final ShipmentService shipmentService = new ShipmentService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute(SessionConstants.CURRENT_USER);

		long orderId = 0;
		try {
			orderId = Long.parseLong(request.getParameter("id"));
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		Order order = null;
		List<OrderItem> orderItems = new ArrayList<>();
		User user = null;
		Payment payment = null;
		Shipment shipment = null;
		double subtotal = 0;

		try {
			order = orderService.getById(orderId);

			if (order != null) {
				if (sessionUser != null && sessionUser.getId() == order.getUserId()) {
					user = sessionUser;
				}

				orderItems = orderItemService.getByOrderId(orderId);

				for (OrderItem item : orderItems) {
					try {
						Product product = productService.getById(item.getProductId());
						item.setProduct(product);
					} catch (Exception e) {
						item.setProduct(new Product());
					}

					double itemPrice = item.getDiscount() > 0
							? item.getPrice() * (100 - item.getDiscount()) / 100.0
							: item.getPrice();
					subtotal += itemPrice * item.getQuantity();
				}

				payment = paymentService.getInitPayment(orderId);

				double totalOrderPrice = subtotal + order.getDeliveryPrice();
				request.setAttribute("totalOrderPrice", totalOrderPrice);

				shipment = shipmentService.getByOrderIdWithDetails(orderId);

				if (order.getCreatedAt() != null) {
					request.setAttribute("orderCreatedAt", Date.from(order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
				}
				if (payment != null && payment.getCreatedAt() != null) {
					request.setAttribute("paymentCreatedAt", Date.from(payment.getCreatedAt().toInstant()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (order == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		request.setAttribute("order", order);
		request.setAttribute("orderItems", orderItems);
		request.setAttribute("user", user);
		request.setAttribute("payment", payment);
		request.setAttribute("subtotal", subtotal);
		request.setAttribute("shipment", shipment);
		request.getRequestDispatcher("/WEB-INF/views/invoiceView.jsp").forward(request, response);
	}
}
