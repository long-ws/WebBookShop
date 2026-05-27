package servlet.admin.shipment;

import java.io.IOException;
import java.util.List;

import beans.Order;
import beans.OrderItem;
import beans.Shipment;
import beans.ShipmentTracking;
import beans.ShippingContact;
import beans.ShippingMethod;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderItemService;
import service.OrderService;
import service.ShipmentService;
import service.ShippingMethodService;

@WebServlet(name = "ShipmentManagerDetailServlet", value = "/admin/shipmentManager/detail")
public class ShipmentManagerDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ShipmentService shipmentService = new ShipmentService();
	private final ShippingMethodService methodService = new ShippingMethodService();
	private final OrderService orderService = new OrderService();
	private final OrderItemService orderItemService = new OrderItemService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String idParam = request.getParameter("id");

		if (idParam == null || idParam.isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/admin/shipmentManager");
			return;
		}

		try {
			long shipmentId = Long.parseLong(idParam);

			Shipment shipment = shipmentService.getById(shipmentId);
			if (shipment == null) {
				request.getSession().setAttribute("errorMessage", "Không tìm thấy vận chuyển!");
				response.sendRedirect(request.getContextPath() + "/admin/shipmentManager");
				return;
			}

			Order order = orderService.getById(shipment.getOrderId());
			List<OrderItem> orderItems = orderItemService.getByOrderIdWithProducts(shipment.getOrderId());
			ShippingMethod method = methodService.getById(shipment.getShippingMethodId());

			List<ShipmentTracking> timeline = shipmentService.getTrackingHistory(shipmentId);
			List<ShippingContact> contacts = shipmentService.getContacts(shipmentId);

			double subtotal = 0;
			if (orderItems != null) {
				for (OrderItem item : orderItems) {
					double itemPrice = item.getDiscount() > 0
						? item.getPrice() * (100 - item.getDiscount()) / 100
						: item.getPrice();
					subtotal += itemPrice * item.getQuantity();
				}
			}

			request.setAttribute("shipment", shipment);
			request.setAttribute("order", order);
			request.setAttribute("orderItems", orderItems);
			request.setAttribute("shippingMethod", method);
			request.setAttribute("timeline", timeline);
			request.setAttribute("contacts", contacts);
			request.setAttribute("subtotal", subtotal);

			request.getRequestDispatcher("/WEB-INF/views/admin/shipmentManagerDetailView.jsp").forward(request, response);

		} catch (NumberFormatException e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/admin/shipmentManager");
		}
	}
}
