package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import beans.User;
import beans.common.Role;
import constants.PermissionConstants;
import config.security.SecurityConfig;
import constants.auth.AuthConstants;
import constants.system.SystemKeys;
import dto.user.AdminSigninRequest;
import dto.user.ChangePasswordRequest;
import dto.user.SigninRequest;
import dto.user.UserCreateRequest;
import dto.user.UserDetailResponse;
import exception.BusinessException;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import utils.BCryptPasswordEncoder;
import utils.DbTransaction;
import utils.PasswordEncoder;
import utils.TransactionCallback;
import validator.core.ValidationResult;
import validator.user.AdminSigninValidator;
import validator.user.ChangePasswordValidator;
import validator.user.ResetPasswordValidator;
import validator.user.SigninValidator;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepository userRepository;
	private final UserManagementService userManagementService;
	private final PasswordEncoder passwordEncoder;
	private final PasswordService passwordService;
	private final AuthorizationService authorizationService;
	private final SigninValidator signinValidator;
	private final AdminSigninValidator adminSigninValidator;

	private static final String ERR_AUTH_SYSTEM = "Hệ thống xác thực đang bận. Vui lòng thử lại sau.";
	private static final String ERR_PASSWORD_CHANGE = "Quá trình đổi mật khẩu gặp sự cố. Vui lòng kiểm tra lại.";

	public AuthenticationServiceImpl() {
		UserRepository repository = new UserRepositoryImpl();
		PasswordEncoder encoder = new BCryptPasswordEncoder();

		this.userRepository = repository;
		this.userManagementService = new UserManagementServiceImpl(repository);
		this.passwordEncoder = encoder;
		this.passwordService = new PasswordServiceImpl(repository, encoder, new ChangePasswordValidator(), new ResetPasswordValidator());
		this.authorizationService = new AuthorizationServiceImpl();
		this.signinValidator = new SigninValidator();
		this.adminSigninValidator = new AdminSigninValidator();
	}

	public AuthenticationServiceImpl(UserRepository userRepository, UserManagementService userManagementService, PasswordEncoder passwordEncoder, PasswordService passwordService,
			AuthorizationService authorizationService, SigninValidator signinValidator, AdminSigninValidator adminSigninValidator) {
		this.userRepository = userRepository;
		this.userManagementService = userManagementService;
		this.passwordEncoder = passwordEncoder;
		this.passwordService = passwordService;
		this.authorizationService = authorizationService;
		this.signinValidator = signinValidator;
		this.adminSigninValidator = adminSigninValidator;
	}

	@Override
	public UserDetailResponse signupUser(UserCreateRequest dto) throws BusinessException {
		return userManagementService.createUser(dto);
	}

	@Override
	public void changePassword(final long userId, final ChangePasswordRequest request) throws BusinessException {
		try {
			DbTransaction.runVoid(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection conn) throws SQLException {
					passwordService.changePassword(conn, userId, request);
					userRepository.incrementTokenVersion(conn, userId);
					return null;
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException(ERR_PASSWORD_CHANGE);
		}
	}

	@Override
	public boolean incrementTokenVersion(long userId) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(Connection conn) throws SQLException {
					return userRepository.incrementTokenVersion(conn, userId);
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Không thể cập nhật trạng thái phiên làm việc lúc này.");
		}
	}

	@Override
	public int getTokenVersion(long userId) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Integer>() {
				@Override
				public Integer doInTransaction(Connection conn) throws SQLException {
					return userRepository.getTokenVersion(conn, userId);
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Không thể đối soát mã phiên làm việc.");
		}
	}

	@Override
	public User authenticate(SigninRequest request) throws BusinessException {
		final Map<String, String> errors = new HashMap<>();

		final ValidationResult validationResult = signinValidator.validate(request);
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		try {
			return DbTransaction.run(new TransactionCallback<User>() {
				@Override
				public User doInTransaction(Connection conn) throws SQLException, BusinessException {
					return checkUserAndPass(conn, request.getUsername(), request.getPassword());
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException(ERR_AUTH_SYSTEM);
		}
	}

	@Override
	public User authenticateAdmin(AdminSigninRequest request) throws BusinessException {
		final Map<String, String> errors = new HashMap<>();

		final ValidationResult validationResult = adminSigninValidator.validate(request);
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		try {
			return DbTransaction.run(new TransactionCallback<User>() {
				@Override
				public User doInTransaction(Connection conn) throws SQLException, BusinessException {
					User user = checkUserAndPass(conn, request.getUsername(), request.getPassword());

					if (SecurityConfig.isSuperAdminUserId(user.getId()) && SecurityConfig.isSuperAdminUsername(user.getUsername())) {
						return user;
					}

					if (!authorizationService.hasAnyPermission(user.getId(), PermissionConstants.ADMIN_PORTAL_ACCESS_PERMISSIONS)) {
						Map<String, String> authErrors = new HashMap<>();
						authErrors.put(SystemKeys.ERROR_GLOBAL, "Tài khoản của bạn thiếu quyền truy cập.");
						throw new BusinessException(authErrors);
					}

					return user;
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException(ERR_AUTH_SYSTEM);
		}
	}

	private User checkUserAndPass(Connection conn, String username, String rawPassword) throws BusinessException, SQLException {
		if (SecurityConfig.isSuperAdminUsername(username)) {
			String passwordHash = SecurityConfig.SUPER_ADMIN_PASSWORD_BCRYPT;
			if (passwordHash == null || passwordHash.isBlank()) {
				passwordHash = AuthConstants.DUMMY_BCRYPT;
			}

			final boolean isPasswordValid = passwordEncoder.matches(rawPassword, passwordHash);
			if (!isPasswordValid) {
				Map<String, String> errors = new HashMap<>();
				errors.put(SystemKeys.ERROR_GLOBAL, "Tài khoản hoặc mật khẩu không chính xác.");
				throw new BusinessException(errors);
			}

			User user = new User();
			user.setId(SecurityConfig.SUPER_ADMIN_USER_ID);
			user.setUsername(SecurityConfig.SUPER_ADMIN_USERNAME);
			Role role = new Role();
			role.setCode(SecurityConfig.SUPER_ADMIN_ROLE_CODE);
			role.setSystem(true);
			role.setActive(true);
			user.setRole(role);
			return user;
		}

		final Optional<User> userOptional = userRepository.findByUsername(conn, username);
		final boolean existUser = userOptional.isPresent();

		String passwordHash = existUser ? userOptional.get().getPasswordHash() : AuthConstants.DUMMY_BCRYPT;
		if (passwordHash == null) {
			passwordHash = AuthConstants.DUMMY_BCRYPT;
		}

		final boolean isPasswordValid = passwordEncoder.matches(rawPassword, passwordHash);

		if (!existUser || !isPasswordValid) {
			Map<String, String> errors = new HashMap<>();
			errors.put(SystemKeys.ERROR_GLOBAL, "Tài khoản hoặc mật khẩu không chính xác.");
			throw new BusinessException(errors);
		}

		return userOptional.get();
	}
}
