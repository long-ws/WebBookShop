package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import beans.User;
import dao.common.LanguageDAO;
import dao.common.LanguageDAOImpl;
import dto.user.UserProfileRequest;
import dto.user.UserProfileResponse;
import exception.BusinessException;
import mapper.user.UserMapper;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import service.user.UserLanguageResolver;
import utils.DbTransaction;
import validator.core.ValidationResult;
import validator.user.UserProfileValidator;

public class UserProfileServiceImpl implements UserProfileService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserLanguageResolver languageResolver;
	private final UserProfileValidator userProfileValidator;

	public UserProfileServiceImpl() {
		this(new UserRepositoryImpl(), new UserMapper(), new LanguageDAOImpl(), new UserProfileValidator());
	}

	public UserProfileServiceImpl(UserRepository userRepository, UserMapper userMapper, LanguageDAO languageDAO,
			UserProfileValidator userProfileValidator) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.languageResolver = new UserLanguageResolver(languageDAO);
		this.userProfileValidator = userProfileValidator;
	}

	@Override
	public UserProfileResponse getUserProfile(long userId) {
		try (Connection conn = utils.DBConnection.getConnection()) {
			Optional<User> userOptional = userRepository.findById(conn, userId);
			if (userOptional.isPresent()) {
				User user = userOptional.get();
				return userMapper.toUserProfileResponse(user);
			}
			return null;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public UserProfileResponse updateUserProfile(long userId, UserProfileRequest request) throws BusinessException {
		ValidationResult validationResult = userProfileValidator.validate(request);
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.getErrors());
		}

		try {
			User existing;
			try (Connection readConn = utils.DBConnection.getConnection()) {

				Optional<User> existingOptional = userRepository.findById(readConn, userId);
				if (!existingOptional.isPresent()) {
					throw new BusinessException("User không tồn tại");
				}
				existing = existingOptional.get();

				Optional<User> emailOwnerOptional = userRepository.findByEmail(readConn, request.getEmail());
				if (emailOwnerOptional.isPresent()) {
					User emailOwner = emailOwnerOptional.get();
					if (emailOwner.getId() != userId) {
						throw new BusinessException("Email đã được sử dụng!");
					}
				}
			}

			final User user = userMapper.toProfileUpdatedUser(existing, request);
			final User finalExisting = existing;

			DbTransaction.runVoid(new utils.TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection writeConn) throws SQLException {
					languageResolver.resolve(writeConn, user, finalExisting);
					userRepository.update(writeConn, user);
					return null;
				}
			});

			UserProfileResponse response = getUserProfile(userId);
			return response;

		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống database: " + e.getMessage());
		}
	}

	@Override
	public User getById(long id) {
		try (Connection conn = utils.DBConnection.getConnection()) {
			Optional<User> userOptional = userRepository.findById(conn, id);
			if (userOptional.isPresent()) {
				return userOptional.get();
			}
			return null;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}
}
