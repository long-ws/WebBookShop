package beans;

public enum OrderStatus {
    PENDING(1, "Đã đặt hàng", "info", "bag-check"),
    CONFIRMED(2, "Đã xác nhận", "primary", "check2-all"),
    PICKED_UP(3, "Đã lấy hàng", "primary", "box-seam"),
    SHIPPING(4, "Đang vận chuyển", "primary", "truck"),
    DELIVERING(5, "Đang giao hàng", "primary", "geo-alt"),
    DELIVERED(6, "Đã giao thành công", "success", "check-circle"),
    CANCELLED(7, "Đã hủy", "danger", "x-circle");

    private final int code;
    private final String label;
    private final String badgeClass;
    private final String iconName;

    OrderStatus(int code, String label, String badgeClass, String iconName) {
        this.code = code;
        this.label = label;
        this.badgeClass = badgeClass;
        this.iconName = iconName;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public String getIconName() {
        return iconName;
    }

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }

    public static OrderStatus fromCodeOrDefault(int code, OrderStatus defaultStatus) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return defaultStatus;
    }
}
