package service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import beans.User;
import beans.common.EmailVerifyStatus;
import beans.common.Language;
import beans.common.UserStatus;
import beans.user.UserAuthInfo;
import beans.user.UserLocalAuth;
import beans.user.UserProfile;
import dto.AdminUserDetailDTO;
import dto.UserCreateUpdateFormDTO;
import dto.AdminUserListDTO;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import utils.HashingUtils;
import utils.UserMapper;
import validation.strategy.UserValidationStrategy;

public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserValidationStrategy validationStrategy;

	public UserServiceImpl() {
		this.userRepository = new UserRepositoryImpl();
		this.validationStrategy = new UserValidationStrategy(this);
	}

	@Override
	public UserCreateUpdateFormDTO createUser(UserCreateUpdateFormDTO dto) {
		validationStrategy.validateCreate(dto);
		if (dto.hasErrors()) {
			return dto;
		}

		try {
			User user = toEntity(dto);

			UserAuthInfo authInfo = new UserAuthInfo();
			UserLocalAuth local = new UserLocalAuth();
			local.setUsername(dto.getUsername());
			local.setPasswordHash(HashingUtils.hash(dto.getPassword()));
			local.setEmail(dto.getEmail());
			authInfo.setLocal(local);
			authInfo.setHasLocalAuth(true);
			user.setAuthInfo(authInfo);

			long id = userRepository.insert(user);
			dto.setId(id);
		} catch (SQLException e) {
			dto.addError("general", "Lỗi database: " + e.getMessage());
		}

		return dto;
	}

	@Override
	public UserCreateUpdateFormDTO updateUser(UserCreateUpdateFormDTO dto) {
		if (dto.getId() == null) {
			dto.addError("general", "Yêu cầu user id");
			return dto;
		}

		validationStrategy.validateUpdate(dto);
		if (dto.hasErrors()) {
			return dto;
		}

		try {
			User user = toEntity(dto);
			user.setId(dto.getId());

			User existing = userRepository.findById(dto.getId()).orElse(null);
			if (existing != null) {
				UserAuthInfo authInfo = user.getAuthInfo() != null ? user.getAuthInfo() : new UserAuthInfo();
				UserLocalAuth local = authInfo.getLocal() != null ? authInfo.getLocal() : new UserLocalAuth();

				local.setUsername(dto.getUsername());
				local.setEmail(dto.getEmail());

				if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
					local.setPasswordHash(HashingUtils.hash(dto.getPassword()));
				} else {
					local.setPasswordHash(existing.getPasswordHash());
				}

				if (local.getEmailVerifyStatus() == null) {
					if (existing.getAuthInfo() != null && existing.getAuthInfo().getLocal() != null
							&& existing.getAuthInfo().getLocal().getEmailVerifyStatus() != null) {
						local.setEmailVerifyStatus(existing.getAuthInfo().getLocal().getEmailVerifyStatus());
					} else {
						EmailVerifyStatus verifyStatus = new EmailVerifyStatus();
						verifyStatus.setId(1);
						local.setEmailVerifyStatus(verifyStatus);
					}
				}

				authInfo.setLocal(local);
				user.setAuthInfo(authInfo);

				if (user.getStatus() == null) {
					if (existing.getStatus() != null) {
						user.setStatus(existing.getStatus());
					} else {
						UserStatus status = new UserStatus();
						status.setId(1);
						user.setStatus(status);
					}
				}

				if (user.getPreferredLanguage() == null) {
					if (existing.getPreferredLanguage() != null) {
						user.setPreferredLanguage(existing.getPreferredLanguage());
					} else {
						Language lang = new Language();
						lang.setId(1);
						user.setPreferredLanguage(lang);
					}
				}

				user.setTokenVersion(existing.getTokenVersion());
			}

			userRepository.update(user);
		} catch (SQLException e) {
			dto.addError("general", "Lỗi database: " + e.getMessage());
		}

		return dto;
	}

	@Override
	public boolean deleteUser(long id) {
		try {
			userRepository.delete(id);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public AdminUserDetailDTO getUserById(long id) {
		Optional<User> userOpt = userRepository.findById(id);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			return UserMapper.toAdminUserDetailDTO(user);
		} else {
			return null;
		}
	}

	@Override
	public User getById(long id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public List<AdminUserListDTO> getUsers(int page, int pageSize, String orderBy, String orderDir) {
		int offset = (page - 1) * pageSize;
		List<User> users = userRepository.findAll(pageSize, offset, orderBy, orderDir);
		List<AdminUserListDTO> dtos = new java.util.ArrayList<>();
		for (User user : users) {
			dtos.add(UserMapper.toAdminUserListDTO(user));
		}
		return dtos;
	}

	@Override
	public long countUsers() {
		return userRepository.count();
	}

	@Override
	public boolean isUsernameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public boolean isUsernameExists(String username, long excludeId) {
		Optional<User> userOptional = userRepository.findByUsername(username);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			return user.getId() != excludeId;
		}
		return false;
	}

	@Override
	public boolean isEmailExists(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean isEmailExists(String email, long excludeId) {
		Optional<User> userOptional = userRepository.findByEmail(email);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			return user.getId() != excludeId;
		}
		return false;
	}

	@Override
	public User getUserEntityByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	@Override
	public void changePassword(long userId, String newPassword) {
		userRepository.changePassword(userId, HashingUtils.hash(newPassword));
		incrementTokenVersion(userId);
	}

	@Override
	public boolean incrementTokenVersion(long userId) {
		return userRepository.incrementTokenVersion(userId);
	}

	@Override
	public int getTokenVersion(long userId) {
		return userRepository.getTokenVersion(userId);
	}

	@Override
	public UserCreateUpdateFormDTO toFormDTO(AdminUserDetailDTO dto) {
		return UserMapper.toUserCreateUpdateFormDTO(dto);
	}

	private User toEntity(UserCreateUpdateFormDTO dto) {
		User user = new User();
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());

		UserProfile profile = new UserProfile();
		profile.setFullname(dto.getFullname());
		profile.setPhoneNumber(dto.getPhoneNumber());
		profile.setGender(dto.getGender());
		profile.setPreferredLanguage(dto.getPreferredLanguage());
		profile.setAvatarUrl(dto.getAvatarUrl());
		
		user.setProfile(profile);

		if (dto.getRole() != null) {
			user.setRole(dto.getRole());
		}

		return user;
	}
}
