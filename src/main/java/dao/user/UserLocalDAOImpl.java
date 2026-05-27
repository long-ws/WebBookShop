package dao.user;

import static config.DatabaseConstants.COL_LOCAL_EMAIL;
import static config.DatabaseConstants.COL_LOCAL_EMAIL_VERIFY_STATUS_ID;
import static config.DatabaseConstants.COL_LOCAL_LOCKED_UNTIL;
import static config.DatabaseConstants.COL_LOCAL_PASSWORD_HASH;
import static config.DatabaseConstants.COL_LOCAL_USERNAME;
import static config.DatabaseConstants.COL_USER_ID;
import static config.DatabaseConstants.TABLE_USER_LOCAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.common.EmailVerifyStatus;
import beans.user.UserLocalAuth;

public class UserLocalDAOImpl implements UserLocalDAO {

	private static final String SELECT_FIELDS = "%s, %s, %s, %s, %s, %s".formatted(COL_USER_ID, COL_LOCAL_USERNAME, COL_LOCAL_PASSWORD_HASH, COL_LOCAL_EMAIL, COL_LOCAL_LOCKED_UNTIL,
			COL_LOCAL_EMAIL_VERIFY_STATUS_ID);

	private static final String SQL_INSERT = """
			INSERT INTO %s (
				%s, %s, %s, %s
			) VALUES (?, ?, ?, ?)
			""".formatted(TABLE_USER_LOCAL, COL_USER_ID, COL_LOCAL_USERNAME, COL_LOCAL_PASSWORD_HASH, COL_LOCAL_EMAIL);

	private static final String SQL_UPDATE_EMAIL = """
			UPDATE %s
			SET %s = ?
			WHERE %s = ?
			""".formatted(TABLE_USER_LOCAL, COL_LOCAL_EMAIL, COL_USER_ID);

	private static final String SQL_UPDATE_PASSWORD = """
			UPDATE %s
			SET %s = ?
			WHERE %s = ?
			""".formatted(TABLE_USER_LOCAL, COL_LOCAL_PASSWORD_HASH, COL_USER_ID);

