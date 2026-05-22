package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.Shipment;
import beans.TopShippingMethod;
import utils.DBConnection;

public class ShipmentDAO implements DAO<Shipment> {

	@Override
	public long insert(Shipment shipment) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			return insert(conn, shipment);
		}
	}

	public long insert(Connection conn, Shipment shipment) throws SQLException {
		String sql = "INSERT INTO shipments (order_id, shipping_method_id, tracking_code, receiver_name, "
				+ "receiver_phone, province, district, ward, address_detail, total_weight, total_volume, "
				+ "shipping_fee, shipping_status, seller_note, customer_note, shipper_contact, "
				+ "estimated_delivery_date, shipped_at, delivered_at, created_at, updated_at, "
				+ "provider_type, provider_order_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, shipment.getOrderId());
			ps.setLong(2, shipment.getShippingMethodId());
			ps.setString(3, shipment.getTrackingCode());
			ps.setString(4, shipment.getReceiverName() != null ? shipment.getReceiverName() : "");
			ps.setString(5, shipment.getReceiverPhone() != null ? shipment.getReceiverPhone() : "");
			ps.setString(6, shipment.getProvince() != null ? shipment.getProvince() : "");
			ps.setString(7, shipment.getDistrict() != null ? shipment.getDistrict() : "");
			ps.setString(8, shipment.getWard() != null ? shipment.getWard() : "");
			ps.setString(9, shipment.getAddressDetail() != null ? shipment.getAddressDetail() : "");
			ps.setDouble(10, shipment.getTotalWeight());
			ps.setDouble(11, shipment.getTotalVolume());
			ps.setDouble(12, shipment.getShippingFee());
			ps.setString(13, shipment.getShippingStatus() != null ? shipment.getShippingStatus() : "");
			ps.setString(14, shipment.getSellerNote() != null ? shipment.getSellerNote() : "");
			ps.setString(15, shipment.getCustomerNote() != null ? shipment.getCustomerNote() : "");
			ps.setString(16, shipment.getShipperContact() != null ? shipment.getShipperContact() : "");
			ps.setTimestamp(17, shipment.getEstimatedDeliveryDate() != null
					? Timestamp.valueOf(shipment.getEstimatedDeliveryDate()) : null);
			ps.setTimestamp(18, shipment.getShippedAt() != null
					? Timestamp.valueOf(shipment.getShippedAt()) : null);
			ps.setTimestamp(19, shipment.getDeliveredAt() != null
					? Timestamp.valueOf(shipment.getDeliveredAt()) : null);
			ps.setTimestamp(20, shipment.getCreatedAt() != null
					? Timestamp.valueOf(shipment.getCreatedAt()) : null);
			ps.setTimestamp(21, shipment.getUpdatedAt() != null
					? Timestamp.valueOf(shipment.getUpdatedAt()) : null);
			ps.setString(22, shipment.getProviderType() != null ? shipment.getProviderType() : "");
			ps.setString(23, shipment.getProviderOrderCode() != null ? shipment.getProviderOrderCode() : "");

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Insert shipment failed, no rows affected");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
				throw new SQLException("Insert shipment failed, no ID obtained");
			}
		}
	}

	@Override
	public void update(Shipment shipment) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			update(conn, shipment);
		}
	}

	public void update(Connection conn, Shipment shipment) throws SQLException {
		String sql = "UPDATE shipments SET order_id=?, shipping_method_id=?, tracking_code=?, "
				+ "receiver_name=?, receiver_phone=?, province=?, district=?, ward=?, "
				+ "address_detail=?, total_weight=?, total_volume=?, shipping_fee=?, "
				+ "shipping_status=?, seller_note=?, customer_note=?, shipper_contact=?, "
				+ "estimated_delivery_date=?, shipped_at=?, delivered_at=?, updated_at=?, "
				+ "provider_type=?, provider_order_code=? WHERE id=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, shipment.getOrderId());
			ps.setLong(2, shipment.getShippingMethodId());
			ps.setString(3, shipment.getTrackingCode());
			ps.setString(4, shipment.getReceiverName() != null ? shipment.getReceiverName() : "");
			ps.setString(5, shipment.getReceiverPhone() != null ? shipment.getReceiverPhone() : "");
			ps.setString(6, shipment.getProvince() != null ? shipment.getProvince() : "");
			ps.setString(7, shipment.getDistrict() != null ? shipment.getDistrict() : "");
			ps.setString(8, shipment.getWard() != null ? shipment.getWard() : "");
			ps.setString(9, shipment.getAddressDetail() != null ? shipment.getAddressDetail() : "");
			ps.setDouble(10, shipment.getTotalWeight());
			ps.setDouble(11, shipment.getTotalVolume());
			ps.setDouble(12, shipment.getShippingFee());
			ps.setString(13, shipment.getShippingStatus() != null ? shipment.getShippingStatus() : "");
			ps.setString(14, shipment.getSellerNote() != null ? shipment.getSellerNote() : "");
			ps.setString(15, shipment.getCustomerNote() != null ? shipment.getCustomerNote() : "");
			ps.setString(16, shipment.getShipperContact() != null ? shipment.getShipperContact() : "");
			ps.setTimestamp(17, shipment.getEstimatedDeliveryDate() != null
					? Timestamp.valueOf(shipment.getEstimatedDeliveryDate()) : null);
			ps.setTimestamp(18, shipment.getShippedAt() != null
					? Timestamp.valueOf(shipment.getShippedAt()) : null);
			ps.setTimestamp(19, shipment.getDeliveredAt() != null
					? Timestamp.valueOf(shipment.getDeliveredAt()) : null);
			ps.setTimestamp(20, shipment.getUpdatedAt() != null
					? Timestamp.valueOf(shipment.getUpdatedAt()) : null);
			ps.setString(21, shipment.getProviderType() != null ? shipment.getProviderType() : "");
			ps.setString(22, shipment.getProviderOrderCode() != null ? shipment.getProviderOrderCode() : "");
			ps.setLong(23, shipment.getId());

			if (ps.executeUpdate() == 0)
				throw new SQLException("Update shipment failed, not found");
		}
	}

	@Override
	public void delete(long id) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			delete(conn, id);
		}
	}

	public void delete(Connection conn, long id) throws SQLException {
		String sql = "DELETE FROM shipments WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			if (ps.executeUpdate() == 0)
				throw new SQLException("Delete shipment failed, not found");
		}
	}

	@Override
	public Shipment getById(long id) {
		String sql = "SELECT * FROM shipments WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToShipment(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Shipment getByOrderId(long orderId) {
		String sql = "SELECT * FROM shipments WHERE order_id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, orderId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToShipment(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Shipment getByTrackingCode(String trackingCode) {
		String sql = "SELECT * FROM shipments WHERE tracking_code = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, trackingCode);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToShipment(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Shipment> getAll() {
		List<Shipment> list = new ArrayList<>();
		String sql = "SELECT * FROM shipments";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToShipment(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Shipment> getPart(int limit, int offset) {
		List<Shipment> list = new ArrayList<>();
		String sql = "SELECT * FROM shipments LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToShipment(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Shipment> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<Shipment> list = new ArrayList<>();
		String sql = "SELECT * FROM shipments ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToShipment(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Shipment> getByStatus(String status) {
		List<Shipment> list = new ArrayList<>();
		String sql = "SELECT * FROM shipments WHERE shipping_status = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, status);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToShipment(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int count() {
		String sql = "SELECT COUNT(id) FROM shipments";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int countByStatus(String status) {
		String sql = "SELECT COUNT(id) FROM shipments WHERE shipping_status = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, status);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int countTotal() {
		String sql = "SELECT COUNT(id) FROM shipments";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public double sumShippingFee() {
		String sql = "SELECT COALESCE(SUM(shipping_fee), 0) FROM shipments";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getDouble(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public int countByShippingMethod(long methodId) {
		String sql = "SELECT COUNT(id) FROM shipments WHERE shipping_method_id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, methodId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public double sumShippingFeeByMethod(long methodId) {
		String sql = "SELECT COALESCE(SUM(shipping_fee), 0) FROM shipments WHERE shipping_method_id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, methodId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public String getTopProvince() {
		String sql = "SELECT province, COUNT(id) as cnt FROM shipments WHERE province IS NOT NULL AND province != '' GROUP BY province ORDER BY cnt DESC LIMIT 1";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getString("province");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getTopRegion() {
		String sql = "SELECT "
				+ "CASE "
				+ "  WHEN LOWER(province) LIKE '%hồ chí minh%' OR LOWER(province) LIKE '%hà nội%' OR LOWER(province) LIKE '%đà nẵng%' THEN 'Miền Lớn' "
				+ "  WHEN LOWER(province) LIKE '%bắc%' OR LOWER(province) LIKE '%thái%' OR LOWER(province) LIKE '%nam%' OR LOWER(province) LIKE '%điện%' OR LOWER(province) LIKE '%hà%' THEN 'Miền Bắc' "
				+ "  WHEN LOWER(province) LIKE '%nghệ%' OR LOWER(province) LIKE '%thanh%' OR LOWER(province) LIKE '%đà nẵng%' OR LOWER(province) LIKE '%huế%' THEN 'Miền Trung' "
				+ "  ELSE 'Miền Nam' END as region, COUNT(id) as cnt "
				+ "FROM shipments "
				+ "WHERE province IS NOT NULL AND province != '' "
				+ "GROUP BY region ORDER BY cnt DESC LIMIT 1";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getString("region");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public double getDeliverySuccessRate() {
		int total = countTotal();
		if (total == 0) return 0.0;
		int delivered = countByStatus("delivered");
		return (delivered * 100.0) / total;
	}

	public double getReturnRate() {
		int total = countTotal();
		if (total == 0) return 0.0;
		int returned = countByStatus("returned");
		return (returned * 100.0) / total;
	}

	public List<Shipment> getRecentShipments(int limit) {
		List<Shipment> list = new ArrayList<>();
		String sql = "SELECT * FROM shipments ORDER BY created_at DESC LIMIT ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToShipment(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public double getAvgDeliveryDays() {
		String sql = "SELECT AVG(TIMESTAMPDIFF(DAY, shipped_at, delivered_at)) FROM shipments WHERE shipped_at IS NOT NULL AND delivered_at IS NOT NULL";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getDouble(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public List<TopShippingMethod> getTopShippingMethods(int limit) {
		List<TopShippingMethod> list = new ArrayList<>();
		String sql = "SELECT "
				+ "  sm.name as method_name, sm.provider_type, sm.status, "
				+ "  COUNT(s.id) as total_orders, "
				+ "  COALESCE(SUM(s.shipping_fee), 0) as total_revenue "
				+ "FROM shipments s "
				+ "JOIN shipping_methods sm ON s.shipping_method_id = sm.id "
				+ "GROUP BY sm.id, sm.name, sm.provider_type, sm.status "
				+ "ORDER BY total_orders DESC "
				+ "LIMIT ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			int totalOrders = countTotal();
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					TopShippingMethod tsm = new TopShippingMethod();
					tsm.setMethodName(rs.getString("method_name"));
					tsm.setProviderType(rs.getString("provider_type"));
					tsm.setStatus(rs.getInt("status"));
					tsm.setTotalOrders(rs.getInt("total_orders"));
					tsm.setTotalRevenue(rs.getDouble("total_revenue"));
					if (totalOrders > 0) {
						tsm.setUsagePercent((tsm.getTotalOrders() * 100.0) / totalOrders);
					} else {
						tsm.setUsagePercent(0);
					}
					list.add(tsm);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countTotalSince(int daysAgo) {
		String sql = "SELECT COUNT(id) FROM shipments WHERE created_at >= DATE_SUB(NOW(), INTERVAL " + daysAgo + " DAY)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public double sumShippingFeeSince(int daysAgo) {
		String sql = "SELECT COALESCE(SUM(shipping_fee), 0) FROM shipments WHERE created_at >= DATE_SUB(NOW(), INTERVAL " + daysAgo + " DAY)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getDouble(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public int countByStatusSince(String status, int daysAgo) {
		String dbStatus = mapStatStatus(status);
		String sql = "SELECT COUNT(id) FROM shipments WHERE shipping_status = ? AND created_at >= DATE_SUB(NOW(), INTERVAL " + daysAgo + " DAY)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, dbStatus);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int countByStatusesSince(String[] statuses, int daysAgo) {
		if (statuses == null || statuses.length == 0) return 0;
		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < statuses.length; i++) {
			placeholders.append(i > 0 ? ",?" : "?");
		}
		String sql = "SELECT COUNT(id) FROM shipments WHERE shipping_status IN (" + placeholders + ") AND created_at >= DATE_SUB(NOW(), INTERVAL " + daysAgo + " DAY)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (int i = 0; i < statuses.length; i++) {
				ps.setString(i + 1, mapStatStatus(statuses[i]));
			}
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String mapStatStatus(String status) {
		if (status == null) return null;
		switch (status.toLowerCase()) {
			case "pending":    return "WAITING_PICKUP";
			case "picked_up":  return "PICKED_UP";
			case "shipping":   return "IN_TRANSIT";
			case "delivering": return "OUT_FOR_DELIVERY";
			case "delivered": return "DELIVERED";
			case "failed":    return "FAILED";
			case "returned":  return "RETURNED";
			case "cancelled": return "CANCELLED";
			case "express":   return null;
			default:          return status;
		}
	}

	public String getTopProvinceSince(int daysAgo) {
		String sql = "SELECT province, COUNT(id) as cnt FROM shipments "
				+ "WHERE province IS NOT NULL AND province != '' "
				+ "AND created_at >= DATE_SUB(NOW(), INTERVAL " + daysAgo + " DAY) "
				+ "GROUP BY province ORDER BY cnt DESC LIMIT 1";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getString("province");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Shipment> getRecentShipmentsSince(int limit, int daysAgo) {
		List<Shipment> list = new ArrayList<>();
		String sql = "SELECT * FROM shipments WHERE created_at >= DATE_SUB(NOW(), INTERVAL " + daysAgo + " DAY) ORDER BY created_at DESC LIMIT ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToShipment(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<TopShippingMethod> getTopShippingMethodsSince(int limit, int daysAgo) {
		List<TopShippingMethod> list = new ArrayList<>();
		String sql = "SELECT "
				+ "  sm.name as method_name, sm.provider_type, sm.status, "
				+ "  COUNT(s.id) as total_orders, "
				+ "  COALESCE(SUM(s.shipping_fee), 0) as total_revenue "
				+ "FROM shipments s "
				+ "JOIN shipping_methods sm ON s.shipping_method_id = sm.id "
				+ "WHERE s.created_at >= DATE_SUB(NOW(), INTERVAL " + daysAgo + " DAY) "
				+ "GROUP BY sm.id, sm.name, sm.provider_type, sm.status "
				+ "ORDER BY total_orders DESC "
				+ "LIMIT ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			int totalOrders = countTotalSince(daysAgo);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					TopShippingMethod tsm = new TopShippingMethod();
					tsm.setMethodName(rs.getString("method_name"));
					tsm.setProviderType(rs.getString("provider_type"));
					tsm.setStatus(rs.getInt("status"));
					tsm.setTotalOrders(rs.getInt("total_orders"));
					tsm.setTotalRevenue(rs.getDouble("total_revenue"));
					if (totalOrders > 0) {
						tsm.setUsagePercent((tsm.getTotalOrders() * 100.0) / totalOrders);
					} else {
						tsm.setUsagePercent(0);
					}
					list.add(tsm);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean updateStatus(long id, String status) {
		String sql = "UPDATE shipments SET shipping_status = ?, updated_at = NOW() WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setLong(2, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Shipment mapResultSetToShipment(ResultSet rs) throws SQLException {
		Shipment shipment = new Shipment();
		shipment.setId(rs.getLong("id"));
		shipment.setOrderId(rs.getLong("order_id"));
		shipment.setShippingMethodId(rs.getLong("shipping_method_id"));
		shipment.setTrackingCode(rs.getString("tracking_code"));
		shipment.setReceiverName(rs.getString("receiver_name"));
		shipment.setReceiverPhone(rs.getString("receiver_phone"));
		shipment.setProvince(rs.getString("province"));
		shipment.setDistrict(rs.getString("district"));
		shipment.setWard(rs.getString("ward"));
		shipment.setAddressDetail(rs.getString("address_detail"));
		shipment.setTotalWeight(rs.getDouble("total_weight"));
		shipment.setTotalVolume(rs.getDouble("total_volume"));
		shipment.setShippingFee(rs.getDouble("shipping_fee"));
		shipment.setShippingStatus(rs.getString("shipping_status"));
		shipment.setSellerNote(rs.getString("seller_note"));
		shipment.setCustomerNote(rs.getString("customer_note"));
		shipment.setShipperContact(rs.getString("shipper_contact"));
		shipment.setShipperName(rs.getString("shipper_name"));
		shipment.setShipperPhone(rs.getString("shipper_phone"));
		shipment.setShipperAvatar(rs.getString("shipper_avatar"));
		Timestamp estimatedDeliveryDate = rs.getTimestamp("estimated_delivery_date");
		if (estimatedDeliveryDate != null)
			shipment.setEstimatedDeliveryDate(estimatedDeliveryDate.toLocalDateTime());
		Timestamp shippedAt = rs.getTimestamp("shipped_at");
		if (shippedAt != null)
			shipment.setShippedAt(shippedAt.toLocalDateTime());
		Timestamp deliveredAt = rs.getTimestamp("delivered_at");
		if (deliveredAt != null)
			shipment.setDeliveredAt(deliveredAt.toLocalDateTime());
		Timestamp createdAt = rs.getTimestamp("created_at");
		if (createdAt != null)
			shipment.setCreatedAt(createdAt.toLocalDateTime());
		Timestamp updatedAt = rs.getTimestamp("updated_at");
		if (updatedAt != null)
			shipment.setUpdatedAt(updatedAt.toLocalDateTime());
		shipment.setProviderType(rs.getString("provider_type"));
		shipment.setProviderOrderCode(rs.getString("provider_order_code"));
		return shipment;
	}
}
