package policy.email;

public final class EmailVerificationPolicy {

	public static final int TOKEN_EXPIRES_MINUTES = 15;
	public static final int MAX_SEND_PER_HOUR = 5;
	public static final int MIN_SEND_INTERVAL_SECONDS = 60;

	private EmailVerificationPolicy() {
	}
}
