package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.ShippingWeightFee;
import utils.DBConnection;

public class ShippingWeightFeeDAO {

    public long insert(ShippingWeightFee fee) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO shipping_weight_fees (shipping_method_id, zone_type, min_weight, "
                    + "max_weight, base_fee, fee_per_kg, created_at, updated_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, fee.getShippingMethodId());
                ps.setString(2, fee.getZoneType());
                ps.setDouble(3, fee.getMinWeight());
                ps.setDouble(4, fee.getMaxWeight());
                ps.setDouble(5, fee.getBaseFee());
                ps.setDouble(6, fee.getFeePerKg());
                ps.setTimestamp(7, fee.getCreatedAt() != null ? Timestamp.valueOf(fee.getCreatedAt()) : null);
                ps.setTimestamp(8, fee.getUpdatedAt() != null ? Timestamp.valueOf(fee.getUpdatedAt()) : null);

                int rows = ps.executeUpdate();
                if (rows == 0) throw new SQLException("Insert fee failed");

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                    throw new SQLException("No ID obtained");
                }
            }
        }
    }

    public void update(ShippingWeightFee fee) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE shipping_weight_fees SET zone_type=?, min_weight=?, max_weight=?, "
                    + "base_fee=?, fee_per_kg=?, updated_at=NOW() WHERE id=?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fee.getZoneType());
                ps.setDouble(2, fee.getMinWeight());
                ps.setDouble(3, fee.getMaxWeight());
                ps.setDouble(4, fee.getBaseFee());
                ps.setDouble(5, fee.getFeePerKg());
                ps.setLong(6, fee.getId());
                ps.executeUpdate();
            }
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM shipping_weight_fees WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        }
    }

    public ShippingWeightFee getById(long id) {
        String sql = "SELECT * FROM shipping_weight_fees WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToFee(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ShippingWeightFee> getAll() {
        List<ShippingWeightFee> list = new ArrayList<>();
        String sql = "SELECT swf.*, sm.name as method_name FROM shipping_weight_fees swf "
                + "JOIN shipping_methods sm ON swf.shipping_method_id = sm.id "
                + "ORDER BY sm.name, swf.zone_type, swf.min_weight";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ShippingWeightFee fee = mapResultSetToFee(rs);
                fee.setMethodName(rs.getString("method_name"));
                list.add(fee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ShippingWeightFee> getByMethodId(long methodId) {
        List<ShippingWeightFee> list = new ArrayList<>();
        String sql = "SELECT * FROM shipping_weight_fees WHERE shipping_method_id = ? ORDER BY zone_type, min_weight";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, methodId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFee(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ShippingWeightFee getByMethodAndZoneAndWeight(long methodId, String zoneType, double weight) {
        String sql = "SELECT * FROM shipping_weight_fees WHERE shipping_method_id = ? AND zone_type = ? "
                + "AND min_weight <= ? AND max_weight >= ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, methodId);
            ps.setString(2, zoneType);
            ps.setDouble(3, weight);
            ps.setDouble(4, weight);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToFee(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ShippingWeightFee> getByMethodAndZone(long methodId, String zoneType) {
        List<ShippingWeightFee> list = new ArrayList<>();
        String sql = "SELECT * FROM shipping_weight_fees WHERE shipping_method_id = ? AND zone_type = ? ORDER BY min_weight";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, methodId);
            ps.setString(2, zoneType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFee(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteByMethodId(long methodId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM shipping_weight_fees WHERE shipping_method_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, methodId);
                ps.executeUpdate();
            }
        }
    }

    private ShippingWeightFee mapResultSetToFee(ResultSet rs) throws SQLException {
        ShippingWeightFee fee = new ShippingWeightFee();
        fee.setId(rs.getLong("id"));
        fee.setShippingMethodId(rs.getLong("shipping_method_id"));
        fee.setZoneType(rs.getString("zone_type"));
        fee.setMinWeight(rs.getDouble("min_weight"));
        fee.setMaxWeight(rs.getDouble("max_weight"));
        fee.setBaseFee(rs.getDouble("base_fee"));
        fee.setFeePerKg(rs.getDouble("fee_per_kg"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) fee.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) fee.setUpdatedAt(updatedAt.toLocalDateTime());
        return fee;
    }
}
