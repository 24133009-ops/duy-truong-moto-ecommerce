package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // VOUCHER-50TR, BIKERVIP, FREESHIP-VIP

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer discountPercent; // % giảm giá (1-50%)

    @Column(precision = 18, scale = 2)
    private BigDecimal maxDiscountAmount; // Mức giảm tối đa (VNĐ)

    @Column(precision = 18, scale = 2)
    private BigDecimal minOrderAmount; // Giá trị đơn hàng tối thiểu

    @Builder.Default
    @Column(nullable = false)
    private Integer usageLimit = 100; // Số lượt dùng tối đa

    @Builder.Default
    @Column(nullable = false)
    private Integer usedCount = 0; // Đã dùng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store; // null = Toàn sàn, có giá trị = Của riêng showroom

    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime endDate = LocalDateTime.now().plusMonths(6);

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public boolean isValid() {
        if (Boolean.TRUE.equals(isDeleted)) return false;
        LocalDateTime now = LocalDateTime.now();
        if (startDate != null && now.isBefore(startDate)) return false;
        if (endDate != null && now.isAfter(endDate)) return false;
        if (usageLimit != null && usedCount != null && usedCount >= usageLimit) return false;
        return true;
    }
}

