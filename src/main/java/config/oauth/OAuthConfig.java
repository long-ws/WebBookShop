package config.oauth;

import java.io.InputStream;
import java.util.Properties;

public final class OAuthConfig {

	private static final String OAUTH_PROPERTIES_RESOURCE = "oauth.properties";

	private static final String KEY_GOOGLE_CLIENT_ID = "google.client.id";
	private static final String KEY_GOOGLE_CLIENT_SECRET = "google.client.secret";
	private static final String KEY_GOOGLE_SCOPE = "google.scope";

	private static final Properties OAUTH_PROPERTIES = loadOAuthProperties();

	private OAuthConfig() {
	}

	public static String googleClientId() {
		return getRequiredOAuthProperty(KEY_GOOGLE_CLIENT_ID);
	}

	public static String googleClientSecret() {
		return getRequiredOAuthProperty(KEY_GOOGLE_CLIENT_SECRET);
	}

	public static String googleScope() {
		return getRequiredOAuthProperty(KEY_GOOGLE_SCOPE);
	}

	private static Properties loadOAuthProperties() {
		Properties props = new Properties();
		try (InputStream in = OAuthConfig.class.getClassLoader().getResourceAsStream(OAUTH_PROPERTIES_RESOURCE)) {
			if (in != null) {
				props.load(in);
			}
		} catch (Exception e) {
			return props;
		}
		return props;
	}

	private static String getRequiredOAuthProperty(final String key) {
		final String value = getOAuthProperty(key);
		if (value != null) {
			return value;
		}
		throw new IllegalStateException("Thiếu cấu hình OAuth: " + key);
	}

	private static String getOAuthProperty(final String key) {
		if (key == null || key.isBlank()) {
			return null;
		}
		
		final String value = OAUTH_PROPERTIES.getProperty(key);
		if (value == null) {
			return null;
		}
		
		final String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}
}
