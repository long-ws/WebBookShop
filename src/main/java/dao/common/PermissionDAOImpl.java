package dao.common;

import static config.db.DatabaseSchema.COL_CREATED_AT;
import static config.db.DatabaseSchema.COL_ID;
import static config.db.DatabaseSchema.COL_PERMISSION_CODE;
import static config.db.DatabaseSchema.COL_PERMISSION_DESCRIPTION;
import static config.db.DatabaseSchema.COL_PERMISSION_ID;
import static config.db.DatabaseSchema.COL_PERMISSION_IS_ACTIVE;
import static config.db.DatabaseSchema.COL_PERMISSION_IS_SYSTEM;
import static config.db.DatabaseSchema.COL_PERMISSION_MODULE;
import static config.db.DatabaseSchema.COL_PERMISSION_NAME;
import static config.db.DatabaseSchema.COL_ROLE_ID;
import static config.db.DatabaseSchema.COL_ROLE_IS_ACTIVE;
import static config.db.DatabaseSchema.COL_ROLE_PERMISSION_IS_ACTIVE;
import static config.db.DatabaseSchema.COL_USER_ID;
import static config.db.DatabaseSchema.TABLE_PERMISSION_REGISTRY;
import static config.db.DatabaseSchema.TABLE_ROLE_PERMISSION_ASSIGNMENT;
import static config.db.DatabaseSchema.TABLE_ROLE_REGISTRY;
import static config.db.DatabaseSchema.TABLE_USER_ROLE_REGISTRY;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import beans.common.Permission;

public class PermissionDAOImpl implements PermissionDAO {

	private static final String SELECT_FIELDS = "%s, %s, %s, %s, %s, %s, %s, %s".formatted(COL_ID, COL_PERMISSION_CODE, COL_PERMISSION_NAME, COL_PERMISSION_DESCRIPTION, COL_PERMISSION_MODULE,
			COL_PERMISSION_IS_SYSTEM, COL_PERMISSION_IS_ACTIVE, COL_CREATED_AT);

	private static final String SQL_INSERT = """
			INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s)
			VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
			""".formatted(TABLE_PERMISSION_REGISTRY, COL_PERMISSION_CODE, COL_PERMISSION_NAME, COL_PERMISSION_DESCRIPTION, COL_PERMISSION_MODULE, COL_PERMISSION_IS_SYSTEM, COL_PERMISSION_IS_ACTIVE,
			COL_CREATED_AT);

	private static final String SQL_UPDATE = """
			UPDATE %s SET %s=?, %s=?, %s=?, %s=? WHERE %s=?
			""".formatted(TABLE_PERMISSION_REGISTRY, COL_PERMISSION_NAME, COL_PERMISSION_DESCRIPTION, COL_PERMISSION_MODULE, COL_PERMISSION_IS_ACTIVE, COL_ID);

	private static final String SQL_DELETE = "DELETE FROM %s WHERE %s = ?".formatted(TABLE_PERMISSION_REGISTRY, COL_ID);

	private static final String SQL_FIND_BY_ID = "SELECT %s FROM %s WHERE %s = ?".formatted(SELECT_FIELDS, TABLE_PERMISSION_REGISTRY, COL_ID);
	private static final String SQL_FIND_BY_CODE = "SELECT %s FROM %s WHERE %s = ?".formatted(SELECT_FIELDS, TABLE_PERMISSION_REGISTRY, COL_PERMISSION_CODE);
	private static final String SQL_FIND_ALL = "SELECT %s FROM %s ORDER BY %s".formatted(SELECT_FIELDS, TABLE_PERMISSION_REGISTRY, COL_ID);
	private static final String SQL_FIND_ACTIVE = "SELECT %s FROM %s WHERE %s = 1 ORDER BY %s".formatted(SELECT_FIELDS, TABLE_PERMISSION_REGISTRY, COL_PERMISSION_IS_ACTIVE, COL_ID);
	private static final String SQL_FIND_BY_MODULE = "SELECT %s FROM %s WHERE %s = ? ORDER BY %s".formatted(SELECT_FIELDS, TABLE_PERMISSION_REGISTRY, COL_PERMISSION_MODULE, COL_ID);
	private static final String SQL_COUNT = "SELECT COUNT(*) FROM %s".formatted(TABLE_PERMISSION_REGISTRY);
	private static final String SQL_EXISTS_BY_CODE = "SELECT COUNT(*) FROM %s WHERE %s = ?".formatted(TABLE_PERMISSION_REGISTRY, COL_PERMISSION_CODE);
	private static final String SQL_IS_SYSTEM = "SELECT %s FROM %s WHERE %s = ?".formatted(COL_PERMISSION_IS_SYSTEM, TABLE_PERMISSION_REGISTRY, COL_ID);
	private static final String SQL_GET_MODULES = "SELECT DISTINCT %s FROM %s WHERE %s = 1".formatted(COL_PERMISSION_MODULE, TABLE_PERMISSION_REGISTRY, COL_PERMISSION_IS_ACTIVE);