	private static final String SQL_FIND_BY_USER_ID = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			""".formatted(SELECT_FIELDS, TABLE_USER_LOCAL, COL_USER_ID);
	
	private static final String SQL_FIND_BY_USER_IDS = """
			SELECT %s
			FROM %s
			WHERE %s IN (
			""".formatted(SELECT_FIELDS, TABLE_USER_LOCAL, COL_USER_ID);

	private static final String SQL_FIND_ID_BY_USERNAME = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			""".formatted(COL_USER_ID, TABLE_USER_LOCAL, COL_LOCAL_USERNAME);

	private static final String SQL_FIND_ID_BY_EMAIL = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			""".formatted(COL_USER_ID, TABLE_USER_LOCAL, COL_LOCAL_EMAIL);

	private static final String SQL_COUNT_BY_USERNAME = """
			SELECT COUNT(*)
			FROM %s
			WHERE %s = ?
			""".formatted(TABLE_USER_LOCAL, COL_LOCAL_USERNAME);

	private static final String SQL_COUNT_BY_USERNAME_EXCLUDE = """
			SELECT COUNT(*)
			FROM %s
			WHERE %s = ?
			  AND %s != ?
			""".formatted(TABLE_USER_LOCAL, COL_LOCAL_USERNAME, COL_USER_ID);

	private static final String SQL_COUNT_BY_EMAIL = """
			SELECT COUNT(*)
			FROM %s
			WHERE %s = ?
			""".formatted(TABLE_USER_LOCAL, COL_LOCAL_EMAIL);

	private static final String SQL_COUNT_BY_EMAIL_EXCLUDE = """
			SELECT COUNT(*)
			FROM %s
			WHERE %s = ?
			  AND %s != ?
			""".formatted(TABLE_USER_LOCAL, COL_LOCAL_EMAIL, COL_USER_ID);
	
	@Override
	public int insert(final Connection conn, final long userId, final UserLocalAuth local) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
			ps.setLong(1, userId);
			ps.setString(2, local.getUsername());
			ps.setString(3, local.getPasswordHash());
			ps.setString(4, local.getEmail());
			return ps.executeUpdate();
		}
	}

	@Override
	public void updateEmail(final Connection conn, final long userId, final String email) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_EMAIL)) {
			ps.setString(1, email);
			ps.setLong(2, userId);
			ps.executeUpdate();
		}
	}

	@Override
	public void updatePassword(final Connection conn, final long userId, final String passwordHash) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {
			ps.setString(1, passwordHash);
			ps.setLong(2, userId);
			ps.executeUpdate();
		}
	}

	@Override
	public void update(final Connection conn, final long userId, final UserLocalAuth local) throws SQLException {
		final StringBuilder sql = new StringBuilder("UPDATE " + TABLE_USER_LOCAL + " SET ");
		final List<Object> params = new ArrayList<>();

		if (local.getEmail() != null) {
			sql.append(COL_LOCAL_EMAIL).append(" = ?, ");
			params.add(local.getEmail());
		}

		final String passwordHash = local.getPasswordHash();
		if (passwordHash != null && !passwordHash.isEmpty()) {
			sql.append(COL_LOCAL_PASSWORD_HASH).append(" = ?, ");
			params.add(passwordHash);
		}

		if (params.isEmpty()) {
			return;
		}

		sql.setLength(sql.length() - 2);
		sql.append(" WHERE ").append(COL_USER_ID).append(" = ?");
		params.add(userId);

		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) {
				ps.setObject(i + 1, params.get(i));
			}
			ps.executeUpdate();
		}
	}

	@Override
	public Optional<UserLocalAuth> findByUserId(final Connection conn, final long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapRow(rs));
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public long findUserIdByUsername(final Connection conn, final String username) throws SQLException {
		if (username == null) {
			return 0L;
		}
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ID_BY_USERNAME)) {
			ps.setString(1, username.trim());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getLong(COL_USER_ID);
				}
			}
		}
		return 0L;
	}

	@Override
	public long findUserIdByEmail(final Connection conn, final String email) throws SQLException {
		if (email == null) {
			return 0L;
		}
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ID_BY_EMAIL)) {
			ps.setString(1, email.trim().toLowerCase());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getLong(COL_USER_ID);
				}
			}
		}
		return 0L;
	}

	@Override
	public boolean existsByUsername(final Connection conn, final String username, final Long excludeUserId) throws SQLException {
		if (username == null) {
			return false;
		}
		final String sql = (excludeUserId == null) ? SQL_COUNT_BY_USERNAME : SQL_COUNT_BY_USERNAME_EXCLUDE;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username.trim());
			if (excludeUserId != null) {
				ps.setLong(2, excludeUserId);
			}
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	@Override
	public boolean existsByEmail(final Connection conn, final String email, final Long excludeUserId) throws SQLException {
		if (email == null) {
			return false;
		}
		final String sql = (excludeUserId == null) ? SQL_COUNT_BY_EMAIL : SQL_COUNT_BY_EMAIL_EXCLUDE;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email.trim().toLowerCase());
			if (excludeUserId != null) {
				ps.setLong(2, excludeUserId);
			}
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}
	
	@Override
	public Map<Long, UserLocalAuth> findByUserIdsAsMap(final Connection conn, final List<Long> userIds) throws SQLException {
		Map<Long, UserLocalAuth> result = new HashMap<>();
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
			
			String sql = SQL_FIND_BY_USER_IDS + placeholdersBuilder.toString() + ")";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < sanitizedIds.size(); i++) {
					ps.setLong(i + 1, sanitizedIds.get(i));
				}
				
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						UserLocalAuth local = mapRow(rs);
						result.put(local.getUserId(), local);
					}
				}
			}
		}
		
		return result;
	}

	private UserLocalAuth mapRow(final ResultSet rs) throws SQLException {
		final UserLocalAuth local = new UserLocalAuth();
		local.setUserId(rs.getLong(COL_USER_ID));
		local.setUsername(rs.getString(COL_LOCAL_USERNAME));
		local.setPasswordHash(rs.getString(COL_LOCAL_PASSWORD_HASH));
		local.setEmail(rs.getString(COL_LOCAL_EMAIL));
		local.setLockedUntil(rs.getTimestamp(COL_LOCAL_LOCKED_UNTIL));

		final int verifyStatusId = rs.getInt(COL_LOCAL_EMAIL_VERIFY_STATUS_ID);
		if (!rs.wasNull()) {
			final EmailVerifyStatus verifyStatus = new EmailVerifyStatus();
			verifyStatus.setId(verifyStatusId);
			local.setEmailVerifyStatus(verifyStatus);
		}
		return local;
	}
}
