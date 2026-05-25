package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import beans.common.Permission;
import beans.common.Role;
import exception.BusinessException;

public interface AuthorizationService {

	boolean hasPermission(long userId, String permissionCode) throws BusinessException;

	boolean hasAnyPermission(long userId, List<String> permissionCodes) throws BusinessException;

	boolean hasRole(long userId, String roleCode) throws BusinessException;

	boolean isSuperAdmin(long userId) throws BusinessException;

	List<Permission> getPermissionsByUserId(long userId) throws BusinessException;

	List<Role> getRolesByUserId(long userId) throws BusinessException;

	boolean hasPermission(Connection conn, long userId, String permissionCode) throws SQLException;

	boolean hasAnyPermission(Connection conn, long userId, List<String> permissionCodes) throws SQLException;

	boolean hasRole(Connection conn, long userId, String roleCode) throws SQLException;

	boolean isSuperAdmin(Connection conn, long userId) throws SQLException;

	List<Permission> getPermissionsByUserId(Connection conn, long userId) throws SQLException;

	List<Role> getRolesByUserId(Connection conn, long userId) throws SQLException;
}