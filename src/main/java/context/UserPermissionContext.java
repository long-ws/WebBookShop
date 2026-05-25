package context;

import constants.PermissionConstants;

import java.util.Set;

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
    public boolean isCanManageUser() {
        return hasPermission(PermissionConstants.USER_MANAGE);
    }

    public boolean isCanCreateUser() {
        return isCanManageUser() || hasPermission(PermissionConstants.USER_CREATE);
    }

    public boolean isCanEditUser() {
        return isCanManageUser() || hasPermission(PermissionConstants.USER_EDIT);
    }

    public boolean isCanViewUser() {
        return isCanManageUser() || hasPermission(PermissionConstants.USER_VIEW);
    }

    public boolean isCanDeleteUser() {
        return isCanManageUser() || hasPermission(PermissionConstants.USER_DELETE);
    }

    // Role Management
    public boolean isCanManageRole() {
        return hasPermission(PermissionConstants.ROLE_MANAGE);
    }

    public boolean isCanCreateRole() {
        return isCanManageRole() || hasPermission(PermissionConstants.ROLE_CREATE);
    }

    public boolean isCanEditRole() {
        return isCanManageRole() || hasPermission(PermissionConstants.ROLE_EDIT);
    }

    public boolean isCanViewRole() {
        return isCanManageRole() || hasPermission(PermissionConstants.ROLE_VIEW);
    }

    public boolean isCanDeleteRole() {
        return isCanManageRole() || hasPermission(PermissionConstants.ROLE_DELETE);
    }

    public boolean isCanAssignPermissionToRole() {
        return isCanManageRole() || hasPermission(PermissionConstants.ROLE_ASSIGN_PERMISSION);
    }

    // Permission Management
    public boolean isCanManagePermission() {
        return hasPermission(PermissionConstants.PERMISSION_MANAGE);
    }

    public boolean isCanCreatePermission() {
        return isCanManagePermission() || hasPermission(PermissionConstants.PERMISSION_CREATE);
    }

    public boolean isCanEditPermission() {
        return isCanManagePermission() || hasPermission(PermissionConstants.PERMISSION_EDIT);
    }

    public boolean isCanViewPermission() {
        return isCanManagePermission() || hasPermission(PermissionConstants.PERMISSION_VIEW);
    }

    public boolean isCanDeletePermission() {
        return isCanManagePermission() || hasPermission(PermissionConstants.PERMISSION_DELETE);
    }

    // Category Management
    public boolean isCanManageCategory() {
        return hasPermission(PermissionConstants.CATEGORY_MANAGE);
    }

    public boolean isCanCreateCategory() {
        return isCanManageCategory() || hasPermission(PermissionConstants.CATEGORY_CREATE);
    }

    public boolean isCanEditCategory() {
        return isCanManageCategory() || hasPermission(PermissionConstants.CATEGORY_EDIT);
    }

    public boolean isCanViewCategory() {
        return isCanManageCategory() || hasPermission(PermissionConstants.CATEGORY_VIEW);
    }

    public boolean isCanDeleteCategory() {
        return isCanManageCategory() || hasPermission(PermissionConstants.CATEGORY_DELETE);
    }

    // Product Management
    public boolean isCanManageProduct() {
        return hasPermission(PermissionConstants.PRODUCT_MANAGE);
    }

    public boolean isCanCreateProduct() {
        return isCanManageProduct() || hasPermission(PermissionConstants.PRODUCT_CREATE);
    }

    public boolean isCanEditProduct() {
        return isCanManageProduct() || hasPermission(PermissionConstants.PRODUCT_EDIT);
    }

    public boolean isCanViewProduct() {
        return isCanManageProduct() || hasPermission(PermissionConstants.PRODUCT_VIEW);
    }

    public boolean isCanDeleteProduct() {
        return isCanManageProduct() || hasPermission(PermissionConstants.PRODUCT_DELETE);
    }

    // Order Management
    public boolean isCanManageOrder() {
        return hasPermission(PermissionConstants.ORDER_MANAGE);
    }

    public boolean isCanCreateOrder() {
        return isCanManageOrder() || hasPermission(PermissionConstants.ORDER_CREATE);
    }

    public boolean isCanEditOrder() {
        return isCanManageOrder() || hasPermission(PermissionConstants.ORDER_EDIT);
    }

    public boolean isCanViewOrder() {
        return isCanManageOrder() || hasPermission(PermissionConstants.ORDER_VIEW);
    }

    public boolean isCanDeleteOrder() {
        return isCanManageOrder() || hasPermission(PermissionConstants.ORDER_DELETE);
    }

    public boolean isCanViewAllOrders() {
        return isCanManageOrder() || hasPermission(PermissionConstants.ORDER_VIEW_ALL);
    }

    // Review Management
    public boolean isCanManageReview() {
        return hasPermission(PermissionConstants.REVIEW_MANAGE);
    }

    public boolean isCanCreateReview() {
        return isCanManageReview() || hasPermission(PermissionConstants.REVIEW_CREATE);
    }

    public boolean isCanEditReview() {
        return isCanManageReview() || hasPermission(PermissionConstants.REVIEW_EDIT);
    }

    public boolean isCanViewReview() {
        return isCanManageReview() || hasPermission(PermissionConstants.REVIEW_VIEW);
    }

    public boolean isCanDeleteReview() {
        return isCanManageReview() || hasPermission(PermissionConstants.REVIEW_DELETE);
    }

    public boolean isCanModerateReview() {
        return isCanManageReview() || hasPermission(PermissionConstants.REVIEW_MODERATE);
    }

    // Voucher Management
    public boolean isCanManageVoucher() {
        return hasPermission(PermissionConstants.VOUCHER_MANAGE);
    }

    public boolean isCanCreateVoucher() {
        return isCanManageVoucher() || hasPermission(PermissionConstants.VOUCHER_CREATE);
    }

    public boolean isCanEditVoucher() {
        return isCanManageVoucher() || hasPermission(PermissionConstants.VOUCHER_EDIT);
    }

    public boolean isCanViewVoucher() {
        return isCanManageVoucher() || hasPermission(PermissionConstants.VOUCHER_VIEW);
    }

    public boolean isCanDeleteVoucher() {
        return isCanManageVoucher() || hasPermission(PermissionConstants.VOUCHER_DELETE);
    }

    // Report Management
    public boolean isCanViewReport() {
        return hasPermission(PermissionConstants.REPORT_VIEW);
    }

    public boolean isCanExportReport() {
        return hasPermission(PermissionConstants.REPORT_EXPORT);
    }

    // Settings Management
    public boolean isCanViewSettings() {
        return hasPermission(PermissionConstants.SETTINGS_VIEW);
    }

    public boolean isCanEditSettings() {
        return hasPermission(PermissionConstants.SETTINGS_EDIT);
    }

    // Shipment Management
    public boolean isCanManageShipment() {
        return hasPermission(PermissionConstants.SHIPMENT_MANAGE);
    }

    public boolean isCanViewShipment() {
        return isCanManageShipment() || hasPermission(PermissionConstants.SHIPMENT_VIEW);
    }

    public boolean isCanEditShipment() {
        return isCanManageShipment() || hasPermission(PermissionConstants.SHIPMENT_EDIT);
    }

    public boolean isCanCreateShipment() {
        return isCanManageShipment() || hasPermission(PermissionConstants.SHIPMENT_CREATE);
    }

    public boolean isCanDeleteShipment() {
        return isCanManageShipment() || hasPermission(PermissionConstants.SHIPMENT_DELETE);
    }

    // Shipping Configuration Management
    public boolean isCanManageShippingConfig() {
        return hasPermission(PermissionConstants.SHIPPING_CONFIG_MANAGE);
    }

    public boolean isCanViewShippingConfig() {
        return isCanManageShippingConfig() || hasPermission(PermissionConstants.SHIPPING_CONFIG_VIEW);
    }

    public boolean isCanEditShippingConfig() {
        return isCanManageShippingConfig() || hasPermission(PermissionConstants.SHIPPING_CONFIG_EDIT);
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
