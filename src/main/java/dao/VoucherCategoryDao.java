package dao;

import beans.VoucherCategory;
import utils.DBConnection;

import java.sql.*;

public class VoucherCategoryDao {
    public long insert(VoucherCategory o){
        String sql = "INSERT INTO voucher_category (voucher_id, category_id) " +
                "VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, o.getVoucherId());
            ps.setLong(2, o.getCategoryId());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Insert voucher failed, no rows affected");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                throw new SQLException("Insert voucher failed, no ID obtained");
            }
        }catch(SQLException e){
            e.printStackTrace();
            return 0;
        }
    }
}
