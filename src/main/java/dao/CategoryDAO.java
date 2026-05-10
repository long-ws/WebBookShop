package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.Category;
import utils.DBConnection;

public class CategoryDAO implements DAO<Category> {

	public long insert(Category category) throws SQLException {
		String sql = "INSERT INTO category (name, description, imageName) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, category.getName());
			ps.setString(2, category.getDescription());
			ps.setString(3, category.getImageName());

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Insert category failed, no rows affected");
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new SQLException("Insert category failed, no ID obtained");
			}
		}
	}

	public void update(Category category) throws SQLException {
		String sql = "UPDATE category SET name=?, description=?, imageName=? WHERE id=? and isDeleted = 0";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, category.getName());
			ps.setString(2, category.getDescription());
			ps.setString(3, category.getImageName());
			ps.setLong(4, category.getId());

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Update category failed, category not found");
			}
		}
	}

	public void delete(long categoryId) throws SQLException {

	    String checkSql =
	        "SELECT COUNT(*) " +
	        "FROM product_category pc " +
	        "JOIN product p ON pc.productId = p.id " +
	        "WHERE pc.categoryId = ? AND p.isDeleted = 0";

	    String deleteSql =
	        "UPDATE category SET isDeleted = 1 WHERE id = ? AND isDeleted = 0";

	    try (Connection conn = DBConnection.getConnection()) {
	        conn.setAutoCommit(false);

	        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
	            ps.setLong(1, categoryId);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next() && rs.getInt(1) > 0) {
	                    throw new SQLException(
	                        "Delete category failed: category still has products"
	                    );
	                }
	            }
	        }

	        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
	            ps.setLong(1, categoryId);
	            int rows = ps.executeUpdate();
	            if (rows == 0) {
	                throw new SQLException("Delete category failed: category not found");
	            }
	        }

	        conn.commit();
	    }
	}

	public Category getById(long id) {
		String sql = "SELECT * FROM category WHERE id = ? and isDeleted = 0";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToCategory(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Category> getAll() {
		List<Category> list = new ArrayList<>();
		String sql = "SELECT * FROM category WHERE isDeleted = 0";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSetToCategory(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Category> getPart(int limit, int offset) {
		List<Category> list = new ArrayList<>();
		String sql = "SELECT * FROM category WHERE isDeleted = 0 LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToCategory(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Category> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<Category> list = new ArrayList<>();
		String sql = "SELECT * FROM category WHERE isDeleted = 0 ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToCategory(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public Category getByProductId(long productId) {
		String sql = "SELECT c.* FROM product_category pc JOIN category c ON pc.categoryId = c.id WHERE productId = ? and isDeleted = 0";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, productId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToCategory(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int count() {
		String sql = "SELECT COUNT(id) FROM category WHERE isDeleted = 0";
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

	private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
		Category category = new Category();
		category.setId(rs.getLong("id"));
		category.setName(rs.getString("name"));
		category.setDescription(rs.getString("description"));
		category.setImageName(rs.getString("imageName"));
		return category;
	}
}
