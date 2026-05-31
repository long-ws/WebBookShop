package constants;

import java.util.List;

public final class PermissionConstants {
    private PermissionConstants() {
    }

    // User Management Permissions
    public static final String USER_VIEW = "user.view";
    public static final String USER_DETAIL = "user.detail";
    public static final String USER_CREATE = "user.create";
    public static final String USER_UPDATE = "user.update";
    public static final String USER_DELETE = "user.delete";
    public static final String USER_ASSIGN_ROLE = "user.assign_role";

    // Role Management Permissions
    public static final String ROLE_VIEW = "role.view";
    public static final String ROLE_CREATE = "role.create";
    public static final String ROLE_UPDATE = "role.update";
    public static final String ROLE_DELETE = "role.delete";
    public static final String ROLE_ASSIGN_PERMISSION = "role.assign_permission";

    // Permission Management Permissions
    public static final String PERMISSION_VIEW = "permission.view";
    public static final String PERMISSION_CREATE = "permission.create";
    public static final String PERMISSION_UPDATE = "permission.update";
    public static final String PERMISSION_DELETE = "permission.delete";

    // Category Management Permissions
    public static final String CATEGORY_VIEW = "category.view";
    public static final String CATEGORY_CREATE = "category.create";
    public static final String CATEGORY_UPDATE = "category.update";
    public static final String CATEGORY_DELETE = "category.delete";

    // Product Management Permissions
    public static final String PRODUCT_VIEW = "product.view";
    public static final String PRODUCT_CREATE = "product.create";
    public static final String PRODUCT_UPDATE = "product.update";
    public static final String PRODUCT_DELETE = "product.delete";

    // Order Management Permissions
    public static final String ORDER_VIEW = "order.view";
    public static final String ORDER_CREATE = "order.create";
    public static final String ORDER_UPDATE = "order.update";
    public static final String ORDER_DELETE = "order.delete";
    public static final String ORDER_VIEW_ALL = "order.view_all";

    // Cart Management Permissions
    public static final String CART_MANAGE = "cart.manage";
    public static final String CART_VIEW = "cart.view";

    // Review Management Permissions
    public static final String REVIEW_VIEW = "review.view";
    public static final String REVIEW_CREATE = "review.create";
    public static final String REVIEW_UPDATE = "review.update";
    public static final String REVIEW_DELETE = "review.delete";
    public static final String REVIEW_MODERATE = "review.moderate";

    // Voucher Management Permissions
    public static final String VOUCHER_VIEW = "voucher.view";
    public static final String VOUCHER_CREATE = "voucher.create";
    public static final String VOUCHER_UPDATE = "voucher.update";
    public static final String VOUCHER_DELETE = "voucher.delete";

    // Report Management Permissions
    public static final String REPORT_VIEW = "report.view";
    public static final String REPORT_EXPORT = "report.export";

    // Settings Management Permissions
    public static final String SETTINGS_VIEW = "settings.view";
    public static final String SETTINGS_UPDATE = "settings.update";

    // Shipment Management Permissions
    public static final String SHIPMENT_VIEW = "shipment.view";
    public static final String SHIPMENT_CREATE = "shipment.create";
    public static final String SHIPMENT_UPDATE = "shipment.update";
    public static final String SHIPMENT_DELETE = "shipment.delete";

    // Shipping Configuration Permissions
    public static final String SHIPPING_CONFIG_VIEW = "shipping_config.view";
    public static final String SHIPPING_CONFIG_CREATE = "shipping_config.create";
    public static final String SHIPPING_CONFIG_UPDATE = "shipping_config.update";
    public static final String SHIPPING_CONFIG_DELETE = "shipping_config.delete";

    /** Bất kỳ quyền nào trong danh sách này cho phép truy cập khu vực Admin */
    public static final List<String> ADMIN_PORTAL_ACCESS_PERMISSIONS = List.of(
            USER_VIEW,
            ROLE_VIEW,
            ROLE_CREATE,
            PERMISSION_VIEW,
            CATEGORY_VIEW,
            PRODUCT_VIEW,
            ORDER_VIEW,
            REVIEW_VIEW,
            VOUCHER_VIEW,
            SHIPMENT_VIEW,
            SHIPPING_CONFIG_VIEW,
            SETTINGS_VIEW,
            REPORT_VIEW);
}
