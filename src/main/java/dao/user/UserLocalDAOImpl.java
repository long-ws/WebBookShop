package dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import beans.common.EmailVerifyStatus;
import beans.user.UserLocalAuth;

public class UserLocalDAOImpl implements UserLocalDAO {

	private static final String COL_USER_ID = "user_id";
	private static final String COL_USERNAME = "username";
	private static final String COL_PASSWORD_HASH = "password_hash";
	private static final String COL_EMAIL = "email";
	private static final String COL_LOCKED_UNTIL = "locked_until";
	private static final String COL_VERIFY_STATUS_ID = "email_verify_status_id";

	private static final String ALL_COLUMNS = String.format("%s, %s, %s, %s, %s, %s", COL_USER_ID, COL_USERNAME,
			COL_PASSWORD_HASH, COL_EMAIL, COL_LOCKED_UNTIL, COL_VERIFY_STATUS_ID);

	private static final String INSERT_SQL = String.format(
			"INSERT INTO user_local (%s, %s, %s, %s) VALUES (?, ?, ?, ?)", COL_USER_ID, COL_USERNAME, COL_PASSWORD_HASH,
			COL_EMAIL);

	private static final String UPDATE_EMAIL_SQL = String.format("UPDATE user_local SET %s=? WHERE %s=?", COL_EMAIL,
			COL_USER_ID);
	private static final String UPDATE_PASSWORD_SQL = String.format("UPDATE user_local SET %s=? WHERE %s=?",
			COL_PASSWORD_HASH, COL_USER_ID);

	private static final String FIND_BY_USER_ID = String.format("SELECT %s FROM user_local WHERE %s = ?", ALL_COLUMNS,
			COL_USER_ID);
	private static final String FIND_ID_BY_USERNAME = String.format("SELECT %s FROM user_local WHERE %s = ?",
			COL_USER_ID, COL_USERNAME);
	private static final String FIND_ID_BY_EMAIL = String.format("SELECT %s FROM user_local WHERE %s = ?", COL_USER_ID,
			COL_EMAIL);

	private static final String COUNT_BY_USERNAME = "SELECT COUNT(*) FROM user_local WHERE username = ?";
	private static final String COUNT_BY_USERNAME_EXCLUDE = "SELECT COUNT(*) FROM user_local WHERE username = ? AND user_id != ?";
	private static final String COUNT_BY_EMAIL = "SELECT COUNT(*) FROM user_local WHERE email = ?";
	private static final String COUNT_BY_EMAIL_EXCLUDE = "SELECT COUNT(*) FROM user_local WHERE email = ? AND user_id != ?";

	@Override
	public int insert(Connection conn, long userId, UserLocalAuth local) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
			ps.setLong(1, userId);
			ps.setString(2, local.getUsername());
			ps.setString(3, local.getPasswordHash());
			ps.setString(4, local.getEmail());
			return ps.executeUpdate();
		}
	}

	@Override
	public void updateEmail(Connection conn, long userId, String email) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(UPDATE_EMAIL_SQL)) {
			ps.setString(1, email);
			ps.setLong(2, userId);
			ps.executeUpdate();
		}
	}

	@Override
	public void updatePassword(Connection conn, long userId, String passwordHash) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(UPDATE_PASSWORD_SQL)) {
			ps.setString(1, passwordHash);
			ps.setLong(2, userId);
			ps.executeUpdate();
		}
	}

	@Override
	public void update(Connection conn, long userId, UserLocalAuth local) throws SQLException {
		StringBuilder sql = new StringBuilder("UPDATE user_local SET ");
		List<Object> params = new ArrayList<>();

		if (local.getEmail() != null) {
			sql.append(COL_EMAIL).append(" = ?, ");
			params.add(local.getEmail());
		}

		String passwordHash = local.getPasswordHash();
		if (passwordHash != null && !passwordHash.isEmpty()) {
			sql.append(COL_PASSWORD_HASH).append(" = ?, ");
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
	public Optional<UserLocalAuth> findByUserId(Connection conn, long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(FIND_BY_USER_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
			}
		}
	}

	@Override
	public Optional<Long> findUserIdByUsername(Connection conn, String username) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(FIND_ID_BY_USERNAME)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(rs.getLong(COL_USER_ID)) : Optional.empty();
			}
		}
	}

	@Override
	public Optional<Long> findUserIdByEmail(Connection conn, String email) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(FIND_ID_BY_EMAIL)) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(rs.getLong(COL_USER_ID)) : Optional.empty();
			}
		}
	}

	@Override
	public boolean existsByUsername(Connection conn, String username, Long excludeUserId) throws SQLException {
		String sql = (excludeUserId == null) ? COUNT_BY_USERNAME : COUNT_BY_USERNAME_EXCLUDE;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			if (excludeUserId != null) {
				ps.setLong(2, excludeUserId);
			}
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	@Override
	public boolean existsByEmail(Connection conn, String email, Long excludeUserId) throws SQLException {
		String sql = (excludeUserId == null) ? COUNT_BY_EMAIL : COUNT_BY_EMAIL_EXCLUDE;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email);
			if (excludeUserId != null) {
				ps.setLong(2, excludeUserId);
			}
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	@Override
	public List<Long> findAllUserIdsOrderByUsername(Connection conn, boolean ascending) throws SQLException {
		// SQL Injection Safe bằng việc hardcode sẵn 2 lựa chọn ASC / DESC cố định thay
		// vì cộng chuỗi vô định
		String sql = "SELECT " + COL_USER_ID + " FROM user_local ORDER BY " + COL_USERNAME
				+ (ascending ? " ASC" : " DESC");
		List<Long> ids = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				ids.add(rs.getLong(COL_USER_ID));
			}
		}
		return ids;
	}

	private UserLocalAuth mapRow(ResultSet rs) throws SQLException {
		UserLocalAuth local = new UserLocalAuth();
		local.setUserId(rs.getLong(COL_USER_ID));
		local.setUsername(rs.getString(COL_USERNAME));
		local.setPasswordHash(rs.getString(COL_PASSWORD_HASH));
		local.setEmail(rs.getString(COL_EMAIL));
		local.setLockedUntil(rs.getTimestamp(COL_LOCKED_UNTIL));

		int verifyStatusId = rs.getInt(COL_VERIFY_STATUS_ID);
		if (!rs.wasNull()) {
			EmailVerifyStatus verifyStatus = new EmailVerifyStatus();
			verifyStatus.setId(verifyStatusId);
			local.setEmailVerifyStatus(verifyStatus);
		}
		return local;
	}
}