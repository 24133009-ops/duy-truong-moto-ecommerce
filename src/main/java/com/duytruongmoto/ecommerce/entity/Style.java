package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "styles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Style {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Ví dụ: Màu sắc, Dung tích động cơ, Phiên bản pô độ

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "style", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StyleValue> values = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
