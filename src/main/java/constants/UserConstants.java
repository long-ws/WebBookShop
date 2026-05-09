package constants;

/**
 * Constants for user-related operations.
 */
public final class UserConstants {

	private UserConstants() {
	}

	public static final class Status {
		public static final int ACTIVE = 1;
		public static final int INACTIVE = 2;
		public static final int SUSPENDED = 3;
		public static final int DELETED = 4;
	}

	public static final class EmailVerifyStatus {
		public static final int VERIFIED = 1;
		public static final int UNVERIFIED = 0;
	}

	public static final class Gender {
		public static final int NOT_SPECIFIED = -1;
		public static final int MALE = 0;
		public static final int FEMALE = 1;
	}

	public static final class Role {
		public static final String SUPER_ADMIN = "SUPER_ADMIN";
		public static final String ADMIN = "ADMIN";
		public static final String STAFF = "STAFF";
		public static final String CUSTOMER = "CUSTOMER";
	}

	public static final class Security {
		public static final int MAX_FAILED_ATTEMPTS = 5;
		public static final int LOCK_DURATION_MINUTES = 30;
		public static final int TOKEN_VERSION_INITIAL = 0;
	}
}