package dto.oauth;

public class OAuthUserResponse {
	private final String id;
	private final String email;
	private final String name;
	private final String pictureUrl;
	private final String provider;

	private OAuthUserResponse(Builder builder) {
		this.id = builder.id;
		this.email = builder.email;
		this.name = builder.name;
		this.pictureUrl = builder.pictureUrl;
		this.provider = builder.provider;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public String getProvider() {
		return provider;
	}

	public static class Builder {
		private String id;
		private String email;
		private String name;
		private String pictureUrl;
		private String provider;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder pictureUrl(String pictureUrl) {
			this.pictureUrl = pictureUrl;
			return this;
		}

		public Builder provider(String provider) {
			this.provider = provider;
			return this;
		}

		public OAuthUserResponse build() {
			return new OAuthUserResponse(this);
		}
	}
}