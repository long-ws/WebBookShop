package dao.common;

import static config.db.DatabaseSchema.COL_CREATED_AT;
import static config.db.DatabaseSchema.COL_ID;
import static config.db.DatabaseSchema.COL_ROLE_CODE;
import static config.db.DatabaseSchema.COL_ROLE_DESCRIPTION;
import static config.db.DatabaseSchema.COL_ROLE_ID;
import static config.db.DatabaseSchema.COL_ROLE_IS_ACTIVE;
import static config.db.DatabaseSchema.COL_ROLE_IS_SYSTEM;
import static config.db.DatabaseSchema.COL_ROLE_NAME;
import static config.db.DatabaseSchema.COL_UPDATED_AT;
import static config.db.DatabaseSchema.COL_USER_ID;
import static config.db.DatabaseSchema.TABLE_ROLE_REGISTRY;
import static config.db.DatabaseSchema.TABLE_USER_ROLE_REGISTRY;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.common.Role;

public class RoleDAOImpl implements RoleDAO {

	private static final String SELECT_FIELDS = "%s, %s, %s, %s, %s, %s, %s, %s".formatted(COL_ID, COL_ROLE_CODE, COL_ROLE_NAME, COL_ROLE_DESCRIPTION, COL_ROLE_IS_SYSTEM, COL_ROLE_IS_ACTIVE,
			COL_CREATED_AT, COL_UPDATED_AT);

	private static final String SQL_INSERT = """
			INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s)
			VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
			""".formatted(TABLE_ROLE_REGISTRY, COL_ROLE_CODE, COL_ROLE_NAME, COL_ROLE_DESCRIPTION, COL_ROLE_IS_SYSTEM, COL_ROLE_IS_ACTIVE, COL_CREATED_AT, COL_UPDATED_AT);

	private static final String SQL_UPDATE = """
			UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=CURRENT_TIMESTAMP WHERE %s=?
			""".formatted(TABLE_ROLE_REGISTRY, COL_ROLE_CODE, COL_ROLE_NAME, COL_ROLE_DESCRIPTION, COL_ROLE_IS_ACTIVE, COL_UPDATED_AT, COL_ID);

	private static final String SQL_DELETE = "DELETE FROM %s WHERE %s = ?".formatted(TABLE_ROLE_REGISTRY, COL_ID);

	private static final String SQL_FIND_BY_ID = "SELECT %s FROM %s WHERE %s = ?".formatted(SELECT_FIELDS, TABLE_ROLE_REGISTRY, COL_ID);
	private static final String SQL_FIND_BY_CODE = "SELECT %s FROM %s WHERE %s = ?".formatted(SELECT_FIELDS, TABLE_ROLE_REGISTRY, COL_ROLE_CODE);
	private static final String SQL_FIND_ALL = "SELECT %s FROM %s ORDER BY %s".formatted(SELECT_FIELDS, TABLE_ROLE_REGISTRY, COL_ID);
	private static final String SQL_FIND_ACTIVE = "SELECT %s FROM %s WHERE %s = 1 ORDER BY %s".formatted(SELECT_FIELDS, TABLE_ROLE_REGISTRY, COL_ROLE_IS_ACTIVE, COL_ID);
	
	private static final String SQL_FIND_BY_IDS = """
			SELECT %s FROM %s WHERE %s IN (
			""".formatted(SELECT_FIELDS, TABLE_ROLE_REGISTRY, COL_ID);

	private static final String SQL_COUNT = "SELECT COUNT(*) FROM %s".formatted(TABLE_ROLE_REGISTRY);
	private static final String SQL_EXISTS_BY_CODE = "SELECT COUNT(*) FROM %s WHERE %s = ?".formatted(TABLE_ROLE_REGISTRY, COL_ROLE_CODE);
	private static final String SQL_EXISTS_BY_NAME = "SELECT COUNT(*) FROM %s WHERE %s = ?".formatted(TABLE_ROLE_REGISTRY, COL_ROLE_NAME);
	private static final String SQL_IS_SYSTEM = "SELECT %s FROM %s WHERE %s = ?".formatted(COL_ROLE_IS_SYSTEM, TABLE_ROLE_REGISTRY, COL_ID);

	private static final String ROLE_FIELDS = "%s, %s, %s, %s, %s, %s".formatted(COL_ID, COL_ROLE_CODE, COL_ROLE_NAME, COL_ROLE_DESCRIPTION, COL_ROLE_IS_SYSTEM, COL_ROLE_IS_ACTIVE);

