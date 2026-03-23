package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.Product;
import utils.DBConnection;

public class ProductDAO implements DAO<Product> {

	public long insert(Product product) throws SQLException {
		String sql = "INSERT INTO product "
				+ "(name, price, discount, quantity, totalBuy, author, pages, publisher, yearPublishing, "
				+ "description, imageName, shop, createdAt, updatedAt, startsAt, endsAt) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			setPreparedStatement(ps, product);

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Insert product failed, no rows affected");
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new SQLException("Insert product failed, no ID obtained");
			}
		}
	}

	public void update(Product product) throws SQLException {
		String sql = "UPDATE product SET name=?, price=?, discount=?, quantity=?, totalBuy=?, author=?, pages=?, "
				+ "publisher=?, yearPublishing=?, description=?, imageName=?, shop=?, updatedAt=?, startsAt=?, endsAt=? "
				+ "WHERE id=? and isDeleted = 0";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			setPreparedStatement(ps, product);
			ps.setLong(16, product.getId());

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Update product failed, product not found");
			}
		}
	}

	public void delete(long id) throws SQLException {

	    String sql = "UPDATE product SET isDeleted = 1 WHERE id = ? AND isDeleted = 0";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, id);

	        int rows = ps.executeUpdate();
	        if (rows == 0) {
	            throw new SQLException("Delete product failed, product not found or already deleted");
	        }
	    }
	}

	public Product getById(long id) {
		try (Connection conn = DBConnection.getConnection()) {
			return getById(conn, id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Product getById(Connection conn, long id) throws SQLException {
	    String sql = "SELECT * FROM product WHERE id = ? AND isDeleted = 0";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setLong(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return mapResultSetToProduct(rs);
	            }
	        }
	    }
	    return null;
	}


	public List<Product> getAll() {
		List<Product> list = new ArrayList<>();
		String sql = "SELECT * FROM product WHERE isDeleted = 0";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next())
				list.add(mapResultSetToProduct(rs));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Product> getPart(int limit, int offset) {
		List<Product> list = new ArrayList<>();
		String sql = "SELECT * FROM product WHERE isDeleted = 0 LIMIT ? OFFSET ? ";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapResultSetToProduct(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Product> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<Product> list = new ArrayList<>();
		String sql = "SELECT * FROM product WHERE isDeleted = 0 ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ? ";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapResultSetToProduct(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Product> getOrderedPartByCategoryId(
	        int limit, int offset, String orderBy, String orderDir, long categoryId) {

	    List<Product> list = new ArrayList<>();
	    String sql =
	        "SELECT p.* FROM product_category pc " +
	        "JOIN product p ON pc.productId = p.id " +
	        "WHERE pc.categoryId = ? AND p.isDeleted = 0 " +
	        "ORDER BY p." + orderBy + " " + orderDir +
	        " LIMIT ? OFFSET ?";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, categoryId);
	        ps.setInt(2, limit);
	        ps.setInt(3, offset);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapResultSetToProduct(rs));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}


	public int countByCategoryId(long categoryId) {
	    String sql =
	        "SELECT COUNT(pc.productId) " +
	        "FROM product_category pc " +
	        "JOIN product p ON pc.productId = p.id " +
	        "WHERE pc.categoryId = ? AND p.isDeleted = 0";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, categoryId);
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


	public List<Product> getRandomPartByCategoryId(int limit, int offset, long categoryId) {

	    List<Product> list = new ArrayList<>();
	    String sql =
	        "SELECT p.* FROM product_category pc " +
	        "JOIN product p ON pc.productId = p.id " +
	        "WHERE pc.categoryId = ? AND p.isDeleted = 0 " +
	        "ORDER BY RAND() LIMIT ? OFFSET ?";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, categoryId);
	        ps.setInt(2, limit);
	        ps.setInt(3, offset);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapResultSetToProduct(rs));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}


	public List<String> getPublishersByCategoryId(long categoryId) {

	    List<String> list = new ArrayList<>();
	    String sql =
	        "SELECT DISTINCT p.publisher FROM product_category pc " +
	        "JOIN product p ON pc.productId = p.id " +
	        "WHERE pc.categoryId = ? AND p.isDeleted = 0 " +
	        "ORDER BY p.publisher";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, categoryId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(rs.getString("publisher"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	public int countByCategoryIdAndFilters(long categoryId, String filters) {

	    String sql =
	        "SELECT COUNT(p.id) FROM product_category pc " +
	        "JOIN product p ON pc.productId = p.id " +
	        "WHERE pc.categoryId = ? AND p.isDeleted = 0 AND " + filters;

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, categoryId);
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

	public List<Product> getOrderedPartByCategoryIdAndFilters(
	        int limit, int offset, String orderBy, String orderDir,
	        long categoryId, String filters) {

	    List<Product> list = new ArrayList<>();
	    String sql =
	        "SELECT p.* FROM product_category pc " +
	        "JOIN product p ON pc.productId = p.id " +
	        "WHERE pc.categoryId = ? AND p.isDeleted = 0 AND " + filters +
	        " ORDER BY p." + orderBy + " " + orderDir +
	        " LIMIT ? OFFSET ?";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, categoryId);
	        ps.setInt(2, limit);
	        ps.setInt(3, offset);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapResultSetToProduct(rs));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	public int count() {
		String sql = "SELECT COUNT(id) FROM product WHere isDeleted = 0;";
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

	public boolean insertProductCategory(long productId, long categoryId) {
		String sql = "INSERT INTO product_category(productId, categoryId) VALUES (?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			conn.setAutoCommit(false);
			ps.setLong(1, productId);
			ps.setLong(2, categoryId);

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Insert product_category failed");

			conn.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateProductCategory(long productId, long categoryId) {
	    String sql = "UPDATE product_category SET categoryId = ? WHERE productId = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, categoryId);
	        ps.setLong(2, productId);

	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	public boolean deleteProductCategory(long productId, long categoryId) {
	    String sql = "DELETE FROM product_category WHERE productId = ? AND categoryId = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, productId);
	        ps.setLong(2, categoryId);

	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public List<Product> getByQuery(String query, int limit, int offset) {
		List<Product> list = new ArrayList<>();
		String sql = "SELECT * FROM product WHERE name LIKE ? and isDeleted = 0 LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, "%" + query + "%");
			ps.setInt(2, limit);
			ps.setInt(3, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapResultSetToProduct(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countByQuery(String query) {
		String sql = "SELECT COUNT(id) FROM product WHERE name LIKE ? and isDeleted = 0";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, "%" + query + "%");
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void setPreparedStatement(PreparedStatement ps, Product product) throws SQLException {
		ps.setString(1, product.getName());
		ps.setDouble(2, product.getPrice());
		ps.setDouble(3, product.getDiscount());
		ps.setInt(4, product.getQuantity());
		ps.setInt(5, product.getTotalBuy());
		ps.setString(6, product.getAuthor());
		ps.setInt(7, product.getPages());
		ps.setString(8, product.getPublisher());
		ps.setInt(9, product.getYearPublishing());
		ps.setString(10, product.getDescription());
		ps.setString(11, product.getImageName());
		ps.setInt(12, product.getShop());
		ps.setTimestamp(13, Timestamp.valueOf(product.getCreatedAt()));
		ps.setTimestamp(14, product.getUpdatedAt() != null ? Timestamp.valueOf(product.getUpdatedAt()) : null);
		ps.setTimestamp(15, product.getStartsAt() != null ? Timestamp.valueOf(product.getStartsAt()) : null);
		ps.setTimestamp(16, product.getEndsAt() != null ? Timestamp.valueOf(product.getEndsAt()) : null);
	}

	private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
		Product p = new Product();
		p.setId(rs.getLong("id"));
		p.setName(rs.getString("name"));
		p.setPrice(rs.getDouble("price"));
		p.setDiscount(rs.getDouble("discount"));
		p.setQuantity(rs.getInt("quantity"));
		p.setTotalBuy(rs.getInt("totalBuy"));
		p.setAuthor(rs.getString("author"));
		p.setPages(rs.getInt("pages"));
		p.setPublisher(rs.getString("publisher"));
		p.setYearPublishing(rs.getInt("yearPublishing"));
		p.setDescription(rs.getString("description"));
		p.setImageName(rs.getString("imageName"));
		p.setShop(rs.getInt("shop"));
		Timestamp createdAt = rs.getTimestamp("createdAt");
		if (createdAt != null)
			p.setCreatedAt(createdAt.toLocalDateTime());
		Timestamp updatedAt = rs.getTimestamp("updatedAt");
		if (updatedAt != null)
			p.setUpdatedAt(updatedAt.toLocalDateTime());
		Timestamp startsAt = rs.getTimestamp("startsAt");
		if (startsAt != null)
			p.setStartsAt(startsAt.toLocalDateTime());
		Timestamp endsAt = rs.getTimestamp("endsAt");
		if (endsAt != null)
			p.setEndsAt(endsAt.toLocalDateTime());
		return p;
	}
}
