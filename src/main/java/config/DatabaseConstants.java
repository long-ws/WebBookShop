package config;

public class DatabaseConstants {

	// ==================== TABLE NAMES ====================
	public static final String TABLE_USER_ACCOUNT = "user_account";
	public static final String TABLE_USER_PROFILE = "user_profile";
	public static final String TABLE_USER_LOCAL = "user_local";
	public static final String TABLE_USER_OAUTH = "user_oauth";
	public static final String TABLE_USER_ROLE_REGISTRY = "user_role_registry";
	public static final String TABLE_ROLE_REGISTRY = "role_registry";
	public static final String TABLE_PERMISSION_REGISTRY = "permission_registry";
	public static final String TABLE_ROLE_PERMISSION_ASSIGNMENT = "role_permission_assignment";
	public static final String TABLE_USER_ACCOUNT_STATUS = "user_account_status";
	public static final String TABLE_LANGUAGE_REGISTRY = "language_registry";
	public static final String TABLE_GENDER_REGISTRY = "gender_registry";

	// ==================== COLUMN NAMES - USER_ACCOUNT ====================
	public static final String COL_USER_ID = "id";
	public static final String COL_USER_STATUS_ID = "status_id";
	public static final String COL_USER_TOKEN_VERSION = "token_version";
	public static final String COL_USER_LAST_LOGIN_AT = "last_login_at";
	public static final String COL_USER_REMEMBER_TOKEN = "remember_token";
	public static final String COL_USER_REMEMBER_EXPIRES_AT = "remember_expires_at";
	public static final String COL_USER_CREATED_AT = "created_at";
	public static final String COL_USER_UPDATED_AT = "updated_at";

	// ==================== COLUMN NAMES - USER_LOCAL ====================
	public static final String COL_LOCAL_USER_ID = "user_id";
	public static final String COL_LOCAL_USERNAME = "username";
	public static final String COL_LOCAL_PASSWORD_HASH = "password_hash";
	public static final String COL_LOCAL_EMAIL = "email";
	public static final String COL_LOCAL_EMAIL_VERIFY_STATUS_ID = "email_verify_status_id";
	public static final String COL_LOCAL_FAILED_ATTEMPTS = "failed_attempts";
	public static final String COL_LOCAL_LOCKED_UNTIL = "locked_until";

	// ==================== COLUMN NAMES - USER_PROFILE ====================
	public static final String COL_PROFILE_USER_ID = "user_id";
	public static final String COL_PROFILE_FULLNAME = "fullname";
	public static final String COL_PROFILE_PHONE_NUMBER = "phone_number";
	public static final String COL_PROFILE_GENDER_ID = "gender_id";
	public static final String COL_PROFILE_PREFERRED_LANGUAGE_ID = "preferred_language_id";
	public static final String COL_PROFILE_AVATAR_URL = "avatar_url";
	public static final String COL_PROFILE_UPDATED_AT = "updated_at";

	// ==================== COLUMN NAMES - USER_OAUTH ====================
	public static final String COL_OAUTH_ID = "id";
	public static final String COL_OAUTH_USER_ID = "user_id";
	public static final String COL_OAUTH_PROVIDER_ID = "provider_id";
	public static final String COL_OAUTH_PROVIDER_USER_ID = "provider_user_id";
	public static final String COL_OAUTH_EMAIL = "email";
	public static final String COL_OAUTH_DISPLAY_NAME = "display_name";
	public static final String COL_OAUTH_AVATAR_URL = "avatar_url";

	// ==================== DEFAULT VALUES ====================
	public static final int DEFAULT_STATUS_ACTIVE = 1;
	public static final int DEFAULT_STATUS_INACTIVE = 2;

	public static final int DEFAULT_EMAIL_VERIFY_UNVERIFIED = 1;
	public static final int DEFAULT_EMAIL_VERIFY_VERIFIED = 2;

	public static final int DEFAULT_LANGUAGE_VIETNAMESE = 1;

	public static final int DEFAULT_TOKEN_VERSION = 0;

	// ==================== ROLE CODES ====================
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_STAFF = "STAFF";
	public static final String ROLE_CUSTOMER = "CUSTOMER";

	private DatabaseConstants() {
	}
}