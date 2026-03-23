package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.Order;
import utils.DBConnection;

public class OrderDAO implements DAO<Order> {

	public long insert(Order order) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			return insert(conn, order);
		}
	}

	public long insert(Connection conn, Order order) throws SQLException {
		String sql = "INSERT INTO orders (userId, status, deliveryMethod, deliveryPrice, createdAt, updatedAt) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, order.getUserId());
			ps.setInt(2, order.getStatus());
			ps.setInt(3, order.getDeliveryMethod());
			ps.setDouble(4, order.getDeliveryPrice());
			ps.setTimestamp(5, Timestamp.valueOf(order.getCreatedAt()));
			ps.setTimestamp(6, order.getUpdatedAt() != null ? Timestamp.valueOf(order.getUpdatedAt()) : null);

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Insert order failed, no rows affected");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
				throw new SQLException("Insert order failed, no ID obtained");
			}
		}
	}

	public void update(Order order) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			update(conn, order);
		}
	}

	public void update(Connection conn, Order order) throws SQLException {
		String sql = "UPDATE orders SET userId=?, status=?, deliveryMethod=?, deliveryPrice=?, createdAt=?, updatedAt=? "
				+ "WHERE id=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, order.getUserId());
			ps.setInt(2, order.getStatus());
			ps.setInt(3, order.getDeliveryMethod());
			ps.setDouble(4, order.getDeliveryPrice());
			ps.setTimestamp(5, Timestamp.valueOf(order.getCreatedAt()));
			ps.setTimestamp(6, order.getUpdatedAt() != null ? Timestamp.valueOf(order.getUpdatedAt()) : null);
			ps.setLong(7, order.getId());

			if (ps.executeUpdate() == 0)
				throw new SQLException("Update order failed, not found");
		}
	}

	public void delete(long id) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			delete(conn, id);
		}
	}

	public void delete(Connection conn, long id) throws SQLException {
		String sql = "DELETE FROM orders WHERE id=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			if (ps.executeUpdate() == 0)
				throw new SQLException("Delete order failed, not found");
		}
	}

	public Order getById(long id) {
		String sql = "SELECT * FROM orders WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToOrder(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Order> getAll() {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT * FROM orders";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSetToOrder(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Order> getPart(int limit, int offset) {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT * FROM orders LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToOrder(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Order> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT * FROM orders ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToOrder(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Order> getOrderedPartByUserId(long userId, int limit, int offset) {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT * FROM orders WHERE userId = ? ORDER BY createdAt DESC LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, userId);
			ps.setInt(2, limit);
			ps.setInt(3, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapResultSetToOrder(rs));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int count() {
		String sql = "SELECT COUNT(id) FROM orders";
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

	public int countByUserId(long userId) {
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

	public boolean cancelOrder(long id) {
		return updateStatus(id, 3);
	}

	public boolean confirm(long id) {
		return updateStatus(id, 2);
	}

	public boolean cancel(long id) {
		return updateStatus(id, 3);
	}

	public boolean reset(long id) {
		return updateStatus(id, 1);
	}

	private boolean updateStatus(long id, int status) {
		String sql = "UPDATE orders SET status = ?, updatedAt = NOW() WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			conn.setAutoCommit(false);
			ps.setInt(1, status);
			ps.setLong(2, id);

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Update order status failed, no rows affected");

			conn.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
		Order order = new Order();
		order.setId(rs.getLong("id"));
		order.setUserId(rs.getLong("userId"));
		order.setStatus(rs.getInt("status"));
		order.setDeliveryMethod(rs.getInt("deliveryMethod")); // <-- int
		order.setDeliveryPrice(rs.getDouble("deliveryPrice"));
		order.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
		Timestamp updatedAt = rs.getTimestamp("updatedAt");
		if (updatedAt != null)
			order.setUpdatedAt(updatedAt.toLocalDateTime());
		return order;
	}
}
