package dto.user;

import beans.common.Gender;
import beans.common.Language;
import beans.common.Role;

public class UserCreateRequest {
	private final Long id;
	private final String username;
	private final String password;
	private final String fullname;
	private final String email;
	private final String phoneNumber;
	private final Gender gender;
	private final Language preferredLanguage;
	private final String avatarUrl;
	private final Role role;

	private UserCreateRequest(Builder builder) {
		this.id = builder.id;
		this.username = builder.username;
		this.password = builder.password;
		this.fullname = builder.fullname;
		this.email = builder.email;
		this.phoneNumber = builder.phoneNumber;
		this.gender = builder.gender;
		this.preferredLanguage = builder.preferredLanguage;
		this.avatarUrl = builder.avatarUrl;
		this.role = builder.role;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
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

	public static class Builder {
		private Long id;
		private String username;
		private String password;
		private String fullname;
		private String email;
		private String phoneNumber;
		private Gender gender;
		private Language preferredLanguage;
		private String avatarUrl;
		private Role role;

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
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

		public UserCreateRequest build() {
			return new UserCreateRequest(this);
		}
	}
}