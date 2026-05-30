package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.common.Role;
import constants.SystemConstants;
import dto.role.RoleCreateRequest;
import dto.role.RoleDetailResponse;
import dto.role.RoleUpdateRequest;
import exception.BusinessException;
import mapper.RoleMapper;
import repository.RoleRepository;
import repository.RoleRepositoryImpl;
import utils.DbTransaction;
import utils.TransactionCallback;
import validator.core.ValidationResult;
import validator.role.RoleCreateValidator;
import validator.role.RoleUpdateValidator;

public class RoleQueryWriteService {

	private final RoleRepository roleRepository;
	private final RoleCreateValidator createValidator;
	private final RoleUpdateValidator updateValidator;
	private final RoleMapper roleMapper;
	private final RoleQueryReadService roleQueryReadService;

	public RoleQueryWriteService() {
		this(new RoleRepositoryImpl(), new RoleCreateValidator(), new RoleUpdateValidator(), new RoleMapper(), new RoleQueryReadService());
	}

	public RoleQueryWriteService(RoleRepository roleRepository, RoleCreateValidator createValidator, RoleUpdateValidator updateValidator, RoleMapper roleMapper, RoleQueryReadService queryService) {
		this.roleRepository = roleRepository;
		this.createValidator = createValidator;
		this.updateValidator = updateValidator;
		this.roleMapper = roleMapper;
		this.roleQueryReadService = queryService;
	}

	public RoleDetailResponse createRole(final RoleCreateRequest dto) throws BusinessException {
		ValidationResult validationResult = createValidator.validate(dto);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		try {
			return DbTransaction.run(new TransactionCallback<RoleDetailResponse>() {
				@Override
				public RoleDetailResponse doInTransaction(Connection conn) throws SQLException, BusinessException {
					if (roleRepository.existsByCode(conn, dto.getCode())) {
						Map<String, String> errors = new HashMap<String, String>();
						errors.put("code", "Mã vai trò '" + dto.getCode() + "' đã tồn tại.");
						throw new BusinessException(errors);
					}

					Role role = roleMapper.toEntity(dto);
					int newId = roleRepository.insert(conn, role);

					return roleQueryReadService.getRoleById(conn, newId);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi tạo vai trò.");
		}
	}

	public RoleDetailResponse updateRole(final RoleUpdateRequest dto) throws BusinessException {
		ValidationResult validationResult = updateValidator.validate(dto);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		try {
			return DbTransaction.run(new TransactionCallback<RoleDetailResponse>() {
				@Override
				public RoleDetailResponse doInTransaction(Connection conn) throws SQLException, BusinessException {
					Role existing = roleQueryReadService.getById(conn, dto.getId());
					if (existing == null) {
						Map<String, String> errors = new HashMap<String, String>();
						errors.put(SystemConstants.ERROR_GLOBAL, "Vai trò không tồn tại.");
						throw new BusinessException(errors);
					}

					if (existing.isSystem()) {
						Map<String, String> errors = new HashMap<String, String>();
						errors.put(SystemConstants.ERROR_GLOBAL, "Không được phép sửa vai trò hệ thống.");
						throw new BusinessException(errors);
					}

					if (roleRepository.findByCode(conn, dto.getCode()).isPresent() && roleRepository.findByCode(conn, dto.getCode()).get().getId() != dto.getId()) {
						Map<String, String> errors = new HashMap<String, String>();
						errors.put(SystemConstants.ERROR_GLOBAL, "Mã vai trò đã được sử dụng.");
						throw new BusinessException(errors);
					}

					Role role = roleMapper.toEntity(dto);
					roleRepository.update(conn, role);
					return roleQueryReadService.getRoleById(conn, dto.getId());
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi cập nhật vai trò.");
		}
	}

	public boolean deleteRoles(final List<Integer> ids) throws BusinessException {
		if (ids == null || ids.isEmpty()) {
			Map<String, String> errors = new HashMap<String, String>();
			errors.put(SystemConstants.ERROR_GLOBAL, "Vui lòng chọn vai trò để xóa.");
			throw new BusinessException(errors);
		}

		try {
			return DbTransaction.run(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(Connection conn) throws SQLException, BusinessException {
					for (Integer id : ids) {
						Role role = roleQueryReadService.getById(conn, id);
						if (role == null || role.isSystem()) {
							Map<String, String> errors = new HashMap<String, String>();
							errors.put(SystemConstants.ERROR_GLOBAL, "Không thể xóa vai trò hệ thống hoặc vai trò không tồn tại.");
							throw new BusinessException(errors);
						}
					}
					return roleRepository.delete(conn, ids);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi khi xóa vai trò.");
		}
	}
}