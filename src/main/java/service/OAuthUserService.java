package service;

import beans.oauth.OAuthUser;
import beans.user.UserOAuthAuth;
import beans.user.UserProfile;
import beans.User;
import beans.common.Role;
import repository.OAuthAuthRepository;
import repository.UserRepository;

import java.sql.SQLException;
import java.util.Optional;

public class OAuthUserService {

	private final UserRepository userRepository;
	private final OAuthAuthRepository oauthAuthRepository;

	public OAuthUserService(UserRepository userRepository, OAuthAuthRepository oauthAuthRepository) {
		this.userRepository = userRepository;
		this.oauthAuthRepository = oauthAuthRepository;
	}

	/**
	 * Xử lý kết quả trả về từ Google: Tìm user cũ hoặc tạo user mới
	 */
	public User handleOAuthCallback(OAuthUser oauthUser, String provider) throws SQLException {
		System.out.println("[OAuthUserService]: Bắt đầu xử lý kết quả trả về từ: " + provider);
		String providerUserId = oauthUser.getId();

		Optional<UserOAuthAuth> existingOAuth = oauthAuthRepository.findByProviderAndProviderUserId(provider,
				providerUserId);

		// Tài khoản OAuth tồn tại
		if (existingOAuth.isPresent()) {
			UserOAuthAuth oauthInfo = existingOAuth.get();
			long userId = oauthInfo.getUserId();

			Optional<User> userOpt = userRepository.findById(userId);

			if (userOpt.isPresent()) {
				System.out.println("[OAuthUserService]: Oauth user đã tìm thấy trong database");
				return userOpt.get();
			} else {
				throw new SQLException("Lỗi dữ liệu: Không tìm thấy user với ID: " + userId);
			}
		}

		// Chưa có
		User newUser = createUserFromOAuth(oauthUser);
		long newUserId = userRepository.insert(newUser);

		if (newUserId <= 0) {
			throw new SQLException("Không thể tạo user từ thông tin OAuth.");
		}

		// Tạo liên kết giữa tài khoản và User hệ thống
		oauthAuthRepository.linkOAuthAccount(newUserId, provider, providerUserId, oauthUser.getEmail(),
				oauthUser.getName(), oauthUser.getPictureUrl());


		// Kiểm tra ngược truy từ database lên xem có không
		Optional<User> createdUserOpt = userRepository.findById(newUserId);

		if (createdUserOpt.isPresent()) {
			return createdUserOpt.get();
		} else {
			throw new SQLException("Lỗi hệ thống: Không thể truy xuất user vừa tạo với ID: " + newUserId);
		}
	}

	/**
	 * Khởi tạo đối tượng User từ thông tin Oauth API (Google, ...) cung cấp
	 */
	private User createUserFromOAuth(OAuthUser oauthUser) {
		System.out.println("[OAuthUserService]: Thực hiện tạo đổi tượng User từ OauthUser");
		User newUser = new User();
		newUser.setUsername(oauthUser.getName());

		UserProfile profile = new UserProfile();
		profile.setFullname(oauthUser.getName());
		profile.setAvatarUrl(oauthUser.getPictureUrl());
		newUser.setProfile(profile);

		Role role = new Role();
		role.setCode("CUSTOMER");
		newUser.setRole(role);

		return newUser;
	}
}