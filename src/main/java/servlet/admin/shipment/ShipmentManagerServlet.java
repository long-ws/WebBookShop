package servlet.admin.shipment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.Order;
import beans.Shipment;
import beans.ShippingMethod;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderService;
import service.ShipmentService;
import service.ShippingMethodService;
import utils.ShippingStatus;

@WebServlet(name = "ShipmentManagerServlet", value = "/admin/shipmentManager")
public class ShipmentManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ShipmentService shipmentService = new ShipmentService();
	private final ShippingMethodService methodService = new ShippingMethodService();
	private final OrderService orderService = new OrderService();

	private static final int SHIPMENTS_PER_PAGE = 10;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("========== [ShipmentManager] START ==========");

		String statusFilter = request.getParameter("status");
		if (statusFilter == null || statusFilter.isEmpty()) {
			statusFilter = "all";
		}

		int totalShipments;
		try {
			if ("all".equals(statusFilter)) {
				totalShipments = shipmentService.count();
			} else {
				totalShipments = shipmentService.countByStatus(statusFilter);
			}
		} catch (Exception e) {
			e.printStackTrace();
			totalShipments = 0;
		}

		System.out.println("[ShipmentManager] Total shipments: " + totalShipments);
		System.out.println("[ShipmentManager] Status filter: " + statusFilter);

		int totalPages = totalShipments / SHIPMENTS_PER_PAGE;
		if (totalShipments % SHIPMENTS_PER_PAGE != 0) {
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
			} catch (NumberFormatException ignored) {
			}
		}

		if (page < 1) {
			page = 1;
		}
		if (page > totalPages) {
			page = totalPages;
		}

		int offset = (page - 1) * SHIPMENTS_PER_PAGE;

		List<Shipment> shipments;
		try {
			if ("all".equals(statusFilter)) {
				shipments = shipmentService.getOrderedPart(SHIPMENTS_PER_PAGE, offset, "id", "DESC");
			} else {
				shipments = getFilteredShipments(statusFilter, SHIPMENTS_PER_PAGE, offset);
			}
		} catch (Exception e) {
			e.printStackTrace();
			shipments = new ArrayList<>();
		}

		for (Shipment shipment : shipments) {
			try {
				Order order = orderService.getById(shipment.getOrderId());
				if (order != null) {
					request.setAttribute("order_" + shipment.getId(), order);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				ShippingMethod method = methodService.getById(shipment.getShippingMethodId());
				if (method != null) {
					request.setAttribute("method_" + shipment.getId(), method);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int waitingCount = shipmentService.countByStatus(ShippingStatus.WAITING_PICKUP);
		int transitCount = shipmentService.countByStatus(ShippingStatus.IN_TRANSIT)
				+ shipmentService.countByStatus(ShippingStatus.OUT_FOR_DELIVERY);
		int deliveredCount = shipmentService.countByStatus(ShippingStatus.DELIVERED);
		int failedCount = shipmentService.countByStatus(ShippingStatus.FAILED)
				+ shipmentService.countByStatus(ShippingStatus.RETURNED);

		request.setAttribute("shipments", shipments);
		request.setAttribute("statusFilter", statusFilter);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("page", page);
		request.setAttribute("totalCount", totalShipments);
		request.setAttribute("waitingCount", waitingCount);
		request.setAttribute("transitCount", transitCount);
		request.setAttribute("deliveredCount", deliveredCount);
		request.setAttribute("failedCount", failedCount);

		System.out.println("[ShipmentManager] Shipments list size: " + shipments.size());
		System.out.println("[ShipmentManager] Forwarding to shipmentManagerView.jsp");
		System.out.println("========== [ShipmentManager] END ==========");

		request.getRequestDispatcher("/WEB-INF/views/admin/shipmentManagerView.jsp").forward(request, response);
	}

	private List<Shipment> getFilteredShipments(String status, int limit, int offset) {
		List<Shipment> result = new ArrayList<>();
		List<Shipment> all = shipmentService.getByStatus(status);
		int end = Math.min(offset + limit, all.size());
		if (offset < all.size()) {
			for (int i = offset; i < end; i++) {
				result.add(all.get(i));
			}
		}
		return result;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String idParam = request.getParameter("id");
		String pageParam = request.getParameter("page");
		String statusFilter = request.getParameter("statusFilter");

		if (pageParam == null || pageParam.isEmpty()) {
			pageParam = "1";
		}
		if (statusFilter == null || statusFilter.isEmpty()) {
			statusFilter = "all";
		}

		String redirectUrl = request.getContextPath() + "/admin/shipmentManager?page=" + pageParam
				+ "&status=" + statusFilter;

		try {
			if (idParam == null || idParam.isEmpty()) {
				request.getSession().setAttribute("errorMessage", "ID vận chuyển không hợp lệ!");
				response.sendRedirect(redirectUrl);
				return;
			}

			long shipmentId = Long.parseLong(idParam);

			switch (action) {
			case "updateStatus":
				String newStatus = request.getParameter("newStatus");
				if (newStatus != null && !newStatus.isEmpty()) {
					boolean updated = shipmentService.updateStatus(shipmentId, newStatus);
					if (updated) {
						request.getSession().setAttribute("successMessage", "Cập nhật trạng thái thành công!");
					} else {
						request.getSession().setAttribute("errorMessage", "Cập nhật trạng thái thất bại!");
					}
				}
				break;

			case "addShipper":
				String shipperContact = request.getParameter("shipperContact");
				Shipment shipment = shipmentService.getById(shipmentId);
				if (shipment != null) {
					shipment.setShipperContact(shipperContact);
					shipmentService.update(shipment);
					request.getSession().setAttribute("successMessage", "Đã cập nhật thông tin shipper!");
				}
				break;

			case "addTracking":
				String trackingStatus = request.getParameter("trackingStatus");
				String trackingNote = request.getParameter("trackingNote");
				String trackingLocation = request.getParameter("trackingLocation");
				String updatedBy = request.getParameter("updatedBy");
				if (updatedBy == null || updatedBy.isEmpty()) {
					updatedBy = "Admin";
				}

				boolean added = shipmentService.addTrackingEvent(shipmentId, trackingStatus,
						trackingNote, trackingLocation, updatedBy);
				if (added) {
					request.getSession().setAttribute("successMessage", "Đã thêm cập nhật theo dõi!");
				} else {
					request.getSession().setAttribute("errorMessage", "Thêm cập nhật thất bại!");
				}
				break;

			case "deleteShipment":
				shipmentService.delete(shipmentId);
				request.getSession().setAttribute("successMessage", "Đã xóa vận chuyển!");
				break;

			default:
				request.getSession().setAttribute("errorMessage", "Hành động không hợp lệ!");
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("errorMessage", "Xử lý thất bại: " + e.getMessage());
		}

		response.sendRedirect(redirectUrl);
	}
}
