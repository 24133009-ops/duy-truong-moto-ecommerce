package com.duytruongmoto.ecommerce.entity;

public enum OrderStatus {
    NOT_PROCESSED("Đơn hàng mới"),
    CONFIRMED("Đã xác nhận"),
    PICKED_UP("Đã lấy hàng"),
    SHIPPING("Đang giao"),
    DELIVERED("Đã giao"),
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
