package constants;

public final class RequestParamConstants {

	private RequestParamConstants() {
	}

	public static final String CHECKBOX_ON = "on";

	public static final String ID = "id";
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String MODULE = "module";
	public static final String IS_SYSTEM = "isSystem";
	public static final String IS_ACTIVE = "isActive";

	public static final class Auth {
		private Auth() {
		}

		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
	}

	public static final class User {
		private User() {
		}

		public static final String USERNAME = Auth.USERNAME;
		public static final String PASSWORD = Auth.PASSWORD;
		public static final String FULLNAME = "fullname";
		public static final String EMAIL = "email";
		public static final String PHONE_NUMBER = "phoneNumber";
		public static final String GENDER = "gender";
		public static final String ROLE = "role";
		public static final String PREFERRED_LANGUAGE_ID = "preferredLanguageId";
		public static final String USER_IDS = "userIds";
	}

	public static final class Role {
		private Role() {
		}

		public static final String ROLE_ID = "roleId";
		public static final String PERMISSION_IDS = "permissionIds";
		public static final String ASSIGNED_ROLE_IDS = "assignedRoleIds";
		public static final String ROLE_IDS = "roleIds";
	}

	public static final class Permission {
		private Permission() {
		}

		public static final String PERMISSION_IDS = "permissionIds";
	}
}
