package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import beans.common.Language;
import beans.user.UserAccount;
import beans.user.UserLocalAuth;
import beans.user.UserOAuthAuth;
import beans.user.UserProfile;
import constants.FormConstants;
import constants.RequestParamConstants;
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
import utils.TransactionCallback;
import validator.core.ValidationResult;
import validator.user.LocalUserRegistrationValidator;
import validator.user.OAuthUserRegistrationValidator;

public class UserRegistrationServiceImpl implements UserRegistrationService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final LocalUserRegistrationValidator localUserRegistrationValidator;
	private final OAuthUserRegistrationValidator oauthUserRegistrationValidator;

	private static final String ERR_DB_REGISTRATION = "Hệ thống không thể thực hiện đăng ký tài khoản lúc này. Vui lòng thử lại sau.";
	private static final String ERR_DB_OAUTH = "Kết nối xác thực tài khoản liên kết thất bại. Vui lòng thử lại.";

	public UserRegistrationServiceImpl() {
		this(new UserRepositoryImpl(), new BCryptPasswordEncoder(), new UserMapper(), new LocalUserRegistrationValidator(), new OAuthUserRegistrationValidator());
	}

	public UserRegistrationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, LocalUserRegistrationValidator localUserRegistrationValidator,
			OAuthUserRegistrationValidator oauthUserRegistrationValidator) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userMapper = userMapper;
		this.localUserRegistrationValidator = localUserRegistrationValidator;
		this.oauthUserRegistrationValidator = oauthUserRegistrationValidator;
	}

	@Override
	public long registerLocalUser(final LocalUserRegistrationRequest request) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(Connection conn) throws SQLException, BusinessException {
					return registerLocalUser(conn, request);
				}
			});
		} catch (BusinessException e) {
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException(ERR_DB_REGISTRATION);
		}
	}

	@Override
	public long registerLocalUser(Connection conn, LocalUserRegistrationRequest request) throws BusinessException, SQLException {
		final Map<String, String> errors = new HashMap<String, String>();

		final ValidationResult validationResult = localUserRegistrationValidator.validate(request);
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
			if (userRepository.existUserByUsername(conn, request.getUsername())) {
				errors.put(RequestParamConstants.User.USERNAME, "Tên đăng nhập này đã được đăng ký sử dụng.");
			}
		}
		if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
			if (userRepository.existUserByEmail(conn, request.getEmail())) {
				errors.put(RequestParamConstants.User.EMAIL, "Địa chỉ email này đã tồn tại trên hệ thống.");
			}
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		final UserAccount account = userMapper.toUserAccount();
		final UserProfile profile = createProfile(request.getFullname(), request.getEmail(), null);
		final String passwordHash = passwordEncoder.encode(request.getPassword());
		final UserLocalAuth localAuth = userMapper.toUserLocalAuth(request, passwordHash);

		try {
			return userRepository.createLocalUser(conn, account, profile, localAuth);
		} catch (SQLException e) {
			handleDuplicateException(e);
			throw e;
		}
	}

	@Override
	public long registerOAuthUser(final OAuthUserRegistrationRequest request) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(Connection conn) throws SQLException, BusinessException {
					return registerOAuthUser(conn, request);
				}
			});
		} catch (BusinessException e) {
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException(ERR_DB_OAUTH);
		}
	}

	@Override
	public long registerOAuthUser(Connection conn, OAuthUserRegistrationRequest request) throws BusinessException, SQLException {
		final Map<String, String> errors = new HashMap<String, String>();

		final ValidationResult validationResult = oauthUserRegistrationValidator.validate(request);
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
			if (userRepository.existUserByEmail(conn, request.getEmail())) {
				errors.put(RequestParamConstants.User.EMAIL, "Tài khoản liên kết thất bại: Email này đã được đăng ký trên hệ thống.");
			}
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		final UserAccount account = userMapper.toUserAccount();
		final UserProfile profile = createProfile(request.getFullname(), request.getEmail(), request.getAvatarUrl());
		final UserOAuthAuth oauthAuth = userMapper.toUserOAuthAuth(request);

		try {
			return userRepository.createOAuthUser(conn, account, profile, oauthAuth);
		} catch (SQLException e) {
			handleDuplicateException(e);
			throw e;
		}
	}

	private UserProfile createProfile(String fullname, String email, String avatarUrl) {
		final Language defaultLanguage = new Language();
		defaultLanguage.setId(SystemConstants.DEFAULT_LANGUAGE_ID);

		final UserProfileRequest profileRequest = new UserProfileRequest.Builder().fullname(fullname).email(email).avatarUrl(avatarUrl).preferredLanguage(defaultLanguage).build();

		return userMapper.toUserProfile(profileRequest);
	}

	private void handleDuplicateException(SQLException e) throws BusinessException {
		String message = (e.getMessage() != null) ? e.getMessage().toLowerCase() : "";

		if (message.contains("duplicate") || message.contains("unique")) {
			Map<String, String> errors = new HashMap<>();

			if (message.contains("email")) {
				errors.put(RequestParamConstants.User.EMAIL, "Địa chỉ email đã tồn tại.");
			} else if (message.contains("username")) {
				errors.put(RequestParamConstants.User.USERNAME, "Tên đăng nhập đã tồn tại.");
			} else {
				errors.put(FormConstants.ERROR_GLOBAL, "Dữ liệu bạn nhập đã tồn tại trong hệ thống.");
			}
			throw new BusinessException(errors);
		}

		e.printStackTrace();
		throw new BusinessException("Đã có lỗi xảy ra trong quá trình đăng ký người dùng mới");
	}
}