package dao.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface RolePermissionAssignmentDAO {
	
	void assign(Connection conn, int roleId, int permissionId) throws SQLException;
	
	void assignBatch(Connection conn, int roleId, List<Integer> permissionIds) throws SQLException;
	
	void remove(Connection conn, int roleId, int permissionId) throws SQLException;
	
	void removeBatch(Connection conn, int roleId, List<Integer> permissionIds) throws SQLException;
	
	void removeAllByRoleId(Connection conn, int roleId) throws SQLException;
	
	List<Integer> findPermissionIdsByRoleId(Connection conn, int roleId) throws SQLException;
	
	List<Integer> findRoleIdsByPermissionId(Connection conn, int permissionId) throws SQLException;
	
	boolean hasPermission(Connection conn, int roleId, int permissionId) throws SQLException;
	
	Map<Integer, List<Integer>> findAllRolePermissionMappings(Connection conn) throws SQLException;
	
	boolean hasPermission(Connection conn, long userId, String permissionCode) throws SQLException;
	
	boolean exists(Connection conn, int roleId, String permissionCode) throws SQLException;
}