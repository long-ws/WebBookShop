package dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import beans.common.UserStatus;
import beans.user.UserAccount;

public class UserAccountDAOImpl implements UserAccountDAO {

	private static final String SQL_INSERT = "INSERT INTO user_account (status_id, token_version, created_at, updated_at) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
	private static final String SQL_UPDATE = "UPDATE user_account SET status_id=?, token_version=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
	private static final String SQL_SOFT_DELETE = "UPDATE user_account SET deleted_at=CURRENT_TIMESTAMP WHERE id=?";
	private static final String SQL_FIND_BY_ID = "SELECT * FROM user_account WHERE id=? AND deleted_at IS NULL";
	private static final String SQL_FIND_ALL = "SELECT * FROM user_account WHERE deleted_at IS NULL ORDER BY id DESC";
	private static final String SQL_COUNT = "SELECT COUNT(*) FROM user_account WHERE deleted_at IS NULL";
	private static final String SQL_INCREMENT_TOKEN = "UPDATE user_account SET token_version = token_version + 1, updated_at=CURRENT_TIMESTAMP WHERE id=?";
	private static final String SQL_GET_TOKEN_VERSION = "SELECT token_version FROM user_account WHERE id=?";

	@Override
	public long insert(Connection conn, UserAccount account) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, account.getStatusId());
			ps.setInt(2, account.getTokenVersion());
			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
				throw new SQLException("Tạo tài khoản thất bại");
			}
		}
	}

	@Override
	public void update(Connection conn, UserAccount account) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
			ps.setInt(1, account.getStatusId());
			ps.setInt(2, account.getTokenVersion());
			ps.setLong(3, account.getId());
			ps.executeUpdate();
		}
	}

	@Override
	public void softDeleteBatch(Connection conn, List<Long> userIds) throws SQLException {
		if (userIds == null || userIds.isEmpty()) {
			return;
		}
		try (PreparedStatement ps = conn.prepareStatement(SQL_SOFT_DELETE)) {
			for (Long id : userIds) {
				ps.setLong(1, id);
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	@Override
	public Optional<UserAccount> findById(Connection conn, long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
			}
		}
	}

	@Override
	public List<UserAccount> findAllNotDeleted(Connection conn) throws SQLException {
		List<UserAccount> list = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapRow(rs));
			}
		}
		return list;
	}

	@Override
	public long countNotDeleted(Connection conn) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				return rs.getLong(1);
			}
			return 0;
		}
	}

	@Override
	public boolean incrementTokenVersion(Connection conn, long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INCREMENT_TOKEN)) {
			ps.setLong(1, userId);
			return ps.executeUpdate() > 0;
		}
	}

	@Override
	public int getTokenVersion(Connection conn, long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_GET_TOKEN_VERSION)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
				return 0;
			}
		}
	}

	private UserAccount mapRow(ResultSet rs) throws SQLException {
		UserAccount account = new UserAccount();
		account.setId(rs.getLong("id"));
		account.setStatusId(rs.getInt("status_id"));
		UserStatus status = new UserStatus();
		status.setId(rs.getInt("status_id"));
		account.setStatus(status);
		account.setTokenVersion(rs.getInt("token_version"));
		account.setLastLoginAt(rs.getTimestamp("last_login_at"));
		account.setRememberToken(rs.getString("remember_token"));
		account.setRememberExpiresAt(rs.getTimestamp("remember_expires_at"));
		account.setDeletedAt(rs.getTimestamp("deleted_at"));
		long deletedBy = rs.getLong("deleted_by");
		if (!rs.wasNull()) {
			account.setDeletedBy(deletedBy);
		}
		account.setDeletionScheduledAt(rs.getTimestamp("deletion_scheduled_at"));
		account.setCreatedAt(rs.getTimestamp("created_at"));
		account.setUpdatedAt(rs.getTimestamp("updated_at"));
		return account;
	}
}
