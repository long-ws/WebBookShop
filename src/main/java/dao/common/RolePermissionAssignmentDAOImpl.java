package dao.common;

import static config.db.DatabaseSchema.COL_CREATED_AT;
import static config.db.DatabaseSchema.COL_ID;
import static config.db.DatabaseSchema.COL_PERMISSION_CODE;
import static config.db.DatabaseSchema.COL_PERMISSION_ID;
import static config.db.DatabaseSchema.COL_PERMISSION_IS_ACTIVE;
import static config.db.DatabaseSchema.COL_ROLE_ID;
import static config.db.DatabaseSchema.COL_ROLE_IS_ACTIVE;
import static config.db.DatabaseSchema.COL_ROLE_PERMISSION_IS_ACTIVE;
import static config.db.DatabaseSchema.COL_ROLE_PERMISSION_PERMISSION_ID;
import static config.db.DatabaseSchema.COL_ROLE_PERMISSION_ROLE_ID;
import static config.db.DatabaseSchema.COL_USER_ID;
import static config.db.DatabaseSchema.TABLE_PERMISSION_REGISTRY;
import static config.db.DatabaseSchema.TABLE_ROLE_PERMISSION_ASSIGNMENT;
import static config.db.DatabaseSchema.TABLE_ROLE_REGISTRY;
import static config.db.DatabaseSchema.TABLE_USER_ROLE_REGISTRY;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolePermissionAssignmentDAOImpl implements RolePermissionAssignmentDAO {

	private static final String SQL_ASSIGN = """
			INSERT INTO %s (%s, %s, %s, %s)
			VALUES (?, ?, 1, CURRENT_TIMESTAMP)
			ON DUPLICATE KEY UPDATE %s=1, %s=CURRENT_TIMESTAMP
			""".formatted(TABLE_ROLE_PERMISSION_ASSIGNMENT, COL_ROLE_PERMISSION_ROLE_ID, COL_ROLE_PERMISSION_PERMISSION_ID, COL_ROLE_PERMISSION_IS_ACTIVE, COL_CREATED_AT,
			COL_ROLE_PERMISSION_IS_ACTIVE, COL_CREATED_AT);

	private static final String SQL_REMOVE = """
			DELETE FROM %s
			WHERE %s = ? AND %s = ?
			""".formatted(TABLE_ROLE_PERMISSION_ASSIGNMENT, COL_ROLE_PERMISSION_ROLE_ID, COL_ROLE_PERMISSION_PERMISSION_ID);

	private static final String SQL_REMOVE_ALL_BY_ROLE = """
			DELETE FROM %s
			WHERE %s = ?
			""".formatted(TABLE_ROLE_PERMISSION_ASSIGNMENT, COL_ROLE_PERMISSION_ROLE_ID);

	private static final String SQL_GET_PERMISSION_IDS = """
			SELECT rpa.%s FROM %s rpa
			JOIN %s p ON rpa.%s = p.%s
			WHERE rpa.%s = ? AND rpa.%s = 1 AND p.%s = 1
			""".formatted(COL_ROLE_PERMISSION_PERMISSION_ID, TABLE_ROLE_PERMISSION_ASSIGNMENT, TABLE_PERMISSION_REGISTRY, COL_ROLE_PERMISSION_PERMISSION_ID, COL_ID, COL_ROLE_PERMISSION_ROLE_ID,
			COL_ROLE_PERMISSION_IS_ACTIVE, COL_PERMISSION_IS_ACTIVE);

	private static final String SQL_GET_ROLE_IDS = """
			SELECT rpa.%s FROM %s rpa
			JOIN %s r ON rpa.%s = r.%s
			WHERE rpa.%s = ? AND rpa.%s = 1 AND r.%s = 1
			""".formatted(COL_ROLE_PERMISSION_ROLE_ID, TABLE_ROLE_PERMISSION_ASSIGNMENT, TABLE_ROLE_REGISTRY, COL_ROLE_PERMISSION_ROLE_ID, COL_ID, COL_ROLE_PERMISSION_PERMISSION_ID,
			COL_ROLE_PERMISSION_IS_ACTIVE, COL_ROLE_IS_ACTIVE);

	private static final String SQL_HAS_PERMISSION_ID = """
			SELECT COUNT(*) FROM %s rpa
			JOIN %s r ON rpa.%s = r.%s
			JOIN %s p ON rpa.%s = p.%s
			WHERE rpa.%s = ? AND rpa.%s = ? AND rpa.%s = 1 AND r.%s = 1 AND p.%s = 1
			""".formatted(TABLE_ROLE_PERMISSION_ASSIGNMENT, TABLE_ROLE_REGISTRY, COL_ROLE_PERMISSION_ROLE_ID, COL_ID, TABLE_PERMISSION_REGISTRY, COL_ROLE_PERMISSION_PERMISSION_ID, COL_ID,
			COL_ROLE_PERMISSION_ROLE_ID, COL_ROLE_PERMISSION_PERMISSION_ID, COL_ROLE_PERMISSION_IS_ACTIVE, COL_ROLE_IS_ACTIVE, COL_PERMISSION_IS_ACTIVE);

	private static final String SQL_GET_ALL_MAPPINGS = """
			SELECT rpa.%s, rpa.%s FROM %s rpa
			JOIN %s r ON rpa.%s = r.%s
			JOIN %s p ON rpa.%s = p.%s
			WHERE rpa.%s = 1 AND r.%s = 1 AND p.%s = 1
			ORDER BY rpa.%s
			""".formatted(COL_ROLE_PERMISSION_ROLE_ID, COL_ROLE_PERMISSION_PERMISSION_ID, TABLE_ROLE_PERMISSION_ASSIGNMENT, TABLE_ROLE_REGISTRY, COL_ROLE_PERMISSION_ROLE_ID, COL_ID,
			TABLE_PERMISSION_REGISTRY, COL_ROLE_PERMISSION_PERMISSION_ID, COL_ID, COL_ROLE_PERMISSION_IS_ACTIVE, COL_ROLE_IS_ACTIVE, COL_PERMISSION_IS_ACTIVE, COL_ROLE_PERMISSION_ROLE_ID);

	private static final String SQL_USER_HAS_PERMISSION = """
			SELECT 1 FROM %s ur
			JOIN %s r ON ur.%s = r.%s
			JOIN %s rpa ON ur.%s = rpa.%s
			JOIN %s p ON rpa.%s = p.%s
			WHERE ur.%s = ? AND p.%s = ? AND r.%s = 1 AND rpa.%s = 1 AND p.%s = 1
			LIMIT 1
			""".formatted(TABLE_USER_ROLE_REGISTRY, TABLE_ROLE_REGISTRY, COL_ROLE_ID, COL_ID, TABLE_ROLE_PERMISSION_ASSIGNMENT, COL_ROLE_ID, COL_ROLE_ID, TABLE_PERMISSION_REGISTRY, COL_PERMISSION_ID,
			COL_ID, COL_USER_ID, COL_PERMISSION_CODE, COL_ROLE_IS_ACTIVE, COL_ROLE_PERMISSION_IS_ACTIVE, COL_PERMISSION_IS_ACTIVE);

	private static final String SQL_EXISTS_PERMISSION = """
			SELECT 1 FROM %s rpa
			JOIN %s r ON rpa.%s = r.%s
			JOIN %s p ON rpa.%s = p.%s
			WHERE rpa.%s = ? AND p.%s = ? AND rpa.%s = 1 AND r.%s = 1 AND p.%s = 1
			LIMIT 1
			""".formatted(TABLE_ROLE_PERMISSION_ASSIGNMENT, TABLE_ROLE_REGISTRY, COL_ROLE_ID, COL_ID, TABLE_PERMISSION_REGISTRY, COL_PERMISSION_ID, COL_ID, COL_ROLE_ID, COL_PERMISSION_CODE,
			COL_ROLE_PERMISSION_IS_ACTIVE, COL_ROLE_IS_ACTIVE, COL_PERMISSION_IS_ACTIVE);

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
		if (permissionIds == null || permissionIds.isEmpty())
			return;
		try (PreparedStatement ps = conn.prepareStatement(SQL_ASSIGN)) {
			for (Integer pId : permissionIds) {
				if (pId != null) {
					ps.setInt(1, roleId);
					ps.setInt(2, pId);
					ps.addBatch();
				}
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
		if (permissionIds == null || permissionIds.isEmpty())
			return;
		try (PreparedStatement ps = conn.prepareStatement(SQL_REMOVE)) {
			for (Integer pId : permissionIds) {
				if (pId != null) {
					ps.setInt(1, roleId);
					ps.setInt(2, pId);
					ps.addBatch();
				}
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
				while (rs.next())
					list.add(rs.getInt(COL_ROLE_PERMISSION_PERMISSION_ID));
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
				while (rs.next())
					ids.add(rs.getInt(COL_ROLE_PERMISSION_ROLE_ID));
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
		try (PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_MAPPINGS); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				int roleId = rs.getInt(COL_ROLE_PERMISSION_ROLE_ID);
				int permissionId = rs.getInt(COL_ROLE_PERMISSION_PERMISSION_ID);
				List<Integer> list = result.get(roleId);
				if (list == null) {
					list = new ArrayList<>();
					result.put(roleId, list);
				}
				list.add(permissionId);
			}
		}
		return result;
	}

	@Override
	public boolean hasPermission(Connection conn, long userId, String permissionCode) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_USER_HAS_PERMISSION)) {
			ps.setLong(1, userId);
			ps.setString(2, permissionCode);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	@Override
	public boolean exists(Connection conn, int roleId, String permissionCode) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_PERMISSION)) {
			ps.setInt(1, roleId);
			ps.setString(2, permissionCode);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next(); // Nếu tồn tại 1 dòng thì quyền hợp lệ
			}
		}
	}
}
