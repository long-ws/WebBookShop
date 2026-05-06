package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.OrderItem;
import utils.DBConnection;

public class OrderItemDAO implements DAO<OrderItem> {

	// ================== INSERT ==================
	public long insert(OrderItem item) {
		try (Connection conn = DBConnection.getConnection()) {
			return insert(conn, item); // gọi version nhận Connection
		} catch (SQLException e) {
			throw new RuntimeException("Insert OrderItem failed", e);
		}
	}

	// INSERT (dùng trong transaction)
	public long insert(Connection conn, OrderItem item) throws SQLException {
		String sql = "INSERT INTO order_item (orderId, productId, price, discount, quantity, createdAt, updatedAt) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, item.getOrderId());
			ps.setLong(2, item.getProductId());
			ps.setDouble(3, item.getPrice());
			ps.setDouble(4, item.getDiscount());
			ps.setInt(5, item.getQuantity());
			ps.setTimestamp(6, Timestamp.valueOf(item.getCreatedAt()));
			ps.setTimestamp(7, item.getUpdatedAt() != null ? Timestamp.valueOf(item.getUpdatedAt()) : null);

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Insert order_item failed, no rows affected");
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new SQLException("Insert order_item failed, no ID obtained");
			}
		}
	}

	// ================== UPDATE ==================
	public void update(OrderItem item) throws SQLException {
		String sql = "UPDATE order_item SET orderId=?, productId=?, price=?, discount=?, quantity=?, createdAt=?, updatedAt=? "
				+ "WHERE id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, item.getOrderId());
			ps.setLong(2, item.getProductId());
			ps.setDouble(3, item.getPrice());
			ps.setDouble(4, item.getDiscount());
			ps.setInt(5, item.getQuantity());
			ps.setTimestamp(6, Timestamp.valueOf(item.getCreatedAt()));
			ps.setTimestamp(7, item.getUpdatedAt() != null ? Timestamp.valueOf(item.getUpdatedAt()) : null);
			ps.setLong(8, item.getId());

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Update order_item failed, item not found");
			}
		}
	}

	// ================== DELETE ==================
	public void delete(long id) throws SQLException {
		String sql = "DELETE FROM order_item WHERE id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Delete order_item failed, item not found");
			}
		}
	}

	// ================== GET BY ID ==================
	public OrderItem getById(long id) {
		String sql = "SELECT * FROM order_item WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToOrderItem(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ================== GET ALL ==================
	public List<OrderItem> getAll() {
		List<OrderItem> list = new ArrayList<>();
		String sql = "SELECT * FROM order_item";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSetToOrderItem(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// ================== GET PART ==================
	public List<OrderItem> getPart(int limit, int offset) {
		List<OrderItem> list = new ArrayList<>();
		String sql = "SELECT * FROM order_item LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToOrderItem(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// ================== GET ORDERED PART ==================
	public List<OrderItem> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<OrderItem> list = new ArrayList<>();
		String sql = "SELECT * FROM order_item ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToOrderItem(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// ================== GET BY ORDER ID ==================
	public List<OrderItem> getByOrderId(long orderId) {
		List<OrderItem> list = new ArrayList<>();
		String sql = "SELECT * FROM order_item WHERE orderId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, orderId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToOrderItem(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// ================== GET PRODUCT NAMES BY ORDER ==================
	public List<String> getProductNamesByOrderId(long orderId) {
		List<String> names = new ArrayList<>();
		String sql = "SELECT name FROM product p JOIN order_item o ON p.id = o.productId WHERE o.orderId = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, orderId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					names.add(rs.getString("name"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return names;
	}

	// ================== BULK INSERT ==================
	public boolean bulkInsert(List<OrderItem> items) {
		String sql = "INSERT INTO order_item (orderId, productId, price, discount, quantity, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			conn.setAutoCommit(false);

			for (OrderItem item : items) {
				ps.setLong(1, item.getOrderId());
				ps.setLong(2, item.getProductId());
				ps.setDouble(3, item.getPrice());
				ps.setDouble(4, item.getDiscount());
				ps.setInt(5, item.getQuantity());
				ps.setTimestamp(6, Timestamp.valueOf(item.getCreatedAt()));
				ps.setTimestamp(7, item.getUpdatedAt() != null ? Timestamp.valueOf(item.getUpdatedAt()) : null);
				ps.addBatch();
			}

			int[] results = ps.executeBatch();
			for (int r : results) {
				if (r == Statement.EXECUTE_FAILED)
					throw new SQLException("Bulk insert failed for one or more items");
			}

			conn.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// ================== MAPPER ==================
	private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
		OrderItem item = new OrderItem();
		item.setId(rs.getLong("id"));
		item.setOrderId(rs.getLong("orderId"));
		item.setProductId(rs.getLong("productId"));
		item.setPrice(rs.getDouble("price"));
		item.setDiscount(rs.getDouble("discount"));
		item.setQuantity(rs.getInt("quantity"));
		item.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
		Timestamp updatedAt = rs.getTimestamp("updatedAt");
		if (updatedAt != null)
			item.setUpdatedAt(updatedAt.toLocalDateTime());
		return item;
	}
}
