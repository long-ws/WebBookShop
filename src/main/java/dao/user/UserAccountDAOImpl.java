package dao.user;

import static config.db.DatabaseSchema.COL_ACCOUNT_DELETED_AT;
import static config.db.DatabaseSchema.COL_ACCOUNT_DELETED_BY;
import static config.db.DatabaseSchema.COL_ACCOUNT_DELETION_SCHEDULED_AT;
import static config.db.DatabaseSchema.COL_ACCOUNT_LAST_LOGIN_AT;
import static config.db.DatabaseSchema.COL_ACCOUNT_REMEMBER_EXPIRES_AT;
import static config.db.DatabaseSchema.COL_ACCOUNT_REMEMBER_TOKEN;
import static config.db.DatabaseSchema.COL_ACCOUNT_STATUS_ID;
import static config.db.DatabaseSchema.COL_ACCOUNT_TOKEN_VERSION;
import static config.db.DatabaseSchema.COL_CREATED_AT;
import static config.db.DatabaseSchema.COL_ID;
import static config.db.DatabaseSchema.COL_UPDATED_AT;
import static config.db.DatabaseSchema.TABLE_USER_ACCOUNT;

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

import beans.common.UserStatus;
import beans.user.UserAccount;
import config.security.SecurityConfig;

public class UserAccountDAOImpl implements UserAccountDAO {

	private static final String SELECT_FIELDS = "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s".formatted(COL_ID, COL_ACCOUNT_STATUS_ID, COL_ACCOUNT_TOKEN_VERSION, COL_ACCOUNT_LAST_LOGIN_AT,
			COL_ACCOUNT_REMEMBER_TOKEN, COL_ACCOUNT_REMEMBER_EXPIRES_AT, COL_ACCOUNT_DELETED_AT, COL_ACCOUNT_DELETED_BY, COL_ACCOUNT_DELETION_SCHEDULED_AT, COL_CREATED_AT, COL_UPDATED_AT);

	private static final String SQL_INSERT = """
			INSERT INTO %s (
				%s, %s, created_at, updated_at
			) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
			""".formatted(TABLE_USER_ACCOUNT, COL_ACCOUNT_STATUS_ID, COL_ACCOUNT_TOKEN_VERSION);

	private static final String SQL_UPDATE = """
			UPDATE %s
			SET
				%s = ?,
				%s = ?,
				updated_at = CURRENT_TIMESTAMP
			WHERE %s = ?
			""".formatted(TABLE_USER_ACCOUNT, COL_ACCOUNT_STATUS_ID, COL_ACCOUNT_TOKEN_VERSION, COL_ID);

	private static final String SQL_SOFT_DELETE = """
			UPDATE %s
			SET
				deleted_at = CURRENT_TIMESTAMP
			WHERE %s = ?
			""".formatted(TABLE_USER_ACCOUNT, COL_ID);

	private static final String SQL_FIND_BY_ID = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			  AND deleted_at IS NULL
			""".formatted(SELECT_FIELDS, TABLE_USER_ACCOUNT, COL_ID);

	private static final String SQL_FIND_ALL = """
			SELECT %s
			FROM %s
			WHERE deleted_at IS NULL
			ORDER BY %s DESC
			""".formatted(SELECT_FIELDS, TABLE_USER_ACCOUNT, COL_ID);

	private static final String SQL_COUNT = """
			SELECT COUNT(*)
			FROM %s
			WHERE deleted_at IS NULL
			""".formatted(TABLE_USER_ACCOUNT);

	private static final String SQL_INCREMENT_TOKEN = """
			UPDATE %s
			SET
				%s = %s + 1,
				updated_at = CURRENT_TIMESTAMP
			WHERE %s = ?
			""".formatted(TABLE_USER_ACCOUNT, COL_ACCOUNT_TOKEN_VERSION, COL_ACCOUNT_TOKEN_VERSION, COL_ID);

	private static final String SQL_GET_TOKEN_VERSION = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			""".formatted(COL_ACCOUNT_TOKEN_VERSION, TABLE_USER_ACCOUNT, COL_ID);

	private static final String SQL_FIND_ALL_IDS = "SELECT " + COL_ID + " FROM " + TABLE_USER_ACCOUNT + " WHERE deleted_at IS NULL ORDER BY " + COL_ID + " DESC";

	private static final String SQL_FIND_BY_IDS = """
			SELECT %s FROM %s WHERE %s IN (
			""".formatted(SELECT_FIELDS, TABLE_USER_ACCOUNT, COL_ID);

