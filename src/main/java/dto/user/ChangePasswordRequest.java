package dto.user;

public class ChangePasswordRequest {
	private final String currentPassword;
	private final String newPassword;
	private final String confirmPassword;

	private ChangePasswordRequest(Builder builder) {
		this.currentPassword = builder.currentPassword;
		this.newPassword = builder.newPassword;
		this.confirmPassword = builder.confirmPassword;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public static class Builder {
		private String currentPassword;
		private String newPassword;
		private String confirmPassword;

		public Builder currentPassword(String currentPassword) {
			this.currentPassword = currentPassword;
			return this;
		}

		public Builder newPassword(String newPassword) {
			this.newPassword = newPassword;
			return this;
		}

		public Builder confirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
			return this;
		}

		public ChangePasswordRequest build() {
			return new ChangePasswordRequest(this);
		}
	}
}