package config;

public final class DatabaseConstants {

	// TÊN BẢNG (TABLE NAMES)
	public static final String TABLE_USER_ACCOUNT = "user_account";
	public static final String TABLE_USER_PROFILE = "user_profile";
	public static final String TABLE_USER_LOCAL = "user_local";
	public static final String TABLE_USER_OAUTH = "user_oauth";
	public static final String TABLE_USER_TOKEN = "user_token";
	public static final String TABLE_USER_ROLE_REGISTRY = "user_role_registry";
	public static final String TABLE_ROLE_REGISTRY = "role_registry";
	public static final String TABLE_PERMISSION_REGISTRY = "permission_registry";
	public static final String TABLE_ROLE_PERMISSION_ASSIGNMENT = "role_permission_assignment";
	public static final String TABLE_USER_ACCOUNT_STATUS = "user_account_status";
	public static final String TABLE_EMAIL_VERIFY_STATUS = "email_verify_status";
	public static final String TABLE_OAUTH_PROVIDER = "oauth_provider";
	public static final String TABLE_TOKEN_TYPE = "token_type";
	public static final String TABLE_TOKEN_STATUS = "token_status";
	public static final String TABLE_LANGUAGE_REGISTRY = "language_registry";
	public static final String TABLE_GENDER_REGISTRY = "gender_registry";

	// TÊN CỘT (COLUMNS)
	public static final String COL_ID = "id";
	public static final String COL_USER_ID = "user_id";
	public static final String COL_ROLE_ID = "role_id";
	public static final String COL_PERMISSION_ID = "permission_id";
	public static final String COL_STATUS_ID = "status_id";
	public static final String COL_CODE = "code";
	public static final String COL_CREATED_AT = "created_at";
	public static final String COL_UPDATED_AT = "updated_at";

	// Cột user_account
	public static final String COL_ACCOUNT_STATUS_ID = "status_id";
	public static final String COL_ACCOUNT_TOKEN_VERSION = "token_version";
	public static final String COL_ACCOUNT_LAST_LOGIN_AT = "last_login_at";
	public static final String COL_ACCOUNT_REMEMBER_TOKEN = "remember_token";
	public static final String COL_ACCOUNT_REMEMBER_EXPIRES_AT = "remember_expires_at";
	public static final String COL_ACCOUNT_DELETED_AT = "deleted_at";
	public static final String COL_ACCOUNT_DELETED_BY = "deleted_by";
	public static final String COL_ACCOUNT_DELETION_SCHEDULED_AT = "deletion_scheduled_at";

	// Cột user_local
	public static final String COL_LOCAL_USERNAME = "username";
	public static final String COL_LOCAL_PASSWORD_HASH = "password_hash";
	public static final String COL_LOCAL_EMAIL = "email";
	public static final String COL_LOCAL_EMAIL_VERIFY_STATUS_ID = "email_verify_status_id";
	public static final String COL_LOCAL_FAILED_ATTEMPTS = "failed_attempts";
	public static final String COL_LOCAL_LOCKED_UNTIL = "locked_until";

	// Cột user_profile
	public static final String COL_PROFILE_FULLNAME = "fullname";
	public static final String COL_PROFILE_PHONE_NUMBER = "phone_number";
	public static final String COL_PROFILE_EMAIL = "email";
	public static final String COL_PROFILE_GENDER_ID = "gender_id";
	public static final String COL_PROFILE_PREFERRED_LANGUAGE_ID = "preferred_language_id";
	public static final String COL_PROFILE_AVATAR_URL = "avatar_url";

	// Cột user_oauth
	public static final String COL_OAUTH_PROVIDER_ID = "provider_id";
	public static final String COL_OAUTH_PROVIDER_USER_ID = "provider_user_id";
	public static final String COL_OAUTH_EMAIL = "email";
	public static final String COL_OAUTH_DISPLAY_NAME = "display_name";
	public static final String COL_OAUTH_AVATAR_URL = "avatar_url";

	// Cột oauth_provider
	public static final String COL_OAUTH_PROVIDER_CODE = "code";
	public static final String COL_OAUTH_PROVIDER_NAME = "name";

	// Cột user_token
	public static final String COL_TOKEN_HASH = "token_hash";
	public static final String COL_TOKEN_TYPE_ID = "type_id";
	public static final String COL_TOKEN_USED_AT = "used_at";
	public static final String COL_TOKEN_EXPIRES_AT = "expires_at";

	// Cột language_registry
	public static final String COL_LANGUAGE_CODE = "code";
	public static final String COL_LANGUAGE_NAME = "name";
	public static final String COL_LANGUAGE_DESCRIPTION = "description";
	public static final String COL_LANGUAGE_IS_ACTIVE = "is_active";

	// Cột permission_registry
	public static final String COL_PERMISSION_CODE = "code";
	public static final String COL_PERMISSION_NAME = "name";
	public static final String COL_PERMISSION_DESCRIPTION = "description";
	public static final String COL_PERMISSION_MODULE = "module";
	public static final String COL_PERMISSION_IS_SYSTEM = "is_system";
	public static final String COL_PERMISSION_IS_ACTIVE = "is_active";

	// Cột role_registry
	public static final String COL_ROLE_CODE = "code";
	public static final String COL_ROLE_NAME = "name";
	public static final String COL_ROLE_DESCRIPTION = "description";
	public static final String COL_ROLE_IS_SYSTEM = "is_system";
	public static final String COL_ROLE_IS_ACTIVE = "is_active";

	// Các cột của 
	public static final String COL_ROLE_PERMISSION_ROLE_ID = "role_id";
	public static final String COL_ROLE_PERMISSION_PERMISSION_ID = "permission_id";
	public static final String COL_ROLE_PERMISSION_IS_ACTIVE = "is_active";

	// Cấu hình mặc định
	public static final int DEFAULT_STATUS_ACTIVE = 1;
	public static final int DEFAULT_STATUS_INACTIVE = 2;

	public static final int DEFAULT_EMAIL_VERIFY_UNVERIFIED = 1;
	public static final int DEFAULT_EMAIL_VERIFY_VERIFIED = 2;

	public static final int DEFAULT_LANGUAGE_VIETNAMESE = 1;

	public static final int DEFAULT_TOKEN_VERSION = 0;

	public static final int DEFAULT_FAILED_ATTEMPTS = 0;

	private DatabaseConstants() {
	}
}