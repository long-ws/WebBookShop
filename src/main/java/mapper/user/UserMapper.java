package mapper.user;

import beans.User;
import beans.common.EmailVerifyStatus;
import beans.common.Role;
import beans.common.UserStatus;
import beans.user.UserAccount;
import beans.user.UserAuthInfo;
import beans.user.UserLocalAuth;
import beans.user.UserOAuthAuth;
import beans.user.UserProfile;
import constants.SystemConstants;
import dto.user.LocalUserRegistrationRequest;
import dto.user.OAuthUserRegistrationRequest;
import dto.user.UserCreateRequest;
import dto.user.UserDetailResponse;
import dto.user.UserManageResponse;
import dto.user.UserProfileRequest;
import dto.user.UserProfileResponse;
import dto.user.UserUpdateRequest;

public class UserMapper {

	public UserMapper() {
	}

	public UserAccount toUserAccount() {
		UserAccount account = new UserAccount();
		account.setStatusId(1);
		account.setTokenVersion(0);
		return account;
	}

	public UserProfile toUserProfile(UserProfileRequest request) {
		if (request == null) {
			return null;
		}

		UserProfile profile = new UserProfile();
		profile.setFullname(request.getFullname());
		profile.setEmail(request.getEmail());
		profile.setPhoneNumber(request.getPhoneNumber());
		profile.setAvatarUrl(request.getAvatarUrl());
		profile.setGender(request.getGender());
		profile.setPreferredLanguage(request.getPreferredLanguage());
		return profile;
	}

	public UserLocalAuth toUserLocalAuth(LocalUserRegistrationRequest request, String passwordHash) {
		if (request == null) {
			return null;
		}

		UserLocalAuth localAuth = new UserLocalAuth();
		localAuth.setUsername(request.getUsername());
		localAuth.setPasswordHash(passwordHash);
		localAuth.setEmail(request.getEmail());
		localAuth.setFailedAttempts(0);

		EmailVerifyStatus verifyStatus = new EmailVerifyStatus();
		verifyStatus.setId(1);
		localAuth.setEmailVerifyStatus(verifyStatus);

		return localAuth;
	}

	public UserOAuthAuth toUserOAuthAuth(OAuthUserRegistrationRequest request) {
		if (request == null) {
			return null;
		}

		UserOAuthAuth oauthAuth = new UserOAuthAuth();
		oauthAuth.setProviderId(request.getProviderId());
		oauthAuth.setProviderUserId(request.getProviderUserId());
		oauthAuth.setEmail(request.getEmail());
		oauthAuth.setDisplayName(request.getFullname());
		oauthAuth.setAvatarUrl(request.getAvatarUrl());
		return oauthAuth;
	}

	public User toUser(UserCreateRequest dto, String passwordHash) {
		if (dto == null) {
			return null;
		}

		User user = new User();
		user.setUsername(dto.getUsername());

		UserStatus status = new UserStatus();
		status.setId(1);
		user.setStatus(status);

		Role role = dto.getRole() != null ? dto.getRole() : new Role();
		if (role.getCode() == null) {
			role.setCode(SystemConstants.DEFAULT_ROLE_CODE);
		}
		user.setRole(role);

		UserAuthInfo authInfo = new UserAuthInfo();
		UserLocalAuth local = new UserLocalAuth();
		local.setUsername(dto.getUsername());
		local.setPasswordHash(passwordHash);
		local.setEmail(dto.getEmail());

		EmailVerifyStatus verifyStatus = new EmailVerifyStatus();
		verifyStatus.setId(1);
		local.setEmailVerifyStatus(verifyStatus);

		authInfo.setLocal(local);
		authInfo.setHasLocalAuth(true);
		user.setAuthInfo(authInfo);

		return user;
	}

	public User toEntity(UserCreateRequest dto, String passwordHash) {
		if (dto == null) {
			return null;
		}

		User user = toUser(dto, passwordHash);
		user.setProfile(toUserProfile(dto));
		return user;
	}

