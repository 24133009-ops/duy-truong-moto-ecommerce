package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name; // Ví dụ: Duy Truong Moto Showroom Quận 1

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(length = 2000)
    private String bio;

    @Column(length = 500)
    private String avatar;

    @Column(length = 500)
    private String cover;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isOpen = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_id")
    private Commission commission;

    @Column(nullable = false)
    private Long point = 0L;

    @Column(nullable = false)
    private Double rating = 5.0;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal eWallet = BigDecimal.ZERO;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
