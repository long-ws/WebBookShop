package dao.common;

import beans.common.Permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PermissionDAOImpl implements PermissionDAO {

    private static final String SQL_INSERT = "INSERT INTO permission_registry (code, name, description, module, is_system, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
    private static final String SQL_UPDATE = "UPDATE permission_registry SET name=?, description=?, module=?, is_active=? WHERE id=?";
    private static final String SQL_DELETE = "DELETE FROM permission_registry WHERE id = ?";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM permission_registry WHERE id=?";
    private static final String SQL_FIND_BY_CODE = "SELECT * FROM permission_registry WHERE code=?";
    private static final String SQL_FIND_ALL = "SELECT * FROM permission_registry ORDER BY id";
    private static final String SQL_FIND_ACTIVE = "SELECT * FROM permission_registry WHERE is_active=1 ORDER BY id";
    private static final String SQL_FIND_BY_MODULE = "SELECT * FROM permission_registry WHERE module=? ORDER BY id";
    private static final String SQL_COUNT = "SELECT COUNT(*) FROM permission_registry";
    private static final String SQL_EXISTS_BY_CODE = "SELECT COUNT(*) FROM permission_registry WHERE code=?";
    private static final String SQL_IS_SYSTEM = "SELECT is_system FROM permission_registry WHERE id=?";
    private static final String SQL_GET_MODULES = "SELECT DISTINCT module FROM permission_registry WHERE is_active=1";

    @Override
    public int insert(Connection conn, Permission permission) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, permission.getCode());
            ps.setString(2, permission.getName());
            ps.setString(3, permission.getDescription());
            ps.setString(4, permission.getModule());
            ps.setBoolean(5, permission.isSystem());
            ps.setBoolean(6, permission.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Tạo quyền thất bại");
    }

    @Override
    public void update(Connection conn, Permission permission) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, permission.getName());
            ps.setString(2, permission.getDescription());
            ps.setString(3, permission.getModule());
            ps.setBoolean(4, permission.isActive());
            ps.setInt(5, permission.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Connection conn, int permissionId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, permissionId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Permission> findById(Connection conn, int permissionId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, permissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPermission(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Permission> findByCode(Connection conn, String code) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CODE)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPermission(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Permission> findAll(Connection conn) throws SQLException {
        List<Permission> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPermission(rs));
            }
        }
        return list;
    }

    @Override
    public List<Permission> findAllActive(Connection conn) throws SQLException {
        List<Permission> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ACTIVE);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPermission(rs));
            }
        }
        return list;
    }

    @Override
    public List<Permission> findByModule(Connection conn, String module) throws SQLException {
        List<Permission> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_MODULE)) {
            ps.setString(1, module);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPermission(rs));
                }
            }
        }
        return list;
    }

    @Override
    public long count(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public boolean existsByCode(Connection conn, String code) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_CODE)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public Optional<Boolean> isSystemPermission(Connection conn, int permissionId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_IS_SYSTEM)) {
            ps.setInt(1, permissionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getBoolean("is_system"));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<String> findAllModules(Connection conn) throws SQLException {
        List<String> modules = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_GET_MODULES);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modules.add(rs.getString("module"));
            }
        }
        return modules;
    }

    private Permission mapResultSetToPermission(ResultSet rs) throws SQLException {
        Permission p = new Permission();
        p.setId(rs.getInt("id"));
        p.setCode(rs.getString("code"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setModule(rs.getString("module"));
        p.setSystem(rs.getBoolean("is_system"));
        p.setActive(rs.getBoolean("is_active"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
