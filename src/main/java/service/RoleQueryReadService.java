package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.common.Permission;
import beans.common.Role;
import dto.permission.ManagePermissionResponse;
import dto.role.ManageRoleResponse;
import dto.role.RoleDetailResponse;
import dto.role.RoleEditFormResponse;
import dto.role.RoleUpdateRequest;
import exception.BusinessException;
import mapper.RoleMapper;
import repository.RoleRepository;
import repository.RoleRepositoryImpl;
import utils.DbTransaction;
import utils.TransactionCallback;

public class RoleQueryReadService {

	private final RoleRepository roleRepository;
	private final RoleMapper roleMapper;
	private final PermissionService permissionService;

	public RoleQueryReadService() {
		this(new RoleRepositoryImpl(), new RoleMapper(), new PermissionServiceImpl());
	}

	public RoleQueryReadService(RoleRepository roleRepository, RoleMapper roleMapper, PermissionService permissionService) {
		this.roleRepository = roleRepository;
		this.roleMapper = roleMapper;
		this.permissionService = permissionService;
	}

	public RoleDetailResponse getRoleById(final int id) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<RoleDetailResponse>() {
				@Override
				public RoleDetailResponse doInTransaction(Connection conn) throws SQLException {
					Optional<Role> optRole = roleRepository.findById(conn, id);
					return optRole.isPresent() ? roleMapper.toRoleDetailResponse(optRole.get()) : null;
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi truy vấn thông tin vai trò.");
		}
	}

	public Role getById(final int id) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Role>() {
				@Override
				public Role doInTransaction(Connection conn) throws SQLException {
					Optional<Role> optRole = roleRepository.findById(conn, id);
					return optRole.isPresent() ? optRole.get() : null;
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi truy vấn vai trò.");
		}
	}

	public Role getRoleByCode(final String code) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Role>() {
				@Override
				public Role doInTransaction(Connection conn) throws SQLException {
					Optional<Role> optRole = roleRepository.findByCode(conn, code);
					return optRole.isPresent() ? optRole.get() : null;
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi truy vấn tìm kiếm vai trò.");
		}
	}

	public List<ManageRoleResponse> getRoles() throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<List<ManageRoleResponse>>() {
				@Override
				public List<ManageRoleResponse> doInTransaction(Connection conn) throws SQLException {
					List<Role> roles = roleRepository.findAll(conn);
					List<ManageRoleResponse> dtos = new ArrayList<ManageRoleResponse>();
					for (Role role : roles) {
						dtos.add(roleMapper.toManageRoleResponse(role));
					}
					return dtos;
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi lấy danh sách vai trò.");
		}
	}

	public List<Role> getAllActiveRoles() throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<List<Role>>() {
				@Override
				public List<Role> doInTransaction(Connection conn) throws SQLException {
					return roleRepository.findAllActive(conn);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi tải danh sách vai trò hoạt động.");
		}
	}

	public long countRoles() throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(Connection conn) throws SQLException {
					return roleRepository.count(conn);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi thống kê vai trò.");
		}
	}

	public boolean isCodeExists(final String code) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(Connection conn) throws SQLException {
					return roleRepository.existsByCode(conn, code);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi kiểm tra mã vai trò.");
		}
	}

	public boolean isCodeExists(final String code, final int excludeId) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(Connection conn) throws SQLException {
					Optional<Role> optRole = roleRepository.findByCode(conn, code);
					if (optRole.isPresent()) {
						return optRole.get().getId() != excludeId;
					}
					return false;
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi kiểm tra trùng lặp mã.");
		}
	}

	public List<Permission> getPermissionsByRoleId(final int roleId) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<List<Permission>>() {
				@Override
				public List<Permission> doInTransaction(Connection conn) throws SQLException {
					return roleRepository.loadPermissionsForRole(conn, roleId);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi lấy danh sách quyền của vai trò.");
		}
	}

	public Map<Integer, List<Integer>> getMapListRoleHasPermission() throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Map<Integer, List<Integer>>>() {
				@Override
				public Map<Integer, List<Integer>> doInTransaction(Connection conn) throws SQLException {
					return roleRepository.findAllRolePermissionMappings(conn);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi lấy sơ đồ quyền.");
		}
	}

	public RoleEditFormResponse getRoleEditForm(final int roleId) throws BusinessException {
		final List<ManagePermissionResponse> allPerms = permissionService.getAllPermissions();

		try {
			return DbTransaction.run(new TransactionCallback<RoleEditFormResponse>() {
				@Override
				public RoleEditFormResponse doInTransaction(Connection conn) throws SQLException, BusinessException {
					Optional<Role> optRole = roleRepository.findById(conn, roleId);
					if (!optRole.isPresent())
						return null;

					RoleDetailResponse detail = roleMapper.toRoleDetailResponse(optRole.get());
					RoleUpdateRequest role = roleMapper.toRoleUpdateRequest(detail);

					List<Role> allRoles = roleRepository.findAllActive(conn);
					Map<Integer, List<Integer>> mapping = roleRepository.findAllRolePermissionMappings(conn);

					Map<Integer, String> permRoleMap = roleMapper.toPermissionRoleMap(allPerms, mapping);
					List<ManagePermissionResponse> rolePerms = roleMapper.toManagePermissionResponses(roleRepository.loadPermissionsForRole(conn, roleId));

					return new RoleEditFormResponse(role, allPerms, allRoles, rolePerms, permRoleMap);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi tải form sửa vai trò.");
		}
	}

	public RoleDetailResponse getRoleById(Connection conn, int id) throws SQLException {
		Optional<Role> opt = roleRepository.findById(conn, id);
		return opt.isPresent() ? roleMapper.toRoleDetailResponse(opt.get()) : null;
	}

	public Role getById(Connection conn, int id) throws SQLException {
		return roleRepository.findById(conn, id).orElse(null);
	}
}