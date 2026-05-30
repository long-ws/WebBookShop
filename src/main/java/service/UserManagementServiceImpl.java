package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.User;
import beans.common.Role;
import constants.RequestParamConstants;
import constants.SystemConstants;
import dao.common.LanguageDAO;
import dao.common.LanguageDAOImpl;
import dao.common.RoleDAO;
import dao.common.RoleDAOImpl;
import dto.user.UserCreateRequest;
import dto.user.UserDetailResponse;
import dto.user.UserManageResponse;
import dto.user.UserUpdateRequest;
import exception.BusinessException;
import mapper.user.UserMapper;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import service.user.UserLanguageResolver;
import utils.BCryptPasswordEncoder;
import utils.DBConnection;
import utils.DbTransaction;
import utils.PasswordEncoder;
import utils.TransactionCallback;
import validator.core.ValidationResult;
import validator.user.UserCreateValidator;
import validator.user.UserUpdateValidator;

public class UserManagementServiceImpl implements UserManagementService {

	private final UserRepository userRepository;
	private final UserCreateValidator userCreateValidator;
	private final UserUpdateValidator userUpdateValidator;
	private final UserMapper userMapper;
	private final UserLanguageResolver languageResolver;
	private final PasswordEncoder passwordEncoder;

	public UserManagementServiceImpl() {
		this(new UserRepositoryImpl(), new UserMapper(), new LanguageDAOImpl(), new BCryptPasswordEncoder(),
				new UserCreateValidator(), new UserUpdateValidator());
	}

	public UserManagementServiceImpl(UserRepository userRepository) {
		this(userRepository, new UserMapper(), new LanguageDAOImpl(), new BCryptPasswordEncoder(),
				new UserCreateValidator(), new UserUpdateValidator());
	}

	public UserManagementServiceImpl(UserRepository userRepository, UserMapper userMapper) {
		this(userRepository, userMapper, new LanguageDAOImpl(), new BCryptPasswordEncoder(), new UserCreateValidator(),
				new UserUpdateValidator());
	}

	public UserManagementServiceImpl(UserRepository userRepository, UserMapper userMapper, LanguageDAO languageDAO) {
		this(userRepository, userMapper, languageDAO, new BCryptPasswordEncoder(), new UserCreateValidator(),
				new UserUpdateValidator());
	}

	public UserManagementServiceImpl(UserRepository userRepository, UserMapper userMapper, LanguageDAO languageDAO,
			PasswordEncoder passwordEncoder) {
		this(userRepository, userMapper, languageDAO, passwordEncoder, new UserCreateValidator(),
				new UserUpdateValidator());
	}