	private static final String SQL_HAS_SYSTEM_ROLE = """
			SELECT 1 FROM %s ur
			JOIN %s r ON ur.%s = r.%s
			WHERE ur.%s = ? AND r.%s = 1 AND r.%s = 1
			LIMIT 1
			""".formatted(TABLE_USER_ROLE_REGISTRY, TABLE_ROLE_REGISTRY, COL_ROLE_ID, COL_ID, COL_USER_ID, COL_ROLE_IS_SYSTEM, COL_ROLE_IS_ACTIVE);

	private static final String SQL_FIND_ROLES_BY_USER_ID = """
			SELECT %s FROM %s r
			JOIN %s ur ON r.%s = ur.%s
			WHERE ur.%s = ? AND r.%s = 1
			ORDER BY r.%s
			""".formatted(ROLE_FIELDS, TABLE_ROLE_REGISTRY, TABLE_USER_ROLE_REGISTRY, COL_ID, COL_ROLE_ID, COL_USER_ID, COL_ROLE_IS_ACTIVE, COL_ID);

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
				if (rs.next())
					return rs.getInt(1);
			}
		}
		throw new SQLException("Thao tác khởi tạo vai trò mới thất bại.");
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
				if (rs.next())
					return Optional.of(mapRow(rs));
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<Role> findByCode(Connection conn, String code) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CODE)) {
			ps.setString(1, code);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return Optional.of(mapRow(rs));
			}
		}
		return Optional.empty();
	}

	@Override
	public List<Role> findAll(Connection conn) throws SQLException {
		List<Role> roles = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL); ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				roles.add(mapRow(rs));
		}
		return roles;
	}

	@Override
	public List<Role> findAllActive(Connection conn) throws SQLException {
		List<Role> roles = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ACTIVE); ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				roles.add(mapRow(rs));
		}
		return roles;
	}

	@Override
	public long count(Connection conn) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT); ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getLong(1);
		}
		return 0L;
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
	public boolean isSystemRole(Connection conn, int roleId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_IS_SYSTEM)) {
			ps.setInt(1, roleId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getBoolean(COL_ROLE_IS_SYSTEM);
			}
		}
		return false;
	}

	@Override
	public boolean hasSystemRole(Connection conn, long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_HAS_SYSTEM_ROLE)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	@Override
	public List<Role> findByUserId(Connection conn, long userId) throws SQLException {
		List<Role> roles = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ROLES_BY_USER_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					roles.add(mapRow(rs));
				}
			}
		}
		return roles;
	}
	
	@Override
	public Map<Integer, Role> findByIdsAsMap(Connection conn, List<Integer> roleIds) throws SQLException {
		Map<Integer, Role> result = new HashMap<>();
		if (roleIds == null || roleIds.isEmpty()) {
			return result;
		}
		
		final int MAX_BATCH_SIZE = 500;
		
		for (int startIndex = 0; startIndex < roleIds.size(); startIndex = startIndex + MAX_BATCH_SIZE) {
			int endIndex = Math.min(startIndex + MAX_BATCH_SIZE, roleIds.size());
			List<Integer> currentBatchIds = roleIds.subList(startIndex, endIndex);
			
			List<Integer> sanitizedIds = new ArrayList<>();
			for (int i = 0; i < currentBatchIds.size(); i++) {
				Integer id = currentBatchIds.get(i);
				if (id != null) {
					sanitizedIds.add(id);
				}
			}
			if (sanitizedIds.isEmpty()) {
				continue;
			}
			
			StringBuilder placeholdersBuilder = new StringBuilder();
			for (int i = 0; i < sanitizedIds.size(); i++) {
				if (i > 0) {
					placeholdersBuilder.append(',');
				}
				placeholdersBuilder.append('?');
			}
			
			String sql = SQL_FIND_BY_IDS + placeholdersBuilder.toString() + ")";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < sanitizedIds.size(); i++) {
					ps.setInt(i + 1, sanitizedIds.get(i));
				}
				
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						Role role = mapRow(rs);
						result.put(role.getId(), role);
					}
				}
			}
		}

		return result;
	}

	private Role mapRow(ResultSet rs) throws SQLException {
		Role role = new Role();
		role.setId(rs.getInt(COL_ID));
		role.setCode(rs.getString(COL_ROLE_CODE));
		role.setName(rs.getString(COL_ROLE_NAME));
		role.setDescription(rs.getString(COL_ROLE_DESCRIPTION));
		role.setSystem(rs.getBoolean(COL_ROLE_IS_SYSTEM));
		role.setActive(rs.getBoolean(COL_ROLE_IS_ACTIVE));
		role.setCreatedAt(rs.getTimestamp(COL_CREATED_AT));
		role.setUpdatedAt(rs.getTimestamp(COL_UPDATED_AT));
		return role;
	}
}
