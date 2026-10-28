package com.duytruongmoto.ecommerce.entity;

public enum OrderStatus {
    NOT_PROCESSED("Đơn hàng mới"),
    CONFIRMED("Đã duyệt HQCN"),
    PICKED_UP("Hoàn tất PDI"),
    SHIPPING("Đang vận chuyển sàn trượt"),
    HANDOVER("Đang bàn giao & test pô"),
    DELIVERED("Đã giao - Bảo hành 3 năm"),
    CANCELLED("Hủy"),
    REFUNDED("Trả hàng - Hoàn tiền");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
