package dao;

import beans.vnpay.Refund;
import utils.DBConnection;

import java.sql.*;

public class RefundDao {
    public long createRefund(Refund r) {
        String sql = "INSERT INTO refunds (order_id, user_id, create_at, vnp_RequestId, vnp_TransactionType, " +
                "vnp_TxnRef, amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, r.getOrderId());
            ps.setLong(2, r.getUserId());
            ps.setTimestamp(3, r.getCreateAt());
            ps.setString(4, r.getVnpRequestId());
            ps.setString(5, r.getVnpTransactionType());
            ps.setString(6, r.getVnpTxnRef());
            ps.setDouble(7, r.getAmount());

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
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateRefundResult(Refund r) {
        String sql = "UPDATE refunds r " +
                "JOIN payments p ON r.vnp_TxnRef = p.vnp_TxnRef " +
                "JOIN orders o ON p.order_id = o.id " +
                "SET r.vnp_ResponseCode = ?, r.vnp_TransactionNo = ?, r.bank_code = ?, " +
                "r.pay_date = ?, r.vnp_TransactionStatus = ?, p.status = 3, o.status = 7 " +
                "WHERE r.id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getVnpResponseCode());
            ps.setString(2, r.getVnpTransactionNo());
            ps.setString(3, r.getBankCode());
            ps.setTimestamp(4, r.getPayDate());
            ps.setString(5, r.getVnpTransactionStatus());
            ps.setLong(6, r.getId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getPendingRefundId(String vnpTxnRef, long vnpAmount) {
        String sql = "SELECT id FROM refunds WHERE vnp_TxnRef = ? AND amount = ? AND status = 3 LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, vnpTxnRef);
            ps.setLong(2, vnpAmount / 100);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
