package service;

import java.sql.Connection;
import java.sql.SQLException;

import beans.common.Language;
import beans.user.UserAccount;
import beans.user.UserLocalAuth;
import beans.user.UserOAuthAuth;
import beans.user.UserProfile;
import constants.SystemConstants;
import dto.user.LocalUserRegistrationRequest;
import dto.user.OAuthUserRegistrationRequest;
import dto.user.UserProfileRequest;
import exception.BusinessException;
import mapper.user.UserMapper;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import utils.BCryptPasswordEncoder;
import utils.DbTransaction;
import utils.PasswordEncoder;
import validator.core.ValidationResult;
import validator.user.LocalUserRegistrationValidator;
import validator.user.OAuthUserRegistrationValidator;

public class UserRegistrationServiceImpl implements UserRegistrationService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final LocalUserRegistrationValidator localUserRegistrationValidator;
	private final OAuthUserRegistrationValidator oauthUserRegistrationValidator;

	public UserRegistrationServiceImpl() {
		this(new UserRepositoryImpl(), new BCryptPasswordEncoder(), new UserMapper(),
				new LocalUserRegistrationValidator(), new OAuthUserRegistrationValidator());
	}

	public UserRegistrationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
			UserMapper userMapper, LocalUserRegistrationValidator localUserRegistrationValidator,
			OAuthUserRegistrationValidator oauthUserRegistrationValidator) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userMapper = userMapper;
		this.localUserRegistrationValidator = localUserRegistrationValidator;
		this.oauthUserRegistrationValidator = oauthUserRegistrationValidator;
	}

	@Override
	public long registerLocalUser(LocalUserRegistrationRequest request) throws BusinessException {
		try {
			return DbTransaction.run(conn -> registerLocalUser(conn, request));
		} catch (BusinessException e) {
			throw e;
		} catch (SQLException e) {
			handleSQLException(e);
			throw new BusinessException("Lỗi đăng ký tài khoản: " + e.getMessage());
		}
	}

	@Override
	public long registerLocalUser(Connection conn, LocalUserRegistrationRequest request)
			throws BusinessException, SQLException {
		ValidationResult validationResult = localUserRegistrationValidator.validate(request);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		if (userRepository.existUserByUsername(conn, request.getUsername())) {
			throw new BusinessException("Username đã được sử dụng");
		}
		if (userRepository.existUserByEmail(conn, request.getEmail())) {
			throw new BusinessException("Email đã được sử dụng");
		}

		UserAccount account = userMapper.toUserAccount();
		UserProfile profile = createProfile(request.getFullname(), request.getEmail(), null);
		String passwordHash = passwordEncoder.encode(request.getPassword());
		UserLocalAuth localAuth = userMapper.toUserLocalAuth(request, passwordHash);

		return userRepository.createLocalUser(conn, account, profile, localAuth);
	}

	@Override
	public long registerOAuthUser(OAuthUserRegistrationRequest request) throws BusinessException {
		try {
			return DbTransaction.run(conn -> registerOAuthUser(conn, request));
		} catch (BusinessException e) {
			throw e;
		} catch (SQLException e) {
			handleSQLException(e);
			throw new BusinessException("Lỗi đăng ký OAuth: " + e.getMessage());
		}
	}

	@Override
	public long registerOAuthUser(Connection conn, OAuthUserRegistrationRequest request)
			throws BusinessException, SQLException {
		ValidationResult validationResult = oauthUserRegistrationValidator.validate(request);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		if (userRepository.existUserByEmail(conn, request.getEmail())) {
			throw new BusinessException("Email đã được sử dụng");
		}

		UserAccount account = userMapper.toUserAccount();
		UserProfile profile = createProfile(request.getFullname(), request.getEmail(), request.getAvatarUrl());
		UserOAuthAuth oauthAuth = userMapper.toUserOAuthAuth(request);

		return userRepository.createOAuthUser(conn, account, profile, oauthAuth);
	}

	private UserProfile createProfile(String fullname, String email, String avatarUrl) {
		Language defaultLanguage = new Language();
		defaultLanguage.setId(SystemConstants.DEFAULT_LANGUAGE_ID);

		UserProfileRequest profileRequest = new UserProfileRequest.Builder().fullname(fullname).email(email)
				.avatarUrl(avatarUrl).preferredLanguage(defaultLanguage).build();

		return userMapper.toUserProfile(profileRequest);
	}

	private void handleSQLException(SQLException e) throws BusinessException {
		String message = e.getMessage();
		if (message != null) {
			if (message.contains("Duplicate entry")) {
				if (message.contains("email")) {
					throw new BusinessException("Email đã được sử dụng");
				}
				if (message.contains("username")) {
					throw new BusinessException("Username đã được sử dụng");
				}
			}
		}
	}
}
