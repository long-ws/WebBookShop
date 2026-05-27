package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import beans.common.Role;
import dto.role.RoleUpdateRequest;
import exception.BusinessException;
import repository.RoleRepository;
import repository.RoleRepositoryImpl;
import utils.DbTransaction;
import utils.TransactionCallback;

public class AssignRolePermissionServiceImpl implements AssignRolePemissionService {

    private final RoleRepository roleRepository;
    private final RoleQueryReadService roleQueryReadService;

    public AssignRolePermissionServiceImpl() {
        this(new RoleRepositoryImpl(), new RoleQueryReadService());
    }

    public AssignRolePermissionServiceImpl(RoleRepository roleRepository, RoleQueryReadService queryService) {
        this.roleRepository = roleRepository;
        this.roleQueryReadService = queryService;
    }

    @Override
    public void assignPermissionsToRole(final int roleId, final List<Integer> permissionIds) throws BusinessException {
        if (permissionIds == null || permissionIds.isEmpty()) return;

        try {
            DbTransaction.run(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(Connection conn) throws SQLException {
                    roleRepository.grantPermissionsToRole(conn, roleId, permissionIds);
                    return null;
                }
            });
        } catch (SQLException e) {
            throw new BusinessException("Gán quyền hạn cho vai trò thất bại.");
        }
    }

    @Override
    public void removePermissionsFromRole(final int roleId, final List<Integer> permissionIds) throws BusinessException {
        if (permissionIds == null || permissionIds.isEmpty()) return;

        try {
            DbTransaction.run(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(Connection conn) throws SQLException {
                    roleRepository.revokePermissionsFromRole(conn, roleId, permissionIds);
                    return null;
                }
            });
        } catch (SQLException e) {
            throw new BusinessException("Gỡ bỏ quyền hạn cho vai trò thất bại.");
        }
    }

    @Override
    public void updateRolePermissions(final RoleUpdateRequest dto) throws BusinessException {
        if (dto == null || dto.getId() == null) {
            throw new BusinessException("Yêu cầu mã vai trò hợp lệ.");
        }

        try {
            DbTransaction.run(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(Connection conn) throws SQLException, BusinessException {
                    Role role = roleQueryReadService.getById(conn, dto.getId());
                    
                    if (role == null) {
                        throw new BusinessException("Cập nhật thất bại: Vai trò không tồn tại.");
                    }
                    if (role.isSystem()) {
                        throw new BusinessException("Hành động bị chặn: Không thể thay đổi vai trò hệ thống.");
                    }

                    syncRolePermissions(conn, dto);
                    return null;
                }
            });
        } catch (SQLException e) {
            throw new BusinessException("Lỗi cập nhật dữ liệu quyền vào hệ thống.");
        }
    }

    private void syncRolePermissions(Connection conn, RoleUpdateRequest dto) throws SQLException {
        List<Integer> effectivePermissionIds = resolveEffectivePermissionIds(conn, dto);
        roleRepository.clearRolePermissions(conn, dto.getId());
        
        if (!effectivePermissionIds.isEmpty()) {
            roleRepository.grantPermissionsToRole(conn, dto.getId(), effectivePermissionIds);
        }
    }

    private List<Integer> resolveEffectivePermissionIds(Connection conn, RoleUpdateRequest dto) throws SQLException {
        Set<Integer> uniquePermissionIds = new LinkedHashSet<Integer>();

        if (dto.getPermissionIds() != null) {
            uniquePermissionIds.addAll(dto.getPermissionIds());
        }

        if (dto.getAssignedRoleIds() != null) {
            for (Integer assignedRoleId : dto.getAssignedRoleIds()) {
                if (assignedRoleId == null || assignedRoleId.equals(dto.getId())) {
                    continue;
                }
                uniquePermissionIds.addAll(roleRepository.findPermissionIdsForRole(conn, assignedRoleId));
            }
        }
        return new ArrayList<Integer>(uniquePermissionIds);
    }
}