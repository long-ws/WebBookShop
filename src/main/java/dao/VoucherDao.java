package dao;

import beans.Category;
import beans.Voucher;
import dto.CategoryDTO;
import dto.ProductDTO;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VoucherDao {
    public boolean createVoucher(Voucher voucher) {
        String sqlVoucher = "INSERT INTO vouchers (code, name, description, calculation_method, apply_to, " +
                "start_date, end_date, value, min_purchase, max_discount, usage_limit, per_user_limit, used_count, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlCategory = "INSERT INTO voucher_categories (voucher_id, category_id) VALUES (?, ?)";
        String sqlProduct = "INSERT INTO voucher_products (voucher_id, product_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            long generatedVoucherId = 0;
            try (PreparedStatement psVoucher = conn.prepareStatement(sqlVoucher, Statement.RETURN_GENERATED_KEYS)) {
                psVoucher.setString(1, voucher.getCode());
                psVoucher.setString(2, voucher.getName());
                psVoucher.setString(3, voucher.getDescription());
                psVoucher.setInt(4, voucher.getCalculationMethod());
                psVoucher.setInt(5, voucher.getApplyTo());
                psVoucher.setTimestamp(6, voucher.getStartDate());
                psVoucher.setTimestamp(7, voucher.getEndDate());
                psVoucher.setDouble(8, voucher.getValue());
                psVoucher.setDouble(9, voucher.getMinPurchase());
                psVoucher.setDouble(10, voucher.getMaxDiscount());
                psVoucher.setInt(11, voucher.getUsageLimit());
                psVoucher.setInt(12, voucher.getPerUserLimit());
                psVoucher.setInt(13, voucher.getUsedCount());
                psVoucher.setBoolean(14, voucher.isActive());

                int affectedRows = psVoucher.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating voucher failed, no rows affected.");
                }

                try (ResultSet generatedKeys = psVoucher.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedVoucherId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Creating voucher failed, no ID obtained.");
                    }
                }
            }
            if (voucher.getCategoryIds() != null && !voucher.getCategoryIds().isEmpty()) {
                try (PreparedStatement psCategory = conn.prepareStatement(sqlCategory)) {
                    for (Long catId : voucher.getCategoryIds()) {
                        psCategory.setLong(1, generatedVoucherId);
                        psCategory.setLong(2, catId);
                        psCategory.addBatch();
                    }
                    psCategory.executeBatch();
                }
            }
            
            if (voucher.getProductIds() != null && !voucher.getProductIds().isEmpty()) {
                try (PreparedStatement psProduct = conn.prepareStatement(sqlProduct)) {
                    for (Long prodId : voucher.getProductIds()) {
                        psProduct.setLong(1, generatedVoucherId);
                        psProduct.setLong(2, prodId);
                        psProduct.addBatch();
                    }
                    psProduct.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    public int count() {
        String sql = "SELECT COUNT(*) FROM vouchers";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Voucher> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        List<Voucher> list = new ArrayList<>();

        if (orderBy == null || orderBy.trim().isEmpty()) orderBy = "id";
        if (orderDir == null || orderDir.trim().isEmpty()) orderDir = "DESC";

        if (!orderBy.matches("[a-zA-Z0-9_]+")) orderBy = "id";
        if (!orderDir.equalsIgnoreCase("ASC") && !orderDir.equalsIgnoreCase("DESC")) orderDir = "DESC";

        String sql = "SELECT * FROM vouchers ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Voucher v = new Voucher();
                    v.setId(rs.getLong("id"));
                    v.setCode(rs.getString("code"));
                    v.setName(rs.getString("name"));
                    v.setDescription(rs.getString("description"));
                    v.setCalculationMethod(rs.getInt("calculation_method"));
                    v.setApplyTo(rs.getInt("apply_to"));
                    v.setStartDate(rs.getTimestamp("start_date"));
                    v.setEndDate(rs.getTimestamp("end_date"));
                    v.setValue(rs.getDouble("value"));
                    v.setMinPurchase(rs.getDouble("min_purchase"));
                    v.setMaxDiscount(rs.getDouble("max_discount"));
                    v.setUsageLimit(rs.getInt("usage_limit"));
                    v.setPerUserLimit(rs.getInt("per_user_limit"));
                    v.setUsedCount(rs.getInt("used_count"));
                    v.setActive(rs.getBoolean("is_active"));
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public Voucher getVoucherById(long vId) {
        String sql = "SELECT * FROM vouchers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, vId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Voucher v = new Voucher();
                    v.setId(rs.getLong("id"));
                    v.setCode(rs.getString("code"));
                    v.setActive(rs.getBoolean("is_active"));
                    return v;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Voucher getVoucherWithRelations(long vId) {
        String sqlVoucher = "SELECT * FROM vouchers WHERE id = ?";
        String sqlCategories = "SELECT c.id, c.name, c.imageName FROM category c " +
                "JOIN voucher_categories vc ON c.id = vc.category_id WHERE vc.voucher_id = ?";
        String sqlProducts = "SELECT p.id, p.name, p.imageName FROM product p " +
                "JOIN voucher_products vp ON p.id = vp.product_id WHERE vp.voucher_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psVoucher = conn.prepareStatement(sqlVoucher)) {

            psVoucher.setLong(1, vId);
            try (ResultSet rs = psVoucher.executeQuery()) {
                if (rs.next()) {
                    Voucher v = new Voucher();
                    v.setId(rs.getLong("id"));
                    v.setCode(rs.getString("code"));
                    v.setName(rs.getString("name"));

                    v.setDescription(rs.getString("description"));
                    v.setCalculationMethod(rs.getInt("calculation_method"));
                    v.setApplyTo(rs.getInt("apply_to"));
                    v.setStartDate(rs.getTimestamp("start_date"));
                    v.setEndDate(rs.getTimestamp("end_date"));
                    v.setValue(rs.getDouble("value"));
                    v.setMinPurchase(rs.getDouble("min_purchase"));
                    v.setMaxDiscount(rs.getDouble("max_discount"));
                    v.setUsageLimit(rs.getInt("usage_limit"));
                    v.setPerUserLimit(rs.getInt("per_user_limit"));
                    v.setUsedCount(rs.getInt("used_count"));
                    v.setActive(rs.getBoolean("is_active"));

                    try (PreparedStatement psCategories = conn.prepareStatement(sqlCategories)) {
                        psCategories.setLong(1, vId);
                        try (ResultSet rsCat = psCategories.executeQuery()) {
                            List<CategoryDTO> cats = new ArrayList<>();
                            while (rsCat.next()) {
                                CategoryDTO cat = new CategoryDTO();
                                cat.setId(rsCat.getLong("id"));
                                cat.setName(rsCat.getString("name"));
                                cat.setImageName(rsCat.getString("imageName"));
                                cats.add(cat);
                            }
                            v.setCategories(cats);
                        }
                    }

                    try (PreparedStatement psProducts = conn.prepareStatement(sqlProducts)) {
                        psProducts.setLong(1, vId);
                        try (ResultSet rsProd = psProducts.executeQuery()) {
                            List<ProductDTO> prods = new ArrayList<>();
                            while (rsProd.next()) {
                                ProductDTO prod = new ProductDTO();
                                prod.setId(rsProd.getLong("id"));
                                prod.setName(rsProd.getString("name"));
                                prod.setImageName(rsProd.getString("imageName"));
                                prods.add(prod);
                            }
                            v.setProducts(prods);
                        }
                    }
                    return v;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean deleteVoucher(long vId) {
        String sql = "DELETE v, vc, vp FROM vouchers v " +
                "LEFT JOIN voucher_categories vc ON v.id = vc.voucher_id " +
                "LEFT JOIN voucher_products vp ON v.id = vp.voucher_id " +
                "WHERE v.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, vId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateVoucher(Voucher voucher) {
        String sqlUpdateVoucher = "UPDATE vouchers SET code = ?, name = ?, description = ?, calculation_method = ?, " +
                "apply_to = ?, start_date = ?, end_date = ?, value = ?, min_purchase = ?, max_discount = ?, " +
                "usage_limit = ?, per_user_limit = ?, is_active = ? WHERE id = ?";

        String sqlDeleteCategories = "DELETE FROM voucher_categories WHERE voucher_id = ?";
        String sqlDeleteProducts = "DELETE FROM voucher_products WHERE voucher_id = ?";
        String sqlInsertCategory = "INSERT INTO voucher_categories (voucher_id, category_id) VALUES (?, ?)";
        String sqlInsertProduct = "INSERT INTO voucher_products (voucher_id, product_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateVoucher)) {
                ps.setString(1, voucher.getCode());
                ps.setString(2, voucher.getName());
                ps.setString(3, voucher.getDescription());
                ps.setInt(4, voucher.getCalculationMethod());
                ps.setInt(5, voucher.getApplyTo());
                ps.setTimestamp(6, voucher.getStartDate());
                ps.setTimestamp(7, voucher.getEndDate());
                ps.setDouble(8, voucher.getValue());
                ps.setDouble(9, voucher.getMinPurchase());
                ps.setDouble(10, voucher.getMaxDiscount());
                ps.setInt(11, voucher.getUsageLimit());
                ps.setInt(12, voucher.getPerUserLimit());
                ps.setBoolean(13, voucher.isActive());
                ps.setLong(14, voucher.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement psDelCat = conn.prepareStatement(sqlDeleteCategories)) {
                psDelCat.setLong(1, voucher.getId());
                psDelCat.executeUpdate();
            }
            if (voucher.getCategoryIds() != null && !voucher.getCategoryIds().isEmpty()) {
                try (PreparedStatement psInsCat = conn.prepareStatement(sqlInsertCategory)) {
                    for (Long catId : voucher.getCategoryIds()) {
                        psInsCat.setLong(1, voucher.getId());
                        psInsCat.setLong(2, catId);
                        psInsCat.addBatch();
                    }
                    psInsCat.executeBatch();
                }
            }

            try (PreparedStatement psDelProd = conn.prepareStatement(sqlDeleteProducts)) {
                psDelProd.setLong(1, voucher.getId());
                psDelProd.executeUpdate();
            }
            if (voucher.getProductIds() != null && !voucher.getProductIds().isEmpty()) {
                try (PreparedStatement psInsProd = conn.prepareStatement(sqlInsertProduct)) {
                    for (Long prodId : voucher.getProductIds()) {
                        psInsProd.setLong(1, voucher.getId());
                        psInsProd.setLong(2, prodId);
                        psInsProd.addBatch();
                    }
                    psInsProd.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return false;
    }
    public List<Voucher> getVouchersForUser(Integer applyTo, int offset, int recordsPerPage) {
        List<Voucher> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM vouchers WHERE is_active = 1 ");
        if (applyTo != null) {
            sql.append("AND apply_to = ? ");
        }
        sql.append("ORDER BY end_date DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (applyTo != null) {
                ps.setInt(paramIndex++, applyTo);
            }
            ps.setInt(paramIndex++, recordsPerPage);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Voucher v = new Voucher();
                    v.setId(rs.getLong("id"));
                    v.setCode(rs.getString("code"));
                    v.setName(rs.getString("name"));
                    v.setDescription(rs.getString("description"));
                    v.setCalculationMethod(rs.getInt("calculation_method"));
                    v.setApplyTo(rs.getInt("apply_to"));
                    if (rs.getTimestamp("start_date") != null) {
                        v.setStartDate(rs.getTimestamp("start_date"));
                    }
                    if (rs.getTimestamp("end_date") != null) {
                        v.setEndDate(rs.getTimestamp("end_date"));
                    }
                    v.setValue(rs.getDouble("value"));
                    v.setMinPurchase(rs.getDouble("min_purchase"));
                    v.setMaxDiscount(rs.getDouble("max_discount"));
                    v.setUsageLimit(rs.getInt("usage_limit"));
                    v.setPerUserLimit(rs.getInt("per_user_limit"));
                    v.setUsedCount(rs.getInt("used_count"));
                    v.setActive(rs.getBoolean("is_active"));

                    list.add(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public int getTotalVouchersCountForUser(Integer applyTo) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM vouchers WHERE is_active = 1 ");
        if (applyTo != null) {
            sql.append("AND apply_to = ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (applyTo != null) {
                ps.setInt(1, applyTo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}