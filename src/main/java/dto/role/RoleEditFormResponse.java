package dto.role;

import java.util.List;
import java.util.Map;

import beans.common.Role;
import dto.permission.ManagePermissionResponse;

public class RoleEditFormResponse {

	private final RoleUpdateRequest role;
	private final List<ManagePermissionResponse> allPermissions;
	private final List<Role> allRoles;
	private final List<ManagePermissionResponse> rolePermissions;
	private final Map<Integer, String> permissionRoleMap;

	public RoleEditFormResponse(RoleUpdateRequest role, List<ManagePermissionResponse> allPermissions, List<Role> allRoles,
			List<ManagePermissionResponse> rolePermissions, Map<Integer, String> permissionRoleMap) {
		this.role = role;
		this.allPermissions = allPermissions;
		this.allRoles = allRoles;
		this.rolePermissions = rolePermissions;
		this.permissionRoleMap = permissionRoleMap;
	}

	public RoleUpdateRequest getRole() {
		return role;
	}

	public List<ManagePermissionResponse> getAllPermissions() {
		return allPermissions;
	}

	public List<Role> getAllRoles() {
		return allRoles;
	}

	public List<ManagePermissionResponse> getRolePermissions() {
		return rolePermissions;
	}

	public Map<Integer, String> getPermissionRoleMap() {
		return permissionRoleMap;
	}
}
