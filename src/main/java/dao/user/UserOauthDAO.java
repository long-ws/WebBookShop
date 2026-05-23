package dao.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import beans.user.UserOAuthAuth;

public interface UserOauthDAO {
    
    void insert(Connection conn, UserOAuthAuth oauthInfo) throws SQLException;
    
    void delete(Connection conn, long oauthId) throws SQLException;
    
    Optional<UserOAuthAuth> findByProviderAndProviderUserId(Connection conn, int providerId, String providerUserId) throws SQLException;
    
    Optional<Long> findUserIdByOAuth(Connection conn, int providerId, String providerUserId) throws SQLException;
    
    List<UserOAuthAuth> findByUserId(Connection conn, long userId) throws SQLException;
    
    boolean hasOAuthProvider(Connection conn, long userId, int providerId) throws SQLException;
    
    int countByUserId(Connection conn, long userId) throws SQLException;
}