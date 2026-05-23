package repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import beans.common.Permission;
import beans.common.Role;

public interface AuthorizationRepository {

	boolean userHasPermission(Connection conn, long userId, String permissionCode) throws SQLException;

	boolean userHasAnyPermission(Connection conn, long userId, List<String> permissionCodes) throws SQLException;

	boolean userHasRole(Connection conn, long userId, String roleCode) throws SQLException;

	boolean userIsSuperAdmin(Connection conn, long userId) throws SQLException;

	List<Permission> findPermissionsByUserId(Connection conn, long userId) throws SQLException;

	List<Role> findRolesByUserId(Connection conn, long userId) throws SQLException;
}
