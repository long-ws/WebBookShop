package beans;

import java.sql.Timestamp;

import beans.common.Gender;
import beans.common.Language;
import beans.common.Role;
import beans.common.UserStatus;
import beans.user.UserAuthInfo;
import beans.user.UserLocalAuth;
import beans.user.UserProfile;

public class User {
	private long id;
	private String username;

	private int tokenVersion;
	private Timestamp lastLoginAt;
	private String rememberToken;
	private Timestamp rememberExpiresAt;

	private Timestamp deletedAt;
	private Long deletedBy;
	private Timestamp deletionScheduledAt;

	private Timestamp createdAt;
	private Timestamp updatedAt;

	private UserStatus status;
	private Role role;

	private UserProfile profile;
	private UserAuthInfo authInfo;

	public User() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public int getTokenVersion() {
		return tokenVersion;
	}

	public void setTokenVersion(int tokenVersion) {
		this.tokenVersion = tokenVersion;
	}

	public Timestamp getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(Timestamp lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public String getRememberToken() {
		return rememberToken;
	}

	public void setRememberToken(String rememberToken) {
		this.rememberToken = rememberToken;
	}

	public Timestamp getRememberExpiresAt() {
		return rememberExpiresAt;
	}

	public void setRememberExpiresAt(Timestamp rememberExpiresAt) {
		this.rememberExpiresAt = rememberExpiresAt;
	}

	public Timestamp getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Long getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(Long deletedBy) {
		this.deletedBy = deletedBy;
	}

	public Timestamp getDeletionScheduledAt() {
		return deletionScheduledAt;
	}

	public void setDeletionScheduledAt(Timestamp deletionScheduledAt) {
		this.deletionScheduledAt = deletionScheduledAt;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return profile != null ? profile.getEmail() : null;
	}

	public void setEmail(String email) {
		if (this.profile == null) {
			this.profile = new UserProfile();
		}
		this.profile.setEmail(email);
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UserProfile getProfile() {
		return profile;
	}

	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}

	public UserAuthInfo getAuthInfo() {
		return authInfo;
	}

	public void setAuthInfo(UserAuthInfo authInfo) {
		this.authInfo = authInfo;
	}

	public boolean isDeleted() {
		return deletedAt != null;
	}

	public boolean isLocked() {
		return authInfo != null && authInfo.getLocal() != null && authInfo.getLocal().getLockedUntil() != null
				&& authInfo.getLocal().getLockedUntil().after(new Timestamp(System.currentTimeMillis()));
	}

	public boolean isEmailVerified() {
		return authInfo != null && authInfo.getLocal() != null && authInfo.getLocal().getEmailVerifyStatus() != null
				&& authInfo.getLocal().getEmailVerifyStatus().getId() == 1;
	}

	public String getFullname() {
		return profile != null ? profile.getFullname() : null;
	}

	public void setFullname(String fullname) {
		if (this.profile == null) {
			this.profile = new UserProfile();
		}
		this.profile.setFullname(fullname);
	}

	public String getPhoneNumber() {
		return profile != null ? profile.getPhoneNumber() : null;
	}

	public void setPhoneNumber(String phoneNumber) {
		if (this.profile == null) {
			this.profile = new UserProfile();
		}
		this.profile.setPhoneNumber(phoneNumber);
	}

	public String getAvatarUrl() {
		return profile != null ? profile.getAvatarUrl() : null;
	}

	public void setAvatarUrl(String avatarUrl) {
		if (this.profile == null) {
			this.profile = new UserProfile();
		}
		this.profile.setAvatarUrl(avatarUrl);
	}

	public Gender getGender() {
		return profile != null ? profile.getGender() : null;
	}

	public void setGender(Gender gender) {
		if (this.profile == null) {
			this.profile = new UserProfile();
		}
		this.profile.setGender(gender);
	}

	public String getPasswordHash() {
		return authInfo != null && authInfo.getLocal() != null ? authInfo.getLocal().getPasswordHash() : null;
	}

	public void setPasswordHash(String passwordHash) {
		if (this.authInfo == null) {
			this.authInfo = new UserAuthInfo();
		}
		if (this.authInfo.getLocal() == null) {
			this.authInfo.setLocal(new UserLocalAuth());
		}
		this.authInfo.getLocal().setPasswordHash(passwordHash);
	}

	public Timestamp getLockedUntil() {
		return authInfo != null && authInfo.getLocal() != null ? authInfo.getLocal().getLockedUntil() : null;
	}

	public int getStatusId() {
		return status != null ? status.getId() : 0;
	}

	public Language getPreferredLanguage() {
		return profile != null ? profile.getPreferredLanguage() : null;
	}

	public void setPreferredLanguage(Language language) {
		if (this.profile == null) {
			this.profile = new UserProfile();
		}
		this.profile.setPreferredLanguage(language);
	}
}
