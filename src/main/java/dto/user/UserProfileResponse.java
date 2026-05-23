package dto.user;

public class UserProfileResponse {
	private final long userId;
	private final String fullname;
	private final String phoneNumber;
	private final String email;
	private final String genderCode;
	private final String languageCode;
	private final String avatarUrl;

	private UserProfileResponse(Builder builder) {
		this.userId = builder.userId;
		this.fullname = builder.fullname;
		this.phoneNumber = builder.phoneNumber;
		this.email = builder.email;
		this.genderCode = builder.genderCode;
		this.languageCode = builder.languageCode;
		this.avatarUrl = builder.avatarUrl;
	}

	public long getUserId() {
		return userId;
	}

	public String getFullname() {
		return fullname;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public static class Builder {
		private long userId;
		private String fullname;
		private String phoneNumber;
		private String email;
		private String genderCode;
		private String languageCode;
		private String avatarUrl;

		public Builder userId(long userId) {
			this.userId = userId;
			return this;
		}

		public Builder fullname(String fullname) {
			this.fullname = fullname;
			return this;
		}

		public Builder phoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder genderCode(String genderCode) {
			this.genderCode = genderCode;
			return this;
		}

		public Builder languageCode(String languageCode) {
			this.languageCode = languageCode;
			return this;
		}

		public Builder avatarUrl(String avatarUrl) {
			this.avatarUrl = avatarUrl;
			return this;
		}

		public UserProfileResponse build() {
			return new UserProfileResponse(this);
		}
	}
}