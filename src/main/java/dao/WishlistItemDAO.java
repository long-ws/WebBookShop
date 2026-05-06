package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.WishlistItem;
import utils.DBConnection;

public class WishlistItemDAO implements DAO<WishlistItem> {

	public long insert(WishlistItem item) throws SQLException {
		String sql = "INSERT INTO wishlist_item (userId, productId, createdAt) VALUES (?, ?, NOW())";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, item.getUserId());
			ps.setLong(2, item.getProductId());

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Insert wishlist item failed, no rows affected");
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new SQLException("Insert wishlist item failed, no ID obtained");
			}
		}
	}

	public void update(WishlistItem item) throws SQLException {
		String sql = "UPDATE wishlist_item SET userId = ?, productId = ?, createdAt = ? WHERE id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, item.getUserId());
			ps.setLong(2, item.getProductId());
			ps.setTimestamp(3, Timestamp.valueOf(item.getCreatedAt()));
			ps.setLong(4, item.getId());

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Update wishlist item failed, item not found");
			}
		}
	}

	public void delete(long id) throws SQLException {
		String sql = "DELETE FROM wishlist_item WHERE id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Delete wishlist item failed, item not found");
			}
		}
	}

	public WishlistItem getById(long id) {
		String sql = "SELECT * FROM wishlist_item WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToWishlistItem(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<WishlistItem> getAll() {
		List<WishlistItem> list = new ArrayList<>();
		String sql = "SELECT * FROM wishlist_item";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSetToWishlistItem(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<WishlistItem> getPart(int limit, int offset) {
		List<WishlistItem> list = new ArrayList<>();
		String sql = "SELECT * FROM wishlist_item LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToWishlistItem(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<WishlistItem> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<WishlistItem> list = new ArrayList<>();
		String sql = "SELECT * FROM wishlist_item ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToWishlistItem(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<WishlistItem> getByUserId(long userId) {
		List<WishlistItem> list = new ArrayList<>();
		String sql = "SELECT * FROM wishlist_item WHERE userId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, userId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToWishlistItem(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countByUserIdAndProductId(long userId, long productId) {
		String sql = "SELECT COUNT(id) FROM wishlist_item WHERE userId = ? AND productId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, userId);
			ps.setLong(2, productId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private WishlistItem mapResultSetToWishlistItem(ResultSet rs) throws SQLException {
		WishlistItem item = new WishlistItem();
		item.setId(rs.getLong("id"));
		item.setUserId(rs.getLong("userId"));
		item.setProductId(rs.getLong("productId"));
		item.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
		return item;
	}
}
