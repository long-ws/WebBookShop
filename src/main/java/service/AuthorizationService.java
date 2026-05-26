package service;

import java.util.List;

import beans.common.Permission;
import beans.common.Role;

public interface AuthorizationService {

	boolean hasPermission(long userId, String permissionCode);
	boolean hasAnyPermission(long userId, List<String> permissionCodes);
	boolean hasRole(long userId, String roleCode);

	boolean isSuperAdmin(long userId);

	List<Permission> getPermissionsByUserId(long userId);
	List<Role> getRolesByUserId(long userId);
}
