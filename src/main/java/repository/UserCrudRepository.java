package repository;

import beans.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserCrudRepository {

	long insert(Connection conn, User user) throws SQLException;

	void update(Connection conn, User user) throws SQLException;

	boolean delete(Connection conn, List<Long> userIds) throws SQLException;

	Optional<User> findById(Connection conn, long userId) throws SQLException;

	List<User> findAllUser(Connection conn) throws SQLException;

	long count(Connection conn) throws SQLException;
}
