package dao.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.user.UserLocalAuth;

public interface UserLocalDAO {

	int insert(final Connection conn, final long userId, final UserLocalAuth local) throws SQLException;

	void updateEmail(final Connection conn, final long userId, final String email) throws SQLException;

	void updatePassword(final Connection conn, final long userId, final String passwordHash) throws SQLException;

	void updateEmailVerifyStatus(final Connection conn, final long userId, final int emailVerifyStatusId) throws SQLException;

	void update(final Connection conn, final long userId, final UserLocalAuth local) throws SQLException;

	Optional<UserLocalAuth> findByUserId(final Connection conn, final long userId) throws SQLException;

	long findUserIdByUsername(final Connection conn, final String username) throws SQLException;

	long findUserIdByEmail(final Connection conn, final String email) throws SQLException;

	boolean existsByUsername(final Connection conn, final String username, final Long excludeUserId) throws SQLException;

	boolean existsByEmail(final Connection conn, final String email, final Long excludeUserId) throws SQLException;
	
	Map<Long, UserLocalAuth> findByUserIdsAsMap(final Connection conn, final List<Long> userIds) throws SQLException;
}
