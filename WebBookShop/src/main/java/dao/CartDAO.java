package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.Cart;
import utils.DBConnection;

public class CartDAO implements DAO<Cart> {

	public long insert(Cart cart) throws SQLException {
		String sql = "INSERT INTO cart (userId, createdAt, updatedAt) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, cart.getUserId());
			ps.setTimestamp(2, Timestamp.valueOf(cart.getCreatedAt()));
			ps.setTimestamp(3, cart.getUpdatedAt() != null ? Timestamp.valueOf(cart.getUpdatedAt()) : null);

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Insert cart failed, no rows affected");
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new SQLException("Insert cart failed, no ID obtained");
			}
		}
	}

	public void update(Cart cart) throws SQLException {
		String sql = "UPDATE cart SET userId = ?, createdAt = ?, updatedAt = ? WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, cart.getUserId());
			ps.setTimestamp(2, Timestamp.valueOf(cart.getCreatedAt()));
			ps.setTimestamp(3, cart.getUpdatedAt() != null ? Timestamp.valueOf(cart.getUpdatedAt()) : null);
			ps.setLong(4, cart.getId());

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Update cart failed, cart not found");
			}
		}
	}

	public void delete(long id) {
	    try (Connection conn = DBConnection.getConnection()) {
	        delete(conn, id); // gọi version nhận Connection
	    } catch (SQLException e) {
	        throw new RuntimeException("Delete cart failed", e);
	    }
	}

	public void delete(Connection conn, long id) throws SQLException {
	    String sql = "DELETE FROM cart WHERE id = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setLong(1, id);
	        int rows = ps.executeUpdate();
	        if (rows == 0) {
	            throw new SQLException("Delete cart failed, cart not found");
	        }
	    }
	}


	public Cart getById(long id) {
		String sql = "SELECT * FROM cart WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToCart(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Cart> getAll() {
		List<Cart> list = new ArrayList<>();
		String sql = "SELECT * FROM cart";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSetToCart(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Cart> getPart(int limit, int offset) {
		List<Cart> list = new ArrayList<>();
		String sql = "SELECT * FROM cart LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToCart(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Cart> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<Cart> list = new ArrayList<>();
		String sql = "SELECT * FROM cart ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToCart(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public Cart getByUserId(long userId) {
		String sql = "SELECT * FROM cart WHERE userId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToCart(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int countCartItemQuantityByUserId(long userId) {
		String sql = "SELECT SUM(ci.quantity) FROM cart c JOIN cart_item ci ON c.id = ci.cartId WHERE c.userId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int countOrderByUserId(long userId) {
		String sql = "SELECT COUNT(id) FROM orders WHERE userId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int countOrderDeliverByUserId(long userId) {
		String sql = "SELECT COUNT(id) FROM orders WHERE userId = ? AND status = 1";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int countOrderReceivedByUserId(long userId) {
		String sql = "SELECT COUNT(id) FROM orders WHERE userId = ? AND status = 2";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private Cart mapResultSetToCart(ResultSet rs) throws SQLException {
		Cart cart = new Cart();
		cart.setId(rs.getLong("id"));
		cart.setUserId(rs.getLong("userId"));
		cart.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
		Timestamp updatedAt = rs.getTimestamp("updatedAt");
		if (updatedAt != null)
			cart.setUpdatedAt(updatedAt.toLocalDateTime());
		return cart;
	}
}
