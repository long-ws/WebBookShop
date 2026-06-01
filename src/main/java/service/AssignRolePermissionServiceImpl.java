package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import beans.common.Permission;
import beans.common.Role;
import dto.role.RoleUpdateRequest;
import exception.BusinessException;
import repository.PermissionRepository;
import repository.PermissionRepositoryImpl;
import repository.RoleRepository;
import repository.RoleRepositoryImpl;
import utils.DbTransaction;
import utils.TransactionCallback;

public class AssignRolePermissionServiceImpl implements AssignRolePemissionService {

	private final RoleRepository roleRepository;
	private final RoleQueryReadService roleQueryReadService;
	private final PermissionRepository permissionRepository;

	public AssignRolePermissionServiceImpl() {
		this(new RoleRepositoryImpl(), new PermissionRepositoryImpl(), new RoleQueryReadService());
	}

	public AssignRolePermissionServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository, RoleQueryReadService queryService) {
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
		this.roleQueryReadService = queryService;
	}

	@Override
	public void assignPermissionsToRole(final int roleId, final List<Integer> permissionIds) throws BusinessException {
		if (permissionIds == null || permissionIds.isEmpty())
			return;

		try {
			DbTransaction.run(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection conn) throws SQLException {
					Set<Integer> effectivePermissionIds = new LinkedHashSet<Integer>(permissionIds);
					effectivePermissionIds = expandWithRequiredViewPermissions(conn, effectivePermissionIds);
					roleRepository.grantPermissionsToRole(conn, roleId, new ArrayList<Integer>(effectivePermissionIds));
					return null;
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Gán quyền hạn cho vai trò thất bại.");
		} catch (BusinessException e) {
			throw e;
		}
	}

	@Override
	public void removePermissionsFromRole(final int roleId, final List<Integer> permissionIds) throws BusinessException {
		if (permissionIds == null || permissionIds.isEmpty())
			return;

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
		} catch (BusinessException e) {
			throw e;
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

	private List<Integer> resolveEffectivePermissionIds(Connection conn, RoleUpdateRequest dto) throws SQLException, BusinessException {
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
		uniquePermissionIds = expandWithRequiredViewPermissions(conn, uniquePermissionIds);
		return new ArrayList<Integer>(uniquePermissionIds);
	}

	private Set<Integer> expandWithRequiredViewPermissions(Connection conn, Set<Integer> permissionIds) throws SQLException, BusinessException {
		if (permissionIds == null || permissionIds.isEmpty()) {
			return new LinkedHashSet<Integer>();
		}

		Set<Integer> result = new LinkedHashSet<Integer>(permissionIds);
		List<Integer> snapshot = new ArrayList<Integer>(permissionIds);

		for (Integer permissionId : snapshot) {
			if (permissionId == null) {
				continue;
			}
			Optional<Permission> permissionOpt = permissionRepository.findById(conn, permissionId);
			if (permissionOpt.isEmpty()) {
				continue;
			}
			String permissionCode = permissionOpt.get().getCode();
			String requiredViewCode = resolveRequiredViewCode(permissionCode);
			if (requiredViewCode == null) {
				continue;
			}
			Optional<Permission> viewPermissionOpt = permissionRepository.findByCode(conn, requiredViewCode);
			if (viewPermissionOpt.isEmpty()) {
				throw new BusinessException("Thiếu quyền bắt buộc: " + requiredViewCode);
			}
			result.add(viewPermissionOpt.get().getId());
		}

		return result;
	}

	private String resolveRequiredViewCode(String permissionCode) {
		if (permissionCode == null || permissionCode.isBlank()) {
			return null;
		}
		if (isViewPermission(permissionCode)) {
			return null;
		}
		int dotIndex = permissionCode.lastIndexOf('.');
		if (dotIndex <= 0 || dotIndex >= permissionCode.length() - 1) {
			return null;
		}
		String module = permissionCode.substring(0, dotIndex);
		return module + ".view";
	}

	private boolean isViewPermission(String permissionCode) {
		return permissionCode != null && permissionCode.endsWith(".view");
	}

}
