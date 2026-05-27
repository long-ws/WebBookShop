package servlet.client;

import java.io.IOException;
import java.util.List;

import beans.Order;
import beans.Shipment;
import beans.ShipmentTracking;
import beans.User;
import constants.SessionConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.OrderService;
import service.ShipmentService;

@WebServlet(name = "TrackingServlet", value = "/tracking")
public class TrackingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final OrderService orderService = new OrderService();
	private final ShipmentService shipmentService = new ShipmentService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(SessionConstants.CURRENT_USER);

		String orderIdParam = request.getParameter("order");
		String trackingCode = request.getParameter("code");

		if (orderIdParam == null && trackingCode == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		Order order = null;
		Shipment shipment = null;
		List<ShipmentTracking> trackingHistory = null;

		try {
			if (orderIdParam != null) {
				long orderId = Long.parseLong(orderIdParam);
				order = orderService.getById(orderId);

				if (order != null) {
					if (user == null || order.getUserId() != user.getId()) {
						if (user == null) {
							response.sendRedirect(request.getContextPath() + "/signin");
							return;
						}
						response.sendRedirect(request.getContextPath() + "/order");
						return;
					}

					shipment = shipmentService.getByOrderIdWithDetails(orderId);
				}
			} else if (trackingCode != null) {
				shipment = shipmentService.getByTrackingCodeWithDetails(trackingCode);
				if (shipment != null) {
					order = orderService.getById(shipment.getOrderId());
				}
			}

			if (shipment != null) {
				trackingHistory = shipmentService.getTrackingHistory(shipment.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (order == null) {
			request.setAttribute("error", "Không tìm thấy đơn hàng");
		}

		request.setAttribute("order", order);
		request.setAttribute("shipment", shipment);
		request.setAttribute("trackingHistory", trackingHistory);
		request.getRequestDispatcher("/WEB-INF/views/trackingView.jsp").forward(request, response);
	}
}
