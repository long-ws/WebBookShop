package dao.user;

import static config.DatabaseConstants.COL_ROLE_ID;
import static config.DatabaseConstants.COL_USER_ID;
import static config.DatabaseConstants.TABLE_USER_ROLE_REGISTRY;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRoleDAOImpl implements UserRoleDAO {

	private static final String SQL_DELETE_BY_USER_ID = """
			DELETE FROM %s
			WHERE %s = ?
			""".formatted(TABLE_USER_ROLE_REGISTRY, COL_USER_ID);

	private static final String SQL_INSERT_USER_ROLE = """
			INSERT INTO %s (%s, %s)
			VALUES (?, ?)
			""".formatted(TABLE_USER_ROLE_REGISTRY, COL_USER_ID, COL_ROLE_ID);

	private static final String SQL_FIND_ROLES_BY_USER_ID = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			""".formatted(COL_ROLE_ID, TABLE_USER_ROLE_REGISTRY, COL_USER_ID);
	
	private static final String SQL_FIND_PRIMARY_ROLE_BY_USER_IDS = """
			SELECT %s, MIN(%s) AS %s
			FROM %s
			WHERE %s IN (
			""".formatted(COL_USER_ID, COL_ROLE_ID, COL_ROLE_ID, TABLE_USER_ROLE_REGISTRY, COL_USER_ID);

	@Override
	public int delete(final Connection conn, final long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_USER_ID)) {
			ps.setLong(1, userId);
			return ps.executeUpdate();
		}
	}

	@Override
	public int assignByRoleId(final Connection conn, final long userId, final int roleId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_USER_ROLE)) {
			ps.setLong(1, userId);
			ps.setInt(2, roleId);
			return ps.executeUpdate();
		}
	}

	@Override
	public List<Integer> findRoleIdsByUserId(final Connection conn, final long userId) throws SQLException {
		final List<Integer> roleIds = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ROLES_BY_USER_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					roleIds.add(rs.getInt(COL_ROLE_ID));
				}
			}
		}
		return roleIds;
	}
	
	@Override
	public Map<Long, Integer> findPrimaryRoleIdByUserIdsAsMap(final Connection conn, final List<Long> userIds) throws SQLException {
		Map<Long, Integer> result = new HashMap<>();
		if (userIds == null || userIds.isEmpty()) {
			return result;
		}
		
		final int MAX_BATCH_SIZE = 500;
		
		for (int startIndex = 0; startIndex < userIds.size(); startIndex = startIndex + MAX_BATCH_SIZE) {
			int endIndex = Math.min(startIndex + MAX_BATCH_SIZE, userIds.size());
			List<Long> currentBatchIds = userIds.subList(startIndex, endIndex);
			
			List<Long> sanitizedIds = new ArrayList<>();
			for (int i = 0; i < currentBatchIds.size(); i++) {
				Long id = currentBatchIds.get(i);
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
			
			String sql = SQL_FIND_PRIMARY_ROLE_BY_USER_IDS + placeholdersBuilder.toString() + ") GROUP BY " + COL_USER_ID;
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < sanitizedIds.size(); i++) {
					ps.setLong(i + 1, sanitizedIds.get(i));
				}
				
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						long userId = rs.getLong(COL_USER_ID);
						int roleId = rs.getInt(COL_ROLE_ID);
						if (!rs.wasNull()) {
							result.put(userId, roleId);
						}
					}
				}
			}
		}
		
		return result;
	}
}