	public User toUpdatedUser(UserUpdateRequest dto, User existing, String newPasswordHash) {
		if (dto == null || existing == null) {
			return null;
		}

		User user = new User();
		user.setId(dto.getId());
		user.setUsername(existing.getUsername());
		user.setTokenVersion(existing.getTokenVersion());

		if (dto.getRole() != null) {
			user.setRole(dto.getRole());
		} else {
			user.setRole(existing.getRole());
		}

		UserAuthInfo authInfo = new UserAuthInfo();
		UserLocalAuth local = new UserLocalAuth();
		local.setUsername(existing.getUsername());
		local.setEmail(dto.getEmail());

		if (newPasswordHash != null && !newPasswordHash.isEmpty()) {
			local.setPasswordHash(newPasswordHash);
		} else {
			local.setPasswordHash(existing.getPasswordHash());
		}

		if (existing.getAuthInfo() != null && existing.getAuthInfo().getLocal() != null && existing.getAuthInfo().getLocal().getEmailVerifyStatus() != null) {
			local.setEmailVerifyStatus(existing.getAuthInfo().getLocal().getEmailVerifyStatus());
		} else {
			EmailVerifyStatus verifyStatus = new EmailVerifyStatus();
			verifyStatus.setId(1);
			local.setEmailVerifyStatus(verifyStatus);
		}

		authInfo.setLocal(local);
		authInfo.setHasLocalAuth(true);
		user.setAuthInfo(authInfo);

		if (existing.getStatus() != null) {
			user.setStatus(existing.getStatus());
		} else {
			UserStatus status = new UserStatus();
			status.setId(1);
			user.setStatus(status);
		}

		return user;
	}

	public User toEntity(UserUpdateRequest dto, User existing, String newPasswordHash) {
		if (dto == null || existing == null) {
			return null;
		}

		User user = toUpdatedUser(dto, existing, newPasswordHash);
		UserProfile profile = toUserProfile(dto);
		if (profile != null) {
			profile.setUserId(dto.getId());
		}
		user.setProfile(profile);
		return user;
	}

	public User toProfileUpdatedUser(User existing, UserProfileRequest request) {
		if (existing == null || request == null) {
			return null;
		}

		UserProfile profile = toUserProfile(request);
		profile.setUserId(existing.getId());

		User user = new User();
		user.setId(existing.getId());
		user.setProfile(profile);
		user.setStatus(existing.getStatus());
		user.setTokenVersion(existing.getTokenVersion());
		user.setRole(existing.getRole());

		UserAuthInfo authInfo = existing.getAuthInfo();
		if (authInfo != null && authInfo.getLocal() != null && request.getEmail() != null) {
			UserAuthInfo updatedAuth = new UserAuthInfo();
			UserLocalAuth local = new UserLocalAuth();
			local.setUsername(authInfo.getLocal().getUsername());
			local.setPasswordHash(authInfo.getLocal().getPasswordHash());
			local.setEmail(request.getEmail());
			local.setEmailVerifyStatus(authInfo.getLocal().getEmailVerifyStatus());
			updatedAuth.setLocal(local);
			updatedAuth.setHasLocalAuth(true);
			user.setAuthInfo(updatedAuth);
		} else {
			user.setAuthInfo(authInfo);
		}

		return user;
	}

	public UserDetailResponse toUserDetailResponse(User user) {
		if (user == null) {
			return null;
		}

		UserDetailResponse.Builder builder = new UserDetailResponse.Builder().id(user.getId()).username(user.getUsername()).tokenVersion(user.getTokenVersion()).lastLoginAt(user.getLastLoginAt())
				.createdAt(user.getCreatedAt()).updatedAt(user.getUpdatedAt()).isDeleted(user.isDeleted()).isLocked(user.isLocked()).status(user.getStatus()).role(user.getRole());

		if (user.getProfile() != null) {
			builder.fullname(user.getProfile().getFullname()).email(user.getProfile().getEmail() != null ? user.getProfile().getEmail() : user.getEmail())
					.phoneNumber(user.getProfile().getPhoneNumber()).gender(user.getProfile().getGender()).preferredLanguage(user.getProfile().getPreferredLanguage())
					.avatarUrl(user.getProfile().getAvatarUrl());
		} else {
			builder.email(user.getEmail());
		}

		return builder.build();
	}

