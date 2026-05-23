package dto.user;

public class OAuthUserRegistrationRequest {
	private final int providerId;
	private final String providerUserId;
	private final String fullname;
	private final String email;
	private final String avatarUrl;

	private OAuthUserRegistrationRequest(Builder builder) {
		this.providerId = builder.providerId;
		this.providerUserId = builder.providerUserId;
		this.fullname = builder.fullname;
		this.email = builder.email;
		this.avatarUrl = builder.avatarUrl;
	}

	public int getProviderId() {
		return providerId;
	}

	public String getProviderUserId() {
		return providerUserId;
	}

	public String getFullname() {
		return fullname;
	}

	public String getEmail() {
		return email;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public static class Builder {
		private int providerId;
		private String providerUserId;
		private String fullname;
		private String email;
		private String avatarUrl;

		public Builder providerId(int providerId) {
			this.providerId = providerId;
			return this;
		}

		public Builder providerUserId(String providerUserId) {
			this.providerUserId = providerUserId;
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

		public Builder avatarUrl(String avatarUrl) {
			this.avatarUrl = avatarUrl;
			return this;
		}

		public OAuthUserRegistrationRequest build() {
			return new OAuthUserRegistrationRequest(this);
		}
	}
}