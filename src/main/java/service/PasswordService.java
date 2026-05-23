package service;

import dto.user.ChangePasswordRequest;
import dto.user.ResetPasswordRequest;
import exception.BusinessException;

public interface PasswordService {
	void changePassword(long userId, ChangePasswordRequest request) throws BusinessException;
	
	void resetPassword(long userId, ResetPasswordRequest request) throws BusinessException;
}
