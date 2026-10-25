package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "style_values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StyleValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Đỏ Ducati Corse, Đen Nhám Carbon, Xanh Ninja, Akrapovic Full System Titanium

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_id", nullable = false)
    private Style style;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
