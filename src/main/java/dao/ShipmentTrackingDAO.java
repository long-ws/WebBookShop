package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.ShipmentTracking;
import utils.DBConnection;

public class ShipmentTrackingDAO {

	public long insert(ShipmentTracking tracking) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			return insert(conn, tracking);
		}
	}

	public long insert(Connection conn, ShipmentTracking tracking) throws SQLException {
		String sql = "INSERT INTO shipment_tracking (shipment_id, status, note, location, updated_by, updated_at) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, tracking.getShipmentId());
			ps.setString(2, tracking.getStatus() != null ? tracking.getStatus() : "");
			ps.setString(3, tracking.getNote() != null ? tracking.getNote() : "");
			ps.setString(4, tracking.getLocation() != null ? tracking.getLocation() : "");
			ps.setString(5, tracking.getUpdatedBy() != null ? tracking.getUpdatedBy() : "");
			ps.setTimestamp(6, tracking.getUpdatedAt() != null
					? Timestamp.valueOf(tracking.getUpdatedAt()) : null);

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Insert shipment tracking failed, no rows affected");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
				throw new SQLException("Insert shipment tracking failed, no ID obtained");
			}
		}
	}

	public List<ShipmentTracking> getByShipmentId(long shipmentId) {
		List<ShipmentTracking> list = new ArrayList<>();
		String sql = "SELECT * FROM shipment_tracking WHERE shipment_id = ? ORDER BY updated_at DESC";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, shipmentId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToTracking(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ShipmentTracking getLatestByShipmentId(long shipmentId) {
		String sql = "SELECT * FROM shipment_tracking WHERE shipment_id = ? ORDER BY updated_at DESC LIMIT 1";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, shipmentId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToTracking(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ShipmentTracking getById(long id) {
		String sql = "SELECT * FROM shipment_tracking WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToTracking(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ShipmentTracking> getAll() {
		List<ShipmentTracking> list = new ArrayList<>();
		String sql = "SELECT * FROM shipment_tracking ORDER BY updated_at DESC";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToTracking(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countByShipmentId(long shipmentId) {
		String sql = "SELECT COUNT(id) FROM shipment_tracking WHERE shipment_id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, shipmentId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean deleteByShipmentId(long shipmentId) throws SQLException {
		String sql = "DELETE FROM shipment_tracking WHERE shipment_id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, shipmentId);
			return ps.executeUpdate() >= 0;
		}
	}

	private ShipmentTracking mapResultSetToTracking(ResultSet rs) throws SQLException {
		ShipmentTracking tracking = new ShipmentTracking();
		tracking.setId(rs.getLong("id"));
		tracking.setShipmentId(rs.getLong("shipment_id"));
		tracking.setStatus(rs.getString("status"));
		tracking.setNote(rs.getString("note"));
		tracking.setLocation(rs.getString("location"));
		tracking.setUpdatedBy(rs.getString("updated_by"));
		Timestamp updatedAt = rs.getTimestamp("updated_at");
		if (updatedAt != null)
			tracking.setUpdatedAt(updatedAt.toLocalDateTime());
		return tracking;
	}
}
