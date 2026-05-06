package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.CartItem;
import utils.DBConnection;

public class CartItemDAO implements DAO<CartItem> {

	public long insert(CartItem cartItem) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			return insert(conn, cartItem);
		}
	}

	public long insert(Connection conn, CartItem cartItem) throws SQLException {
		String sql = "INSERT INTO cart_item (cartId, productId, quantity, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, cartItem.getCartId());
			ps.setLong(2, cartItem.getProductId());
			ps.setInt(3, cartItem.getQuantity());
			ps.setTimestamp(4, Timestamp.valueOf(cartItem.getCreatedAt()));
			ps.setTimestamp(5, cartItem.getUpdatedAt() != null ? Timestamp.valueOf(cartItem.getUpdatedAt()) : null);

			if (ps.executeUpdate() == 0)
				throw new SQLException("Insert cart_item failed");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
				throw new SQLException("No ID obtained");
			}
		}
	}

	public void update(CartItem cartItem) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			update(conn, cartItem);
		}
	}

	public void update(Connection conn, CartItem cartItem) throws SQLException {
		String sql = "UPDATE cart_item SET cartId=?, productId=?, quantity=?, createdAt=?, updatedAt=? WHERE id=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, cartItem.getCartId());
			ps.setLong(2, cartItem.getProductId());
			ps.setInt(3, cartItem.getQuantity());
			ps.setTimestamp(4, Timestamp.valueOf(cartItem.getCreatedAt()));
			ps.setTimestamp(5, cartItem.getUpdatedAt() != null ? Timestamp.valueOf(cartItem.getUpdatedAt()) : null);
			ps.setLong(6, cartItem.getId());

			if (ps.executeUpdate() == 0)
				throw new SQLException("Update cart_item failed");
		}
	}
	
	public void delete(long id) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			delete(conn, id);
		}
	}
	
	public void delete(Connection conn, long id) throws SQLException {
		String sql = "DELETE FROM cart_item WHERE id=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			if (ps.executeUpdate() == 0)
				throw new SQLException("Delete cart_item failed");
		}
	}

	public CartItem getById(long id) {
		String sql = "SELECT * FROM cart_item WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToCartItem(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<CartItem> getAll() {
		List<CartItem> list = new ArrayList<>();
		String sql = "SELECT * FROM cart_item";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSetToCartItem(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<CartItem> getPart(int limit, int offset) {
		List<CartItem> list = new ArrayList<>();
		String sql = "SELECT * FROM cart_item LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapResultSetToCartItem(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<CartItem> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<CartItem> list = new ArrayList<>();
		String sql = "SELECT * FROM cart_item ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapResultSetToCartItem(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<CartItem> getByCartId(long cartId) {
		try (Connection conn = DBConnection.getConnection()) {
			return getByCartId(conn, cartId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<CartItem> getByCartId(Connection conn, long cartId) throws SQLException {
		List<CartItem> list = new ArrayList<>();
		String sql = """
				SELECT ci.*, p.name AS product_name, p.price AS product_price,
				       p.discount AS product_discount, p.quantity AS product_quantity,
				       p.imageName AS product_imageName
				FROM cart_item ci
				JOIN product p ON p.id = ci.productId
				WHERE ci.cartId = ?
				ORDER BY ci.createdAt DESC
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, cartId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToCartItem(rs));
				}
			}
		}
		return list;
	}

	public CartItem getByCartIdAndProductId(long cartId, long productId) {
		String sql = "SELECT * FROM cart_item WHERE cartId = ? AND productId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, cartId);
			ps.setLong(2, productId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToCartItem(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int sumQuantityByUserId(long userId) {
		String sql = "SELECT SUM(ci.quantity) FROM cart_item ci JOIN cart c ON c.id = ci.cartId WHERE c.userId = ?";
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

	private CartItem mapResultSetToCartItem(ResultSet rs) throws SQLException {
		CartItem ci = new CartItem();
		ci.setId(rs.getLong("id"));
		ci.setCartId(rs.getLong("cartId"));
		ci.setProductId(rs.getLong("productId"));
		ci.setQuantity(rs.getInt("quantity"));
		ci.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
		Timestamp updatedAt = rs.getTimestamp("updatedAt");
		if (updatedAt != null)
			ci.setUpdatedAt(updatedAt.toLocalDateTime());
		return ci;
	}
}
