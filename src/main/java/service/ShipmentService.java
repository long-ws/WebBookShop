package service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import beans.Shipment;
import beans.ShipmentTracking;
import beans.ShippingContact;
import dao.OrderDAO;
import dao.ShipmentDAO;
import dao.ShipmentTrackingDAO;
import dao.ShippingContactDAO;
import utils.ShippingStatus;

public class ShipmentService {

	private final ShipmentDAO shipmentDAO;
	private final ShipmentTrackingDAO trackingDAO;
	private final ShippingContactDAO contactDAO;
	private final OrderDAO orderDAO;

	public ShipmentService() {
		this.shipmentDAO = new ShipmentDAO();
		this.trackingDAO = new ShipmentTrackingDAO();
		this.contactDAO = new ShippingContactDAO();
		this.orderDAO = new OrderDAO();
	}

	public long insert(Shipment shipment) throws SQLException {
		return shipmentDAO.insert(shipment);
	}

	public void update(Shipment shipment) throws SQLException {
		shipmentDAO.update(shipment);
	}

	public void delete(long id) throws SQLException {
		shipmentDAO.delete(id);
	}

	public Shipment getById(long id) {
		return shipmentDAO.getById(id);
	}

	public Shipment getByIdWithDetails(long id) {
		Shipment shipment = shipmentDAO.getById(id);
		if (shipment != null && shipment.getShippingMethodId() > 0) {
			beans.ShippingMethod existingMethod = new dao.ShippingMethodDAO().getById(shipment.getShippingMethodId());
			if (existingMethod != null) {
				shipment.setShippingMethod(existingMethod);
			}
		}
		return shipment;
	}

	public Shipment getByOrderId(long orderId) {
		return shipmentDAO.getByOrderId(orderId);
	}

	public Shipment getByOrderIdWithDetails(long orderId) {
		Shipment shipment = shipmentDAO.getByOrderId(orderId);
		if (shipment != null && shipment.getShippingMethodId() > 0) {
			beans.ShippingMethod existingMethod = new dao.ShippingMethodDAO().getById(shipment.getShippingMethodId());
			if (existingMethod != null) {
				shipment.setShippingMethod(existingMethod);
			}
		}
		return shipment;
	}

	public Shipment getByTrackingCode(String trackingCode) {
		return shipmentDAO.getByTrackingCode(trackingCode);
	}

	public Shipment getByTrackingCodeWithDetails(String trackingCode) {
		Shipment shipment = shipmentDAO.getByTrackingCode(trackingCode);
		if (shipment != null && shipment.getShippingMethodId() > 0) {
			beans.ShippingMethod existingMethod = new dao.ShippingMethodDAO().getById(shipment.getShippingMethodId());
			if (existingMethod != null) {
				shipment.setShippingMethod(existingMethod);
			}
		}
		return shipment;
	}

	public List<Shipment> getAll() {
		return shipmentDAO.getAll();
	}

	public List<Shipment> getPart(int limit, int offset) {
		return shipmentDAO.getPart(limit, offset);
	}

	public List<Shipment> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		return shipmentDAO.getOrderedPart(limit, offset, orderBy, orderDir);
	}

	public List<Shipment> getByStatus(String status) {
		return shipmentDAO.getByStatus(status);
	}

	public int count() {
		return shipmentDAO.count();
	}

	public int countByStatus(String status) {
		return shipmentDAO.countByStatus(status);
	}

	public boolean updateStatus(long id, String status) throws SQLException {
		boolean updated = shipmentDAO.updateStatus(id, status);

		if (updated) {
			syncOrderStatus(id, status);
		}

		return updated;
	}

	private void syncOrderStatus(long shipmentId, String shipmentStatus) throws SQLException {
		Shipment shipment = shipmentDAO.getById(shipmentId);
		if (shipment == null) return;

		int orderStatus = mapShipmentStatusToOrderStatus(shipmentStatus);
		if (orderStatus > 0) {
			orderDAO.updateStatus(shipment.getOrderId(), orderStatus);
		}
	}

	private int mapShipmentStatusToOrderStatus(String shipmentStatus) {
		if (shipmentStatus == null) return 0;

		switch (shipmentStatus) {
			case ShippingStatus.WAITING_PICKUP:
				return 1; // PENDING - Đã đặt hàng
			case ShippingStatus.PICKED_UP:
				return 3; // PICKED_UP - Đã lấy hàng
			case ShippingStatus.IN_TRANSIT:
			case ShippingStatus.SHIPPING:
				return 4; // SHIPPING - Đang vận chuyển
			case ShippingStatus.OUT_FOR_DELIVERY:
				return 5; // DELIVERING - Đang giao hàng
			case ShippingStatus.DELIVERED:
				return 6; // DELIVERED - Đã giao thành công
			case ShippingStatus.FAILED:
			case ShippingStatus.RETURNED:
			case ShippingStatus.CANCELLED:
				return 7; // CANCELLED - Đã hủy
			default:
				return 0;
		}
	}

	public boolean createShipmentWithTracking(Shipment shipment) {
		try {
			long shipmentId = shipmentDAO.insert(shipment);
			shipment.setId(shipmentId);

			if (shipment.getShippingStatus() != null) {
				shipmentDAO.updateStatus(shipmentId, shipment.getShippingStatus());
				syncOrderStatus(shipmentId, shipment.getShippingStatus());
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean addTrackingEvent(long shipmentId, String status, String note, String location, String updatedBy) {
		try {
			ShipmentTracking tracking = new ShipmentTracking();
			tracking.setShipmentId(shipmentId);
			tracking.setStatus(status);
			tracking.setNote(note);
			tracking.setLocation(location);
			tracking.setUpdatedBy(updatedBy);
			tracking.setUpdatedAt(LocalDateTime.now());
			trackingDAO.insert(tracking);

			shipmentDAO.updateStatus(shipmentId, status);

			syncOrderStatus(shipmentId, status);

			updateTimestamps(shipmentId, status);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void updateTimestamps(long shipmentId, String status) {
		Shipment shipment = shipmentDAO.getById(shipmentId);
		if (shipment == null) return;

		shipment.setUpdatedAt(LocalDateTime.now());

		switch (status) {
			case ShippingStatus.PICKED_UP:
				shipment.setShippedAt(LocalDateTime.now());
				break;
			case ShippingStatus.DELIVERED:
				shipment.setDeliveredAt(LocalDateTime.now());
				break;
		}

		try {
			shipmentDAO.update(shipment);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<ShipmentTracking> getTrackingHistory(long shipmentId) {
		return trackingDAO.getByShipmentId(shipmentId);
	}

	public List<ShippingContact> getContacts(long shipmentId) {
		return contactDAO.getByShipmentId(shipmentId);
	}
}
