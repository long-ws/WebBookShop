package constants;

import java.util.List;

public final class PermissionConstants {
    private PermissionConstants() {
    }

    // User Management Permissions
    public static final String USER_MANAGE = "user.manage";
    public static final String USER_CREATE = "user.create";
    public static final String USER_EDIT = "user.edit";
    public static final String USER_VIEW = "user.view";
    public static final String USER_DELETE = "user.delete";
    public static final String USER_ASSIGN_ROLE = "user.assign_role";

    // Role Management Permissions
    public static final String ROLE_MANAGE = "role.manage";
    public static final String ROLE_CREATE = "role.create";
    public static final String ROLE_EDIT = "role.edit";
    public static final String ROLE_VIEW = "role.view";
    public static final String ROLE_DELETE = "role.delete";
    public static final String ROLE_ASSIGN_PERMISSION = "role.assign_permission";

    // Permission Management Permissions
    public static final String PERMISSION_MANAGE = "permission.manage";
    public static final String PERMISSION_CREATE = "permission.create";
    public static final String PERMISSION_EDIT = "permission.edit";
    public static final String PERMISSION_VIEW = "permission.view";
    public static final String PERMISSION_DELETE = "permission.delete";

    // Category Management Permissions
    public static final String CATEGORY_MANAGE = "category.manage";
    public static final String CATEGORY_CREATE = "category.create";
    public static final String CATEGORY_EDIT = "category.edit";
    public static final String CATEGORY_VIEW = "category.view";
    public static final String CATEGORY_DELETE = "category.delete";

    // Product Management Permissions
    public static final String PRODUCT_MANAGE = "product.manage";
    public static final String PRODUCT_CREATE = "product.create";
    public static final String PRODUCT_EDIT = "product.edit";
    public static final String PRODUCT_VIEW = "product.view";
    public static final String PRODUCT_DELETE = "product.delete";

    // Order Management Permissions
    public static final String ORDER_MANAGE = "order.manage";
    public static final String ORDER_CREATE = "order.create";
    public static final String ORDER_EDIT = "order.edit";
    public static final String ORDER_VIEW = "order.view";
    public static final String ORDER_DELETE = "order.delete";
    public static final String ORDER_VIEW_ALL = "order.view_all";

    // Cart Management Permissions
    public static final String CART_MANAGE = "cart.manage";
    public static final String CART_VIEW = "cart.view";

    // Review Management Permissions
    public static final String REVIEW_MANAGE = "review.manage";
    public static final String REVIEW_CREATE = "review.create";
    public static final String REVIEW_EDIT = "review.edit";
    public static final String REVIEW_VIEW = "review.view";
    public static final String REVIEW_DELETE = "review.delete";
    public static final String REVIEW_MODERATE = "review.moderate";

    // Voucher Management Permissions
    public static final String VOUCHER_MANAGE = "voucher.manage";
    public static final String VOUCHER_CREATE = "voucher.create";
    public static final String VOUCHER_EDIT = "voucher.edit";
    public static final String VOUCHER_VIEW = "voucher.view";
    public static final String VOUCHER_DELETE = "voucher.delete";

    // Report Management Permissions
    public static final String REPORT_VIEW = "report.view";
    public static final String REPORT_EXPORT = "report.export";

    // Settings Management Permissions
    public static final String SETTINGS_VIEW = "settings.view";
    public static final String SETTINGS_EDIT = "settings.edit";

    /** Bất kỳ quyền nào trong danh sách này cho phép truy cập khu vực Admin */
    public static final List<String> ADMIN_PORTAL_ACCESS_PERMISSIONS = List.of(
            USER_VIEW, USER_CREATE, USER_EDIT, USER_DELETE,
            ROLE_VIEW, ROLE_CREATE, ROLE_EDIT, ROLE_DELETE,
            PERMISSION_VIEW, PERMISSION_MANAGE,
            CATEGORY_VIEW, CATEGORY_CREATE, CATEGORY_EDIT, CATEGORY_DELETE,
            PRODUCT_VIEW, PRODUCT_CREATE, PRODUCT_EDIT, PRODUCT_DELETE,
            ORDER_VIEW, ORDER_EDIT, ORDER_DELETE,
            REVIEW_VIEW, REVIEW_EDIT, REVIEW_DELETE,
            VOUCHER_VIEW, VOUCHER_CREATE, VOUCHER_EDIT, VOUCHER_DELETE);
}
