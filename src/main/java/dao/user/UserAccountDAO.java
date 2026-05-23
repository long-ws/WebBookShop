package dao.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import beans.user.UserAccount;

public interface UserAccountDAO {

	long insert(Connection conn, UserAccount account) throws SQLException;

	void update(Connection conn, UserAccount account) throws SQLException;

	void softDeleteBatch(Connection conn, List<Long> userIds) throws SQLException;

	Optional<UserAccount> findById(Connection conn, long userId) throws SQLException;

	List<UserAccount> findAllNotDeleted(Connection conn) throws SQLException;

	long countNotDeleted(Connection conn) throws SQLException;

	boolean incrementTokenVersion(Connection conn, long userId) throws SQLException;

	int getTokenVersion(Connection conn, long userId) throws SQLException;
}
