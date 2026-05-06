package servlet.admin.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.Order;
import beans.OrderItem;
import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderItemService;
import service.OrderService;
import service.UserService;

@WebServlet(name = "OrderManagerServlet", value = "/admin/orderManager")
public class OrderManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final OrderService orderService = new OrderService();
	private final UserService userService = new UserService();
	private final OrderItemService orderItemService = new OrderItemService();

	private static final int ORDERS_PER_PAGE = 5;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int totalOrders;
		try {
			totalOrders = orderService.count();
		} catch (Exception e) {
			e.printStackTrace();
			totalOrders = 0;
		}

		int totalPages = totalOrders / ORDERS_PER_PAGE;
		if (totalOrders % ORDERS_PER_PAGE != 0) {
			totalPages++;
		}
		if (totalPages == 0) {
			totalPages = 1;
		}

		int page = 1;
		String pageParam = request.getParameter("page");
		if (pageParam != null) {
			try {
				page = Integer.parseInt(pageParam);
			} catch (Exception ignored) {
			}
		}

		if (page < 1 || page > totalPages) {
			page = 1;
		}

		int offset = (page - 1) * ORDERS_PER_PAGE;

		List<Order> orders;
		try {
			orders = orderService.getOrderedPart(ORDERS_PER_PAGE, offset, "id", "DESC");
		} catch (Exception e) {
			e.printStackTrace();
			orders = new ArrayList<>();
		}

		for (int i = 0; i < orders.size(); i++) {
			Order order = orders.get(i);

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
				orderItems = new ArrayList<>();
			}

			order.setOrderItems(orderItems);

			order.setTotalPrice(orderService.calculateTotalPrice(orderItems, order.getDeliveryPrice()));
		}

		request.setAttribute("totalPages", totalPages);
		request.setAttribute("page", page);
		request.setAttribute("orders", orders);

		request.getRequestDispatcher("/WEB-INF/views/orderManagerView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String idParam = request.getParameter("id");
		String action = request.getParameter("action");
		String pageParam = request.getParameter("page");

		if (pageParam == null || pageParam.isEmpty()) {
			pageParam = "1";
		}

		try {
			int orderId = Integer.parseInt(idParam);

			switch (action) {
			case "CONFIRM":
				orderService.confirm(orderId);
				request.getSession().setAttribute("successMessage", "Đã xác nhận đơn hàng");
				break;
			case "CANCEL":
				orderService.cancel(orderId);
				request.getSession().setAttribute("successMessage", "Đã hủy đơn hàng");
				break;
			case "RESET":
				orderService.reset(orderId);
				request.getSession().setAttribute("successMessage", "Đã reset trạng thái đơn hàng");
				break;
			}

		} catch (Exception e) {
			request.getSession().setAttribute("errorMessage", "Cập nhật đơn hàng thất bại!");
		}

		response.sendRedirect(request.getContextPath() + "/admin/orderManager?page=" + pageParam);
	}

}
