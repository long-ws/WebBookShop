package dao.common;

import beans.common.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleDAOImpl implements RoleDAO {

    private static final String SQL_INSERT = "INSERT INTO role_registry (code, name, description, is_system, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
    private static final String SQL_UPDATE = "UPDATE role_registry SET code=?, name=?, description=?, is_active=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
    private static final String SQL_DELETE = "DELETE FROM role_registry WHERE id = ?";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM role_registry WHERE id=?";
    private static final String SQL_FIND_BY_CODE = "SELECT * FROM role_registry WHERE code=?";
    private static final String SQL_FIND_ALL = "SELECT * FROM role_registry ORDER BY id";
    private static final String SQL_FIND_ACTIVE = "SELECT * FROM role_registry WHERE is_active=1 ORDER BY id";
    private static final String SQL_COUNT = "SELECT COUNT(*) FROM role_registry";
    private static final String SQL_EXISTS_BY_CODE = "SELECT COUNT(*) FROM role_registry WHERE code=?";
    private static final String SQL_EXISTS_BY_NAME = "SELECT COUNT(*) FROM role_registry WHERE name=?";
    private static final String SQL_IS_SYSTEM = "SELECT is_system FROM role_registry WHERE id=?";

    @Override
    public int insert(Connection conn, Role role) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, role.getCode());
            ps.setString(2, role.getName());
            ps.setString(3, role.getDescription());
            ps.setBoolean(4, role.isSystem());
            ps.setBoolean(5, role.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Tạo vai trò thất bại");
    }

    @Override
    public void update(Connection conn, Role role) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, role.getCode());
            ps.setString(2, role.getName());
            ps.setString(3, role.getDescription());
            ps.setBoolean(4, role.isActive());
            ps.setInt(5, role.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Connection conn, int roleId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, roleId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Role> findById(Connection conn, int roleId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRole(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Role> findByCode(Connection conn, String code) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CODE)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRole(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Role> findAll(Connection conn) throws SQLException {
        List<Role> roles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
        }
        return roles;
    }

    @Override
    public List<Role> findAllActive(Connection conn) throws SQLException {
        List<Role> roles = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ACTIVE);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
        }
        return roles;
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
    public boolean existsByName(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_NAME)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public Optional<Boolean> isSystemRole(Connection conn, int roleId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_IS_SYSTEM)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getBoolean("is_system"));
                }
            }
        }
        return Optional.empty();
    }

    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("id"));
        role.setCode(rs.getString("code"));
        role.setName(rs.getString("name"));
        role.setDescription(rs.getString("description"));
        role.setSystem(rs.getBoolean("is_system"));
        role.setActive(rs.getBoolean("is_active"));
        role.setCreatedAt(rs.getTimestamp("created_at"));
        role.setUpdatedAt(rs.getTimestamp("updated_at"));
        return role;
    }
}
