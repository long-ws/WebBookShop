package service;

import java.util.List;

import beans.User;
import dto.user.UserManageResponse;
import dto.user.UserCreateRequest;
import dto.user.UserDetailResponse;
import dto.user.UserUpdateRequest;
import exception.BusinessException;

public interface UserManagementService {

	boolean deleteUsers(List<Long> ids) throws BusinessException;

	User getById(long id) throws BusinessException;

	UserDetailResponse createUser(UserCreateRequest dto) throws BusinessException;

	UserDetailResponse updateUser(UserUpdateRequest dto) throws BusinessException;

	UserDetailResponse getUserById(long id) throws BusinessException;

	List<UserManageResponse> getUsers(String orderBy, String orderDir) throws BusinessException;

	long countUsers() throws BusinessException;

	boolean isUsernameExists(String username) throws BusinessException;

	boolean isUsernameExists(String username, long excludeId) throws BusinessException;

	boolean isEmailExists(String email) throws BusinessException;

	boolean isEmailExists(String email, long excludeId) throws BusinessException;

	User getUserEntityByUsername(String username) throws BusinessException;

	UserCreateRequest toCreateDTO(UserDetailResponse detail);

	UserUpdateRequest toUpdateDTO(UserDetailResponse detail);
}
