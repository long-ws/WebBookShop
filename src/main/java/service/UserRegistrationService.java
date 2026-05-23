package service;

import java.sql.Connection;
import java.sql.SQLException;

import dto.user.LocalUserRegistrationRequest;
import dto.user.OAuthUserRegistrationRequest;
import exception.BusinessException;

public interface UserRegistrationService {

	long registerLocalUser(LocalUserRegistrationRequest request) throws BusinessException;

	long registerLocalUser(Connection conn, LocalUserRegistrationRequest request) throws BusinessException, SQLException;

	long registerOAuthUser(OAuthUserRegistrationRequest request) throws BusinessException;

	long registerOAuthUser(Connection conn, OAuthUserRegistrationRequest request) throws BusinessException, SQLException;
}
