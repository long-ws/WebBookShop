package dao.oauth;

import beans.oauth.OAuthProvider;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface OAuthProviderDAO {
    
    Optional<OAuthProvider> findByCode(Connection conn, String code) throws SQLException;
    
    Optional<Integer> findIdByCode(Connection conn, String code) throws SQLException;
}
