package service;

import java.util.List;

import beans.common.Permission;
import dto.permission.ManagePermissionResponse;
import dto.permission.PermissionCreateRequest;
import dto.permission.PermissionDetailResponse;
import dto.permission.PermissionUpdateRequest;
import exception.BusinessException;
import mapper.PermissionMapper;
import repository.PermissionRepository;
import repository.PermissionRepositoryImpl;
import validator.permission.PermissionCreateValidator;
import validator.permission.PermissionUpdateValidator;

public class PermissionServiceImpl implements PermissionService {

	private final PermissionQueryReadService permissionQueryReadService;
	private final PermissionQueryWriteService permissionQueryWriteService;

	public PermissionServiceImpl() {
		this(new PermissionRepositoryImpl(), new PermissionCreateValidator(), new PermissionUpdateValidator(), new PermissionMapper());
	}

	public PermissionServiceImpl(PermissionRepository permissionRepository) {
		this(permissionRepository, new PermissionCreateValidator(), new PermissionUpdateValidator(), new PermissionMapper());
	}

	public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
		this(permissionRepository, new PermissionCreateValidator(), new PermissionUpdateValidator(), permissionMapper);
	}

	public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionCreateValidator permissionCreateValidator, PermissionUpdateValidator permissionUpdateValidator,
			PermissionMapper permissionMapper) {
		this.permissionQueryReadService = new PermissionQueryReadService(permissionRepository, permissionMapper);
		this.permissionQueryWriteService = new PermissionQueryWriteService(permissionRepository, permissionCreateValidator, permissionUpdateValidator, permissionMapper);
	}

	@Override
	public PermissionDetailResponse createPermission(final PermissionCreateRequest dto) throws BusinessException {
		return permissionQueryWriteService.createPermission(dto);
	}

	@Override
	public PermissionDetailResponse updatePermission(final PermissionUpdateRequest dto) throws BusinessException {
		return permissionQueryWriteService.updatePermission(dto);
	}

	@Override
	public boolean deletePermissions(final List<Integer> ids) throws BusinessException {
		return permissionQueryWriteService.deletePermissions(ids);
	}

	@Override
	public Permission getById(int id) throws BusinessException {
		return permissionQueryReadService.getById(id);
	}

	@Override
	public PermissionDetailResponse getPermissionById(int id) throws BusinessException {
		return permissionQueryReadService.getPermissionById(id);
	}

	@Override
	public boolean isCodeExists(String code) throws BusinessException {
		return permissionQueryReadService.isCodeExists(code);
	}

	@Override
	public boolean isCodeExists(String code, int excludeId) throws BusinessException {
		return permissionQueryReadService.isCodeExists(code, excludeId);
	}

	@Override
	public List<ManagePermissionResponse> getAllPermissions() throws BusinessException {
		return permissionQueryReadService.getAllPermissions();
	}

	@Override
	public long countPermissions() throws BusinessException {
		return permissionQueryReadService.countPermissions();
	}

	@Override
	public List<String> getAllModules() throws BusinessException {
		return permissionQueryReadService.getAllModules();
	}

	@Override
	public PermissionCreateRequest toCreateDTO(PermissionDetailResponse detail) {
		return permissionQueryReadService.toCreateDTO(detail);
	}

	@Override
	public PermissionUpdateRequest toUpdateDTO(PermissionDetailResponse detail) {
		return permissionQueryReadService.toUpdateDTO(detail);
	}
}