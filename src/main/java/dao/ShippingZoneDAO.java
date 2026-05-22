package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.ShippingZone;
import utils.DBConnection;

public class ShippingZoneDAO {

    public long insert(ShippingZone zone) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO shipping_zones (zone_name, zone_type, description, base_fee, "
                    + "price_per_kg, price_per_volume, estimated_days_min, estimated_days_max, status, "
                    + "created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, zone.getZoneName());
                ps.setString(2, zone.getZoneType());
                ps.setString(3, zone.getDescription());
                ps.setDouble(4, zone.getBaseFee());
                ps.setDouble(5, zone.getPricePerKg());
                ps.setDouble(6, zone.getPricePerVolume());
                ps.setInt(7, zone.getEstimatedDaysMin());
                ps.setInt(8, zone.getEstimatedDaysMax());
                ps.setInt(9, zone.getStatus());
                ps.setTimestamp(10, zone.getCreatedAt() != null ? Timestamp.valueOf(zone.getCreatedAt()) : null);
                ps.setTimestamp(11, zone.getUpdatedAt() != null ? Timestamp.valueOf(zone.getUpdatedAt()) : null);

                int rows = ps.executeUpdate();
                if (rows == 0) throw new SQLException("Insert zone failed");

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                    throw new SQLException("No ID obtained");
                }
            }
        }
    }

    public void update(ShippingZone zone) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE shipping_zones SET zone_name=?, zone_type=?, description=?, base_fee=?, "
                    + "price_per_kg=?, price_per_volume=?, estimated_days_min=?, estimated_days_max=?, "
                    + "status=?, updated_at=NOW() WHERE id=?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, zone.getZoneName());
                ps.setString(2, zone.getZoneType());
                ps.setString(3, zone.getDescription());
                ps.setDouble(4, zone.getBaseFee());
                ps.setDouble(5, zone.getPricePerKg());
                ps.setDouble(6, zone.getPricePerVolume());
                ps.setInt(7, zone.getEstimatedDaysMin());
                ps.setInt(8, zone.getEstimatedDaysMax());
                ps.setInt(9, zone.getStatus());
                ps.setLong(10, zone.getId());
                ps.executeUpdate();
            }
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM shipping_zones WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        }
    }

    public ShippingZone getById(long id) {
        String sql = "SELECT * FROM shipping_zones WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToZone(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ShippingZone getByZoneType(String zoneType) {
        String sql = "SELECT * FROM shipping_zones WHERE zone_type = ? AND status = 1 LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, zoneType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToZone(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ShippingZone> getAll() {
        List<ShippingZone> list = new ArrayList<>();
        String sql = "SELECT * FROM shipping_zones ORDER BY zone_type, base_fee";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToZone(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ShippingZone> getAllActive() {
        List<ShippingZone> list = new ArrayList<>();
        String sql = "SELECT * FROM shipping_zones WHERE status = 1 ORDER BY zone_type, base_fee";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToZone(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ShippingZone mapResultSetToZone(ResultSet rs) throws SQLException {
        ShippingZone zone = new ShippingZone();
        zone.setId(rs.getLong("id"));
        zone.setZoneName(rs.getString("zone_name"));
        zone.setZoneType(rs.getString("zone_type"));
        zone.setDescription(rs.getString("description"));
        zone.setBaseFee(rs.getDouble("base_fee"));
        zone.setPricePerKg(rs.getDouble("price_per_kg"));
        zone.setPricePerVolume(rs.getDouble("price_per_volume"));
        zone.setEstimatedDaysMin(rs.getInt("estimated_days_min"));
        zone.setEstimatedDaysMax(rs.getInt("estimated_days_max"));
        zone.setStatus(rs.getInt("status"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) zone.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) zone.setUpdatedAt(updatedAt.toLocalDateTime());
        return zone;
    }
}
