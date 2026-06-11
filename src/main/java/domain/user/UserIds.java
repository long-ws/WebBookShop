package domain.user;

public final class UserIds {

	public static final class Status {
		public static final int ACTIVE = 1;
		public static final int INACTIVE = 2;
		public static final int SUSPENDED = 3;
		public static final int DELETED = 4;
	}

	public static final class EmailVerifyStatus {
		public static final int UNVERIFIED = 0;
		public static final int VERIFIED = 1;
	}

	public static final class Gender {
		public static final int NOT_SPECIFIED = -1;
		public static final int MALE = 0;
		public static final int FEMALE = 1;
	}

	private UserIds() {
	}
}
