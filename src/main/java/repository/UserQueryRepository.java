package repository;

import beans.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface UserQueryRepository {

	Optional<User> findByUsername(Connection conn, String username) throws SQLException;

	Optional<User> findByEmail(Connection conn, String email) throws SQLException;

	boolean existUserByUsername(Connection conn, String username) throws SQLException;

	boolean existUserByUsername(Connection conn, String username, long excludeUserId) throws SQLException;

	boolean existUserByEmail(Connection conn, String email) throws SQLException;

	boolean existUserByEmail(Connection conn, String email, long excludeUserId) throws SQLException;
}
