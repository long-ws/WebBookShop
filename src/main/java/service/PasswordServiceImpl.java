package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import beans.User;
import dto.user.ChangePasswordRequest;
import dto.user.ResetPasswordRequest;
import exception.BusinessException;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import utils.BCryptPasswordEncoder;
import utils.DbTransaction;
import utils.PasswordEncoder;
import validator.core.ValidationResult;
import validator.user.ChangePasswordValidator;
import validator.user.ResetPasswordValidator;

public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChangePasswordValidator changePasswordValidator;
    private final ResetPasswordValidator resetPasswordValidator;

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
    public void changePassword(long userId, ChangePasswordRequest request) throws BusinessException {
        ValidationResult validationResult = changePasswordValidator.validate(request);
        if (validationResult.hasErrors()) {
            throw new BusinessException(validationResult.getErrors());
        }

        try {
            User user;
            try (Connection readConn = utils.DBConnection.getConnection()) {
                
                Optional<User> userOptional = userRepository.findById(readConn, userId);
                if (!userOptional.isPresent()) {
                    throw new BusinessException("User không tồn tại");
                }
                user = userOptional.get();

                if (user.getPasswordHash() == null) {
                    throw new BusinessException("User không có mật khẩu local");
                }

                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
                    throw new BusinessException("Mật khẩu hiện tại không đúng");
                }
            }

            final String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());

            DbTransaction.runVoid(new utils.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(Connection writeConn) throws SQLException {
                    userRepository.changePassword(writeConn, userId, hashedNewPassword);
                    return null;
                }
            });

        } catch (SQLException e) {
            throw new BusinessException("Lỗi hệ thống database: " + e.getMessage());
        }
    }
    @Override
    public void resetPassword(long userId, ResetPasswordRequest request) throws BusinessException {
        ValidationResult validationResult = resetPasswordValidator.validate(request);
        if (validationResult.hasErrors()) {
            throw new BusinessException(validationResult.getErrors());
        }

        try {
            try (Connection readConn = utils.DBConnection.getConnection()) {
                Optional<User> userOptional = userRepository.findById(readConn, userId);
                if (!userOptional.isPresent()) {
                    throw new BusinessException("User không tồn tại");
                }
            }

            final String hashedNewPassword = passwordEncoder.encode(request.getNewPassword());

            DbTransaction.runVoid(new utils.TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(Connection writeConn) throws SQLException {
                    userRepository.changePassword(writeConn, userId, hashedNewPassword);
                    return null;
                }
            });

        } catch (SQLException e) {
            throw new BusinessException("Lỗi hệ thống database: " + e.getMessage());
        }
    }
}
