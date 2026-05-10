package beans.user;

import java.util.List;

public class UserAuthInfo {
	private long userId;
	private UserLocalAuth local;
	private List<UserOAuthAuth> oauth;
	private boolean hasLocalAuth;
	private boolean hasOAuthAuth;

	public UserAuthInfo() {
	}

	public UserAuthInfo(long userId, UserLocalAuth local, List<UserOAuthAuth> oauth) {
		this.userId = userId;
		this.local = local;
		this.oauth = oauth;
		this.hasLocalAuth = local != null;
		this.hasOAuthAuth = oauth != null && !oauth.isEmpty();
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public UserLocalAuth getLocal() {
		return local;
	}

	public void setLocal(UserLocalAuth local) {
		this.local = local;
		this.hasLocalAuth = local != null;
	}

	public List<UserOAuthAuth> getOauth() {
		return oauth;
	}

	public void setOauth(List<UserOAuthAuth> oauth) {
		this.oauth = oauth;
		this.hasOAuthAuth = oauth != null && !oauth.isEmpty();
	}

	public boolean isHasLocalAuth() {
		return hasLocalAuth;
	}

	public void setHasLocalAuth(boolean hasLocalAuth) {
		this.hasLocalAuth = hasLocalAuth;
	}

	public boolean isHasOAuthAuth() {
		return hasOAuthAuth;
	}

	public void setHasOAuthAuth(boolean hasOAuthAuth) {
		this.hasOAuthAuth = hasOAuthAuth;
	}
}