	@Override
	public long insert(final Connection conn, final UserAccount account) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, account.getStatusId());
			ps.setInt(2, account.getTokenVersion());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new SQLException("Thao tác khởi tạo bản ghi tài khoản thất bại.");
			}
		}
	}

	@Override
	public void update(final Connection conn, final UserAccount account) throws SQLException {
		if (account != null && SecurityConfig.isSystemGhostUserId(account.getId())) {
			throw new SQLException("Không thể cập nhật tài khoản hệ thống.");
		}
		try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
			ps.setInt(1, account.getStatusId());
			ps.setInt(2, account.getTokenVersion());
			ps.setLong(3, account.getId());
			ps.executeUpdate();
		}
	}

	@Override
	public void softDeleteBatch(final Connection conn, final List<Long> userIds) throws SQLException {
		if (userIds == null || userIds.isEmpty()) {
			return;
		}
		try (PreparedStatement ps = conn.prepareStatement(SQL_SOFT_DELETE)) {
			for (final Long id : userIds) {
				if (id != null && !SecurityConfig.isSystemGhostUserId(id)) {
					ps.setLong(1, id);
					ps.addBatch();
				}
			}
			ps.executeBatch();
		}
	}

	@Override
	public Optional<UserAccount> findById(final Connection conn, final long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
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
	public List<UserAccount> findAllNotDeleted(final Connection conn) throws SQLException {
		final List<UserAccount> list = new ArrayList<UserAccount>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapRow(rs));
			}
		}
		return list;
	}

	@Override
	public long countNotDeleted(final Connection conn) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT); ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				return rs.getLong(1);
			}
			return 0L;
		}
	}

	@Override
	public boolean incrementTokenVersion(final Connection conn, final long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INCREMENT_TOKEN)) {
			ps.setLong(1, userId);
			return ps.executeUpdate() > 0;
		}
	}

	@Override
	public int getTokenVersion(final Connection conn, final long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_GET_TOKEN_VERSION)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(COL_ACCOUNT_TOKEN_VERSION);
				}
				return 0;
			}
		}
	}

	@Override
	public List<Long> findAllIds(Connection conn) throws SQLException {
		List<Long> ids = new ArrayList<Long>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_IDS); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				ids.add(rs.getLong(COL_ID));
			}
		}
		return ids;
	}

	@Override
	public List<UserAccount> findAllAccountsByIds(Connection connection, List<Long> accountIds) throws SQLException {
		if (accountIds == null || accountIds.isEmpty()) {
			return new ArrayList<>();
		}

		Map<Long, UserAccount> accountMap = findByIdsAsMap(connection, accountIds);
		List<UserAccount> ordered = new ArrayList<>();

		for (int i = 0; i < accountIds.size(); i++) {
			Long id = accountIds.get(i);
			if (id != null) {
				UserAccount account = accountMap.get(id);
				if (account != null) {
					ordered.add(account);
				}
			}
		}

		return ordered;
	}

	@Override
	public Map<Long, UserAccount> findByIdsAsMap(Connection conn, List<Long> ids) throws SQLException {
		Map<Long, UserAccount> result = new HashMap<>();
		if (ids == null || ids.isEmpty()) {
			return result;
		}

		final int MAX_BATCH_SIZE = 500;

		for (int startIndex = 0; startIndex < ids.size(); startIndex = startIndex + MAX_BATCH_SIZE) {
			int endIndex = Math.min(startIndex + MAX_BATCH_SIZE, ids.size());
			List<Long> currentBatchIds = ids.subList(startIndex, endIndex);

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

			String sqlQuery = SQL_FIND_BY_IDS + placeholdersBuilder.toString() + ")";
			try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
				for (int i = 0; i < sanitizedIds.size(); i++) {
					ps.setLong(i + 1, sanitizedIds.get(i));
				}

				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						UserAccount account = mapRow(rs);
						result.put(account.getId(), account);
					}
				}
			}
		}

		return result;
	}

	private UserAccount mapRow(final ResultSet rs) throws SQLException {
		final UserAccount account = new UserAccount();
		account.setId(rs.getLong(COL_ID));

		final int statusId = rs.getInt(COL_ACCOUNT_STATUS_ID);
		account.setStatusId(statusId);

		final UserStatus status = new UserStatus();
		status.setId(statusId);
		account.setStatus(status);

		account.setTokenVersion(rs.getInt(COL_ACCOUNT_TOKEN_VERSION));
		account.setLastLoginAt(rs.getTimestamp(COL_ACCOUNT_LAST_LOGIN_AT));
		account.setRememberToken(rs.getString(COL_ACCOUNT_REMEMBER_TOKEN));
		account.setRememberExpiresAt(rs.getTimestamp(COL_ACCOUNT_REMEMBER_EXPIRES_AT));
		account.setDeletedAt(rs.getTimestamp(COL_ACCOUNT_DELETED_AT));

		final long deletedBy = rs.getLong(COL_ACCOUNT_DELETED_BY);
		if (!rs.wasNull()) {
			account.setDeletedBy(deletedBy);
		}

		account.setDeletionScheduledAt(rs.getTimestamp(COL_ACCOUNT_DELETION_SCHEDULED_AT));
		account.setCreatedAt(rs.getTimestamp(COL_CREATED_AT));
		account.setUpdatedAt(rs.getTimestamp(COL_UPDATED_AT));
		return account;
	}
}
