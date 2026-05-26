package constants;

public final class ViewAttributeConstants {

	private ViewAttributeConstants() {
	}

	public static final String ERRORS = "errors";
	public static final String VALUES = "values";
	public static final String ERROR_MESSAGE = SessionConstants.ERROR_MESSAGE;

	public static final class Dashboard {
		private Dashboard() {
		}

		public static final String TOTAL_USERS = "totalUsers";
		public static final String TOTAL_CATEGORIES = "totalCategories";
		public static final String TOTAL_PRODUCTS = "totalProducts";
		public static final String TOTAL_ORDERS = "totalOrders";
	}

	public static final class Security {
		private Security() {
		}

		public static final String SECURITY_CONTEXT = "securityContext";
		public static final String CAN_VIEW_USERS = "canViewUsers";
		public static final String CAN_VIEW_ROLES = "canViewRoles";
		public static final String CAN_VIEW_PERMISSIONS = "canViewPermissions";
		public static final String CAN_VIEW_CATEGORIES = "canViewCategories";
		public static final String CAN_VIEW_PRODUCTS = "canViewProducts";
		public static final String CAN_VIEW_REVIEWS = "canViewReviews";
		public static final String CAN_VIEW_ORDERS = "canViewOrders";
		public static final String CAN_VIEW_VOUCHERS = "canViewVouchers";
	}

	public static final class User {
		private User() {
		}

		public static final String USERS = "users";
		public static final String USER = "user";
		public static final String ALL_ROLES = "allRoles";
		public static final String LANGUAGES = "languages";
		public static final String HAS_CREATE = "hasUserCreate";
		public static final String HAS_EDIT = "hasUserEdit";
		public static final String HAS_DELETE = "hasUserDelete";
		public static final String IS_SYSTEM = "isSystem";
	}

	public static final class Role {
		private Role() {
		}

		public static final String ROLES = "roles";
		public static final String ROLE = "role";
		public static final String ALL_PERMISSIONS = "allPermissions";
		public static final String ALL_ROLES = "allRoles";
		public static final String ROLE_PERMISSIONS = "rolePermissions";
		public static final String PERMISSION_ROLE_MAP = "permissionRoleMap";
		public static final String HAS_CREATE = "hasRoleCreate";
		public static final String HAS_EDIT = "hasRoleEdit";
		public static final String HAS_DELETE = "hasRoleDelete";
	}

	public static final class Permission {
		private Permission() {
		}

		public static final String PERMISSION = "permission";
		public static final String MODULES = "modules";
		public static final String PERMISSIONS_BY_MODULE = "permissionsByModule";
		public static final String HAS_CREATE = "hasPermissionCreate";
		public static final String HAS_EDIT = "hasPermissionEdit";
		public static final String HAS_DELETE = "hasPermissionDelete";
	}
}
