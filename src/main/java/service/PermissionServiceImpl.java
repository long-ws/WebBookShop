package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.common.Permission;
import dto.permission.ManagePermissionResponse;
import dto.permission.PermissionCreateRequest;
import dto.permission.PermissionDetailResponse;
import dto.permission.PermissionUpdateRequest;
import constants.FormConstants;
import exception.BusinessException;
import repository.PermissionRepository;
import repository.PermissionRepositoryImpl;
import utils.DBConnection;
import utils.DbTransaction;
import utils.TransactionCallback;
import validator.core.ValidationResult;
import validator.permission.PermissionCreateValidator;
import validator.permission.PermissionUpdateValidator;

public class PermissionServiceImpl implements PermissionService {

	private final PermissionRepository permissionRepository;
	private final PermissionCreateValidator permissionCreateValidator;
	private final PermissionUpdateValidator permissionUpdateValidator;
	private final mapper.PermissionMapper permissionMapper;

	public PermissionServiceImpl() {
		this(new PermissionRepositoryImpl(), new PermissionCreateValidator(), new PermissionUpdateValidator(),
				new mapper.PermissionMapper());
	}

	public PermissionServiceImpl(PermissionRepository permissionRepository) {
		this(permissionRepository, new PermissionCreateValidator(), new PermissionUpdateValidator(),
				new mapper.PermissionMapper());
	}

	public PermissionServiceImpl(PermissionRepository permissionRepository, mapper.PermissionMapper permissionMapper) {
		this(permissionRepository, new PermissionCreateValidator(), new PermissionUpdateValidator(), permissionMapper);
	}

	public PermissionServiceImpl(PermissionRepository permissionRepository,
			PermissionCreateValidator permissionCreateValidator, PermissionUpdateValidator permissionUpdateValidator,
			mapper.PermissionMapper permissionMapper) {
		this.permissionRepository = permissionRepository;
		this.permissionCreateValidator = permissionCreateValidator;
		this.permissionUpdateValidator = permissionUpdateValidator;
		this.permissionMapper = permissionMapper;
	}

	@Override
	public PermissionDetailResponse createPermission(PermissionCreateRequest dto) throws BusinessException {
		final ValidationResult validationResult = permissionCreateValidator.validate(dto);
		final Map<String, String> errors = new HashMap<>();
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (isCodeExists(dto.getCode())) {
			errors.put("code", "Mã quyền đã tồn tại");
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		final Permission permission = permissionMapper.toEntity(dto);

		try {
			final int id = DbTransaction.run(new TransactionCallback<Integer>() {
				@Override
				public Integer doInTransaction(Connection conn) throws SQLException {
					return permissionRepository.insert(conn, permission);
				}
			});
			return getPermissionById(id);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi ghi dữ liệu vào Database: " + e.getMessage());
		}
	}

	@Override
	public PermissionDetailResponse updatePermission(PermissionUpdateRequest dto) throws BusinessException {
		final ValidationResult validationResult = permissionUpdateValidator.validate(dto);
		final Map<String, String> errors = new HashMap<>();
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (dto.getId() == null) {
			errors.put("id", "Yêu cầu id quyền");
		}

		final Permission existing = getById(dto.getId());

		if (existing == null) {
			errors.put(FormConstants.ERROR_GLOBAL, "Quyền không tồn tại trên hệ thống");
		} else {
			if (existing.isSystem()) {
				errors.put(FormConstants.ERROR_GLOBAL, "Không thể cập nhật quyền hệ thống");
			}

			if (isCodeExists(dto.getCode(), dto.getId())) {
				errors.put("code", "Mã quyền đã tồn tại");
			}
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		final Permission permission = permissionMapper.toEntity(dto);
		permission.setCode(existing.getCode());

		try {
			DbTransaction.runVoid(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection conn) throws SQLException {
					permissionRepository.update(conn, permission);
					return null;
				}
			});
			return getPermissionById(dto.getId());
		} catch (SQLException e) {
			throw new BusinessException("Lỗi cập nhật dữ liệu vào Database: " + e.getMessage());
		}
	}

	@Override
	public boolean deletePermissions(List<Integer> ids) throws BusinessException {
		if (ids == null || ids.isEmpty()) {
			return false;
		}

		for (final Integer id : ids) {
			final Permission permission = getById(id);
			if (permission != null && permission.isSystem()) {
				throw new BusinessException("Không thể xóa quyền hệ thống");
			}
		}

		try {
			return DbTransaction.run(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(Connection conn) throws SQLException {
					return permissionRepository.delete(conn, ids);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi xóa dữ liệu quyền hạn: " + e.getMessage());
		}
	}

	@Override
	public PermissionDetailResponse getPermissionById(int id) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			final Optional<Permission> permissionOpt = permissionRepository.findById(conn, id);
			if (permissionOpt.isPresent()) {
				return permissionMapper.toPermissionDetailResponse(permissionOpt.get());
			}
			return null;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi tìm kiếm chi tiết quyền: " + e.getMessage());
		}
	}

	@Override
	public Permission getById(int id) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return permissionRepository.findById(conn, id).orElse(null);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi truy vấn thực thể quyền hạn theo ID: " + e.getMessage());
		}
	}

	@Override
	public List<ManagePermissionResponse> getPermissions(String orderBy, String orderDir) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			final List<Permission> permissions = permissionRepository.findAll(conn);
			final List<ManagePermissionResponse> dtos = new ArrayList<>();
			for (final Permission permission : permissions) {
				dtos.add(permissionMapper.toManagePermissionResponse(permission));
			}
			return dtos;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi lấy danh sách phân trang phân quyền: " + e.getMessage());
		}
	}

	@Override
	public long countPermissions() throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return permissionRepository.count(conn);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi đếm tổng số quyền hạn: " + e.getMessage());
		}
	}

	@Override
	public boolean isCodeExists(String code) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return permissionRepository.existsByCode(conn, code);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi kiểm tra tồn tại mã quyền: " + e.getMessage());
		}
	}

	@Override
	public boolean isCodeExists(String code, int excludeId) throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			final Optional<Permission> permissionOpt = permissionRepository.findByCode(conn, code);
			if (permissionOpt.isPresent()) {
				return permissionOpt.get().getId() != excludeId;
			}
			return false;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi đối chiếu trùng lặp mã quyền: " + e.getMessage());
		}
	}

	@Override
	public List<String> getAllModules() throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			return permissionRepository.findAllModules(conn);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi lấy danh mục Modules: " + e.getMessage());
		}
	}

	@Override
	public List<ManagePermissionResponse> getAllPermissions() throws BusinessException {
		try (Connection conn = DBConnection.getConnection()) {
			final List<Permission> permissions = permissionRepository.findAll(conn);
			final List<ManagePermissionResponse> dtos = new ArrayList<>();
			for (final Permission permission : permissions) {
				dtos.add(permissionMapper.toManagePermissionResponse(permission));
			}
			return dtos;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi tải toàn bộ danh sách quyền hạn: " + e.getMessage());
		}
	}

	@Override
	public PermissionCreateRequest toCreateDTO(PermissionDetailResponse detail) {
		return permissionMapper.toPermissionCreateRequest(detail);
	}

	@Override
	public PermissionUpdateRequest toUpdateDTO(PermissionDetailResponse detail) {
		return permissionMapper.toPermissionUpdateRequest(detail);
	}
}
