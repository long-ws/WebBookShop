package service;

import java.sql.Connection;

import dto.user.ChangePasswordRequest;
import dto.user.ResetPasswordRequest;
import exception.BusinessException;

public interface PasswordService {
	void changePassword(Connection conn, long userId, ChangePasswordRequest request) throws BusinessException;
	
	void resetPassword(long userId, ResetPasswordRequest request) throws BusinessException;
}
