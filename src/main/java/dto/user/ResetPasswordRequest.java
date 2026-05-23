package dto.user;

public class ResetPasswordRequest {
	private final String newPassword;
	private final String confirmPassword;

	private ResetPasswordRequest(Builder builder) {
		this.newPassword = builder.newPassword;
		this.confirmPassword = builder.confirmPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public static class Builder {
		private String newPassword;
		private String confirmPassword;

		public Builder newPassword(String newPassword) {
			this.newPassword = newPassword;
			return this;
		}

		public Builder confirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
			return this;
		}

		public ResetPasswordRequest build() {
			return new ResetPasswordRequest(this);
		}
	}
}