	public UserManagementServiceImpl(UserRepository userRepository, UserMapper userMapper, LanguageDAO languageDAO,
			PasswordEncoder passwordEncoder, UserCreateValidator userCreateValidator,
			UserUpdateValidator userUpdateValidator) {
		this.userRepository = userRepository;
		this.userCreateValidator = userCreateValidator;
		this.userUpdateValidator = userUpdateValidator;
		this.userMapper = userMapper;
		this.languageResolver = new UserLanguageResolver(languageDAO);
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetailResponse createUser(final UserCreateRequest dto) throws BusinessException {
		final ValidationResult validationResult = userCreateValidator.validate(dto);
		final Map<String, String> errors = new HashMap<>();
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		try (Connection readConn = DBConnection.getConnection()) {
			if (SystemConstants.Security.isSuperAdminUsername(dto.getUsername())) {
				errors.put(RequestParamConstants.User.USERNAME, "Tên đăng nhập này được bảo vệ bởi hệ thống.");
			}

			if (dto.getRole() != null && dto.getRole().getCode() != null && !dto.getRole().getCode().isBlank()) {
				final RoleDAO roleDAO = new RoleDAOImpl();
				final Optional<Role> roleOpt = roleDAO.findByCode(readConn, dto.getRole().getCode().trim());
				if (roleOpt.isEmpty()) {
					errors.put(RequestParamConstants.User.ROLE, "Vai trò không tồn tại.");
				} else if (roleOpt.get().isSystem()) {
					errors.put(RequestParamConstants.User.ROLE, "Không được phép gán vai trò hệ thống.");
				}
			}

			if (userRepository.existUserByUsername(readConn, dto.getUsername())) {
				errors.put(RequestParamConstants.User.USERNAME, "Tên đăng nhập đã tồn tại!");
			}
			if (userRepository.existUserByEmail(readConn, dto.getEmail())) {
				errors.put(RequestParamConstants.User.EMAIL, "Email đã được sử dụng!");
			}
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi kiểm tra tài khoản: " + e.getMessage());
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		final String passwordHash = dto.getPassword() != null ? passwordEncoder.encode(dto.getPassword()) : null;
		final User user = userMapper.toEntity(dto, passwordHash);

		try {
			final long id = DbTransaction.run(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(Connection conn) throws SQLException {
					languageResolver.resolve(conn, user, null);
					return userRepository.insert(conn, user);
				}
			});

			return getUserById(id);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi ghi dữ liệu vào Database: " + e.getMessage());
		}
	}

	@Override
	public UserDetailResponse updateUser(final UserUpdateRequest dto) throws BusinessException {
		final ValidationResult validationResult = userUpdateValidator.validate(dto);
		final Map<String, String> errors = new HashMap<>();
		if (validationResult.hasErrors()) {
			errors.putAll(validationResult.getErrors());
		}

		if (dto.getId() == null) {
			errors.put(RequestParamConstants.ID, "Yêu cầu user id");
		}

		if (dto.getId() != null && SystemConstants.Security.isSystemGhostUserId(dto.getId())) {
			errors.put(SystemConstants.ERROR_GLOBAL, "Không thể cập nhật tài khoản hệ thống.");
		}

		if (!errors.isEmpty()) {
			throw new BusinessException(errors);
		}

		final User existing;
		try (Connection readConn = DBConnection.getConnection()) {
			final Optional<User> emailOwnerOptional = userRepository.findByEmail(readConn, dto.getEmail());
			if (emailOwnerOptional.isPresent()) {
				final User emailOwner = emailOwnerOptional.get();
				if (emailOwner.getId() != dto.getId()) {
					errors.put(RequestParamConstants.User.EMAIL, "Email đã được sử dụng!");
				}
			}

			final Optional<User> existingOptional = userRepository.findById(readConn, dto.getId());
			if (!existingOptional.isPresent()) {
				errors.put(SystemConstants.ERROR_GLOBAL, "Người dùng không tồn tại trên hệ thống");
			} else {
				final User user = existingOptional.get();
				if (user.getRole() != null && user.getRole().isSystem()) {
					errors.put(SystemConstants.ERROR_GLOBAL, "Không thể cập nhật người dùng có vai trò hệ thống");
				}
			}

			if (!errors.isEmpty()) {
				throw new BusinessException(errors);
			}

			existing = existingOptional.get();

			if (SystemConstants.Security.isSuperAdminUsername(dto.getUsername()) && !SystemConstants.Security.isSuperAdminUsername(existing.getUsername())) {
				errors.put(RequestParamConstants.User.USERNAME, "Tên đăng nhập này được bảo vệ bởi hệ thống.");
				throw new BusinessException(errors);
			}

			if (dto.getRole() != null && dto.getRole().getCode() != null && !dto.getRole().getCode().isBlank()) {
				final RoleDAO roleDAO = new RoleDAOImpl();
				final Optional<beans.common.Role> roleOpt = roleDAO.findByCode(readConn, dto.getRole().getCode().trim());
				if (roleOpt.isEmpty()) {
					errors.put(RequestParamConstants.User.ROLE, "Vai trò không tồn tại.");
					throw new BusinessException(errors);
				}
				if (roleOpt.get().isSystem()) {
					errors.put(RequestParamConstants.User.ROLE, "Không được phép gán vai trò hệ thống.");
					throw new BusinessException(errors);
				}
			}
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi đọc dữ liệu: " + e.getMessage());
		}

		String newPasswordHash = null;
		if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
			newPasswordHash = passwordEncoder.encode(dto.getPassword());
		}

		final User user = userMapper.toEntity(dto, existing, newPasswordHash);

		try {
			DbTransaction.runVoid(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(Connection writeConn) throws SQLException {
					languageResolver.resolve(writeConn, user, existing);
					userRepository.update(writeConn, user);
					return null;
				}
			});

			return getUserById(dto.getId());
		} catch (SQLException e) {
			throw new BusinessException("Lỗi cập nhật dữ liệu vào Database: " + e.getMessage());
		}
	}

	@Override
	public boolean deleteUsers(final List<Long> ids) throws BusinessException {
		if (ids == null || ids.isEmpty()) {
			return false;
		}

		try (Connection readConn = DBConnection.getConnection()) {
			for (final Long id : ids) {
				if (id != null && SystemConstants.Security.isSystemGhostUserId(id)) {
					throw new BusinessException("Không thể xóa tài khoản hệ thống.");
				}
				final Optional<User> userOptional = userRepository.findById(readConn, id);
				if (userOptional.isPresent()) {
					final User user = userOptional.get();
					if (user.getRole() != null && user.getRole().isSystem()) {
						throw new BusinessException("Không thể xóa người dùng có vai trò hệ thống");
					}
				}
			}
		} catch (SQLException e) {
			throw new BusinessException("Lỗi hệ thống khi kiểm tra người dùng: " + e.getMessage());
		}

		try {
			return DbTransaction.run(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(Connection conn) throws SQLException {
					return userRepository.delete(conn, ids);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public UserDetailResponse getUserById(long id) {
		if (SystemConstants.Security.isSystemGhostUserId(id)) {
			return null;
		}
		try (Connection conn = DBConnection.getConnection()) {
			Optional<User> userOptional = userRepository.findById(conn, id);
			if (userOptional.isPresent()) {
				User user = userOptional.get();
				return userMapper.toUserDetailResponse(user);
			}
			return null;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public User getById(long id) {
		if (SystemConstants.Security.isSystemGhostUserId(id)) {
			return null;
		}
		try (Connection conn = DBConnection.getConnection()) {
			Optional<User> userOptional = userRepository.findById(conn, id);
			if (userOptional.isPresent()) {
				return userOptional.get();
			}
			return null;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public List<UserManageResponse> getUsers() {
		try (Connection conn = DBConnection.getConnection()) {
			List<User> users = userRepository.findAllUser(conn);
			List<UserManageResponse> dtos = new ArrayList<>();
			for (User user : users) {
				dtos.add(userMapper.toManageUserResponse(user));
			}
			return dtos;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public long countUsers() {
		try (Connection conn = DBConnection.getConnection()) {
			return userRepository.count(conn);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public boolean isUsernameExists(String username) {
		try (Connection conn = DBConnection.getConnection()) {
			return userRepository.existUserByUsername(conn, username);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public boolean isUsernameExists(String username, long excludeId) {
		try (Connection conn = DBConnection.getConnection()) {
			return userRepository.existUserByUsername(conn, username, excludeId);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public boolean isEmailExists(String email) {
		try (Connection conn = DBConnection.getConnection()) {
			return userRepository.existUserByEmail(conn, email);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public boolean isEmailExists(String email, long excludeId) {
		try (Connection conn = DBConnection.getConnection()) {
			return userRepository.existUserByEmail(conn, email, excludeId);
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public User getUserEntityByUsername(String username) {
		try (Connection conn = DBConnection.getConnection()) {
			Optional<User> userOptional = userRepository.findByUsername(conn, username);
			if (userOptional.isPresent()) {
				return userOptional.get();
			}
			return null;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi database: " + e.getMessage());
		}
	}

	@Override
	public UserCreateRequest toCreateDTO(UserDetailResponse detail) {
		return userMapper.toUserCreateRequest(detail);
	}

	@Override
	public UserUpdateRequest toUpdateDTO(UserDetailResponse detail) {
		return userMapper.toUserUpdateRequest(detail);
	}
}
