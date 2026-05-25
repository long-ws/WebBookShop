package dao.oauth;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import beans.oauth.OAuthProvider;

public interface OAuthProviderDAO {
	
	Optional<OAuthProvider> findByCode(Connection conn, String code) throws SQLException;
	
	int findIdByCode(Connection conn, String code) throws SQLException;
}