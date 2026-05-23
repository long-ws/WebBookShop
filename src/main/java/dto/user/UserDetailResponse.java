package dto.user;

import beans.common.Gender;
import beans.common.Language;
import beans.common.Role;
import beans.common.UserStatus;

import java.sql.Timestamp;

public class UserDetailResponse {
	private final Long id;
	private final String username;
	private final String fullname;
	private final String email;
	private final String phoneNumber;
	private final Gender gender;
	private final Language preferredLanguage;
	private final String avatarUrl;
	private final Role role;
	private final UserStatus status;
	private final int tokenVersion;
	private final Timestamp lastLoginAt;
	private final Timestamp createdAt;
	private final Timestamp updatedAt;
	private final boolean deleted;
	private final boolean locked;

	private UserDetailResponse(Builder builder) {
		this.id = builder.id;
		this.username = builder.username;
		this.fullname = builder.fullname;
		this.email = builder.email;
		this.phoneNumber = builder.phoneNumber;
		this.gender = builder.gender;
		this.preferredLanguage = builder.preferredLanguage;
		this.avatarUrl = builder.avatarUrl;
		this.role = builder.role;
		this.status = builder.status;
		this.tokenVersion = builder.tokenVersion;
		this.lastLoginAt = builder.lastLoginAt;
		this.createdAt = builder.createdAt;
		this.updatedAt = builder.updatedAt;
		this.deleted = builder.deleted;
		this.locked = builder.locked;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getFullname() {
		return fullname;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public Gender getGender() {
		return gender;
	}

	public Language getPreferredLanguage() {
		return preferredLanguage;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public Role getRole() {
		return role;
	}

	public UserStatus getStatus() {
		return status;
	}

	public int getTokenVersion() {
		return tokenVersion;
	}

	public Timestamp getLastLoginAt() {
		return lastLoginAt;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public boolean isLocked() {
		return locked;
	}

	public static class Builder {
		private Long id;
		private String username;
		private String fullname;
		private String email;
		private String phoneNumber;
		private Gender gender;
		private Language preferredLanguage;
		private String avatarUrl;
		private Role role;
		private UserStatus status;
		private int tokenVersion;
		private Timestamp lastLoginAt;
		private Timestamp createdAt;
		private Timestamp updatedAt;
		private boolean deleted;
		private boolean locked;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder fullname(String fullname) {
			this.fullname = fullname;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder phoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public Builder gender(Gender gender) {
			this.gender = gender;
			return this;
		}

		public Builder preferredLanguage(Language preferredLanguage) {
			this.preferredLanguage = preferredLanguage;
			return this;
		}

		public Builder avatarUrl(String avatarUrl) {
			this.avatarUrl = avatarUrl;
			return this;
		}

		public Builder role(Role role) {
			this.role = role;
			return this;
		}

		public Builder status(UserStatus status) {
			this.status = status;
			return this;
		}

		public Builder tokenVersion(int tokenVersion) {
			this.tokenVersion = tokenVersion;
			return this;
		}

		public Builder lastLoginAt(Timestamp lastLoginAt) {
			this.lastLoginAt = lastLoginAt;
			return this;
		}

		public Builder createdAt(Timestamp createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Builder updatedAt(Timestamp updatedAt) {
			this.updatedAt = updatedAt;
			return this;
		}

		public Builder isDeleted(boolean deleted) {
			this.deleted = deleted;
			return this;
		}

		public Builder isLocked(boolean locked) {
			this.locked = locked;
			return this;
		}

		public UserDetailResponse build() {
			return new UserDetailResponse(this);
		}
	}
}