package repository;

import beans.user.UserOAuthAuth;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OAuthAuthRepository {
	Optional<UserOAuthAuth> findByProviderAndProviderUserId(String provider, String providerUserId) throws SQLException;

	Optional<Long> findUserIdByOAuth(String provider, String providerUserId) throws SQLException;

	void linkOAuthAccount(long userId, String provider, String providerUserId, String email, String displayName,
			String avatarUrl) throws SQLException;

	void delete(long oauthId) throws SQLException;

	List<UserOAuthAuth> findByUserId(long userId) throws SQLException;

	boolean hasOAuthProvider(long userId, String provider) throws SQLException;

	int countByUserId(long userId) throws SQLException;

	int getProviderId(String provider) throws SQLException;
}