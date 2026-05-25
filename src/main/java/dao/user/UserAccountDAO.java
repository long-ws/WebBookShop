package dao.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.user.UserAccount;

public interface UserAccountDAO {

	long insert(final Connection conn, final UserAccount account) throws SQLException;

	void update(final Connection conn, final UserAccount account) throws SQLException;

	void softDeleteBatch(final Connection conn, final List<Long> userIds) throws SQLException;

	Optional<UserAccount> findById(final Connection conn, final long userId) throws SQLException;

	List<UserAccount> findAllNotDeleted(final Connection conn) throws SQLException;

	long countNotDeleted(final Connection conn) throws SQLException;

	boolean incrementTokenVersion(final Connection conn, final long userId) throws SQLException;

	int getTokenVersion(final Connection conn, final long userId) throws SQLException;
	
	List<Long> findAllIds(Connection conn) throws SQLException;
	
	List<UserAccount> findAllAccountsByIds(Connection conn, List<Long> ids) throws SQLException;
	
	Map<Long, UserAccount> findByIdsAsMap(Connection conn, List<Long> ids) throws SQLException;
}
