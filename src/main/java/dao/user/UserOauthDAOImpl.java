package dao.user;

import static config.DatabaseConstants.COL_ID;
import static config.DatabaseConstants.COL_OAUTH_AVATAR_URL;
import static config.DatabaseConstants.COL_OAUTH_DISPLAY_NAME;
import static config.DatabaseConstants.COL_OAUTH_EMAIL;
import static config.DatabaseConstants.COL_OAUTH_PROVIDER_CODE;
import static config.DatabaseConstants.COL_OAUTH_PROVIDER_ID;
import static config.DatabaseConstants.COL_OAUTH_PROVIDER_USER_ID;
import static config.DatabaseConstants.COL_USER_ID;
import static config.DatabaseConstants.TABLE_OAUTH_PROVIDER;
import static config.DatabaseConstants.TABLE_USER_OAUTH;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import beans.user.UserOAuthAuth;

public class UserOauthDAOImpl implements UserOauthDAO {

	private static final String SELECT_FIELDS = "%s, %s, %s, %s, %s, %s, %s".formatted(COL_ID, COL_USER_ID, COL_OAUTH_PROVIDER_ID, COL_OAUTH_PROVIDER_USER_ID, COL_OAUTH_EMAIL, COL_OAUTH_DISPLAY_NAME,
			COL_OAUTH_AVATAR_URL);

	private static final String SQL_INSERT = """
			INSERT INTO %s (
				%s, %s, %s, %s, %s, %s
			) VALUES (?, ?, ?, ?, ?, ?)
			""".formatted(TABLE_USER_OAUTH, COL_USER_ID, COL_OAUTH_PROVIDER_ID, COL_OAUTH_PROVIDER_USER_ID, COL_OAUTH_EMAIL, COL_OAUTH_DISPLAY_NAME, COL_OAUTH_AVATAR_URL);

	private static final String SQL_DELETE = """
			DELETE FROM %s
			WHERE %s = ?
			""".formatted(TABLE_USER_OAUTH, COL_ID);

	private static final String SQL_FIND_BY_PROVIDER_AND_USER_ID = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			  AND %s = ?
			""".formatted(SELECT_FIELDS, TABLE_USER_OAUTH, COL_OAUTH_PROVIDER_ID, COL_OAUTH_PROVIDER_USER_ID);

	private static final String SQL_FIND_USER_ID_BY_OAUTH = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			  AND %s = ?
			""".formatted(COL_USER_ID, TABLE_USER_OAUTH, COL_OAUTH_PROVIDER_ID, COL_OAUTH_PROVIDER_USER_ID);

	private static final String SQL_FIND_BY_USER_ID = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			""".formatted(SELECT_FIELDS, TABLE_USER_OAUTH, COL_USER_ID);

	private static final String SQL_HAS_PROVIDER = """
			SELECT COUNT(*)
			FROM %s
			WHERE %s = ?
			  AND %s = ?
			""".formatted(TABLE_USER_OAUTH, COL_USER_ID, COL_OAUTH_PROVIDER_ID);

	private static final String SQL_COUNT_BY_USER_ID = """
			SELECT COUNT(*)
			FROM %s
			WHERE %s = ?
			""".formatted(TABLE_USER_OAUTH, COL_USER_ID);
	
	private static final String SQL_FIND_BY_PROVIDER_CODE = """
			SELECT uo.* FROM %s uo
			JOIN %s op ON uo.%s = op.%s
			WHERE op.%s = ? AND uo.%s = ?
			LIMIT 1
			""".formatted(TABLE_USER_OAUTH, TABLE_OAUTH_PROVIDER, COL_OAUTH_PROVIDER_ID, COL_ID, COL_OAUTH_PROVIDER_CODE, COL_OAUTH_PROVIDER_USER_ID);

	@Override
	public void insert(final Connection conn, final UserOAuthAuth oauthInfo) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
			ps.setLong(1, oauthInfo.getUserId());
			ps.setInt(2, oauthInfo.getProviderId());
			ps.setString(3, oauthInfo.getProviderUserId());
			ps.setString(4, oauthInfo.getEmail());
			ps.setString(5, oauthInfo.getDisplayName());
			ps.setString(6, oauthInfo.getAvatarUrl());
			ps.executeUpdate();
		}
	}

	@Override
	public void delete(final Connection conn, final long oauthId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
			ps.setLong(1, oauthId);
			ps.executeUpdate();
		}
	}

	@Override
	public Optional<UserOAuthAuth> findByProviderAndProviderUserId(final Connection conn, final int providerId, final String providerUserId) throws SQLException {
		if (providerUserId == null) {
			return Optional.empty();
		}
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_PROVIDER_AND_USER_ID)) {
			ps.setInt(1, providerId);
			ps.setString(2, providerUserId.trim());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToOAuthAuth(rs));
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public long findUserIdByOAuth(final Connection conn, final int providerId, final String providerUserId) throws SQLException {
		if (providerUserId == null) {
			return 0L;
		}
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_USER_ID_BY_OAUTH)) {
			ps.setInt(1, providerId);
			ps.setString(2, providerUserId.trim());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getLong(COL_USER_ID);
				}
			}
		}
		return 0L;
	}

	@Override
	public List<UserOAuthAuth> findByUserId(final Connection conn, final long userId) throws SQLException {
		final List<UserOAuthAuth> accounts = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					accounts.add(mapResultSetToOAuthAuth(rs));
				}
			}
		}
		return accounts;
	}

	@Override
	public boolean hasOAuthProvider(final Connection conn, final long userId, final int providerId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_HAS_PROVIDER)) {
			ps.setLong(1, userId);
			ps.setInt(2, providerId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	@Override
	public int countByUserId(final Connection conn, final long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT_BY_USER_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		}
		return 0;
	}
	
	@Override
	public Optional<UserOAuthAuth> findByProviderCode(Connection conn, String providerCode, String providerUserId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_PROVIDER_CODE)) {
			ps.setString(1, providerCode);
			ps.setString(2, providerUserId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToOAuthAuth(rs));
				}
			}
		}
		return Optional.empty();
	}

	private UserOAuthAuth mapResultSetToOAuthAuth(final ResultSet rs) throws SQLException {
		final UserOAuthAuth auth = new UserOAuthAuth();
		auth.setId(rs.getLong(COL_ID));
		auth.setUserId(rs.getLong(COL_USER_ID));
		auth.setProviderId(rs.getInt(COL_OAUTH_PROVIDER_ID));
		auth.setProviderUserId(rs.getString(COL_OAUTH_PROVIDER_USER_ID));
		auth.setEmail(rs.getString(COL_OAUTH_EMAIL));
		auth.setDisplayName(rs.getString(COL_OAUTH_DISPLAY_NAME));
		auth.setAvatarUrl(rs.getString(COL_OAUTH_AVATAR_URL));
		return auth;
	}
}