package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import beans.User;
import constants.RequestParamConstants;
import config.security.SecurityConfig;
import constants.system.SystemKeys;
import dao.common.LanguageDAO;
import dao.common.LanguageDAOImpl;
import dto.user.UserProfileRequest;
import dto.user.UserProfileResponse;
import exception.BusinessException;
import mapper.user.UserMapper;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import service.user.UserLanguageService;
import utils.DbTransaction;
import utils.TransactionCallback;
import validator.core.ValidationResult;
import validator.user.UserProfileValidator;

public class UserProfileServiceImpl implements UserProfileService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserLanguageService languageResolver;
	private final UserProfileValidator userProfileValidator;

	private static final String ERR_DB_UPDATE = "Cập nhật thông tin hồ sơ thất bại. Vui lòng thử lại sau.";
	private static final String ERR_DB_FETCH = "Tải dữ liệu hồ sơ cá nhân thất bại. Vui lòng thử lại sau.";

	public UserProfileServiceImpl() {
		this(new UserRepositoryImpl(), new UserMapper(), new LanguageDAOImpl(), new UserProfileValidator());
	}

	public UserProfileServiceImpl(UserRepository userRepository, UserMapper userMapper, LanguageDAO languageDAO, UserProfileValidator userProfileValidator) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.languageResolver = new UserLanguageService(languageDAO);
		this.userProfileValidator = userProfileValidator;
	}

	@Override
	public UserProfileResponse getUserProfile(final long userId) throws BusinessException {
		User user = getById(userId);
		return (user != null) ? userMapper.toUserProfileResponse(user) : null;
	}

	@Override
	public UserProfileResponse updateUserProfile(final long userId, final UserProfileRequest request) throws BusinessException {
		if (SecurityConfig.isSystemGhostUserId(userId)) {
			throw new BusinessException(SystemKeys.ERROR_GLOBAL, "Không thể cập nhật tài khoản hệ thống.");
		}
		ValidationResult validationResult = userProfileValidator.validate(request);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		try {
			return DbTransaction.run(new TransactionCallback<UserProfileResponse>() {
				@Override
				public UserProfileResponse doInTransaction(Connection conn) throws SQLException, BusinessException {
					Map<String, String> errors = new HashMap<>();

					Optional<User> existingOpt = userRepository.findById(conn, userId);
					if (!existingOpt.isPresent()) {
						errors.put(SystemKeys.ERROR_GLOBAL, "Hồ sơ tài khoản không tồn tại.");
						throw new BusinessException(errors);
					}

					if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
						Optional<User> emailOwnerOpt = userRepository.findByEmail(conn, request.getEmail());
						if (emailOwnerOpt.isPresent() && emailOwnerOpt.get().getId() != userId) {
							errors.put(RequestParamConstants.User.EMAIL, "Địa chỉ email đã được sử dụng.");
							throw new BusinessException(errors);
						}
					}

					User existing = existingOpt.get();
					User user = userMapper.toProfileUpdatedUser(existing, request);

					languageResolver.resolve(conn, user, existing);
					userRepository.update(conn, user);

					return userMapper.toUserProfileResponse(user);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException(ERR_DB_UPDATE);
		}
	}

	@Override
	public User getById(final long id) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<User>() {
				@Override
				public User doInTransaction(Connection conn) throws SQLException {
					return userRepository.findById(conn, id).orElse(null);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException(ERR_DB_FETCH);
		}
	}
}
