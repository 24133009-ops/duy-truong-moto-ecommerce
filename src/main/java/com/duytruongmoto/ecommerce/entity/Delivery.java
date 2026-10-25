package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name; // Xe nâng hạ sàn chuyên dụng Duy Truong Express, Vận chuyển xe thùng kín VIP, Giao hàng tiêu chuẩn phụ kiện

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price; // Phí giao xe

    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
