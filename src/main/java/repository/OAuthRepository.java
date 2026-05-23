package repository;

import beans.user.UserOAuthAuth;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OAuthRepository {

	Optional<UserOAuthAuth> findByProviderAndProviderUserId(Connection conn, String provider, String providerUserId)
			throws SQLException;
	Optional<Long> findUserIdByOAuth(Connection conn, String provider, String providerUserId) throws SQLException;

	void linkOAuthAccount(Connection conn, long userId, String provider, String providerUserId, String email,
			String displayName, String avatarUrl) throws SQLException;
	void delete(Connection conn, long oauthId) throws SQLException;

	List<UserOAuthAuth> findByUserId(Connection conn, long userId) throws SQLException;

	boolean hasOAuthProvider(Connection conn, long userId, String provider) throws SQLException;
	int countByUserId(Connection conn, long userId) throws SQLException;
	int getProviderId(Connection conn, String provider) throws SQLException;
}
