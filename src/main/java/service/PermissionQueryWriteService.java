package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.common.Permission;
import dto.permission.PermissionCreateRequest;
import dto.permission.PermissionDetailResponse;
import dto.permission.PermissionUpdateRequest;
import exception.BusinessException;
import mapper.PermissionMapper;
import repository.PermissionRepository;
import repository.PermissionRepositoryImpl;
import utils.DbTransaction;
import utils.TransactionCallback;
import validator.core.ValidationResult;
import validator.permission.PermissionCreateValidator;
import validator.permission.PermissionUpdateValidator;

public class PermissionQueryWriteService {

	private final PermissionRepository permissionRepository;
	private final PermissionCreateValidator permissionCreateValidator;
	private final PermissionUpdateValidator permissionUpdateValidator;
	private final PermissionMapper permissionMapper;

	public PermissionQueryWriteService() {
		this(new PermissionRepositoryImpl(), new PermissionCreateValidator(), new PermissionUpdateValidator(), new PermissionMapper());
	}

	public PermissionQueryWriteService(PermissionRepository permissionRepository, PermissionCreateValidator permissionCreateValidator, PermissionUpdateValidator permissionUpdateValidator,
			PermissionMapper permissionMapper) {
		this.permissionRepository = permissionRepository;
		this.permissionCreateValidator = permissionCreateValidator;
		this.permissionUpdateValidator = permissionUpdateValidator;
		this.permissionMapper = permissionMapper;
	}

	public PermissionDetailResponse createPermission(final PermissionCreateRequest dto) throws BusinessException {
		final ValidationResult validationResult = permissionCreateValidator.validate(dto);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		try {
			return DbTransaction.run(new TransactionCallback<PermissionDetailResponse>() {
				@Override
				public PermissionDetailResponse doInTransaction(Connection conn) throws SQLException, BusinessException {
					if (isCodeExists(conn, dto.getCode())) {
						Map<String, String> errors = new HashMap<>();
						errors.put("code", "Mã quyền '" + dto.getCode() + "' đã tồn tại trên hệ thống");
						throw new BusinessException(errors);
					}

					final Permission permission = permissionMapper.toEntity(dto);
					final int id = permissionRepository.insert(conn, permission);

					return getPermissionById(conn, id);
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Không thể thêm mới quyền vào hệ thống. Vui lòng kiểm tra lại.");
		}
	}

	public PermissionDetailResponse updatePermission(final PermissionUpdateRequest dto) throws BusinessException {
		final ValidationResult validationResult = permissionUpdateValidator.validate(dto);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		if (dto.getId() == null) {
			throw new BusinessException("id", "Mã ID xác định quyền không được để trống");
		}

		try {
			return DbTransaction.run(new TransactionCallback<PermissionDetailResponse>() {
				@Override
				public PermissionDetailResponse doInTransaction(Connection conn) throws SQLException, BusinessException {
					final Permission existing = getById(conn, dto.getId());

					if (existing == null) {
						throw new BusinessException("Thao tác thất bại: Quyền này đã bị xóa hoặc không tồn tại.");
					}

					if (existing.isSystem()) {
						throw new BusinessException("Từ chối thực hiện: Không cho phép thay đổi quyền mặc định.");
					}

					if (isCodeExists(conn, dto.getCode(), dto.getId())) {
						throw new BusinessException("code", "Mã quyền '" + dto.getCode() + "' đã được sử dụng.");
					}

					final Permission permission = permissionMapper.toEntity(dto);
					permissionRepository.update(conn, permission);

					return getPermissionById(conn, dto.getId());
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Cập nhật thông tin quyền thất bại. Vui lòng thử lại sau.");
		}
	}

	public boolean deletePermissions(final List<Integer> ids) throws BusinessException {
		if (ids == null || ids.isEmpty()) {
			throw new BusinessException("Vui lòng chọn ít nhất một quyền để thực hiện xóa");
		}

		try {
			return DbTransaction.run(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(Connection conn) throws SQLException, BusinessException {
					for (final Integer id : ids) {
						final Permission permission = getById(conn, id);
						if (permission == null) {
							throw new BusinessException("Hành động bị hủy: Không tồn tại quyền mang mã số ID = " + id);
						}
						if (permission.isSystem()) {
							throw new BusinessException("Từ chối thực hiện: Quyền '" + permission.getName() + "' là quyền mặc định và không thể xóa.");
						}
					}
					return permissionRepository.delete(conn, ids);
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Quá trình xóa quyền gặp sự cố. Hệ thống chưa thể thực hiện hành động này.");
		}
	}

	private PermissionDetailResponse getPermissionById(final Connection conn, final int id) throws SQLException {
		Optional<Permission> permissionOpt = permissionRepository.findById(conn, id);
		if (permissionOpt.isPresent()) {
			return permissionMapper.toPermissionDetailResponse(permissionOpt.get());
		}
		return null;
	}

	private Permission getById(final Connection conn, final int id) throws SQLException {
		Optional<Permission> permissionOpt = permissionRepository.findById(conn, id);
		if (permissionOpt.isPresent()) {
			return permissionOpt.get();
		}
		return null;
	}

	private boolean isCodeExists(final Connection conn, final String code) throws SQLException {
		return permissionRepository.existsByCode(conn, code);
	}

	private boolean isCodeExists(final Connection conn, final String code, final int excludeId) throws SQLException {
		final Optional<Permission> permissionOpt = permissionRepository.findByCode(conn, code);
		return permissionOpt.isPresent() && permissionOpt.get().getId() != excludeId;
	}
}