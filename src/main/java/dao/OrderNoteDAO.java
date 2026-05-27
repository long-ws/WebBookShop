package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import beans.OrderNote;
import utils.DBConnection;

public class OrderNoteDAO {

	public long insert(OrderNote note) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			return insert(conn, note);
		}
	}

	public long insert(Connection conn, OrderNote note) throws SQLException {
		String sql = "INSERT INTO order_notes (order_id, user_id, note_type, content, is_read, created_at) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, note.getOrderId());
			if (note.getUserId() != null) {
				ps.setLong(2, note.getUserId());
			} else {
				ps.setNull(2, java.sql.Types.BIGINT);
			}
			ps.setString(3, note.getNoteType() != null ? note.getNoteType() : "");
			ps.setString(4, note.getContent() != null ? note.getContent() : "");
			ps.setBoolean(5, note.isRead());
			ps.setTimestamp(6, note.getCreatedAt() != null ? Timestamp.valueOf(note.getCreatedAt()) : null);

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Insert order note failed, no rows affected");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
				throw new SQLException("Insert order note failed, no ID obtained");
			}
		}
	}

	public List<OrderNote> getByOrderId(long orderId) throws SQLException {
		String sql = "SELECT * FROM order_notes WHERE order_id = ? ORDER BY created_at DESC";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, orderId);

			try (ResultSet rs = ps.executeQuery()) {
				List<OrderNote> notes = new ArrayList<>();
				while (rs.next()) {
					notes.add(extractFromResultSet(rs));
				}
				return notes;
			}
		}
	}

	public List<OrderNote> getUnreadByOrderId(long orderId) throws SQLException {
		String sql = "SELECT * FROM order_notes WHERE order_id = ? AND is_read = 0 ORDER BY created_at DESC";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, orderId);

			try (ResultSet rs = ps.executeQuery()) {
				List<OrderNote> notes = new ArrayList<>();
				while (rs.next()) {
					notes.add(extractFromResultSet(rs));
				}
				return notes;
			}
		}
	}

	public int countUnreadByOrderId(long orderId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM order_notes WHERE order_id = ? AND is_read = 0";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, orderId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
				return 0;
			}
		}
	}

	public boolean markAsRead(long id) throws SQLException {
		String sql = "UPDATE order_notes SET is_read = 1 WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			int rows = ps.executeUpdate();
			return rows > 0;
		}
	}

	public boolean markAllReadByOrderId(long orderId) throws SQLException {
		String sql = "UPDATE order_notes SET is_read = 1 WHERE order_id = ? AND is_read = 0";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, orderId);
			ps.executeUpdate();
			return true;
		}
	}

	public boolean delete(long id) throws SQLException {
		String sql = "DELETE FROM order_notes WHERE id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			int rows = ps.executeUpdate();
			return rows > 0;
		}
	}

	private OrderNote extractFromResultSet(ResultSet rs) throws SQLException {
		OrderNote note = new OrderNote();
		note.setId(rs.getLong("id"));
		note.setOrderId(rs.getLong("order_id"));
		long userId = rs.getLong("user_id");
		if (!rs.wasNull()) {
			note.setUserId(userId);
		}
		note.setNoteType(rs.getString("note_type"));
		note.setContent(rs.getString("content"));
		note.setRead(rs.getBoolean("is_read"));
		Timestamp createdAt = rs.getTimestamp("created_at");
		if (createdAt != null) {
			note.setCreatedAt(createdAt.toLocalDateTime());
		}
		Timestamp updatedAt = rs.getTimestamp("updated_at");
		if (updatedAt != null) {
			note.setUpdatedAt(updatedAt.toLocalDateTime());
		}
		return note;
	}
}
