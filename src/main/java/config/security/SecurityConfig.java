package config.security;

import java.io.InputStream;
import java.util.Properties;

import constants.auth.AuthConstants;
import domain.user.RoleCodes;

public final class SecurityConfig {

	public static final long SUPER_ADMIN_USER_ID = -1L;

	private static final String SECURITY_PROPERTIES_RESOURCE = "security.properties";
	private static final String KEY_SUPER_ADMIN_USERNAME = "security.super_admin.username";
	private static final String KEY_SUPER_ADMIN_PASSWORD_BCRYPT = "security.super_admin.password_bcrypt";
	private static final String KEY_SUPER_ADMIN_ROLE_CODE = "security.super_admin.role_code";
	private static final String KEY_SYSTEM_GHOST_USER_ID = "security.system_ghost.user_id";
	private static final String KEY_SYSTEM_GHOST_DISPLAY_NAME = "security.system_ghost.display_name";

	private static final Properties SECURITY_PROPERTIES = loadSecurityProperties();

	public static final String SUPER_ADMIN_USERNAME = getSecurityProperty(KEY_SUPER_ADMIN_USERNAME, "admin");
	public static final String SUPER_ADMIN_PASSWORD_BCRYPT = getSecurityProperty(KEY_SUPER_ADMIN_PASSWORD_BCRYPT, AuthConstants.DUMMY_BCRYPT);
	public static final String SUPER_ADMIN_ROLE_CODE = getSecurityProperty(KEY_SUPER_ADMIN_ROLE_CODE, RoleCodes.SUPER_ADMIN);

	public static final long SYSTEM_GHOST_USER_ID = getSecurityLongProperty(KEY_SYSTEM_GHOST_USER_ID, 1L);
	public static final String SYSTEM_GHOST_DISPLAY_NAME = getSecurityProperty(KEY_SYSTEM_GHOST_DISPLAY_NAME, "SYSTEM");

	private SecurityConfig() {
	}

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
		try (InputStream in = SecurityConfig.class.getClassLoader().getResourceAsStream(SECURITY_PROPERTIES_RESOURCE)) {
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
