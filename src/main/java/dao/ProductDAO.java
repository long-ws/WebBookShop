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

	public void updateQuantityAndTotalBuy(Connection conn, long productId, int quantity, int totalBuy) throws SQLException {
		String sql = "UPDATE product SET quantity = ?, totalBuy = ?, updatedAt = NOW() WHERE id = ? AND isDeleted = 0";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, quantity);
			ps.setInt(2, totalBuy);
			ps.setLong(3, productId);
			if (ps.executeUpdate() == 0) {
				throw new SQLException("Update product quantity failed");
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

	public List<String> getAllPublishers() {
	    List<String> list = new ArrayList<>();
	    String sql = "SELECT DISTINCT publisher FROM product WHERE isDeleted = 0 AND publisher IS NOT NULL AND publisher != '' ORDER BY publisher";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            list.add(rs.getString("publisher"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	public List<Integer> getYearsByCategoryId(long categoryId) {
	    List<Integer> list = new ArrayList<>();
	    String sql =
	        "SELECT DISTINCT p.yearPublishing FROM product_category pc " +
	        "JOIN product p ON pc.productId = p.id " +
	        "WHERE pc.categoryId = ? AND p.isDeleted = 0 AND p.yearPublishing > 0 " +
	        "ORDER BY p.yearPublishing DESC";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setLong(1, categoryId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(rs.getInt("yearPublishing"));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	public List<Integer> getAllYears() {
	    List<Integer> list = new ArrayList<>();
	    String sql = "SELECT DISTINCT yearPublishing FROM product WHERE isDeleted = 0 AND yearPublishing > 0 ORDER BY yearPublishing DESC";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            list.add(rs.getInt("yearPublishing"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	public int countByFilters(String filters) {
	    String sql = "SELECT COUNT(id) FROM product WHERE isDeleted = 0";
	    if (filters != null && !filters.isEmpty()) {
	        sql += " AND " + filters;
	    }
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) return rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}

	public List<Product> getOrderedPartByFilters(int limit, int offset, String orderBy, String orderDir, String filters) {
	    List<Product> list = new ArrayList<>();
	    String sql = "SELECT * FROM product WHERE isDeleted = 0";
	    if (filters != null && !filters.isEmpty()) {
	        sql += " AND " + filters;
	    }
	    sql += " ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, limit);
	        ps.setInt(2, offset);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) list.add(mapResultSetToProduct(rs));
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

	public List<Product> getByAdvancedQuery(String query, int limit, int offset) {
		List<Product> list = new ArrayList<>();

		if (query == null || query.trim().isEmpty()) {
			return list;
		}

		query = query.trim();
		if (query.length() > 100) {
			query = query.substring(0, 100);
		}

		String[] keywords = query.split("\\s+");
		List<String> validKeywords = new ArrayList<>();
		for (String keyword : keywords) {
			String cleanKeyword = keyword.trim();
			if (!cleanKeyword.isEmpty() && cleanKeyword.length() <= 50) {
				validKeywords.add(cleanKeyword);
			}
		}

		if (validKeywords.isEmpty()) {
			return list;
		}

		StringBuilder whereClause = new StringBuilder("isDeleted = 0 AND (");
		StringBuilder conditions = new StringBuilder();
		for (int i = 0; i < validKeywords.size(); i++) {
			if (i > 0) conditions.append(" OR ");
			conditions.append("(LOWER(name) LIKE ? OR LOWER(author) LIKE ? OR LOWER(publisher) LIKE ? OR LOWER(description) LIKE ?)");
		}
		whereClause.append(conditions).append(")");

		StringBuilder nameScore = new StringBuilder("(");
		for (int i = 0; i < validKeywords.size(); i++) {
			if (i > 0) nameScore.append(" + ");
			nameScore.append("(CASE WHEN LOWER(name) LIKE ? THEN 1 ELSE 0 END)");
		}
		nameScore.append(")");

		String sql = "SELECT * FROM product WHERE " + whereClause
				+ " ORDER BY " + nameScore + " DESC, name ASC"
				+ " LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			int paramIndex = 1;

			for (String keyword : validKeywords) {
				String term = "%" + keyword.toLowerCase() + "%";
				ps.setString(paramIndex++, term);
				ps.setString(paramIndex++, term);
				ps.setString(paramIndex++, term);
				ps.setString(paramIndex++, term);
			}

			for (String keyword : validKeywords) {
				ps.setString(paramIndex++, "%" + keyword.toLowerCase() + "%");
			}

			ps.setInt(paramIndex++, limit);
			ps.setInt(paramIndex++, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToProduct(rs));
				}
			}

		} catch (SQLException e) {
			System.err.println("SQL Error in getByAdvancedQuery: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected Error in getByAdvancedQuery: " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	public int countByAdvancedQuery(String query) {
		// Enhanced input validation (same as getByAdvancedQuery)
		if (query == null || query.trim().isEmpty()) {
			return 0;
		}
		
		// Clean and validate query (same as getByAdvancedQuery)
		query = query.trim();
		if (query.length() > 100) {
			query = query.substring(0, 100);
		}
		
		String[] keywords = query.split("\\s+");
		
		// Remove empty keywords and limit number of keywords (same as getByAdvancedQuery)
		List<String> validKeywords = new ArrayList<>();
		for (String keyword : keywords) {
			String cleanKeyword = keyword.trim();
			if (!cleanKeyword.isEmpty() && cleanKeyword.length() <= 50) {
				validKeywords.add(cleanKeyword);
			}
		}
		
		if (validKeywords.isEmpty()) {
			return 0;
		}
		
		// Build SQL: each keyword must match at least one field (OR between keywords, AND across keywords)
		StringBuilder sql = new StringBuilder("SELECT COUNT(id) FROM product WHERE isDeleted = 0 AND (");
		StringBuilder conditions = new StringBuilder();
		for (int i = 0; i < validKeywords.size(); i++) {
			if (i > 0) conditions.append(" OR ");
			conditions.append("(name LIKE ? OR author LIKE ? OR publisher LIKE ? OR description LIKE ?)");
		}
		sql.append(conditions.toString()).append(")");
		
		try (Connection conn = DBConnection.getConnection(); 
			 PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			
			int paramIndex = 1;
			for (String keyword : validKeywords) {
				String searchTerm = "%" + keyword + "%";
				ps.setString(paramIndex++, searchTerm);
				ps.setString(paramIndex++, searchTerm);
				ps.setString(paramIndex++, searchTerm);
				ps.setString(paramIndex++, searchTerm);
			}

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}

		} catch (SQLException e) {
			System.err.println("SQL Error in countByAdvancedQuery: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected Error in countByAdvancedQuery: " + e.getMessage());
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

	public Product mapResultSetToProduct(ResultSet rs) throws SQLException {
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
		String img = rs.getString("imageName");
		p.setImageName(img);
		System.out.println("[ProductDAO] mapResultSetToProduct: id=" + p.getId() + ", imageName=" + img);
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

	public List<ProductSuggestion> getSearchSuggestions(String query, int limit) {
		List<ProductSuggestion> suggestions = new ArrayList<>();
		if (query == null || query.trim().isEmpty()) {
			return suggestions;
		}
		query = query.trim();
		if (query.length() > 100) {
			query = query.substring(0, 100);
		}
		String[] keywords = query.split("\\s+");
		List<String> validKeywords = new ArrayList<>();
		for (String kw : keywords) {
			String clean = kw.trim();
			if (!clean.isEmpty() && clean.length() <= 50) {
				validKeywords.add(clean);
			}
		}
		if (validKeywords.isEmpty()) {
			return suggestions;
		}

		StringBuilder whereClause = new StringBuilder("isDeleted = 0 AND (");
		StringBuilder conditions = new StringBuilder();
		for (int i = 0; i < validKeywords.size(); i++) {
			if (i > 0) conditions.append(" OR ");
			conditions.append("(LOWER(name) LIKE ? OR LOWER(author) LIKE ? OR LOWER(publisher) LIKE ? OR LOWER(description) LIKE ?)");
		}
		whereClause.append(conditions).append(")");

		StringBuilder nameScore = new StringBuilder("(");
		for (int i = 0; i < validKeywords.size(); i++) {
			if (i > 0) nameScore.append(" + ");
			nameScore.append("(CASE WHEN LOWER(name) LIKE ? THEN 1 ELSE 0 END)");
		}
		nameScore.append(")");

		String sql = "SELECT * FROM product WHERE " + whereClause
				+ " ORDER BY " + nameScore + " DESC, name ASC"
				+ " LIMIT ?";

		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			int paramIndex = 1;
			for (String keyword : validKeywords) {
				String term = "%" + keyword.toLowerCase() + "%";
				ps.setString(paramIndex++, term);
				ps.setString(paramIndex++, term);
				ps.setString(paramIndex++, term);
				ps.setString(paramIndex++, term);
			}
			for (String keyword : validKeywords) {
				ps.setString(paramIndex++, "%" + keyword.toLowerCase() + "%");
			}
			ps.setInt(paramIndex, limit);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Product p = mapResultSetToProduct(rs);
					suggestions.add(new ProductSuggestion(p, validKeywords));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return suggestions;
	}

	public static class ProductSuggestion {
		private final long id;
		private final String name;
		private final String author;
		private final double price;
		private final double discount;
		private final String imageName;
		private final String highlightedName;
		private final String highlightedAuthor;

		public ProductSuggestion(Product p, List<String> keywords) {
			this.id = p.getId();
			this.name = p.getName();
			this.author = p.getAuthor() != null ? p.getAuthor() : "";
			this.price = p.getPrice();
			this.discount = p.getDiscount();
			this.imageName = p.getImageName();
			this.highlightedName = highlightMatches(p.getName(), keywords);
			this.highlightedAuthor = highlightMatches(this.author, keywords);
		}

		private static String highlightMatches(String text, List<String> keywords) {
			if (text == null || text.isEmpty()) return text;
			String result = text;
			for (String kw : keywords) {
				if (kw.isEmpty()) continue;
				String lowerText = result.toLowerCase();
				String lowerKw = kw.toLowerCase();
				int idx = lowerText.indexOf(lowerKw);
				if (idx >= 0) {
					String before = result.substring(0, idx);
					String match = result.substring(idx, idx + kw.length());
					String after = result.substring(idx + kw.length());
					result = before + "\u0000" + match + "\u0001" + after;
				}
			}
			return result.replace("\u0000", "<mark class=\"search-highlight\">").replace("\u0001", "</mark>");
		}

		public long getId() { return id; }
		public String getName() { return name; }
		public String getAuthor() { return author; }
		public double getPrice() { return price; }
		public double getDiscount() { return discount; }
		public String getImageName() { return imageName; }
		public String getHighlightedName() { return highlightedName; }
		public String getHighlightedAuthor() { return highlightedAuthor; }
		public double getFinalPrice() {
			return discount > 0 ? price * (100 - discount) / 100 : price;
		}
	}
}
