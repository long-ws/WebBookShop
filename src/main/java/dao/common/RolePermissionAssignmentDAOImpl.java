package dao.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolePermissionAssignmentDAOImpl implements RolePermissionAssignmentDAO {

    private static final String SQL_ASSIGN = "INSERT INTO role_permission_assignment (role_id, permission_id, is_active, created_at) VALUES (?, ?, 1, CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE is_active=1, created_at=CURRENT_TIMESTAMP";
    private static final String SQL_REMOVE = "DELETE FROM role_permission_assignment WHERE role_id = ? AND permission_id = ?";
    private static final String SQL_REMOVE_ALL_BY_ROLE = "DELETE FROM role_permission_assignment WHERE role_id=?";
    private static final String SQL_GET_PERMISSION_IDS = "SELECT permission_id FROM role_permission_assignment WHERE role_id=? AND is_active=1";
    private static final String SQL_GET_ROLE_IDS = "SELECT role_id FROM role_permission_assignment WHERE permission_id=? AND is_active=1";
    private static final String SQL_HAS_PERMISSION_ID = "SELECT COUNT(*) FROM role_permission_assignment WHERE role_id=? AND permission_id=? AND is_active=1";
    private static final String SQL_GET_ALL_MAPPINGS = "SELECT permission_id, role_id FROM role_permission_assignment WHERE is_active = 1";

    @Override
    public void assign(Connection conn, int roleId, int permissionId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_ASSIGN)) {
            ps.setInt(1, roleId);
            ps.setInt(2, permissionId);
            ps.executeUpdate();
        }
    }

    @Override
    public void assignBatch(Connection conn, int roleId, List<Integer> permissionIds) throws SQLException {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(SQL_ASSIGN)) {
            for (Integer pId : permissionIds) {
                ps.setInt(1, roleId);
                ps.setInt(2, pId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public void remove(Connection conn, int roleId, int permissionId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_REMOVE)) {
            ps.setInt(1, roleId);
            ps.setInt(2, permissionId);
            ps.executeUpdate();
        }
    }

    @Override
    public void removeBatch(Connection conn, int roleId, List<Integer> permissionIds) throws SQLException {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(SQL_REMOVE)) {
            for (Integer pId : permissionIds) {
                ps.setInt(1, roleId);
                ps.setInt(2, pId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public void removeAllByRoleId(Connection conn, int roleId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_REMOVE_ALL_BY_ROLE)) {
            ps.setInt(1, roleId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Integer> findPermissionIdsByRoleId(Connection conn, int roleId) throws SQLException {
        List<Integer> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_PERMISSION_IDS)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("permission_id"));
                }
            }
        }
        return list;
    }

    @Override
    public List<Integer> findRoleIdsByPermissionId(Connection conn, int permissionId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_ROLE_IDS)) {
            ps.setInt(1, permissionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("role_id"));
                }
            }
        }
        return ids;
    }

    @Override
    public boolean hasPermission(Connection conn, int roleId, int permissionId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_HAS_PERMISSION_ID)) {
            ps.setInt(1, roleId);
            ps.setInt(2, permissionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public Map<Integer, List<Integer>> findAllRolePermissionMappings(Connection conn) throws SQLException {
        Map<Integer, List<Integer>> result = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_MAPPINGS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int permissionId = rs.getInt("permission_id");
                int roleId = rs.getInt("role_id");
                if (!result.containsKey(permissionId)) {
                    result.put(permissionId, new ArrayList<>());
                }
                result.get(permissionId).add(roleId);
            }
        }
        return result;
    }
}