	private static final String SQL_FIND_BY_USER_ID = """
			SELECT DISTINCT p.* FROM %s ur
			JOIN %s r ON ur.%s = r.%s
			JOIN %s rpa ON r.%s = rpa.%s
			JOIN %s p ON rpa.%s = p.%s
			WHERE ur.%s = ? AND r.%s = 1 AND rpa.%s = 1 AND p.%s = 1
			""".formatted(TABLE_USER_ROLE_REGISTRY, TABLE_ROLE_REGISTRY, COL_ROLE_ID, COL_ID, TABLE_ROLE_PERMISSION_ASSIGNMENT, COL_ID, COL_ROLE_ID, TABLE_PERMISSION_REGISTRY, COL_PERMISSION_ID,
			COL_ID, COL_USER_ID, COL_ROLE_IS_ACTIVE, COL_ROLE_PERMISSION_IS_ACTIVE, COL_PERMISSION_IS_ACTIVE);

	private static final String SQL_FIND_BY_ROLE_ID = """
			SELECT p.* FROM %s p
			JOIN %s rpa ON p.%s = rpa.%s
			WHERE rpa.%s = ? AND rpa.%s = 1 AND p.%s = 1
			""".formatted(TABLE_PERMISSION_REGISTRY, TABLE_ROLE_PERMISSION_ASSIGNMENT, COL_ID, COL_PERMISSION_ID, COL_ROLE_ID, COL_ROLE_PERMISSION_IS_ACTIVE, COL_PERMISSION_IS_ACTIVE);

	@Override
	public int insert(Connection conn, Permission p) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, p.getCode());
			ps.setString(2, p.getName());
			ps.setString(3, p.getDescription());
			ps.setString(4, p.getModule());
			ps.setBoolean(5, p.isSystem());
			ps.setBoolean(6, p.isActive());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getInt(1);
			}
		}
		throw new SQLException("Thao tác khởi tạo quyền mới thất bại.");
	}

	@Override
	public void update(Connection conn, Permission p) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
			ps.setString(1, p.getName());
			ps.setString(2, p.getDescription());
			ps.setString(3, p.getModule());
			ps.setBoolean(4, p.isActive());
			ps.setInt(5, p.getId());
			ps.executeUpdate();
		}
	}

	@Override
	public boolean deleteBatch(Connection conn, List<Integer> permissionIds) throws SQLException {
		if (permissionIds == null || permissionIds.isEmpty()) {
			return false;
		}

		try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
			for (Integer id : permissionIds) {
				ps.setInt(1, id);
				ps.addBatch();
			}

			int[] results = ps.executeBatch();

			for (int result : results) {
				if (result > 0)
					return true;
			}
			return false;
		}
	}

	@Override
	public Optional<Permission> findById(Connection conn, int id) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return Optional.of(mapRow(rs));
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<Permission> findByCode(Connection conn, String code) throws SQLException {
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
	public List<Permission> findAll(Connection conn) throws SQLException {
		List<Permission> list = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL); ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				list.add(mapRow(rs));
		}
		return list;
	}

	@Override
	public List<Permission> findAllActive(Connection conn) throws SQLException {
		List<Permission> list = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ACTIVE); ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				list.add(mapRow(rs));
		}
		return list;
	}

	@Override
	public List<Permission> findByModule(Connection conn, String module) throws SQLException {
		List<Permission> list = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_MODULE)) {
			ps.setString(1, module);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapRow(rs));
			}
		}
		return list;
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
	public boolean isSystemPermission(Connection conn, int id) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_IS_SYSTEM)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getBoolean(COL_PERMISSION_IS_SYSTEM);
			}
		}
		return false;
	}

	@Override
	public List<String> findAllModules(Connection conn) throws SQLException {
		List<String> modules = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_GET_MODULES); ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				modules.add(rs.getString(COL_PERMISSION_MODULE));
		}
		return modules;
	}

	@Override
	public List<Permission> findByUserId(Connection conn, long userId) throws SQLException {
		List<Permission> permissions = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					permissions.add(mapRow(rs));
				}
			}
		}
		return permissions;
	}

	@Override
	public List<Permission> findByRoleId(Connection conn, int roleId) throws SQLException {
		List<Permission> permissions = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ROLE_ID)) {
			ps.setInt(1, roleId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					permissions.add(mapRow(rs));
				}
			}
		}
		return permissions;
	}

	private Permission mapRow(ResultSet rs) throws SQLException {
		Permission p = new Permission();
		p.setId(rs.getInt(COL_ID));
		p.setCode(rs.getString(COL_PERMISSION_CODE));
		p.setName(rs.getString(COL_PERMISSION_NAME));
		p.setDescription(rs.getString(COL_PERMISSION_DESCRIPTION));
		p.setModule(rs.getString(COL_PERMISSION_MODULE));
		p.setSystem(rs.getBoolean(COL_PERMISSION_IS_SYSTEM));
		p.setActive(rs.getBoolean(COL_PERMISSION_IS_ACTIVE));
		p.setCreatedAt(rs.getTimestamp(COL_CREATED_AT));
		return p;
	}

	public static void main(String[] args) {
		System.out.println(SELECT_FIELDS);
		System.out.println(SQL_INSERT);
		System.out.println(SQL_UPDATE);
		System.out.println(SQL_DELETE);
		System.out.println(SQL_FIND_BY_ID);
		System.out.println(SQL_FIND_BY_CODE);
		System.out.println(SQL_FIND_ALL);
		System.out.println(SQL_FIND_ACTIVE);
		System.out.println(SQL_FIND_BY_MODULE);
		System.out.println(SQL_COUNT);
		System.out.println(SQL_EXISTS_BY_CODE);
		System.out.println(SQL_IS_SYSTEM);
		System.out.println(SQL_GET_MODULES);
		System.out.println(SQL_FIND_BY_USER_ID);
		System.out.println(SQL_FIND_BY_ROLE_ID);
	}
}
