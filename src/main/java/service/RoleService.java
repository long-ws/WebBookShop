package service;

import java.util.List;
import java.util.Map;

import beans.common.Permission;
import beans.common.Role;
import dto.role.ManageRoleResponse;
import dto.role.RoleCreateRequest;
import dto.role.RoleDetailResponse;
import dto.role.RoleEditFormResponse;
import dto.role.RoleUpdateRequest;
import exception.BusinessException;

public interface RoleService {

    RoleDetailResponse createRole(RoleCreateRequest dto) throws BusinessException;
    RoleDetailResponse updateRole(RoleUpdateRequest dto) throws BusinessException;
    boolean deleteRoles(List<Integer> ids) throws BusinessException;

    RoleDetailResponse getRoleById(int id) throws BusinessException;
    RoleEditFormResponse getRoleEditForm(int roleId) throws BusinessException;
    Role getById(int id) throws BusinessException;
    Role getRoleByCode(String code) throws BusinessException;
    List<ManageRoleResponse> getRoles() throws BusinessException;
    List<Role> getAllActiveRoles();
    long countRoles() throws BusinessException;
    boolean isCodeExists(String code) throws BusinessException;
    boolean isCodeExists(String code, int excludeId) throws BusinessException;
    List<Permission> getPermissionsByRoleId(int roleId) throws BusinessException;
    Map<Integer, List<Integer>> getMapListRoleHasPermission() throws BusinessException;

    RoleCreateRequest toCreateDTO(RoleDetailResponse detail);
    RoleUpdateRequest toUpdateDTO(RoleDetailResponse detail);
}