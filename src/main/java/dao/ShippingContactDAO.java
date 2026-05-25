package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.ShippingContact;
import utils.DBConnection;

public class ShippingContactDAO {

	public long insert(ShippingContact contact) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			return insert(conn, contact);
		}
	}

	public long insert(Connection conn, ShippingContact contact) throws SQLException {
		String sql = "INSERT INTO shipment_contacts (shipment_id, contact_type, contact_role, "
				+ "contact_name, contact_phone, created_at) VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, contact.getShipmentId());
			ps.setString(2, contact.getContactType());
			ps.setString(3, contact.getContactRole());
			ps.setString(4, contact.getContactName());
			ps.setString(5, contact.getContactPhone());
			ps.setTimestamp(6, contact.getCreatedAt() != null
					? Timestamp.valueOf(contact.getCreatedAt()) : null);

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Insert shipping contact failed, no rows affected");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
				throw new SQLException("Insert shipping contact failed, no ID obtained");
			}
		}
	}

	public void update(ShippingContact contact) throws SQLException {
		String sql = "UPDATE shipment_contacts SET shipment_id=?, contact_type=?, contact_role=?, "
				+ "contact_name=?, contact_phone=? WHERE id=?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, contact.getShipmentId());
			ps.setString(2, contact.getContactType());
			ps.setString(3, contact.getContactRole());
			ps.setString(4, contact.getContactName());
			ps.setString(5, contact.getContactPhone());
			ps.setLong(6, contact.getId());
			if (ps.executeUpdate() == 0)
				throw new SQLException("Update shipping contact failed, not found");
		}
	}

	public void delete(long id) throws SQLException {
		String sql = "DELETE FROM shipment_contacts WHERE id=?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			if (ps.executeUpdate() == 0)
				throw new SQLException("Delete shipping contact failed, not found");
		}
	}

	public ShippingContact getById(long id) {
		String sql = "SELECT * FROM shipment_contacts WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToContact(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ShippingContact> getByShipmentId(long shipmentId) {
		List<ShippingContact> list = new ArrayList<>();
		String sql = "SELECT * FROM shipment_contacts WHERE shipment_id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, shipmentId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToContact(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ShippingContact> getByContactType(String contactType) {
		List<ShippingContact> list = new ArrayList<>();
		String sql = "SELECT * FROM shipment_contacts WHERE contact_type = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, contactType);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToContact(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ShippingContact> getAll() {
		List<ShippingContact> list = new ArrayList<>();
		String sql = "SELECT * FROM shipment_contacts";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToContact(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int count() {
		String sql = "SELECT COUNT(id) FROM shipment_contacts";
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

	public boolean deleteByShipmentId(long shipmentId) throws SQLException {
		String sql = "DELETE FROM shipment_contacts WHERE shipment_id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, shipmentId);
			return ps.executeUpdate() >= 0;
		}
	}

	private ShippingContact mapResultSetToContact(ResultSet rs) throws SQLException {
		ShippingContact contact = new ShippingContact();
		contact.setId(rs.getLong("id"));
		contact.setShipmentId(rs.getLong("shipment_id"));
		contact.setContactType(rs.getString("contact_type"));
		contact.setContactRole(rs.getString("contact_role"));
		contact.setContactName(rs.getString("contact_name"));
		contact.setContactPhone(rs.getString("contact_phone"));
		Timestamp createdAt = rs.getTimestamp("created_at");
		if (createdAt != null)
			contact.setCreatedAt(createdAt.toLocalDateTime());
		return contact;
	}
}
