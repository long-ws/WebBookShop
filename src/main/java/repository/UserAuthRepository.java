package repository;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserAuthRepository {

	void changePassword(Connection conn, long userId, String hashedPassword) throws SQLException;

	boolean incrementTokenVersion(Connection conn, long userId) throws SQLException;

	int getTokenVersion(Connection conn, long userId) throws SQLException;
}
