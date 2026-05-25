package repository;

import beans.User;
import beans.user.UserAccount;
import beans.user.UserLocalAuth;
import beans.user.UserOAuthAuth;
import beans.user.UserProfile;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

	long insert(Connection conn, User user) throws SQLException;

	void update(Connection conn, User user) throws SQLException;

	boolean delete(Connection conn, List<Long> userIds) throws SQLException;

	void changePassword(Connection conn, long userId, String hashedPassword) throws SQLException;

	boolean incrementTokenVersion(Connection conn, long userId) throws SQLException;

	long createLocalUser(Connection conn, UserAccount account, UserProfile profile, UserLocalAuth localAuth)
			throws SQLException;

	long createOAuthUser(Connection conn, UserAccount account, UserProfile profile, UserOAuthAuth oauthAuth)
			throws SQLException;

	Optional<User> findById(Connection conn, long userId) throws SQLException;

	Optional<User> findById(long userId) throws SQLException;

	Optional<User> findByUsername(Connection conn, String username) throws SQLException;

	Optional<User> findByEmail(Connection conn, String email) throws SQLException;

	boolean existUserByUsername(Connection conn, String username) throws SQLException;

	boolean existUserByUsername(Connection conn, String username, long excludeUserId) throws SQLException;

	boolean existUserByEmail(Connection conn, String email) throws SQLException;

	boolean existUserByEmail(Connection conn, String email, long excludeUserId) throws SQLException;

	List<User> findAllUser(Connection conn) throws SQLException;

	List<User> findAllUser(Connection conn, String orderBy, String orderDir) throws SQLException;

	long count(Connection conn) throws SQLException;

	int getTokenVersion(Connection conn, long userId) throws SQLException;
}
