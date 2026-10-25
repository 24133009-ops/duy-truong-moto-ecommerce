package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private Long minPoint;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discount; // discount percentage e.g. 5.0%

    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
