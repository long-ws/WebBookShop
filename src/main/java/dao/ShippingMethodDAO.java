package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.ShippingMethod;
import utils.DBConnection;

public class ShippingMethodDAO implements DAO<ShippingMethod> {

	@Override
	public long insert(ShippingMethod method) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			return insert(conn, method);
		}
	}

	public long insert(Connection conn, ShippingMethod method) throws SQLException {
		String sql = "INSERT INTO shipping_methods (name, estimated_days, price_per_kg, support_phone, "
				+ "support_email, status, provider_type, api_key, api_secret, webhook_token, "
				+ "ghn_service_id, ghn_from_district_id, ghn_from_ward_code, ghn_shop_id, ghn_token, "
				+ "is_express, express_surcharge, min_weight_kg, max_weight_kg, free_shipping_threshold, "
				+ "created_at, updated_at) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, method.getName());
			ps.setInt(2, method.getEstimatedDays());
			ps.setDouble(3, method.getPricePerKg());
			ps.setString(4, method.getSupportPhone());
			ps.setString(5, method.getSupportEmail());
			ps.setInt(6, method.getStatus());
			ps.setString(7, method.getProviderType());
			ps.setString(8, method.getApiKey());
			ps.setString(9, method.getApiSecret());
			ps.setString(10, method.getWebhookToken());
			ps.setInt(11, method.getGhnServiceId());
			ps.setString(12, method.getGhnFromDistrictId());
			ps.setString(13, method.getGhnFromWardCode());
			ps.setString(14, method.getGhnShopId());
			ps.setString(15, method.getGhnToken());
			ps.setBoolean(16, method.isExpress());
			ps.setDouble(17, method.getExpressSurcharge());
			ps.setDouble(18, method.getMinWeightKg());
			ps.setDouble(19, method.getMaxWeightKg());
			ps.setDouble(20, method.getFreeShippingThreshold());
			ps.setTimestamp(21, method.getCreatedAt() != null
					? Timestamp.valueOf(method.getCreatedAt()) : null);
			ps.setTimestamp(22, method.getUpdatedAt() != null
					? Timestamp.valueOf(method.getUpdatedAt()) : null);

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Insert shipping method failed, no rows affected");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
				throw new SQLException("Insert shipping method failed, no ID obtained");
			}
		}
	}

	@Override
	public void update(ShippingMethod method) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			update(conn, method);
		}
	}

	public void update(Connection conn, ShippingMethod method) throws SQLException {
		String sql = "UPDATE shipping_methods SET name=?, estimated_days=?, price_per_kg=?, "
				+ "support_phone=?, support_email=?, status=?, provider_type=?, api_key=?, "
				+ "api_secret=?, webhook_token=?, ghn_service_id=?, ghn_from_district_id=?, "
				+ "ghn_from_ward_code=?, ghn_shop_id=?, ghn_token=?, "
				+ "is_express=?, express_surcharge=?, min_weight_kg=?, max_weight_kg=?, free_shipping_threshold=?, "
				+ "updated_at=? WHERE id=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, method.getName());
			ps.setInt(2, method.getEstimatedDays());
			ps.setDouble(3, method.getPricePerKg());
			ps.setString(4, method.getSupportPhone());
			ps.setString(5, method.getSupportEmail());
			ps.setInt(6, method.getStatus());
			ps.setString(7, method.getProviderType());
			ps.setString(8, method.getApiKey());
			ps.setString(9, method.getApiSecret());
			ps.setString(10, method.getWebhookToken());
			ps.setInt(11, method.getGhnServiceId());
			ps.setString(12, method.getGhnFromDistrictId());
			ps.setString(13, method.getGhnFromWardCode());
			ps.setString(14, method.getGhnShopId());
			ps.setString(15, method.getGhnToken());
			ps.setBoolean(16, method.isExpress());
			ps.setDouble(17, method.getExpressSurcharge());
			ps.setDouble(18, method.getMinWeightKg());
			ps.setDouble(19, method.getMaxWeightKg());
			ps.setDouble(20, method.getFreeShippingThreshold());
			ps.setTimestamp(21, method.getUpdatedAt() != null
					? Timestamp.valueOf(method.getUpdatedAt()) : null);
			ps.setLong(22, method.getId());

			if (ps.executeUpdate() == 0)
				throw new SQLException("Update shipping method failed, not found");
		}
	}

	@Override
	public void delete(long id) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			delete(conn, id);
		}
	}

	public void delete(Connection conn, long id) throws SQLException {
		String sql = "DELETE FROM shipping_methods WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			if (ps.executeUpdate() == 0)
				throw new SQLException("Delete shipping method failed, not found");
		}
	}

	@Override
	public ShippingMethod getById(long id) {
		String sql = "SELECT * FROM shipping_methods WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToShippingMethod(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ShippingMethod getByProviderType(String providerType) {
		String sql = "SELECT * FROM shipping_methods WHERE provider_type = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, providerType);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToShippingMethod(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ShippingMethod> getAll() {
		List<ShippingMethod> list = new ArrayList<>();
		String sql = "SELECT * FROM shipping_methods ORDER BY id DESC";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToShippingMethod(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ShippingMethod> getAllActive() {
		List<ShippingMethod> list = new ArrayList<>();
		String sql = "SELECT * FROM shipping_methods WHERE status = 1 ORDER BY id DESC";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToShippingMethod(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ShippingMethod> getPart(int limit, int offset) {
		List<ShippingMethod> list = new ArrayList<>();
		String sql = "SELECT * FROM shipping_methods ORDER BY id DESC LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToShippingMethod(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ShippingMethod> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<ShippingMethod> list = new ArrayList<>();
		String sql = "SELECT * FROM shipping_methods ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToShippingMethod(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int count() {
		String sql = "SELECT COUNT(id) FROM shipping_methods";
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

	public boolean updateStatus(long id, int status) {
		String sql = "UPDATE shipping_methods SET status = ?, updated_at = NOW() WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, status);
			ps.setLong(2, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private ShippingMethod mapResultSetToShippingMethod(ResultSet rs) throws SQLException {
		ShippingMethod method = new ShippingMethod();
		method.setId(rs.getLong("id"));
		method.setName(rs.getString("name"));
		method.setEstimatedDays(rs.getInt("estimated_days"));
		method.setPricePerKg(rs.getDouble("price_per_kg"));
		method.setSupportPhone(rs.getString("support_phone"));
		method.setSupportEmail(rs.getString("support_email"));
		method.setStatus(rs.getInt("status"));
		method.setProviderType(rs.getString("provider_type"));
		method.setApiKey(rs.getString("api_key"));
		method.setApiSecret(rs.getString("api_secret"));
		method.setWebhookToken(rs.getString("webhook_token"));
		method.setGhnServiceId(rs.getInt("ghn_service_id"));
		method.setGhnFromDistrictId(rs.getString("ghn_from_district_id"));
		method.setGhnFromWardCode(rs.getString("ghn_from_ward_code"));
		method.setGhnShopId(rs.getString("ghn_shop_id"));
		method.setGhnToken(rs.getString("ghn_token"));

		try {
			method.setExpress(rs.getBoolean("is_express"));
			method.setExpressSurcharge(rs.getDouble("express_surcharge"));
			method.setMinWeightKg(rs.getDouble("min_weight_kg"));
			method.setMaxWeightKg(rs.getDouble("max_weight_kg"));
			method.setFreeShippingThreshold(rs.getDouble("free_shipping_threshold"));
		} catch (Exception e) {
			method.setExpress(true);
			method.setExpressSurcharge(1.5);
			method.setMinWeightKg(0.5);
			method.setMaxWeightKg(50.0);
			method.setFreeShippingThreshold(0);
		}

		Timestamp createdAt = rs.getTimestamp("created_at");
		if (createdAt != null)
			method.setCreatedAt(createdAt.toLocalDateTime());
		Timestamp updatedAt = rs.getTimestamp("updated_at");
		if (updatedAt != null)
			method.setUpdatedAt(updatedAt.toLocalDateTime());
		return method;
	}
}
