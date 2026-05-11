package service.user.impl;

import java.sql.SQLException;

import beans.common.EmailVerifyStatus;
import beans.common.UserStatus;
import beans.user.User;
import beans.user.UserAuthInfo;
import beans.user.UserLocalAuth;
import beans.user.UserProfile;
import dto.UserFormDTO;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import service.user.UserService;
import utils.HashingUtils;

public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	public UserServiceImpl() {
		this.userRepository = new UserRepositoryImpl();
	}

	@Override
	public UserFormDTO createUser(UserFormDTO dto) {
		try {
			User user = new User();
			user.setUsername(dto.getUsername());
			
			UserProfile profile = new UserProfile();
			profile.setFullname(dto.getFullname());
			user.setProfile(profile);
			
			user.setRole(dto.getRole());
			
			UserAuthInfo authInfo = new UserAuthInfo();
			UserLocalAuth local = new UserLocalAuth();
			local.setUsername(dto.getUsername());
			local.setPasswordHash(HashingUtils.hash(dto.getPassword()));
			local.setEmail(dto.getEmail());
			
			EmailVerifyStatus verifyStatus = new EmailVerifyStatus();
			verifyStatus.setId(1); // VERIFIED
			local.setEmailVerifyStatus(verifyStatus);
			
			authInfo.setLocal(local);
			user.setAuthInfo(authInfo);
			
			UserStatus status = new UserStatus();
			status.setId(1); // ACTIVE
			user.setStatus(status);
			
			long id = userRepository.save(user);
			dto.setId(id);
		} catch (SQLException e) {
			e.printStackTrace();
			dto.addError("general", "Lỗi database: " + e.getMessage());
		}
		return dto;
	}

	@Override
	public boolean isUsernameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public boolean isEmailExists(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public User getUserEntityByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	@Override
	public User getById(long id) {
		return userRepository.findById(id).orElse(null);
	}
}