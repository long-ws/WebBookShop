package beans.oauth;

public class OAuthUser {
	private String id;
	private String email;
	private String name;
	private String picture;
	private String provider;

	public OAuthUser() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPictureUrl() {
		return picture;
	}

	public void setPictureUrl(String picture) {
		this.picture = picture;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}