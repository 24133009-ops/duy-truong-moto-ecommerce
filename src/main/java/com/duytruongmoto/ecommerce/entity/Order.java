package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id")
    private User shipper;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String recipientName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.NOT_PROCESSED;

    @Column(nullable = false)
    private Boolean isPaidBefore = false;

    @Column(nullable = false, length = 30)
    private String paymentMethod; // COD, VNPAY, MOMO

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amountFromUser; // Tong tien khach tra

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amountToStore; // Tien showroom nhan (sau khi tru chiet khau san)

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amountToGD; // Tien hoa hong san Duy Truong thu

    @Column(length = 1000)
    private String note;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public BigDecimal getTotalAmount() {
        return amountFromUser;
    }

    public static String cleanCustomerNote(String rawNote) {
        if (rawNote == null || rawNote.trim().isEmpty()) {
            return null;
        }
        // Tách lấy đúng lời dặn thực sự của khách trước các đoạn log timestamp [2026-10-29...
        String clean = rawNote.split("\\[\\d{4}-\\d{2}-\\d{2}")[0].trim();
        clean = clean.split("\\[Trạng thái")[0].trim();
        clean = clean.split("(?i)trạng thái:")[0].trim();
        clean = clean.split("(?i)chuyển trạng thái:")[0].trim();
        if (clean.isEmpty() || clean.equals("-") || clean.startsWith("[")) {
            return null;
        }
        return clean;
    }

    public String getCustomerNote() {
        return cleanCustomerNote(this.note);
    }

    public String getNote() {
        return cleanCustomerNote(this.note);
    }

    public String getRawNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = cleanCustomerNote(note);
    }

    /**
     * Kiểm tra đơn hàng có chứa xe mô tô nguyên chiếc hay là đơn phụ tùng / đồ bảo hộ
     */
    public boolean isVehicleOrder() {
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                if (item.getProduct() != null && item.getProduct().getCategory() != null) {
                    String slug = item.getProduct().getCategory().getSlug();
                    String catName = item.getProduct().getCategory().getName();
                    if (slug != null) {
                        String sLower = slug.toLowerCase();
                        if (sLower.contains("gear-parts") || sLower.contains("do-bao-ho") || sLower.contains("phu-tung") || sLower.contains("accessory")) {
                            continue;
                        }
                        if (sLower.contains("superbike") || sLower.contains("naked") || sLower.contains("cruiser")
                                || sLower.contains("adventure") || sLower.contains("scooter") || sLower.contains("xe") || sLower.contains("moto")) {
                            return true;
                        }
                    }
                    if (catName != null) {
                        String cLower = catName.toLowerCase();
                        if (cLower.contains("bảo hộ") || cLower.contains("phụ tùng") || cLower.contains("pô độ") || cLower.contains("nón") || cLower.contains("phụ kiện")) {
                            continue;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        if (delivery != null && delivery.getName() != null) {
            String dName = delivery.getName().toLowerCase();
            if (dName.contains("tiêu chuẩn") || dName.contains("phụ tùng") || dName.contains("bảo hộ")) {
                return false;
            }
        }
        return true;
    }

    public String getProcessTitle() {
        return isVehicleOrder()
                ? "TIẾN TRÌNH BÀN GIAO 6 CHẶNG CHUYÊN NGHIỆP"
                : "TIẾN TRÌNH XẾP HÀNG & GIAO PHỤ KIỆN 6 BƯỚC";
    }

    public String getStepName(int step) {
        boolean isVehicle = isVehicleOrder();
        switch (step) {
            case 1: return isVehicle ? "Đặt Cọc Online" : "Đặt Mua Online";
            case 2: return isVehicle ? "Duyệt HQCN" : "Soạn Hàng";
            case 3: return isVehicle ? "Xuất Kho PDI" : "Đóng Gói Chống Sốc";
            case 4: return isVehicle ? "Xe Sàn Trượt" : "Bốc Hàng Lên Xe";
            case 5: return isVehicle ? "Bàn Giao & Test Pô" : "Giao Hàng & Đồng Kiểm";
            case 6: return isVehicle ? "Bảo Hành 3 Năm" : "Hoàn Tất Đơn Hàng";
            default: return "Chặng " + step;
        }
    }

    public String getStepDesc(int step) {
        boolean isVehicle = isVehicleOrder();
        switch (step) {
            case 1: return isVehicle ? "Khách cọc VNPay" : "Đã xác nhận đơn hàng";
            case 2: return isVehicle ? "Showroom kiểm tra" : "Xếp hàng & kiểm đúng mẫu";
            case 3: return isVehicle ? "Siết ốc & Rửa xe" : "Bọc xốp khí & niêm phong";
            case 4: return isVehicle ? "Vận chuyển VIP" : "Bốc xếp & bàn giao shipper";
            case 5: return isVehicle ? "Hạ sàn & nổ máy" : "Khách kiểm tra hàng & nhận";
            case 6: return isVehicle ? "Kích hoạt chính hãng" : "Bảo hành phụ kiện chính hãng";
            default: return "";
        }
    }

    public String getStepIcon(int step) {
        boolean isVehicle = isVehicleOrder();
        switch (step) {
            case 1: return isVehicle ? "fa-solid fa-file-invoice-dollar" : "fa-solid fa-bag-shopping";
            case 2: return isVehicle ? "fa-solid fa-stamp" : "fa-solid fa-dolly";
            case 3: return isVehicle ? "fa-solid fa-warehouse" : "fa-solid fa-box-open";
            case 4: return isVehicle ? "fa-solid fa-truck-ramp-box" : "fa-solid fa-truck-fast";
            case 5: return isVehicle ? "fa-solid fa-volume-high" : "fa-solid fa-handshake";
            case 6: return isVehicle ? "fa-solid fa-shield-halved" : "fa-solid fa-circle-check";
            default: return "fa-solid fa-circle";
        }
    }
}
