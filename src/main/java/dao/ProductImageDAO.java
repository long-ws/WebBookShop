package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.ProductImage;
import utils.DBConnection;

public class ProductImageDAO implements DAO<ProductImage> {

    @Override
    public long insert(ProductImage img) throws SQLException {
        String sql = "INSERT INTO product_images (product_id, image_name, is_primary, sort_order, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, img.getProductId());
            ps.setString(2, img.getImageName());
            ps.setInt(3, img.getIsPrimary());
            ps.setInt(4, img.getSortOrder());
            ps.setTimestamp(5, img.getCreatedAt() != null ? Timestamp.valueOf(img.getCreatedAt()) : null);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Insert failed");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                throw new SQLException("No ID obtained");
            }
        }
    }

    @Override
    public void update(ProductImage img) throws SQLException {
        String sql = "UPDATE product_images SET image_name=?, is_primary=?, sort_order=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, img.getImageName());
            ps.setInt(2, img.getIsPrimary());
            ps.setInt(3, img.getSortOrder());
            ps.setLong(4, img.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM product_images WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public ProductImage getById(long id) {
        String sql = "SELECT * FROM product_images WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToProductImage(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ProductImage> getAll() {
        List<ProductImage> list = new ArrayList<>();
        String sql = "SELECT * FROM product_images ORDER BY sort_order ASC, created_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultSetToProductImage(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ProductImage> getPart(int limit, int offset) {
        return getAll();
    }

    @Override
    public List<ProductImage> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return getAll();
    }

    public List<ProductImage> getByProductId(long productId) {
        List<ProductImage> list = new ArrayList<>();
        String sql = "SELECT * FROM product_images WHERE product_id = ? ORDER BY is_primary DESC, sort_order ASC, created_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToProductImage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ProductImage getPrimaryByProductId(long productId) {
        String sql = "SELECT * FROM product_images WHERE product_id = ? AND is_primary = 1 ORDER BY created_at ASC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToProductImage(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countByProductId(long productId) {
        String sql = "SELECT COUNT(id) FROM product_images WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setPrimary(long imageId, long productId) {
        String sql1 = "UPDATE product_images SET is_primary = 0 WHERE product_id = ?";
        String sql2 = "UPDATE product_images SET is_primary = 1 WHERE id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                ps1.setLong(1, productId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                ps2.setLong(1, imageId);
                ps2.setLong(2, productId);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllByProductId(long productId) {
        String sql = "DELETE FROM product_images WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ProductImage mapResultSetToProductImage(ResultSet rs) throws SQLException {
        ProductImage img = new ProductImage();
        img.setId(rs.getLong("id"));
        img.setProductId(rs.getLong("product_id"));
        img.setImageName(rs.getString("image_name"));
        img.setIsPrimary(rs.getInt("is_primary"));
        img.setSortOrder(rs.getInt("sort_order"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) img.setCreatedAt(ts.toLocalDateTime());
        return img;
    }
}
