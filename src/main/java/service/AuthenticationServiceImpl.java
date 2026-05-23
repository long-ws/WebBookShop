package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import beans.User;
import constants.PermissionConstants;
import dto.user.AdminSigninRequest;
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
import validator.user.SigninValidator;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepository userRepository;
	private final UserManagementService userManagementService;
	private final PasswordEncoder passwordEncoder;
	private final AuthorizationService authorizationService;
	private final SigninValidator signinValidator;
	private final AdminSigninValidator adminSigninValidator;

	public AuthenticationServiceImpl() {
		this(new UserRepositoryImpl(), new UserManagementServiceImpl(), new BCryptPasswordEncoder(), new AuthorizationServiceImpl(),
				new SigninValidator(), new AdminSigninValidator());
	}

	public AuthenticationServiceImpl(UserRepository userRepository, UserManagementService userManagementService,
			PasswordEncoder passwordEncoder, AuthorizationService authorizationService,
			SigninValidator signinValidator, AdminSigninValidator adminSigninValidator) {
		this.userRepository = userRepository;
		this.userManagementService = userManagementService;
		this.passwordEncoder = passwordEncoder;
		this.authorizationService = authorizationService;
		this.signinValidator = signinValidator;
		this.adminSigninValidator = adminSigninValidator;
	}

	@Override
	public UserDetailResponse signupUser(UserCreateRequest dto) throws BusinessException {
		UserDetailResponse response = userManagementService.createUser(dto);
		return response;
	}

	@Override
	public void changePassword(long userId, String newPassword) throws BusinessException {
		try {
			String hashedPassword = passwordEncoder.encode(newPassword);

			DbTransaction.runVoid(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection conn) throws SQLException {
					userRepository.changePassword(conn, userId, hashedPassword);
					userRepository.incrementTokenVersion(conn, userId);
					return null;
				}
			});

		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public boolean incrementTokenVersion(long userId) throws BusinessException {
		try {
			boolean isSuccess = DbTransaction.run(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(Connection conn) throws SQLException {
					boolean result = userRepository.incrementTokenVersion(conn, userId);
					return result;
				}
			});
			return isSuccess;

		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public int getTokenVersion(long userId) throws BusinessException {
		try (Connection conn = utils.DBConnection.getConnection()) {
			int currentVersion = userRepository.getTokenVersion(conn, userId);
			return currentVersion;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public User authenticate(SigninRequest request) throws BusinessException {
		ValidationResult validationResult = signinValidator.validate(request);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		try (Connection conn = utils.DBConnection.getConnection()) {
			Optional<User> userOptional = userRepository.findByUsername(conn, request.getUsername());
			if (!userOptional.isPresent()) {
				throw new BusinessException("Tên đăng nhập hoặc mật khẩu không đúng");
			}

			User user = userOptional.get();
			String passwordHash = user.getPasswordHash();
			if (passwordHash == null || !passwordEncoder.matches(request.getPassword(), passwordHash)) {
				throw new BusinessException("Tên đăng nhập hoặc mật khẩu không đúng");
			}

			return user;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống");
		}
	}

	@Override
	public User authenticateAdmin(AdminSigninRequest request) throws BusinessException {
		ValidationResult validationResult = adminSigninValidator.validate(request);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		try (Connection conn = utils.DBConnection.getConnection()) {
			Optional<User> userOptional = userRepository.findByUsername(conn, request.getUsername());
			if (!userOptional.isPresent()) {
				throw new BusinessException("Tên đăng nhập không tồn tại");
			}

			User user = userOptional.get();
			String passwordHash = user.getPasswordHash();
			if (passwordHash == null || !passwordEncoder.matches(request.getPassword(), passwordHash)) {
				throw new BusinessException("Mật khẩu không đúng");
			}

			if (!authorizationService.hasAnyPermission(user.getId(), PermissionConstants.ADMIN_PORTAL_ACCESS_PERMISSIONS)) {
				throw new BusinessException("Người dùng không có quyền truy cập Admin");
			}

			return user;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống");
		}
	}
}