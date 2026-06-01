package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import beans.User;
import constants.RequestParamConstants;
import constants.SystemConstants;
import dto.user.ChangePasswordRequest;
import dto.user.ResetPasswordRequest;
import exception.BusinessException;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import utils.BCryptPasswordEncoder;
import utils.DBConnection;
import utils.DbTransaction;
import utils.PasswordEncoder;
import utils.TransactionCallback;
import validator.core.ValidationResult;
import validator.user.ChangePasswordValidator;
import validator.user.ResetPasswordValidator;

public class PasswordServiceImpl implements PasswordService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ChangePasswordValidator changePasswordValidator;
	private final ResetPasswordValidator resetPasswordValidator;

	private static final String ERR_SYSTEM_BUSY = "Yêu cầu của bạn tạm thời không thể xử lý do sự cố hệ thống. Vui lòng thử lại sau.";

	public PasswordServiceImpl() {
		this(new UserRepositoryImpl(), new BCryptPasswordEncoder(), new ChangePasswordValidator(), new ResetPasswordValidator());
	}

	public PasswordServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ChangePasswordValidator changePasswordValidator, ResetPasswordValidator resetPasswordValidator) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.changePasswordValidator = changePasswordValidator;
		this.resetPasswordValidator = resetPasswordValidator;
	}

	@Override
	public void changePassword(final Connection conn, final long userId, final ChangePasswordRequest request) throws BusinessException {
		if (SystemConstants.Security.isSuperAdminUserId(userId) || SystemConstants.Security.isSystemGhostUserId(userId)) {
			throw new BusinessException(SystemConstants.ERROR_GLOBAL, "Không thể thao tác trên tài khoản hệ thống.");
		}
		
		final Map<String, String> errors = new HashMap<>();
		validateInputFormat(request, errors);
		User user = validateBusinessLogic(conn, userId, request, errors);
		updatePassword(conn, user, request);
	}

	@Override
	public void resetPassword(final long userId, final ResetPasswordRequest request) throws BusinessException {
		final Map<String, String> errors = new HashMap<>();

		final ValidationResult validationResult = resetPasswordValidator.validate(request);
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		try (Connection readConn = DBConnection.getConnection()) {
			final Optional<User> userOptional = userRepository.findById(readConn, userId);
			if (!userOptional.isPresent()) {
				errors.put(SystemConstants.ERROR_GLOBAL, "Tài khoản cần đặt lại mật khẩu không tồn tại.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException(ERR_SYSTEM_BUSY);
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		try {
			final String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());
			DbTransaction.runVoid(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection conn) throws SQLException {
					userRepository.changePassword(conn, userId, hashedNewPassword);
					userRepository.incrementTokenVersion(conn, userId);
					return null;
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException(ERR_SYSTEM_BUSY);
		}
	}

	private void validateInputFormat(final ChangePasswordRequest request, final Map<String, String> errors) throws BusinessException {
		final ValidationResult validationResult = changePasswordValidator.validate(request);
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}
	}

	private User validateBusinessLogic(final Connection conn, final long userId, final ChangePasswordRequest request, final Map<String, String> errors) throws BusinessException {
		User user = null;
		try {
			final Optional<User> userOptional = userRepository.findById(conn, userId);

			if (!userOptional.isPresent()) {
				errors.put(SystemConstants.ERROR_GLOBAL, "Tài khoản không tồn tại trên hệ thống.");
			} else {
				user = userOptional.get();
				final String passwordHash = user.getPasswordHash();

				if (passwordHash == null) {
					errors.put(SystemConstants.ERROR_GLOBAL, "Tài khoản này chưa được cấu hình mật khẩu hệ thống.");
				} else {
					final boolean isPasswordValid = passwordEncoder.matches(request.getCurrentPassword(), passwordHash);
					if (!isPasswordValid) {
						errors.put(RequestParamConstants.User.CURRENT_PASSWORD, "Mật khẩu hiện tại không chính xác.");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException(ERR_SYSTEM_BUSY);
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}
		return user;
	}

	private void updatePassword(final Connection conn, final User user, final ChangePasswordRequest request) throws BusinessException {
		try {
			final String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());
			userRepository.changePassword(conn, user.getId(), hashedNewPassword);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException(ERR_SYSTEM_BUSY);
		}
	}
}
