package dao.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.user.UserProfile;

public interface UserProfileDAO {
	
	int insert(final Connection conn, final UserProfile profile) throws SQLException;
	
	int update(final Connection conn, final UserProfile profile) throws SQLException;
	
	int delete(final Connection conn, final long userId) throws SQLException;
	
	Optional<UserProfile> findUserProfileById(final Connection conn, final long userId) throws SQLException;

	long findUserIdByEmail(final Connection conn, final String email) throws SQLException;

	boolean existsByEmail(final Connection conn, final String email, final Long excludeUserId) throws SQLException;
	
	List<Long> findAllIdsOrderByFullname(Connection conn, boolean ascending) throws SQLException;
	
	Map<Long, UserProfile> findByUserIdsAsMap(Connection conn, List<Long> userIds) throws SQLException;
}
