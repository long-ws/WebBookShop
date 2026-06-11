package policy.security;

public final class AuthenticationPolicy {

	public static final int MAX_FAILED_ATTEMPTS = 5;
	public static final int LOCK_DURATION_MINUTES = 30;
	public static final int TOKEN_VERSION_INITIAL = 0;

	private AuthenticationPolicy() {
	}
}
