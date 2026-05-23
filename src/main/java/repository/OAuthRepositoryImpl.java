package repository;

import beans.user.UserOAuthAuth;
import dao.oauth.OAuthProviderDAO;
import dao.oauth.OAuthProviderDAOImpl;
import dao.user.UserOauthDAO;
import dao.user.UserOauthDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OAuthRepositoryImpl implements OAuthRepository {

	private final UserOauthDAO userOauthDAO;
	private final OAuthProviderDAO oauthProviderDAO;

	public OAuthRepositoryImpl() {
		this(new UserOauthDAOImpl(), new OAuthProviderDAOImpl());
	}

	public OAuthRepositoryImpl(UserOauthDAO userOauthDAO, OAuthProviderDAO oauthProviderDAO) {
		this.userOauthDAO = userOauthDAO;
		this.oauthProviderDAO = oauthProviderDAO;
	}

	@Override
	public Optional<UserOAuthAuth> findByProviderAndProviderUserId(Connection conn, String provider, String providerUserId) throws SQLException {
		Optional<Integer> providerIdOpt = oauthProviderDAO.findIdByCode(conn, provider);
		if (providerIdOpt.isEmpty()) {
			return Optional.empty();
		}
		return userOauthDAO.findByProviderAndProviderUserId(conn, providerIdOpt.get(), providerUserId);
	}

	@Override
	public Optional<Long> findUserIdByOAuth(Connection conn, String provider, String providerUserId) throws SQLException {
		Optional<Integer> providerIdOpt = oauthProviderDAO.findIdByCode(conn, provider);
		if (providerIdOpt.isEmpty()) {
			return Optional.empty();
		}
		return userOauthDAO.findUserIdByOAuth(conn, providerIdOpt.get(), providerUserId);
	}

	@Override
	public void linkOAuthAccount(Connection conn, long userId, String provider, String providerUserId, String email, String displayName, String avatarUrl) throws SQLException {
		Optional<Integer> providerIdOpt = oauthProviderDAO.findIdByCode(conn, provider);
		if (providerIdOpt.isEmpty()) {
			throw new SQLException("Không tìm thấy OAuth provider: " + provider);
		}
		int providerId = providerIdOpt.get();

		if (userOauthDAO.hasOAuthProvider(conn, userId, providerId)) {
			throw new SQLException("User already has " + provider + " account linked");
		}

		UserOAuthAuth oauthAuth = new UserOAuthAuth();
		oauthAuth.setUserId(userId);
		oauthAuth.setProviderId(providerId);
		oauthAuth.setProviderUserId(providerUserId);
		oauthAuth.setEmail(email);
		oauthAuth.setDisplayName(displayName);
		oauthAuth.setAvatarUrl(avatarUrl);

		try {
			userOauthDAO.insert(conn, oauthAuth);
		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
				throw new SQLException("This " + provider + " account is already linked to another user");
			}
			throw e;
		}
	}

	@Override
	public void delete(Connection conn, long oauthId) throws SQLException {
		userOauthDAO.delete(conn, oauthId);
	}

	@Override
	public List<UserOAuthAuth> findByUserId(Connection conn, long userId) throws SQLException {
		return userOauthDAO.findByUserId(conn, userId);
	}

	@Override
	public boolean hasOAuthProvider(Connection conn, long userId, String provider) throws SQLException {
		Optional<Integer> providerIdOpt = oauthProviderDAO.findIdByCode(conn, provider);
		return providerIdOpt.isPresent() && userOauthDAO.hasOAuthProvider(conn, userId, providerIdOpt.get());
	}

	@Override
	public int countByUserId(Connection conn, long userId) throws SQLException {
		return userOauthDAO.countByUserId(conn, userId);
	}

	@Override
	public int getProviderId(Connection conn, String provider) throws SQLException {
		Optional<Integer> providerIdOpt = oauthProviderDAO.findIdByCode(conn, provider);
		if (providerIdOpt.isEmpty()) {
			throw new SQLException("Không tìm thấy OAuth provider: " + provider);
		}
		return providerIdOpt.get();
	}
}
