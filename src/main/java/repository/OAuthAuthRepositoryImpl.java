package repository;

import beans.user.UserOAuthAuth;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OAuthAuthRepositoryImpl implements OAuthAuthRepository {

	@Override
	public Optional<UserOAuthAuth> findByProviderAndProviderUserId(String provider, String providerUserId)
			throws SQLException {
		String sql = "SELECT uo.* FROM user_oauth uo " + "INNER JOIN oauth_provider op ON uo.provider_id = op.id "
				+ "WHERE op.code = ? AND uo.provider_user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, provider.toUpperCase());
			ps.setString(2, providerUserId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToOAuthAuth(rs));
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<Long> findUserIdByOAuth(String provider, String providerUserId) throws SQLException {
		String sql = "SELECT uo.user_id FROM user_oauth uo " + "INNER JOIN oauth_provider op ON uo.provider_id = op.id "
				+ "WHERE op.code = ? AND uo.provider_user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, provider.toUpperCase());
			ps.setString(2, providerUserId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(rs.getLong("user_id"));
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public void linkOAuthAccount(long userId, String provider, String providerUserId, String email, String displayName,
			String avatarUrl) throws SQLException {
		// Check if user already has this provider linked
		if (hasOAuthProvider(userId, provider)) {
			throw new SQLException("User already has " + provider + " account linked");
		}

		int providerId = getProviderId(provider);

		String sql = "INSERT INTO user_oauth (user_id, provider_id, provider_user_id, email, display_name, avatar_url) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setInt(2, providerId);
			ps.setString(3, providerUserId);
			ps.setString(4, email);
			ps.setString(5, displayName);
			ps.setString(6, avatarUrl);
			ps.executeUpdate();
		} catch (SQLException e) {
			// Handle duplicate key error - user OAuth account already exists
			if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
				throw new SQLException("This " + provider + " account is already linked to another user");
			} else {
				throw e;
			}
		}
	}

	@Override
	public void delete(long oauthId) throws SQLException {
		String sql = "DELETE FROM user_oauth WHERE id=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, oauthId);
			ps.executeUpdate();
		}
	}

	@Override
	public List<UserOAuthAuth> findByUserId(long userId) throws SQLException {
		List<UserOAuthAuth> accounts = new ArrayList<>();
		String sql = "SELECT * FROM user_oauth WHERE user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
	public boolean hasOAuthProvider(long userId, String provider) throws SQLException {
		String sql = "SELECT COUNT(*) FROM user_oauth uo " + "INNER JOIN oauth_provider op ON uo.provider_id = op.id "
				+ "WHERE uo.user_id = ? AND op.code = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			ps.setString(2, provider.toUpperCase());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1) > 0;
			}
		}
		return false;
	}

	@Override
	public int countByUserId(long userId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM user_oauth WHERE user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		}
		return 0;
	}

	@Override
	public int getProviderId(String provider) throws SQLException {
		String sql = "SELECT id FROM oauth_provider WHERE code = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, provider.toUpperCase());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt("id");
			}
		}
		throw new SQLException("Failed to get or create provider ID");
	}

	private UserOAuthAuth mapResultSetToOAuthAuth(ResultSet rs) throws SQLException {
		UserOAuthAuth auth = new UserOAuthAuth();
		auth.setId(rs.getLong("id"));
		auth.setUserId(rs.getLong("user_id"));
		auth.setProviderId(rs.getInt("provider_id"));
		auth.setProviderUserId(rs.getString("provider_user_id"));
		auth.setEmail(rs.getString("email"));
		auth.setDisplayName(rs.getString("display_name"));
		auth.setAvatarUrl(rs.getString("avatar_url"));
		return auth;
	}
}