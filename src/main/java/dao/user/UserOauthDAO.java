package dao.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import beans.user.UserOAuthAuth;

public interface UserOauthDAO {
	
	void insert(final Connection conn, final UserOAuthAuth oauthInfo) throws SQLException;
	
	void delete(final Connection conn, final long oauthId) throws SQLException;
	
	Optional<UserOAuthAuth> findByProviderAndProviderUserId(final Connection conn, final int providerId, final String providerUserId) throws SQLException;
	
	long findUserIdByOAuth(final Connection conn, final int providerId, final String providerUserId) throws SQLException;
	
	List<UserOAuthAuth> findByUserId(final Connection conn, final long userId) throws SQLException;
	
	boolean hasOAuthProvider(final Connection conn, final long userId, final int providerId) throws SQLException;
	
	int countByUserId(final Connection conn, final long userId) throws SQLException;
	
	Optional<UserOAuthAuth> findByProviderCode(Connection conn, String providerCode, String providerUserId) throws SQLException;
}