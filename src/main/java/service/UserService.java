package service;

import java.util.List;

import beans.User;
import dto.AdminUserDetailDTO;
import dto.UserCreateUpdateFormDTO;
import dto.AdminUserListDTO;

public interface UserService {
	UserCreateUpdateFormDTO createUser(UserCreateUpdateFormDTO dto);

	UserCreateUpdateFormDTO updateUser(UserCreateUpdateFormDTO dto);

	boolean deleteUser(long id);

	AdminUserDetailDTO getUserById(long id);

	User getById(long id);

	List<AdminUserListDTO> getUsers(int page, int pageSize, String orderBy, String orderDir);

	long countUsers();

	boolean isUsernameExists(String username);

	boolean isUsernameExists(String username, long excludeId);

	boolean isEmailExists(String email);

	boolean isEmailExists(String email, long excludeId);

	User getUserEntityByUsername(String username);

	void changePassword(long userId, String newPassword);

	boolean incrementTokenVersion(long userId);

	int getTokenVersion(long userId);

	UserCreateUpdateFormDTO toFormDTO(AdminUserDetailDTO detail);
}
