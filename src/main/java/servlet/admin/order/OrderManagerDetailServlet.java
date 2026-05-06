package servlet.admin.order;

import java.io.IOException;
import java.util.List;

import beans.Order;
import beans.OrderItem;
import beans.Product;
import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderItemService;
import service.OrderService;
import service.ProductService;
import service.UserService;

@WebServlet(name = "OrderManagerDetailServlet", value = "/admin/orderManager/detail")
public class OrderManagerDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final OrderService orderService = new OrderService();
	private final UserService userService = new UserService();
	private final OrderItemService orderItemService = new OrderItemService();
	private final ProductService productService = new ProductService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String idParam = request.getParameter("id");
		long id;

		try {
			id = Long.parseLong(idParam);
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/admin/orderManager");
			return;
		}

		Order order;
		try {
			order = orderService.getById(id);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/admin/orderManager");
			return;
		}

		if (order == null) {
			response.sendRedirect(request.getContextPath() + "/admin/orderManager");
			return;
		}

		try {
			User user = userService.getById(order.getUserId());
			order.setUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<OrderItem> orderItems;
		try {
			orderItems = orderItemService.getByOrderId(order.getId());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/admin/orderManager");
			return;
		}

		for (int i = 0; i < orderItems.size(); i++) {
			OrderItem orderItem = orderItems.get(i);
			try {
				Product product = productService.getById(orderItem.getProductId());
				orderItem.setProduct(product);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		order.setOrderItems(orderItems);
		order.setTotalPrice(orderService.calculateTotalPrice(orderItems, order.getDeliveryPrice()));

		request.setAttribute("order", order);
		request.getRequestDispatcher("/WEB-INF/views/orderManagerDetailView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}
}
