package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.ProductReview;
import utils.DBConnection;

public class ProductReviewDAO implements DAO<ProductReview> {

	public long insert(ProductReview review) throws SQLException {
		String sql = "INSERT INTO product_review "
				+ "(userId, productId, ratingScore, content, isShow, createdAt, updatedAt) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, review.getUserId());
			ps.setLong(2, review.getProductId());
			ps.setInt(3, review.getRatingScore());
			ps.setString(4, review.getContent());
			ps.setInt(5, review.getIsShow());
			ps.setTimestamp(6, Timestamp.valueOf(review.getCreatedAt()));
			ps.setTimestamp(7, review.getUpdatedAt() != null ? Timestamp.valueOf(review.getUpdatedAt()) : null);

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Insert product review failed, no rows affected");
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new SQLException("Insert product review failed, no ID obtained");
			}
		}
	}

	public void update(ProductReview review) throws SQLException {
		String sql = "UPDATE product_review " + "SET ratingScore = ?, content = ?, updatedAt = NOW() " + "WHERE id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, review.getRatingScore());
			ps.setString(2, review.getContent());
			ps.setLong(3, review.getId());

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Update product review failed, review not found");
			}
		}
	}

	public void delete(long id) throws SQLException {
		String sql = "DELETE FROM product_review WHERE id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Delete product review failed, review not found");
			}
		}
	}

	public ProductReview getById(long id) {
		String sql = "SELECT * FROM product_review WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToProductReview(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<ProductReview> getAll() {
		List<ProductReview> list = new ArrayList<>();
		String sql = "SELECT * FROM product_review";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSetToProductReview(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ProductReview> getPart(int limit, int offset) {
		List<ProductReview> list = new ArrayList<>();
		String sql = "SELECT * FROM product_review LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToProductReview(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<ProductReview> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<ProductReview> list = new ArrayList<>();
		String sql = "SELECT * FROM product_review ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToProductReview(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<ProductReview> getOrderedPartByProductId(int limit, int offset, String orderBy, String orderDir,
			long productId) {
		if (!orderDir.equalsIgnoreCase("ASC") && !orderDir.equalsIgnoreCase("DESC"))
			orderDir = "ASC";
		if (!orderBy.matches("[a-zA-Z0-9_]+"))
			orderBy = "id";

		String sql = "SELECT * FROM product_review WHERE productId=? ORDER BY " + orderBy + " " + orderDir
				+ " LIMIT ? OFFSET ?";
		List<ProductReview> list = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, productId);
			ps.setInt(2, limit);
			ps.setInt(3, offset);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				list.add(mapResultSetToProductReview(rs));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countByProductId(long productId) {
		String sql = "SELECT COUNT(id) FROM product_review WHERE productId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, productId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int sumRatingScoresByProductId(long productId) {
		String sql = "SELECT SUM(ratingScore) FROM product_review WHERE productId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, productId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int count() {
		String sql = "SELECT COUNT(id) FROM product_review";
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

	public boolean hide(long id) {
		return setShow(id, false);
	}

	public boolean show(long id) {
		return setShow(id, true);
	}

	private boolean setShow(long id, boolean show) {
		String sql = "UPDATE product_review SET isShow = ?, updatedAt = NOW() WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			conn.setAutoCommit(false);
			ps.setBoolean(1, show);
			ps.setLong(2, id);

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Update isShow failed, no rows affected");

			conn.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private ProductReview mapResultSetToProductReview(ResultSet rs) throws SQLException {
		ProductReview review = new ProductReview();
		review.setId(rs.getLong("id"));
		review.setUserId(rs.getLong("userId"));
		review.setProductId(rs.getLong("productId"));
		review.setRatingScore(rs.getInt("ratingScore"));
		review.setContent(rs.getString("content"));
		review.setIsShow(rs.getInt("isShow"));
		review.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
		Timestamp updatedAt = rs.getTimestamp("updatedAt");
		if (updatedAt != null)
			review.setUpdatedAt(updatedAt.toLocalDateTime());
		return review;
	}
}
