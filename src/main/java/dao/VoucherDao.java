package dao;

import beans.Category;
import beans.Voucher;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherDao implements DAO<Voucher> {

    @Override
    public long insert(Voucher voucher) throws SQLException {
        String sql = "INSERT INTO vouchers (code, name, description, calculation_method, apply_scope, apply_to, " +
                "start_date, end_date, value, min_purchase, max_discount, usage_limit, per_user_limit, used_count, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, voucher.getCode());
            ps.setString(2, voucher.getName());
            ps.setString(3, voucher.getDescription());
            ps.setString(4, voucher.getCalculationMethod());
            ps.setString(5, voucher.getApplyScope());
            ps.setString(6, voucher.getApplyTo());
            ps.setTimestamp(7, Timestamp.valueOf(voucher.getStartDate()));
            ps.setTimestamp(8, Timestamp.valueOf(voucher.getEndDate()));
            ps.setDouble(9, voucher.getValue());
            ps.setDouble(10, voucher.getMinPurchase());
            ps.setDouble(11, voucher.getMaxDiscount());
            ps.setInt(12, voucher.getUsageLimit());
            ps.setInt(13, voucher.getPerUserLimit());
            ps.setInt(14, voucher.getUsedCount());
            ps.setBoolean(15, voucher.getIsActive());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Insert voucher failed, no rows affected");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                throw new SQLException("Insert voucher failed, no ID obtained");
            }
        }
    }

    @Override
    public void update(Voucher voucher) throws SQLException {
        String sql = "UPDATE vouchers SET code=?, name=?, description=?, calculation_method=?, apply_scope=?, " +
                "apply_to=?, start_date=?, end_date=?, value=?, min_purchase=?, max_discount=?, usage_limit=?, " +
                "per_user_limit=?, used_count=?, is_active=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, voucher.getCode());
            ps.setString(2, voucher.getName());
            ps.setString(3, voucher.getDescription());
            ps.setString(4, voucher.getCalculationMethod());
            ps.setString(5, voucher.getApplyScope());
            ps.setString(6, voucher.getApplyTo());
            ps.setTimestamp(7, Timestamp.valueOf(voucher.getStartDate()));
            ps.setTimestamp(8, Timestamp.valueOf(voucher.getEndDate()));
            ps.setDouble(9, voucher.getValue());
            ps.setDouble(10, voucher.getMinPurchase());
            ps.setDouble(11, voucher.getMaxDiscount());
            ps.setInt(12, voucher.getUsageLimit());
            ps.setInt(13, voucher.getPerUserLimit());
            ps.setInt(14, voucher.getUsedCount());
            ps.setBoolean(15, voucher.getIsActive());
            ps.setInt(16, voucher.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM vouchers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Voucher getById(long id) {
        String sql = "SELECT * FROM vouchers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToVoucher(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Voucher> getAll() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Voucher mapResultSetToVoucher(ResultSet rs) throws SQLException {
        Voucher v = new Voucher();
        v.setId(rs.getInt("id"));
        v.setCode(rs.getString("code"));
        v.setName(rs.getString("name"));
        v.setDescription(rs.getString("description"));
        v.setCalculationMethod(rs.getString("calculation_method"));
        v.setApplyScope(rs.getString("apply_scope"));
        v.setApplyTo(rs.getString("apply_to"));
        v.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
        v.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
        v.setValue(rs.getDouble("value"));
        v.setMinPurchase(rs.getDouble("min_purchase"));
        v.setMaxDiscount(rs.getDouble("max_discount"));
        v.setUsageLimit(rs.getInt("usage_limit"));
        v.setPerUserLimit(rs.getInt("per_user_limit"));
        v.setUsedCount(rs.getInt("used_count"));
        v.setActive(rs.getBoolean("is_active"));
        return v;
    }

    @Override
    public List<Voucher> getPart(int limit, int offset) {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Voucher> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM vouchers ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToVoucher(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int count() {
        String sql = "SELECT COUNT(id) FROM vouchers";
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
}