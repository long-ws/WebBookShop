package repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.common.Permission;
import beans.common.Role;

public interface RoleRepository {

	int insert(Connection conn, Role role) throws SQLException;

	void update(Connection conn, Role role) throws SQLException;

	boolean delete(Connection conn, List<Integer> roleIds) throws SQLException;

	Optional<Role> findById(Connection conn, int roleId) throws SQLException;

	Optional<Role> findByCode(Connection conn, String roleCode) throws SQLException;

	List<Role> findAll(Connection conn) throws SQLException;

	List<Role> findAllActive(Connection conn) throws SQLException;

	long count(Connection conn) throws SQLException;

	boolean existsByCode(Connection conn, String roleCode) throws SQLException;

	boolean existsByName(Connection conn, String roleName) throws SQLException;

	boolean isSystemRole(Connection conn, int roleId) throws SQLException;

	List<Permission> loadPermissionsForRole(Connection conn, int roleId) throws SQLException;

	List<Integer> findPermissionIdsForRole(Connection conn, int roleId) throws SQLException;

	void grantPermissionsToRole(Connection conn, int roleId, List<Integer> permissionIds) throws SQLException;

	void revokePermissionsFromRole(Connection conn, int roleId, List<Integer> permissionIds) throws SQLException;

	void clearRolePermissions(Connection conn, int roleId) throws SQLException;

	boolean roleHasPermission(Connection conn, int roleId, String permissionCode) throws SQLException;

	Map<Integer, List<Integer>> findAllRolePermissionMappings(Connection conn) throws SQLException;
}