	public UserManageResponse toManageUserResponse(User user) {
		if (user == null) {
			return null;
		}

		UserManageResponse.Builder builder = new UserManageResponse.Builder().id(user.getId()).username(user.getUsername()).status(user.getStatus()).createdAt(user.getCreatedAt());

		if (user.getProfile() != null) {
			builder.fullname(user.getProfile().getFullname()).email(user.getProfile().getEmail() != null ? user.getProfile().getEmail() : user.getEmail())
					.phoneNumber(user.getProfile().getPhoneNumber()).gender(user.getProfile().getGender());
		} else {
			builder.email(user.getEmail());
		}

		if (user.getRole() != null) {
			builder.role(user.getRole().getCode());
			builder.isSystem(user.getRole().isSystem());
		}

		return builder.build();
	}

	public UserProfileResponse toUserProfileResponse(User user) {
		if (user == null) {
			return null;
		}

		UserProfileResponse.Builder builder = new UserProfileResponse.Builder().userId(user.getId());

		if (user.getProfile() != null) {
			builder.fullname(user.getProfile().getFullname()).phoneNumber(user.getProfile().getPhoneNumber())
					.email(user.getProfile().getEmail() != null ? user.getProfile().getEmail() : user.getEmail()).avatarUrl(user.getProfile().getAvatarUrl());

			if (user.getProfile().getGender() != null) {
				builder.genderCode(user.getProfile().getGender().getCode());
			}

			if (user.getProfile().getPreferredLanguage() != null) {
				builder.languageCode(user.getProfile().getPreferredLanguage().getCode());
			}
		} else {
			builder.email(user.getEmail());
		}

		return builder.build();
	}

	public UserCreateRequest toUserCreateRequest(UserDetailResponse detail) {
		if (detail == null) {
			return null;
		}

		return new UserCreateRequest.Builder().id(detail.getId()).username(detail.getUsername()).fullname(detail.getFullname()).email(detail.getEmail()).phoneNumber(detail.getPhoneNumber())
				.gender(detail.getGender()).preferredLanguage(detail.getPreferredLanguage()).avatarUrl(detail.getAvatarUrl()).role(detail.getRole()).build();
	}

	public UserUpdateRequest toUserUpdateRequest(UserDetailResponse detail) {
		if (detail == null) {
			return null;
		}

		return new UserUpdateRequest.Builder().id(detail.getId()).username(detail.getUsername()).fullname(detail.getFullname()).email(detail.getEmail()).phoneNumber(detail.getPhoneNumber())
				.gender(detail.getGender()).preferredLanguage(detail.getPreferredLanguage()).avatarUrl(detail.getAvatarUrl()).role(detail.getRole()).build();
	}

	private UserProfile toUserProfile(UserCreateRequest dto) {
		if (dto == null) {
			return null;
		}

		UserProfile profile = new UserProfile();
		profile.setFullname(dto.getFullname());
		profile.setEmail(dto.getEmail());
		profile.setPhoneNumber(dto.getPhoneNumber());
		profile.setGender(dto.getGender());
		profile.setPreferredLanguage(dto.getPreferredLanguage());
		profile.setAvatarUrl(dto.getAvatarUrl());
		return profile;
	}

	private UserProfile toUserProfile(UserUpdateRequest dto) {
		if (dto == null) {
			return null;
		}

		UserProfile profile = new UserProfile();
		profile.setFullname(dto.getFullname());
		profile.setEmail(dto.getEmail());
		profile.setPhoneNumber(dto.getPhoneNumber());
		profile.setGender(dto.getGender());
		profile.setPreferredLanguage(dto.getPreferredLanguage());
		profile.setAvatarUrl(dto.getAvatarUrl());
		return profile;
	}
}