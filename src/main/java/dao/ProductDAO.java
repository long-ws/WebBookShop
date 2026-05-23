package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import beans.Product;
import dao.ProductDAO;
import dto.AdvancedSearchRequest;
import dto.AdvancedSearchResponse;
import dto.ProductDTO;
import dto.ProductSuggestionDTO;
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

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
		String sql = "SELECT * FROM product WHERE isDeleted = 0 ORDER BY " + orderBy + " " + orderDir
				+ " LIMIT ? OFFSET ? ";
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

	public List<Product> getOrderedPartByCategoryId(int limit, int offset, String orderBy, String orderDir,
			long categoryId) {

		List<Product> list = new ArrayList<>();
		String sql = "SELECT p.* FROM product_category pc " + "JOIN product p ON pc.productId = p.id "
				+ "WHERE pc.categoryId = ? AND p.isDeleted = 0 " + "ORDER BY p." + orderBy + " " + orderDir
				+ " LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
		String sql = "SELECT COUNT(pc.productId) " + "FROM product_category pc "
				+ "JOIN product p ON pc.productId = p.id " + "WHERE pc.categoryId = ? AND p.isDeleted = 0";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
		String sql = "SELECT p.* FROM product_category pc " + "JOIN product p ON pc.productId = p.id "
				+ "WHERE pc.categoryId = ? AND p.isDeleted = 0 " + "ORDER BY RAND() LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
		String sql = "SELECT DISTINCT p.publisher FROM product_category pc " + "JOIN product p ON pc.productId = p.id "
				+ "WHERE pc.categoryId = ? AND p.isDeleted = 0 " + "ORDER BY p.publisher";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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

		String sql = "SELECT COUNT(p.id) FROM product_category pc " + "JOIN product p ON pc.productId = p.id "
				+ "WHERE pc.categoryId = ? AND p.isDeleted = 0 AND " + filters;

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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

	public List<Product> getOrderedPartByCategoryIdAndFilters(int limit, int offset, String orderBy, String orderDir,
			long categoryId, String filters) {

		List<Product> list = new ArrayList<>();
		String sql = "SELECT p.* FROM product_category pc " + "JOIN product p ON pc.productId = p.id "
				+ "WHERE pc.categoryId = ? AND p.isDeleted = 0 AND " + filters + " ORDER BY p." + orderBy + " "
				+ orderDir + " LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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

	public List<ProductDTO> searchByName(String keyword) {
		List<ProductDTO> list = new ArrayList<>();
		if (keyword == null || keyword.trim().isEmpty()) {
			return list;
		}
		String sql = "SELECT id, name, imageName FROM product WHERE isDeleted = 0 AND name LIKE ? ORDER BY name LIMIT 5";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, "%" + keyword.trim() + "%");

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ProductDTO dto = new ProductDTO();
					dto.setId(rs.getLong("id"));
					dto.setName(rs.getString("name"));
					dto.setImageName(rs.getString("imageName"));
					list.add(dto);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
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
			if (i > 0)
				conditions.append(" OR ");
			conditions.append(
					"(LOWER(name) LIKE ? OR LOWER(author) LIKE ? OR LOWER(publisher) LIKE ? OR LOWER(description) LIKE ?)");
		}
		whereClause.append(conditions).append(")");

		StringBuilder nameScore = new StringBuilder("(");
		for (int i = 0; i < validKeywords.size(); i++) {
			if (i > 0)
				nameScore.append(" + ");
			nameScore.append("(CASE WHEN LOWER(name) LIKE ? THEN 1 ELSE 0 END)");
		}
		nameScore.append(")");

		String sql = "SELECT * FROM product WHERE " + whereClause + " ORDER BY " + nameScore + " DESC, name ASC"
				+ " LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

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
		if (query == null || query.trim().isEmpty()) {
			return 0;
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
			return 0;
		}

		StringBuilder sql = new StringBuilder("SELECT COUNT(id) FROM product WHERE isDeleted = 0 AND (");
		StringBuilder conditions = new StringBuilder();
		for (int i = 0; i < validKeywords.size(); i++) {
			if (i > 0)
				conditions.append(" OR ");
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

	public List<ProductSuggestionDTO> getSearchSuggestions(String query, int limit) {
		List<ProductSuggestionDTO> suggestions = new ArrayList<>();
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
			if (i > 0)
				conditions.append(" OR ");
			conditions.append(
					"(LOWER(name) LIKE ? OR LOWER(author) LIKE ? OR LOWER(publisher) LIKE ? OR LOWER(description) LIKE ?)");
		}
		whereClause.append(conditions).append(")");

		StringBuilder nameScore = new StringBuilder("(");
		for (int i = 0; i < validKeywords.size(); i++) {
			if (i > 0)
				nameScore.append(" + ");
			nameScore.append("(CASE WHEN LOWER(name) LIKE ? THEN 1 ELSE 0 END)");
		}
		nameScore.append(")");

		String sql = "SELECT * FROM product WHERE " + whereClause + " ORDER BY " + nameScore + " DESC, name ASC"
				+ " LIMIT ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
					suggestions.add(new ProductSuggestionDTO(
							p.getId(),
							p.getName(),
							p.getAuthor() != null ? p.getAuthor() : "",
							p.getPrice(),
							p.getDiscount(),
							p.getImageName(),
							highlightMatches(p.getName(), validKeywords),
							highlightMatches(p.getAuthor() != null ? p.getAuthor() : "", validKeywords),
							validKeywords));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return suggestions;
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

	public List<Integer> getYearsByCategoryId(long categoryId) {
		List<Integer> list = new ArrayList<>();
		String sql = "SELECT DISTINCT p.yearPublishing FROM product_category pc "
				+ "JOIN product p ON pc.productId = p.id "
				+ "WHERE pc.categoryId = ? AND p.isDeleted = 0 AND p.yearPublishing > 0 "
				+ "ORDER BY p.yearPublishing DESC";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

	private static String highlightMatches(String text, List<String> keywords) {
		if (text == null || text.isEmpty())
			return text;
		String result = text;
		for (String kw : keywords) {
			if (kw.isEmpty())
				continue;
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

	public AdvancedSearchResponse advancedSearch(AdvancedSearchRequest request) {
		AdvancedSearchResponse response = new AdvancedSearchResponse();

		List<String> conditions = new ArrayList<>();
		List<Object> params = new ArrayList<>();
		conditions.add("p.isDeleted = 0");

		if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
			String[] keywords = request.getKeyword().trim().split("\\s+");
			for (String kw : keywords) {
				if (!kw.isEmpty()) {
					conditions.add("(p.name LIKE ? OR p.author LIKE ? OR p.publisher LIKE ? OR p.description LIKE ?)");
					String likeKw = "%" + kw + "%";
					params.add(likeKw);
					params.add(likeKw);
					params.add(likeKw);
					params.add(likeKw);
				}
			}
		}

		if (request.getCategoryId() != null) {
			conditions.add("EXISTS (SELECT 1 FROM product_category pc WHERE pc.productId = p.id AND pc.categoryId = ?)");
			params.add(request.getCategoryId());
		}

		if (request.getAuthor() != null && !request.getAuthor().trim().isEmpty()) {
			conditions.add("p.author LIKE ?");
			params.add("%" + request.getAuthor().trim() + "%");
		}

		if (request.getPublisher() != null && !request.getPublisher().trim().isEmpty()) {
			conditions.add("p.publisher LIKE ?");
			params.add("%" + request.getPublisher().trim() + "%");
		}

		if (request.getMinPrice() != null && request.getMinPrice() > 0
				|| request.getMaxPrice() != null && request.getMaxPrice() < Integer.MAX_VALUE) {
			int minPrice = request.getMinPrice() != null ? request.getMinPrice() : 0;
			int maxPrice = request.getMaxPrice() != null ? request.getMaxPrice() : Integer.MAX_VALUE;
			conditions.add("p.price BETWEEN ? AND ?");
			params.add(minPrice);
			params.add(maxPrice);
		}

		if (request.getMinYear() != null || request.getMaxYear() != null) {
			int minYear = request.getMinYear() != null ? request.getMinYear() : AdvancedSearchRequest.DEFAULT_MIN_YEAR;
			int maxYear = request.getMaxYear() != null ? request.getMaxYear() : AdvancedSearchRequest.DEFAULT_MAX_YEAR;
			if (minYear > AdvancedSearchRequest.DEFAULT_MIN_YEAR || maxYear < AdvancedSearchRequest.DEFAULT_MAX_YEAR) {
				conditions.add("p.yearPublishing BETWEEN ? AND ?");
				params.add(minYear);
				params.add(maxYear);
			}
		}

		String whereClause = String.join(" AND ", conditions);

		int totalProducts = countAdvancedSearch(whereClause, params);

		int totalPages = totalProducts / request.getLimit();
		if (totalProducts % request.getLimit() != 0) {
			totalPages++;
		}

		int page = request.getPage();
		if (page < 1) page = 1;
		if (page > totalPages && totalPages > 0) page = totalPages;

		int offset = (page - 1) * request.getLimit();

		List<Product> products = getAdvancedSearchResults(whereClause, params,
				request.getSortBy(), request.getSortDir(), request.getLimit(), offset);

		response.setProducts(products);
		response.setTotalProducts(totalProducts);
		response.setTotalPages(totalPages);
		response.setCurrentPage(page);
		response.setLimit(request.getLimit());
		response.setKeyword(request.getKeyword());
		response.setCategoryId(request.getCategoryId());
		response.setSelectedCategoryName(request.getSelectedCategoryName());
		response.setAuthor(request.getAuthor());
		response.setPublisher(request.getPublisher());
		response.setMinPrice(request.getMinPrice());
		response.setMaxPrice(request.getMaxPrice());
		response.setMinYear(request.getMinYear());
		response.setMaxYear(request.getMaxYear());
		response.setSortBy(request.getSortBy());
		response.setSortDir(request.getSortDir());

		return response;
	}

	private int countAdvancedSearch(String whereClause, List<Object> params) {
		String sql = "SELECT COUNT(p.id) FROM product p WHERE " + whereClause;
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (int i = 0; i < params.size(); i++) {
				ps.setObject(i + 1, params.get(i));
			}
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private List<Product> getAdvancedSearchResults(String whereClause, List<Object> params,
			String sortBy, String sortDir, int limit, int offset) {
		List<Product> list = new ArrayList<>();

		String sortColumn = switch (sortBy) {
			case "name" -> "p.name";
			case "price" -> "p.price";
			case "yearPublishing" -> "p.yearPublishing";
			case "totalBuy" -> "p.totalBuy";
			default -> "p.totalBuy";
		};
		String sortDirection = "ASC".equalsIgnoreCase(sortDir) ? "ASC" : "DESC";

		String sql = "SELECT p.* FROM product p WHERE " + whereClause
				+ " ORDER BY " + sortColumn + " " + sortDirection
				+ " LIMIT ? OFFSET ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			for (int i = 0; i < params.size(); i++) {
				ps.setObject(i + 1, params.get(i));
			}
			ps.setInt(params.size() + 1, limit);
			ps.setInt(params.size() + 2, offset);

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
    public Map<Long, Integer> getQty(List<Long> productIds){
        Map<Long, Integer> map = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) {
            return map;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < productIds.size(); i++) {
            sb.append("?");
            if (i < productIds.size() - 1) {
                sb.append(", ");
            }
        }
        String placeholders = sb.toString();
        String sql = "SELECT id, quantity FROM product WHERE id IN (" + placeholders + ")";
        try(Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)){
            for (int i = 0; i < productIds.size(); i++) {
                ps.setLong(i + 1, productIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    int qty = rs.getInt("stock_quantity");
                    map.put(id, qty);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }
    public boolean updateQty(Map<Long, Integer> map) {
        if (map == null || map.isEmpty()) return true;
        String sql = "UPDATE product SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);

            for (Map.Entry<Long, Integer> entry : map.entrySet()) {
                long productId = entry.getKey();
                int qtyToDecrease = entry.getValue();

                ps.setInt(1, qtyToDecrease);
                ps.setLong(2, productId);
                ps.setInt(3, qtyToDecrease);

                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
