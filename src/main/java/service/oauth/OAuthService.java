package service.oauth;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import beans.User;
import dto.oauth.OAuthUserResponse;
import dto.user.OAuthUserRegistrationRequest;
import exception.BusinessException;
import mapper.user.OAuthUserMapper;
import repository.OAuthRepository;
import repository.OAuthRepositoryImpl;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import service.UserRegistrationService;
import service.UserRegistrationServiceImpl;
import utils.DbTransaction;

public class OAuthService {
	private final UserRepository userRepository;
	private final OAuthRepository oauthAuthRepository;
	private final UserRegistrationService userRegistrationService;
	private final OAuthUserMapper oauthUserMapper;

	public OAuthService() {
		this(new UserRepositoryImpl(), new OAuthRepositoryImpl(), new UserRegistrationServiceImpl(),
				new OAuthUserMapper());
	}

	public OAuthService(UserRepository userRepository, OAuthRepository oauthAuthRepository,
			UserRegistrationService userRegistrationService, OAuthUserMapper oauthUserMapper) {
		this.userRepository = userRepository;
		this.oauthAuthRepository = oauthAuthRepository;
		this.userRegistrationService = userRegistrationService;
		this.oauthUserMapper = oauthUserMapper;
	}

	public User handleOAuthCallback(OAuthUserResponse oauthUser, String provider) throws BusinessException {
		String providerUserId = oauthUser.getId();

		try {
			return DbTransaction.run(conn -> resolveOAuthUser(conn, oauthUser, provider, providerUserId));
		} catch (BusinessException e) {
			throw e;
		} catch (SQLException e) {
			throw new BusinessException("Lỗi xử lý OAuth: " + e.getMessage());
		}
	}

	private User resolveOAuthUser(Connection conn, OAuthUserResponse oauthUser, String provider, String providerUserId)
			throws SQLException {
		Optional<Long> existingUserIdOptional = oauthAuthRepository.findUserIdByOAuth(conn, provider, providerUserId);
		if (existingUserIdOptional.isPresent()) {
			long userId = existingUserIdOptional.get();
			Optional<User> userOptional = userRepository.findById(conn, userId);
			if (userOptional.isPresent()) {
				return userOptional.get();
			}
			throw new BusinessException("Lỗi dữ liệu: Không tìm thấy user với ID: " + userId);
		}

		int providerId = oauthAuthRepository.getProviderId(conn, provider);
		OAuthUserRegistrationRequest registrationRequest = oauthUserMapper.toOAuthUserRegistrationRequest(oauthUser,
				providerId);

		long newUserId = userRegistrationService.registerOAuthUser(conn, registrationRequest);

		Optional<User> createdUserOptional = userRepository.findById(conn, newUserId);
		if (createdUserOptional.isPresent()) {
			return createdUserOptional.get();
		}
		throw new BusinessException("Lỗi hệ thống: Không thể truy xuất user vừa tạo với ID: " + newUserId);
	}
}
