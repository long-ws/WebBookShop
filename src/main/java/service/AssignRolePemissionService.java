package service;

import java.util.List;

import dto.role.RoleUpdateRequest;
import exception.BusinessException;

public interface AssignRolePemissionService {
	void assignPermissionsToRole(int roleId, List<Integer> permissionIds) throws BusinessException;

	void removePermissionsFromRole(int roleId, List<Integer> permissionIds) throws BusinessException;

	void updateRolePermissions(RoleUpdateRequest dto) throws BusinessException;
}