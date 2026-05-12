package service.user;

import beans.user.User;
import dto.UserFormDTO;

public interface UserService {

	UserFormDTO createUser(UserFormDTO dto);

	boolean isUsernameExists(String username);

	boolean isEmailExists(String email);

	User getUserEntityByUsername(String username);

	User getById(long id);
}