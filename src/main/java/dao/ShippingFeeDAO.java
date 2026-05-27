package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.ShippingFee;
import utils.DBConnection;

public class ShippingFeeDAO {

	public ShippingFee getFeeByMethodAndZoneAndWeight(long methodId, String zoneType, double weight) {
		String sql = "SELECT * FROM shipping_fees WHERE shipping_method_id = ? AND zone_type = ? "
				   + "AND min_weight <= ? AND max_weight >= ? LIMIT 1";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, methodId);
			ps.setString(2, zoneType);
			ps.setDouble(3, weight);
			ps.setDouble(4, weight);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapResultSetToFee(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ShippingFee> getFeesByMethodAndZone(long methodId, String zoneType) {
		List<ShippingFee> fees = new ArrayList<>();
		String sql = "SELECT * FROM shipping_fees WHERE shipping_method_id = ? AND zone_type = ? ORDER BY min_weight";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, methodId);
			ps.setString(2, zoneType);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					fees.add(mapResultSetToFee(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fees;
	}

	public List<ShippingFee> getFeesByMethod(long methodId) {
		List<ShippingFee> fees = new ArrayList<>();
		String sql = "SELECT * FROM shipping_fees WHERE shipping_method_id = ? ORDER BY zone_type, min_weight";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, methodId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					fees.add(mapResultSetToFee(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fees;
	}

	public List<ShippingFee> getAll() {
		List<ShippingFee> fees = new ArrayList<>();
		String sql = "SELECT * FROM shipping_fees ORDER BY shipping_method_id, zone_type, min_weight";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				fees.add(mapResultSetToFee(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fees;
	}

	public ShippingFee getById(long id) {
		String sql = "SELECT * FROM shipping_fees WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapResultSetToFee(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long insert(ShippingFee fee) throws SQLException {
		String sql = "INSERT INTO shipping_fees (shipping_method_id, zone_type, min_weight, max_weight, "
				   + "base_fee, fee_per_kg, price_per_volume, volumetric_ratio, "
				   + "estimated_days_min, estimated_days_max, created_at, updated_at) "
				   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, fee.getShippingMethodId());
			ps.setString(2, fee.getZoneType());
			ps.setDouble(3, fee.getWeightMin());
			ps.setDouble(4, fee.getWeightMax());
			ps.setDouble(5, fee.getBaseFee());
			ps.setDouble(6, fee.getFeePerKg());
			ps.setDouble(7, fee.getPricePerVolume());
			ps.setInt(8, fee.getVolumetricRatio());
			ps.setInt(9, fee.getEstimatedDaysMin());
			ps.setInt(10, fee.getEstimatedDaysMax());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
			}
		}
		return 0;
	}

	public void update(ShippingFee fee) throws SQLException {
		String sql = "UPDATE shipping_fees SET zone_type = ?, min_weight = ?, max_weight = ?, "
				   + "base_fee = ?, fee_per_kg = ?, price_per_volume = ?, volumetric_ratio = ?, "
				   + "estimated_days_min = ?, estimated_days_max = ?, updated_at = NOW() WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, fee.getZoneType());
			ps.setDouble(2, fee.getWeightMin());
			ps.setDouble(3, fee.getWeightMax());
			ps.setDouble(4, fee.getBaseFee());
			ps.setDouble(5, fee.getFeePerKg());
			ps.setDouble(6, fee.getPricePerVolume());
			ps.setInt(7, fee.getVolumetricRatio());
			ps.setInt(8, fee.getEstimatedDaysMin());
			ps.setInt(9, fee.getEstimatedDaysMax());
			ps.setLong(10, fee.getId());
			ps.executeUpdate();
		}
	}

	public void delete(long id) throws SQLException {
		String sql = "DELETE FROM shipping_fees WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			ps.executeUpdate();
		}
	}

	public void deleteByMethodId(long methodId) throws SQLException {
		String sql = "DELETE FROM shipping_fees WHERE shipping_method_id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, methodId);
			ps.executeUpdate();
		}
	}

	private ShippingFee mapResultSetToFee(ResultSet rs) throws SQLException {
		ShippingFee fee = new ShippingFee();
		fee.setId(rs.getLong("id"));
		fee.setShippingMethodId(rs.getLong("shipping_method_id"));
		fee.setZoneType(rs.getString("zone_type"));
		fee.setWeightMin(rs.getDouble("min_weight"));
		fee.setWeightMax(rs.getDouble("max_weight"));
		fee.setBaseFee(rs.getDouble("base_fee"));
		fee.setFeePerKg(rs.getDouble("fee_per_kg"));
		fee.setPricePerVolume(rs.getDouble("price_per_volume"));
		fee.setVolumetricRatio(rs.getInt("volumetric_ratio"));
		fee.setEstimatedDaysMin(rs.getInt("estimated_days_min"));
		fee.setEstimatedDaysMax(rs.getInt("estimated_days_max"));

		Timestamp createdAt = rs.getTimestamp("created_at");
		if (createdAt != null) {
			fee.setCreatedAt(createdAt.toLocalDateTime());
		}
		Timestamp updatedAt = rs.getTimestamp("updated_at");
		if (updatedAt != null) {
			fee.setUpdatedAt(updatedAt.toLocalDateTime());
		}
		return fee;
	}
}
