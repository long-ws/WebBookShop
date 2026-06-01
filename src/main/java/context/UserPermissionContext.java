package context;

import java.util.Set;

import constants.PermissionConstants;

public class UserPermissionContext {
    private final Set<String> permissions;
    private final boolean isSuperAdmin;

    public UserPermissionContext(Set<String> permissions, boolean isSuperAdmin) {
        this.permissions = permissions;
        this.isSuperAdmin = isSuperAdmin;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    private boolean hasPermission(String permission) {
        return isSuperAdmin || permissions.contains(permission);
    }

    // User Management
    public boolean isCanCreateUser() {
        return hasPermission(PermissionConstants.USER_VIEW) && hasPermission(PermissionConstants.USER_CREATE);
    }

    public boolean isCanEditUser() {
        return hasPermission(PermissionConstants.USER_VIEW) && hasPermission(PermissionConstants.USER_UPDATE);
    }

    public boolean isCanViewUser() {
        return hasPermission(PermissionConstants.USER_VIEW);
    }

    public boolean isCanDetailUser() {
        return hasPermission(PermissionConstants.USER_VIEW) && hasPermission(PermissionConstants.USER_DETAIL);
    }

    public boolean isCanDeleteUser() {
        return hasPermission(PermissionConstants.USER_VIEW) && hasPermission(PermissionConstants.USER_DELETE);
    }

    // Role Management
    public boolean isCanCreateRole() {
        return hasPermission(PermissionConstants.ROLE_VIEW) && hasPermission(PermissionConstants.ROLE_CREATE);
    }

    public boolean isCanEditRole() {
        return hasPermission(PermissionConstants.ROLE_VIEW) && hasPermission(PermissionConstants.ROLE_UPDATE);
    }

    public boolean isCanViewRole() {
        return hasPermission(PermissionConstants.ROLE_VIEW);
    }

    public boolean isCanDeleteRole() {
        return hasPermission(PermissionConstants.ROLE_VIEW) && hasPermission(PermissionConstants.ROLE_DELETE);
    }

    public boolean isCanAssignPermissionToRole() {
        return hasPermission(PermissionConstants.ROLE_VIEW) && hasPermission(PermissionConstants.ROLE_ASSIGN_PERMISSION);
    }

    // Permission Management
    public boolean isCanCreatePermission() {
        return hasPermission(PermissionConstants.PERMISSION_VIEW) && hasPermission(PermissionConstants.PERMISSION_CREATE);
    }

    public boolean isCanEditPermission() {
        return hasPermission(PermissionConstants.PERMISSION_VIEW) && hasPermission(PermissionConstants.PERMISSION_UPDATE);
    }

    public boolean isCanViewPermission() {
        return hasPermission(PermissionConstants.PERMISSION_VIEW);
    }

    public boolean isCanDeletePermission() {
        return hasPermission(PermissionConstants.PERMISSION_VIEW) && hasPermission(PermissionConstants.PERMISSION_DELETE);
    }

    // Category Management
    public boolean isCanCreateCategory() {
        return hasPermission(PermissionConstants.CATEGORY_VIEW) && hasPermission(PermissionConstants.CATEGORY_CREATE);
    }

    public boolean isCanEditCategory() {
        return hasPermission(PermissionConstants.CATEGORY_VIEW) && hasPermission(PermissionConstants.CATEGORY_UPDATE);
    }

    public boolean isCanViewCategory() {
        return hasPermission(PermissionConstants.CATEGORY_VIEW);
    }

    public boolean isCanDeleteCategory() {
        return hasPermission(PermissionConstants.CATEGORY_VIEW) && hasPermission(PermissionConstants.CATEGORY_DELETE);
    }

    // Product Management
    public boolean isCanCreateProduct() {
        return hasPermission(PermissionConstants.PRODUCT_VIEW) && hasPermission(PermissionConstants.PRODUCT_CREATE);
    }

    public boolean isCanEditProduct() {
        return hasPermission(PermissionConstants.PRODUCT_VIEW) && hasPermission(PermissionConstants.PRODUCT_UPDATE);
    }

    public boolean isCanViewProduct() {
        return hasPermission(PermissionConstants.PRODUCT_VIEW);
    }

    public boolean isCanDeleteProduct() {
        return hasPermission(PermissionConstants.PRODUCT_VIEW) && hasPermission(PermissionConstants.PRODUCT_DELETE);
    }

    // Order Management
    public boolean isCanCreateOrder() {
        return hasPermission(PermissionConstants.ORDER_VIEW) && hasPermission(PermissionConstants.ORDER_CREATE);
    }

    public boolean isCanEditOrder() {
        return hasPermission(PermissionConstants.ORDER_VIEW) && hasPermission(PermissionConstants.ORDER_UPDATE);
    }

    public boolean isCanViewOrder() {
        return hasPermission(PermissionConstants.ORDER_VIEW);
    }

    public boolean isCanDeleteOrder() {
        return hasPermission(PermissionConstants.ORDER_VIEW) && hasPermission(PermissionConstants.ORDER_DELETE);
    }

    public boolean isCanViewAllOrders() {
        return hasPermission(PermissionConstants.ORDER_VIEW) && hasPermission(PermissionConstants.ORDER_VIEW_ALL);
    }

    // Review Management
    public boolean isCanCreateReview() {
        return hasPermission(PermissionConstants.REVIEW_VIEW) && hasPermission(PermissionConstants.REVIEW_CREATE);
    }

    public boolean isCanEditReview() {
        return hasPermission(PermissionConstants.REVIEW_VIEW) && hasPermission(PermissionConstants.REVIEW_UPDATE);
    }

    public boolean isCanViewReview() {
        return hasPermission(PermissionConstants.REVIEW_VIEW);
    }

    public boolean isCanDeleteReview() {
        return hasPermission(PermissionConstants.REVIEW_VIEW) && hasPermission(PermissionConstants.REVIEW_DELETE);
    }

    public boolean isCanModerateReview() {
        return hasPermission(PermissionConstants.REVIEW_VIEW) && hasPermission(PermissionConstants.REVIEW_MODERATE);
    }

    // Voucher Management
    public boolean isCanCreateVoucher() {
        return hasPermission(PermissionConstants.VOUCHER_VIEW) && hasPermission(PermissionConstants.VOUCHER_CREATE);
    }

    public boolean isCanEditVoucher() {
        return hasPermission(PermissionConstants.VOUCHER_VIEW) && hasPermission(PermissionConstants.VOUCHER_UPDATE);
    }

    public boolean isCanViewVoucher() {
        return hasPermission(PermissionConstants.VOUCHER_VIEW);
    }

    public boolean isCanDeleteVoucher() {
        return hasPermission(PermissionConstants.VOUCHER_VIEW) && hasPermission(PermissionConstants.VOUCHER_DELETE);
    }

    // Report Management
    public boolean isCanViewReport() {
        return hasPermission(PermissionConstants.REPORT_VIEW);
    }

    public boolean isCanExportReport() {
        return hasPermission(PermissionConstants.REPORT_VIEW) && hasPermission(PermissionConstants.REPORT_EXPORT);
    }

    // Settings Management
    public boolean isCanViewSettings() {
        return hasPermission(PermissionConstants.SETTINGS_VIEW);
    }

    public boolean isCanEditSettings() {
        return hasPermission(PermissionConstants.SETTINGS_VIEW) && hasPermission(PermissionConstants.SETTINGS_UPDATE);
    }

    // Shipment Management
    public boolean isCanViewShipment() {
        return hasPermission(PermissionConstants.SHIPMENT_VIEW);
    }

    public boolean isCanEditShipment() {
        return hasPermission(PermissionConstants.SHIPMENT_VIEW) && hasPermission(PermissionConstants.SHIPMENT_UPDATE);
    }

    public boolean isCanCreateShipment() {
        return hasPermission(PermissionConstants.SHIPMENT_VIEW) && hasPermission(PermissionConstants.SHIPMENT_CREATE);
    }

    public boolean isCanDeleteShipment() {
        return hasPermission(PermissionConstants.SHIPMENT_VIEW) && hasPermission(PermissionConstants.SHIPMENT_DELETE);
    }

    // Shipping Configuration Management
    public boolean isCanViewShippingConfig() {
        return hasPermission(PermissionConstants.SHIPPING_CONFIG_VIEW);
    }

    public boolean isCanEditShippingConfig() {
        return hasPermission(PermissionConstants.SHIPPING_CONFIG_VIEW) && hasPermission(PermissionConstants.SHIPPING_CONFIG_UPDATE);
    }

    public boolean isCanCreateShippingConfig() {
        return hasPermission(PermissionConstants.SHIPPING_CONFIG_VIEW) && hasPermission(PermissionConstants.SHIPPING_CONFIG_CREATE);
    }

    public boolean isCanDeleteShippingConfig() {
        return hasPermission(PermissionConstants.SHIPPING_CONFIG_VIEW) && hasPermission(PermissionConstants.SHIPPING_CONFIG_DELETE);
    }

    // Check if user has any admin permissions
    public boolean hasAnyAdminPermission() {
        return isSuperAdmin ||
            isCanViewUser() || isCanCreateUser() || isCanEditUser() || isCanDeleteUser() ||
            isCanViewRole() || isCanCreateRole() || isCanEditRole() || isCanDeleteRole() ||
            isCanViewPermission() || isCanCreatePermission() || isCanEditPermission() || isCanDeletePermission() ||
            isCanViewCategory() || isCanCreateCategory() || isCanEditCategory() || isCanDeleteCategory() ||
            isCanViewProduct() || isCanCreateProduct() || isCanEditProduct() || isCanDeleteProduct() ||
            isCanViewReview() || isCanEditReview() || isCanDeleteReview() ||
            isCanViewOrder() || isCanEditOrder() || isCanDeleteOrder() ||
            isCanViewVoucher() || isCanCreateVoucher() || isCanEditVoucher() || isCanDeleteVoucher() ||
            isCanViewShipment() || isCanEditShipment() || isCanCreateShipment() || isCanDeleteShipment() ||
            isCanViewShippingConfig() || isCanEditShippingConfig();
    }
}
