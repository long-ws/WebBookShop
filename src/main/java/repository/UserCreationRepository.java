package repository;

import beans.user.UserAccount;
import beans.user.UserLocalAuth;
import beans.user.UserOAuthAuth;
import beans.user.UserProfile;
import java.sql.Connection;
import java.sql.SQLException;

public interface UserCreationRepository {

	long createLocalUser(Connection conn, UserAccount account, UserProfile profile, UserLocalAuth localAuth)
			throws SQLException;

	long createOAuthUser(Connection conn, UserAccount account, UserProfile profile, UserOAuthAuth oauthAuth)
			throws SQLException;
}
