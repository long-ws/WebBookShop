package dto.user;

public class LocalUserRegistrationRequest {
	private final String username;
	private final String password;
	private final String fullname;
	private final String email;

	private LocalUserRegistrationRequest(Builder builder) {
		this.username = builder.username;
		this.password = builder.password;
		this.fullname = builder.fullname;
		this.email = builder.email;
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

	public static class Builder {
		private String username;
		private String password;
		private String fullname;
		private String email;

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

		public LocalUserRegistrationRequest build() {
			return new LocalUserRegistrationRequest(this);
		}
	}
}