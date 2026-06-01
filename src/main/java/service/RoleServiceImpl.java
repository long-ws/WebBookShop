package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import beans.common.Permission;
import beans.common.Role;
import dto.permission.ManagePermissionResponse;
import dto.role.ManageRoleResponse;
import dto.role.RoleCreateRequest;
import dto.role.RoleDetailResponse;
import dto.role.RoleEditFormResponse;
import dto.role.RoleUpdateRequest;
import constants.FormConstants;
import exception.BusinessException;
import repository.RoleRepository;
import repository.RoleRepositoryImpl;
import utils.DBConnection;
import utils.DbTransaction;
import utils.TransactionCallback;
import validator.core.ValidationResult;
import validator.role.RoleCreateValidator;
import validator.role.RoleUpdateValidator;

public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;
	private final PermissionService permissionService;
	private final RoleCreateValidator roleCreateValidator;
	private final RoleUpdateValidator roleUpdateValidator;
	private final mapper.RoleMapper roleMapper;

	public RoleServiceImpl() {
		this(new RoleRepositoryImpl(), new PermissionServiceImpl(), new RoleCreateValidator(), new RoleUpdateValidator(),
				new mapper.RoleMapper());
	}

	public RoleServiceImpl(RoleRepository roleRepository) {
		this(roleRepository, new PermissionServiceImpl(), new RoleCreateValidator(), new RoleUpdateValidator(),
				new mapper.RoleMapper());
	}

	public RoleServiceImpl(RoleRepository roleRepository, mapper.RoleMapper roleMapper) {
		this(roleRepository, new PermissionServiceImpl(), new RoleCreateValidator(), new RoleUpdateValidator(), roleMapper);
	}

	public RoleServiceImpl(RoleRepository roleRepository, PermissionService permissionService, mapper.RoleMapper roleMapper) {
		this(roleRepository, permissionService, new RoleCreateValidator(), new RoleUpdateValidator(), roleMapper);
	}

	public RoleServiceImpl(RoleRepository roleRepository, PermissionService permissionService,
			RoleCreateValidator roleCreateValidator, RoleUpdateValidator roleUpdateValidator, mapper.RoleMapper roleMapper) {
		this.roleRepository = roleRepository;
		this.permissionService = permissionService;
		this.roleCreateValidator = roleCreateValidator;
		this.roleUpdateValidator = roleUpdateValidator;
		this.roleMapper = roleMapper;
	}

	@Override
	public RoleDetailResponse createRole(RoleCreateRequest dto) throws BusinessException {
		final ValidationResult validationResult = roleCreateValidator.validate(dto);
		final Map<String, String> errors = new HashMap<>();
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (isCodeExists(dto.getCode())) {
			errors.put("code", "Mã vai trò đã tồn tại");
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		final Role role = roleMapper.toEntity(dto);
		final List<Integer> permissionIds = dto.getPermissionIds() != null ? dto.getPermissionIds() : new ArrayList<>();

		try {
			final int id = DbTransaction.run(new TransactionCallback<Integer>() {
				@Override
				public Integer doInTransaction(Connection conn) throws SQLException {
					final int roleId = roleRepository.insert(conn, role);
					if (!permissionIds.isEmpty()) {
						roleRepository.grantPermissionsToRole(conn, roleId, permissionIds);
					}
					return roleId;
				}
			});
			return getRoleById(id);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi ghi dữ liệu vào Database: " + e.getMessage());
		}
	}

	@Override
	public RoleDetailResponse updateRole(RoleUpdateRequest dto) throws BusinessException {
		final ValidationResult validationResult = roleUpdateValidator.validate(dto);
		final Map<String, String> errors = new HashMap<>();
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (dto.getId() == null) {
			errors.put("id", "Yêu cầu id vai trò");
		}

		final Role existing = getById(dto.getId());
		if (existing == null) {
			errors.put(FormConstants.ERROR_GLOBAL, "Vai trò không tồn tại trên hệ thống");
		} else {
			if (existing.isSystem()) {
				errors.put(FormConstants.ERROR_GLOBAL, "Chặn cập nhật vai trò hệ thống");
			}
			if (isCodeExists(dto.getCode(), dto.getId())) {
				errors.put("code", "Mã vai trò đã tồn tại");
			}
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		final Role role = roleMapper.toEntity(dto);

		try {
			DbTransaction.runVoid(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection conn) throws SQLException {
					roleRepository.update(conn, role);
					syncRolePermissions(conn, dto);
					return null;
				}
			});
			return getRoleById(dto.getId());
		} catch (SQLException e) {
			throw new BusinessException("Lỗi cập nhật dữ liệu vào Database: " + e.getMessage());
		}
	}

	@Override
	public boolean deleteRoles(List<Integer> ids) throws BusinessException {
		if (ids == null || ids.isEmpty()) {
			return false;
		}

		for (final Integer id : ids) {
			final Role role = getById(id);
			if (role != null && role.isSystem()) {
				throw new BusinessException("Không thể xóa vai trò hệ thống");
			}
		}

		try {
			return DbTransaction.run(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(Connection conn) throws SQLException {
					return roleRepository.delete(conn, ids);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi xóa dữ liệu: " + e.getMessage());
		}
	}

	@Override
	public RoleEditFormResponse getRoleEditForm(int roleId) throws BusinessException {
		final RoleDetailResponse detail = getRoleById(roleId);
		if (detail == null) {
			return null;
		}

		final RoleUpdateRequest role = toUpdateDTO(detail);
		final List<ManagePermissionResponse> allPermissions = permissionService.getAllPermissions();
		final List<Role> allRoles = getAllActiveRoles();
		final Map<Integer, List<Integer>> roleIdsByPermission = getMapListRoleHasPermission();
		final Map<Integer, String> permissionRoleMap = roleMapper.toPermissionRoleMap(allPermissions, roleIdsByPermission);
		final List<ManagePermissionResponse> rolePermissions = roleMapper
				.toManagePermissionResponses(getPermissionsByRoleId(roleId));

		return new RoleEditFormResponse(role, allPermissions, allRoles, rolePermissions, permissionRoleMap);
	}

	@Override
	public RoleDetailResponse getRoleById(int id) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			final Optional<Role> roleOpt = roleRepository.findById(conn, id);
			if (roleOpt.isPresent()) {
				return roleMapper.toRoleDetailResponse(roleOpt.get());
			}
			return null;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi tìm kiếm chi tiết vai trò: " + e.getMessage());
		}
	}

	@Override
	public Role getById(int id) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return roleRepository.findById(conn, id).orElse(null);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi tìm kiếm vai trò theo ID: " + e.getMessage());
		}
	}

	@Override
	public List<ManageRoleResponse> getRoles(String orderBy, String orderDir) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			final List<Role> roles = roleRepository.findAll(conn);
			final List<ManageRoleResponse> dtos = new ArrayList<>();
			for (final Role role : roles) {
				dtos.add(roleMapper.toManageRoleResponse(role));
			}
			return dtos;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi lấy danh sách vai trò quản trị: " + e.getMessage());
		}
	}

	@Override
	public long countRoles() throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return roleRepository.count(conn);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi đếm số lượng vai trò: " + e.getMessage());
		}
	}

	@Override
	public List<Role> getAllActiveRoles() throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return roleRepository.findAllActive(conn);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi tải danh sách vai trò đang hoạt động: " + e.getMessage());
		}
	}

	@Override
	public Optional<Role> getRoleByCode(String code) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return roleRepository.findByCode(conn, code);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi truy vấn mã vai trò: " + e.getMessage());
		}
	}

	@Override
	public boolean isCodeExists(String code) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return roleRepository.existsByCode(conn, code);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi kiểm tra tồn tại mã vai trò: " + e.getMessage());
		}
	}

	@Override
	public boolean isCodeExists(String code, int excludeId) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			final Optional<Role> roleOpt = roleRepository.findByCode(conn, code);
			if (roleOpt.isPresent()) {
				return roleOpt.get().getId() != excludeId;
			}
			return false;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi kiểm tra loại trừ mã vai trò: " + e.getMessage());
		}
	}

	@Override
	public void assignPermissionsToRole(int roleId, List<Integer> permissionIds) throws BusinessException {
		if (permissionIds == null || permissionIds.isEmpty()) {
			return;
		}
		try {
			DbTransaction.runVoid(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection conn) throws SQLException {
					roleRepository.grantPermissionsToRole(conn, roleId, permissionIds);
					return null;
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi thực thi gán danh sách quyền: " + e.getMessage());
		}
	}

	@Override
	public void removePermissionsFromRole(int roleId, List<Integer> permissionIds) throws BusinessException {
		if (permissionIds == null || permissionIds.isEmpty()) {
			return;
		}
		try {
			DbTransaction.runVoid(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection conn) throws SQLException {
					roleRepository.revokePermissionsFromRole(conn, roleId, permissionIds);
					return null;
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi thực thi gỡ bỏ danh sách quyền: " + e.getMessage());
		}
	}

	@Override
	public List<Permission> getPermissionsByRoleId(int roleId) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return roleRepository.loadPermissionsForRole(conn, roleId);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi lấy danh sách quyền của vai trò: " + e.getMessage());
		}
	}

	@Override
	public Map<Integer, List<Integer>> getMapListRoleHasPermission() throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return roleRepository.findAllRolePermissionMappings(conn);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi lấy bản đồ vai trò có quyền: " + e.getMessage());
		}
	}

	@Override
	public RoleCreateRequest toCreateDTO(RoleDetailResponse detail) {
		return roleMapper.toRoleCreateRequest(detail);
	}

	@Override
	public RoleUpdateRequest toUpdateDTO(RoleDetailResponse detail) {
		return roleMapper.toRoleUpdateRequest(detail);
	}

	@Override
	public void updateRolePermissions(RoleUpdateRequest dto) throws BusinessException {
		if (dto == null || dto.getId() == null) {
			throw new BusinessException("Yêu cầu mã vai trò (roleId)");
		}

		final Role role = getById(dto.getId());
		if (role == null) {
			throw new BusinessException("Vai trò không tồn tại");
		}
		if (role.isSystem()) {
			throw new BusinessException("Không thể cập nhật quyền cho vai trò hệ thống");
		}

		try {
			DbTransaction.runVoid(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection conn) throws SQLException {
					syncRolePermissions(conn, dto);
					return null;
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi ghi dữ liệu cập nhật quyền vào Database: " + e.getMessage());
		}
	}

	private void syncRolePermissions(Connection conn, RoleUpdateRequest dto) throws SQLException {
		final List<Integer> effectivePermissionIds = resolveEffectivePermissionIds(conn, dto);
		roleRepository.clearRolePermissions(conn, dto.getId());
		if (!effectivePermissionIds.isEmpty()) {
			roleRepository.grantPermissionsToRole(conn, dto.getId(), effectivePermissionIds);
		}
	}

	private List<Integer> resolveEffectivePermissionIds(Connection conn, RoleUpdateRequest dto) throws SQLException {
		final Set<Integer> uniquePermissionIds = new LinkedHashSet<>();
		if (dto.getPermissionIds() != null) {
			uniquePermissionIds.addAll(dto.getPermissionIds());
		}
		if (dto.getAssignedRoleIds() != null) {
			for (final Integer assignedRoleId : dto.getAssignedRoleIds()) {
				if (assignedRoleId == null || assignedRoleId.equals(dto.getId())) {
					continue;
				}
				uniquePermissionIds.addAll(roleRepository.findPermissionIdsForRole(conn, assignedRoleId));
			}
		}
		return new ArrayList<>(uniquePermissionIds);
	}
}
