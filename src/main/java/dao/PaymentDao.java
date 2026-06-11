package dao;

import beans.vnpay.Payment;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDao {
	public long createPayment(Connection con, Payment p) throws SQLException {
		String sql = "INSERT INTO payments (order_id, user_id, vnp_TxnRef, amount, status, created_at, expired_at) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setLong(1, p.getOrderId());
			ps.setLong(2, p.getUserId());
			ps.setString(3, p.getVnpTxnRef());
			ps.setDouble(4, p.getAmount());
			ps.setInt(5, p.getStatus());
			ps.setTimestamp(6, p.getCreatedAt());
			ps.setTimestamp(7, p.getExpiredAt());

			int rows = ps.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Không có dòng nào được thêm.");
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new SQLException("Không lấy được ID.");
			}
		}
	}

	public Payment getInitPayment(String vnpTxnRef) {
		String sql = "SELECT order_id, user_id, vnp_TxnRef, amount, status, created_at, expired_at "
				+ "FROM payments WHERE vnp_TxnRef = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, vnpTxnRef);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Payment p = new Payment();

					p.setOrderId(rs.getLong("order_id"));
					p.setUserId(rs.getLong("user_id"));
					p.setVnpTxnRef(rs.getString("vnp_TxnRef"));
					p.setAmount(rs.getDouble("amount"));
					p.setStatus(rs.getInt("status"));
					p.setCreatedAt(rs.getTimestamp("created_at"));
					p.setExpiredAt(rs.getTimestamp("expired_at"));

					return p;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Payment getInitPayment(long orderId) {
		String sql = "SELECT order_id, user_id, vnp_TxnRef, amount, status, created_at, expired_at "
				+ "FROM payments WHERE order_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, orderId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Payment p = new Payment();

					p.setOrderId(rs.getLong("order_id"));
					p.setUserId(rs.getLong("user_id"));
					p.setVnpTxnRef(rs.getString("vnp_TxnRef"));
					p.setAmount(rs.getDouble("amount"));
					p.setStatus(rs.getInt("status"));
					p.setCreatedAt(rs.getTimestamp("created_at"));
					p.setExpiredAt(rs.getTimestamp("expired_at"));

					return p;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Payment getInitPaymentByOrderId(long orderId) {
		String sql = "SELECT order_id, user_id, vnp_TxnRef, amount, status, created_at, "
				+ "vnp_ResponseCode, vnp_TransactionNo, bank_code, pay_date " + "FROM payments WHERE order_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, orderId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Payment p = new Payment();
					p.setOrderId(rs.getLong("order_id"));
					p.setUserId(rs.getLong("user_id"));
					p.setVnpTxnRef(rs.getString("vnp_TxnRef"));
					p.setAmount(rs.getDouble("amount"));
					p.setStatus(rs.getInt("status"));
					p.setCreatedAt(rs.getTimestamp("created_at"));
					p.setVnpResponseCode(rs.getString("vnp_ResponseCode"));
					p.setVnpTransactionNo(rs.getString("vnp_TransactionNo"));
					p.setBankCode(rs.getString("bank_code"));
					p.setPayDate(rs.getTimestamp("pay_date"));
					return p;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updatePaymentResult(Payment p) {
		String sql = "UPDATE payments SET vnp_ResponseCode = ?, vnp_TransactionNo = ?, "
				+ "bank_code = ?, pay_date = ?, status = ? " + "WHERE vnp_TxnRef = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, p.getVnpResponseCode());
			ps.setString(2, p.getVnpTransactionNo());
			ps.setString(3, p.getBankCode());
			ps.setTimestamp(4, p.getPayDate());
			ps.setInt(5, p.getStatus());
			ps.setString(6, p.getVnpTxnRef());

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Payment getPaymentByOrderId(long oId) {
		String sql = "SELECT id, order_id, user_id, vnp_TxnRef, amount, status, created_at, "
				+ "expired_at, vnp_ResponseCode, vnp_TransactionNo, bank_code, pay_date, is_expired "
				+ "FROM payments WHERE order_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, oId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Payment p = new Payment();
					p.setId(rs.getLong("id"));
					p.setOrderId(rs.getLong("order_id"));
					p.setUserId(rs.getLong("user_id"));
					p.setVnpTxnRef(rs.getString("vnp_TxnRef"));
					p.setAmount(rs.getDouble("amount"));
					p.setStatus(rs.getInt("status"));
					p.setCreatedAt(rs.getTimestamp("created_at"));
					p.setExpiredAt(rs.getTimestamp("expired_at"));
					p.setVnpResponseCode(rs.getString("vnp_ResponseCode"));
					p.setVnpTransactionNo(rs.getString("vnp_TransactionNo"));
					p.setBankCode(rs.getString("bank_code"));
					p.setPayDate(rs.getTimestamp("pay_date"));
					p.setExpired(rs.getBoolean("is_expired"));
					return p;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Payment getPaymentById(long id) {
		String sql = "SELECT id, order_id, user_id, vnp_TxnRef, amount, status, created_at, "
				+ "expired_at, vnp_ResponseCode, vnp_TransactionNo, bank_code, pay_date, is_expired "
				+ "FROM payments WHERE id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Payment p = new Payment();
					p.setId(rs.getLong("id"));
					p.setOrderId(rs.getLong("order_id"));
					p.setUserId(rs.getLong("user_id"));
					p.setVnpTxnRef(rs.getString("vnp_TxnRef"));
					p.setAmount(rs.getDouble("amount"));
					p.setStatus(rs.getInt("status"));
					p.setCreatedAt(rs.getTimestamp("created_at"));
					p.setExpiredAt(rs.getTimestamp("expired_at"));
					p.setVnpResponseCode(rs.getString("vnp_ResponseCode"));
					p.setVnpTransactionNo(rs.getString("vnp_TransactionNo"));
					p.setBankCode(rs.getString("bank_code"));
					p.setPayDate(rs.getTimestamp("pay_date"));
					p.setExpired(rs.getBoolean("is_expired"));
					return p;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isPaymentExpired(long id) {
		String sqlCheck = "SELECT p.expired_at, p.order_id FROM payments p "
				+ "LEFT JOIN orders o ON o.id = p.order_id " + "WHERE (p.id = ? OR o.id = ?) AND p.status = 0";

		String sqlUpdatePayment = "UPDATE payments SET status = 2, is_expired = 1 WHERE order_id = ?";
		String sqlUpdateOrder = "UPDATE orders SET status = 7 WHERE id = ?";

		Connection con = null;
		try {
			con = DBConnection.getConnection();

			try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
				psCheck.setLong(1, id);
				psCheck.setLong(2, id);

				try (ResultSet rs = psCheck.executeQuery()) {
					if (rs.next()) {
						Timestamp expiredAt = rs.getTimestamp("expired_at");
						long actualOrderId = rs.getLong("order_id");
						if (expiredAt == null) {
							return false;
						}

						Timestamp now = new Timestamp(System.currentTimeMillis());

						if (now.after(expiredAt)) {

							con.setAutoCommit(false);

							try (PreparedStatement psPay = con.prepareStatement(sqlUpdatePayment)) {
								psPay.setLong(1, actualOrderId);
								psPay.executeUpdate();
							}

							try (PreparedStatement psOrder = con.prepareStatement(sqlUpdateOrder)) {
								psOrder.setLong(1, actualOrderId);
								psOrder.executeUpdate();
							}
							con.commit();
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
