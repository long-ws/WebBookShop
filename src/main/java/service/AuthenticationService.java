package service;

import beans.User;
import dto.user.AdminSigninRequest;
import dto.user.SigninRequest;
import dto.user.UserCreateRequest;
import dto.user.UserDetailResponse;
import exception.BusinessException;

public interface AuthenticationService {

	UserDetailResponse signupUser(UserCreateRequest dto) throws BusinessException;

	void changePassword(long userId, String newPassword) throws BusinessException;

	boolean incrementTokenVersion(long userId) throws BusinessException;

	int getTokenVersion(long userId) throws BusinessException;

	User authenticate(SigninRequest request) throws BusinessException;

	User authenticateAdmin(AdminSigninRequest request) throws BusinessException;
}
