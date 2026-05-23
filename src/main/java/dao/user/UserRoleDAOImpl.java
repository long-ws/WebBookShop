package dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRoleDAOImpl implements UserRoleDAO {

    private static final String COL_USER_ID = "user_id";
    private static final String COL_ROLE_ID = "role_id";

    private static final String DELETE_BY_USER_ID = String.format("DELETE FROM user_role_registry WHERE %s = ?", COL_USER_ID);
    private static final String INSERT_USER_ROLE = String.format("INSERT INTO user_role_registry (%s, %s) VALUES (?, ?)", COL_USER_ID, COL_ROLE_ID);
    private static final String FIND_ROLES_BY_USER_ID = String.format("SELECT %s FROM user_role_registry WHERE %s = ?", COL_ROLE_ID, COL_USER_ID);

    @Override
    public int delete(Connection conn, long userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_BY_USER_ID)) {
            ps.setLong(1, userId);
            return ps.executeUpdate();
        }
    }

    @Override
    public int assignByRoleId(Connection conn, long userId, int roleId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_USER_ROLE)) {
            ps.setLong(1, userId);
            ps.setInt(2, roleId);
            return ps.executeUpdate();
        }
    }

    @Override
    public List<Integer> findRoleIdsByUserId(Connection conn, long userId) throws SQLException {
        List<Integer> roleIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(FIND_ROLES_BY_USER_ID)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roleIds.add(rs.getInt(COL_ROLE_ID));
                }
            }
        }
        return roleIds;
    }
}