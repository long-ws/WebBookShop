package dao.user;

import beans.user.UserProfile;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface UserProfileDAO {
	int insert(Connection conn, UserProfile profile) throws SQLException;
	int update(Connection conn, UserProfile profile) throws SQLException;
	int delete(Connection conn, long userId) throws SQLException;
	Optional<UserProfile> findUserProfileById(Connection conn, long userId) throws SQLException;
}
