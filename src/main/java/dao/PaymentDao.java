package dao;

import beans.vnpay.Payment;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDao {
    public long createPayment(Payment p) throws SQLException {
        String sql = "INSERT INTO payments (order_id, user_id, vnp_TxnRef, amount, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, p.getOrderId());
            ps.setLong(2, p.getUserId());
            ps.setString(3, p.getVnpTxnRef());
            ps.setDouble(4, p.getAmount());
            ps.setInt(5, p.getStatus());
            ps.setTimestamp(6, p.getCreatedAt());

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
    public Payment getInitPayment(String vnpTxnRef){
        String sql = "SELECT order_id, user_id, vnp_TxnRef, amount, status, created_at " +
                "FROM payments WHERE vnp_TxnRef = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

                    return p;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public void updatePaymentResult(Payment p) {
        String sql = "UPDATE payments SET vnp_ResponseCode = ?, vnp_TransactionNo = ?, " +
                "bank_code = ?, pay_date = ?, status = ? " +
                "WHERE vnp_TxnRef = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
}
