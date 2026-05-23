package dao.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserRoleDAO {
	int delete(Connection conn, long userId) throws SQLException;

	int assignByRoleId(Connection conn, long userId, int roleId) throws SQLException;

	List<Integer> findRoleIdsByUserId(Connection conn, long userId) throws SQLException;
}
