package dao.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UserRoleDAO {
	
	int delete(final Connection conn, final long userId) throws SQLException;

	int assignByRoleId(final Connection conn, final long userId, final int roleId) throws SQLException;

	List<Integer> findRoleIdsByUserId(final Connection conn, final long userId) throws SQLException;
	
	Map<Long, Integer> findPrimaryRoleIdByUserIdsAsMap(final Connection conn, final List<Long> userIds) throws SQLException;
}
