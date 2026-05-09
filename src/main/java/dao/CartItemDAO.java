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
import beans.Product;
import utils.DBConnection;

public class CartItemDAO implements DAO<CartItem> {

	public long insert(CartItem cartItem) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			return insert(conn, cartItem);
		}
	}

	public long insert(Connection conn, CartItem cartItem) throws SQLException {
		String sql = "INSERT INTO cart_item (cartId, productId, quantity, selected, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, cartItem.getCartId());
			ps.setLong(2, cartItem.getProductId());
			ps.setInt(3, cartItem.getQuantity());
			ps.setInt(4, cartItem.isSelected() ? 1 : 0);
			ps.setTimestamp(5, Timestamp.valueOf(cartItem.getCreatedAt()));
			ps.setTimestamp(6, cartItem.getUpdatedAt() != null ? Timestamp.valueOf(cartItem.getUpdatedAt()) : null);

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
		String sql = "UPDATE cart_item SET cartId=?, productId=?, quantity=?, selected=?, createdAt=?, updatedAt=? WHERE id=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, cartItem.getCartId());
			ps.setLong(2, cartItem.getProductId());
			ps.setInt(3, cartItem.getQuantity());
			ps.setInt(4, cartItem.isSelected() ? 1 : 0);
			ps.setTimestamp(5, Timestamp.valueOf(cartItem.getCreatedAt()));
			ps.setTimestamp(6, cartItem.getUpdatedAt() != null ? Timestamp.valueOf(cartItem.getUpdatedAt()) : null);
			ps.setLong(7, cartItem.getId());

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
		String sql = """
				SELECT ci.*, p.name AS product_name, p.price AS product_price,
				       p.discount AS product_discount, p.quantity AS product_quantity,
				       p.imageName AS product_imageName
				FROM cart_item ci
				JOIN product p ON p.id = ci.productId
				WHERE ci.cartId = ? AND ci.productId = ?
				""";
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
		try {
			ci.setSelected(rs.getInt("selected") == 1);
		} catch (SQLException e) {
			ci.setSelected(true);
		}
		ci.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
		Timestamp updatedAt = rs.getTimestamp("updatedAt");
		if (updatedAt != null)
			ci.setUpdatedAt(updatedAt.toLocalDateTime());
		
		// Handle additional product information from JOIN queries
		try {
			// Check if product name column exists (from JOIN query)
			String productName = rs.getString("product_name");
			if (productName != null) {
				Product product = new Product();
				product.setName(productName);
				
				// Try to get other product info if available
				try {
					double price = rs.getDouble("product_price");
					if (!rs.wasNull()) product.setPrice(price);
				} catch (Exception e) {
					// Column might not exist in all queries
				}
				
				try {
					double discount = rs.getDouble("product_discount");
					if (!rs.wasNull()) product.setDiscount(discount);
				} catch (Exception e) {
					// Column might not exist in all queries
				}
				
				try {
					int productQuantity = rs.getInt("product_quantity");
					if (!rs.wasNull()) product.setQuantity(productQuantity);
				} catch (Exception e) {
					// Column might not exist in all queries
				}
				
				try {
					String imageName = rs.getString("product_imageName");
					if (imageName != null) product.setImageName(imageName);
				} catch (Exception e) {
					// Column might not exist in all queries
				}
				
				ci.setProduct(product);
			}
		} catch (Exception e) {
			// Product columns might not be available in all queries
			// This is normal for basic queries
		}
		
		return ci;
	}

	public List<CartItem> getSelectedByCartId(long cartId) {
		List<CartItem> list = new ArrayList<>();
		String sql = """
				SELECT ci.*, p.name AS product_name, p.price AS product_price,
				       p.discount AS product_discount, p.quantity AS product_quantity,
				       p.imageName AS product_imageName
				FROM cart_item ci
				JOIN product p ON p.id = ci.productId
				WHERE ci.cartId = ? AND ci.selected = 1
				ORDER BY ci.createdAt DESC
				""";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, cartId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) list.add(mapResultSetToCartItem(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void updateSelected(long cartItemId, boolean selected) {
		String sql = "UPDATE cart_item SET selected = ? WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, selected ? 1 : 0);
			ps.setLong(2, cartItemId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateSelectedByCartId(long cartId, boolean selected) {
		String sql = "UPDATE cart_item SET selected = ? WHERE cartId = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, selected ? 1 : 0);
			ps.setLong(2, cartId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getSelectedCountByCartId(long cartId) {
		String sql = "SELECT COUNT(*) FROM cart_item WHERE cartId = ? AND selected = 1";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, cartId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getSelectedQuantityByCartId(long cartId) {
		String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_item WHERE cartId = ? AND selected = 1";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, cartId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
