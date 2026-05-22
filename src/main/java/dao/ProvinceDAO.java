package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.Province;
import beans.ShippingZone;
import utils.DBConnection;

public class ProvinceDAO {

    public long insert(Province province) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO provinces (province_code, province_name, province_type, "
                    + "shipping_zone_id, is_metro_city, region, created_at, updated_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, province.getProvinceCode());
                ps.setString(2, province.getProvinceName());
                ps.setString(3, province.getProvinceType());
                if (province.getShippingZoneId() > 0) {
                    ps.setLong(4, province.getShippingZoneId());
                } else {
                    ps.setNull(4, java.sql.Types.BIGINT);
                }
                ps.setBoolean(5, province.getMetroCity());
                ps.setString(6, province.getRegion());
                ps.setTimestamp(7, province.getCreatedAt() != null ? Timestamp.valueOf(province.getCreatedAt()) : null);
                ps.setTimestamp(8, province.getUpdatedAt() != null ? Timestamp.valueOf(province.getUpdatedAt()) : null);

                int rows = ps.executeUpdate();
                if (rows == 0) throw new SQLException("Insert province failed");

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getLong(1);
                    throw new SQLException("No ID obtained");
                }
            }
        }
    }

    public void update(Province province) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE provinces SET province_code=?, province_name=?, province_type=?, "
                    + "shipping_zone_id=?, is_metro_city=?, region=?, updated_at=NOW() WHERE id=?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, province.getProvinceCode());
                ps.setString(2, province.getProvinceName());
                ps.setString(3, province.getProvinceType());
                if (province.getShippingZoneId() > 0) {
                    ps.setLong(4, province.getShippingZoneId());
                } else {
                    ps.setNull(4, java.sql.Types.BIGINT);
                }
                ps.setBoolean(5, province.getMetroCity() != null && province.getMetroCity());
                ps.setString(6, province.getRegion());
                ps.setLong(7, province.getId());
                ps.executeUpdate();
            }
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM provinces WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        }
    }

    public Province getById(long id) {
        String sql = "SELECT p.*, sz.zone_name, sz.zone_type, sz.base_fee, sz.price_per_kg, "
                + "sz.price_per_volume, sz.estimated_days_min, sz.estimated_days_max "
                + "FROM provinces p "
                + "LEFT JOIN shipping_zones sz ON p.shipping_zone_id = sz.id "
                + "WHERE p.id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToProvince(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Province getByProvinceCode(String provinceCode) {
        String sql = "SELECT p.*, sz.zone_name, sz.zone_type, sz.base_fee, sz.price_per_kg, "
                + "sz.price_per_volume, sz.estimated_days_min, sz.estimated_days_max "
                + "FROM provinces p "
                + "LEFT JOIN shipping_zones sz ON p.shipping_zone_id = sz.id "
                + "WHERE p.province_code = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, provinceCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToProvince(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Province> getAll() {
        List<Province> list = new ArrayList<>();
        String sql = "SELECT p.*, sz.zone_name, sz.zone_type, sz.base_fee, sz.price_per_kg, "
                + "sz.price_per_volume, sz.estimated_days_min, sz.estimated_days_max "
                + "FROM provinces p "
                + "LEFT JOIN shipping_zones sz ON p.shipping_zone_id = sz.id "
                + "ORDER BY p.province_name";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProvince(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Province> getByRegion(String region) {
        List<Province> list = new ArrayList<>();
        String sql = "SELECT p.*, sz.zone_name, sz.zone_type, sz.base_fee, sz.price_per_kg, "
                + "sz.price_per_volume, sz.estimated_days_min, sz.estimated_days_max "
                + "FROM provinces p "
                + "LEFT JOIN shipping_zones sz ON p.shipping_zone_id = sz.id "
                + "WHERE p.region = ? ORDER BY p.province_name";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, region);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProvince(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Province> getMetroCities() {
        List<Province> list = new ArrayList<>();
        String sql = "SELECT p.*, sz.zone_name, sz.zone_type, sz.base_fee, sz.price_per_kg, "
                + "sz.price_per_volume, sz.estimated_days_min, sz.estimated_days_max "
                + "FROM provinces p "
                + "LEFT JOIN shipping_zones sz ON p.shipping_zone_id = sz.id "
                + "WHERE p.is_metro_city = TRUE ORDER BY p.province_name";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProvince(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ShippingZone getShippingZoneByProvinceCode(String provinceCode) {
        String sql = "SELECT sz.* FROM provinces p "
                + "JOIN shipping_zones sz ON p.shipping_zone_id = sz.id "
                + "WHERE p.province_code = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, provinceCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ShippingZone zone = new ShippingZone();
                    zone.setId(rs.getLong("id"));
                    zone.setZoneName(rs.getString("zone_name"));
                    zone.setZoneType(rs.getString("zone_type"));
                    zone.setBaseFee(rs.getDouble("base_fee"));
                    zone.setPricePerKg(rs.getDouble("price_per_kg"));
                    zone.setPricePerVolume(rs.getDouble("price_per_volume"));
                    zone.setEstimatedDaysMin(rs.getInt("estimated_days_min"));
                    zone.setEstimatedDaysMax(rs.getInt("estimated_days_max"));
                    return zone;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Province mapResultSetToProvince(ResultSet rs) throws SQLException {
        Province province = new Province();
        province.setId(rs.getLong("id"));
        province.setProvinceCode(rs.getString("province_code"));
        province.setProvinceName(rs.getString("province_name"));
        province.setProvinceType(rs.getString("province_type"));
        province.setShippingZoneId(rs.getLong("shipping_zone_id"));

        try {
            province.setMetroCity(rs.getBoolean("is_metro_city"));
        } catch (SQLException e) {
            province.setMetroCity(false);
        }

        province.setRegion(rs.getString("region"));

        try {
            String zoneName = rs.getString("zone_name");
            if (zoneName != null) {
                ShippingZone zone = new ShippingZone();
                zone.setId(province.getShippingZoneId());
                zone.setZoneName(zoneName);
                zone.setZoneType(rs.getString("zone_type"));
                zone.setBaseFee(rs.getDouble("base_fee"));
                zone.setPricePerKg(rs.getDouble("price_per_kg"));
                zone.setPricePerVolume(rs.getDouble("price_per_volume"));
                zone.setEstimatedDaysMin(rs.getInt("estimated_days_min"));
                zone.setEstimatedDaysMax(rs.getInt("estimated_days_max"));
                province.setShippingZone(zone);
            }
        } catch (SQLException e) {
            // Zone info not available
        }

        return province;
    }
}
