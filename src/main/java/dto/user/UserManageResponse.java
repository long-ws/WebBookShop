package dto.user;

import beans.common.Gender;
import beans.common.UserStatus;

import java.sql.Timestamp;

public class UserManageResponse {
	private final Long id;
	private final String username;
	private final String fullname;
	private final String email;
	private final String phoneNumber;
	private final Gender gender;
	private final String role;
	private final UserStatus status;
	private final Timestamp createdAt;
	private final boolean isSystem;

	private UserManageResponse(Builder builder) {
		this.id = builder.id;
		this.username = builder.username;
		this.fullname = builder.fullname;
		this.email = builder.email;
		this.phoneNumber = builder.phoneNumber;
		this.gender = builder.gender;
		this.role = builder.role;
		this.status = builder.status;
		this.createdAt = builder.createdAt;
		this.isSystem = builder.isSystem;
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

	public String getRole() {
		return role;
	}

	public UserStatus getStatus() {
		return status;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public boolean isSystem() {
		return isSystem;
	}

	public static class Builder {
		private Long id;
		private String username;
		private String fullname;
		private String email;
		private String phoneNumber;
		private Gender gender;
		private String role;
		private UserStatus status;
		private Timestamp createdAt;
		private boolean isSystem;

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

		public Builder role(String role) {
			this.role = role;
			return this;
		}

		public Builder status(UserStatus status) {
			this.status = status;
			return this;
		}

		public Builder createdAt(Timestamp createdAt) {
			this.createdAt = createdAt;
			return this;
		}
		
		public Builder isSystem(boolean isSystem) {
			this.isSystem = isSystem;
			return this;
		}

		public UserManageResponse build() {
			return new UserManageResponse(this);
		}
	}
}