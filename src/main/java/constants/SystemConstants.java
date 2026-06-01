package constants;

import java.io.InputStream;
import java.util.Properties;

public final class SystemConstants {

	public static final String ERROR_GLOBAL = "globalError";
	public static final int DEFAULT_LANGUAGE_ID = 1;
	public static final String DEFAULT_ROLE_CODE = "CUSTOMER";
	public static final String DUMMY_BCRYPT = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

	private SystemConstants() {
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
		public static final long SUPER_ADMIN_USER_ID = -1L;

		private static final String SECURITY_PROPERTIES_RESOURCE = "security.properties";
		private static final String KEY_SUPER_ADMIN_USERNAME = "security.super_admin.username";
		private static final String KEY_SUPER_ADMIN_PASSWORD_BCRYPT = "security.super_admin.password_bcrypt";
		private static final String KEY_SUPER_ADMIN_ROLE_CODE = "security.super_admin.role_code";
		private static final String KEY_SYSTEM_GHOST_USER_ID = "security.system_ghost.user_id";
		private static final String KEY_SYSTEM_GHOST_DISPLAY_NAME = "security.system_ghost.display_name";

		private static final Properties SECURITY_PROPERTIES = loadSecurityProperties();

		public static final String SUPER_ADMIN_USERNAME = getSecurityProperty(KEY_SUPER_ADMIN_USERNAME, "admin");
		public static final String SUPER_ADMIN_PASSWORD_BCRYPT = getSecurityProperty(KEY_SUPER_ADMIN_PASSWORD_BCRYPT, SystemConstants.DUMMY_BCRYPT);
		public static final String SUPER_ADMIN_ROLE_CODE = getSecurityProperty(KEY_SUPER_ADMIN_ROLE_CODE, "SUPER_ADMIN");
		public static final long SYSTEM_GHOST_USER_ID = getSecurityLongProperty(KEY_SYSTEM_GHOST_USER_ID, 1L);
		public static final String SYSTEM_GHOST_DISPLAY_NAME = getSecurityProperty(KEY_SYSTEM_GHOST_DISPLAY_NAME, "SYSTEM");

		public static boolean isSuperAdminUserId(final long userId) {
			return userId == SUPER_ADMIN_USER_ID;
		}

		public static boolean isSystemGhostUserId(final long userId) {
			return userId == SYSTEM_GHOST_USER_ID;
		}

		public static boolean isSuperAdminUsername(final String username) {
			if (username == null) {
				return false;
			}
			final String normalized = username.trim();
			return !normalized.isEmpty() && SUPER_ADMIN_USERNAME.equalsIgnoreCase(normalized);
		}

		private static Properties loadSecurityProperties() {
			Properties props = new Properties();

			try (InputStream in = SystemConstants.class.getClassLoader().getResourceAsStream(SECURITY_PROPERTIES_RESOURCE)) {
				if (in != null) {
					props.load(in);
				}
			} catch (Exception e) {
				return props;
			}

			return props;
		}

		private static String getSecurityProperty(final String key, final String defaultValue) {
			if (key == null || key.isBlank()) {
				return defaultValue;
			}

			final String value = SECURITY_PROPERTIES.getProperty(key);

			if (value == null) {
				return defaultValue;
			}

			final String normalized = value.trim();
			return normalized.isEmpty() ? defaultValue : normalized;
		}

		private static long getSecurityLongProperty(final String key, final long defaultValue) {
			final String value = getSecurityProperty(key, null);

			if (value == null) {
				return defaultValue;
			}
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	public static final class Validation {
		public static final int USERNAME_MIN_LENGTH = 3;
		public static final int USERNAME_MAX_LENGTH = 50;

		public static final int PASSWORD_MIN_LENGTH = 8;
		public static final int PASSWORD_MAX_LENGTH = 100;

		public static final int FULLNAME_MAX_LENGTH = 100;

		public static final int PHONE_MIN_LENGTH = 10;
		public static final int PHONE_MAX_LENGTH = 15;

		public static final int EMAIL_MAX_LENGTH = 100;
	}
}
