package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 200)
    private String name; // e.g., Ducati Panigale V4 S 2024

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price; // Gia niem yet

    @Column(precision = 18, scale = 2)
    private BigDecimal promotionalPrice; // Gia khuyen mai

    @Builder.Default
    @Column(nullable = false)
    private Integer quantity = 1; // So luong ton kho showroom

    @Builder.Default
    @Column(nullable = false)
    private Integer sold = 0; // So luong da ban

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true; // Duoc admin cap phep

    @Builder.Default
    @Column(nullable = false)
    private Boolean isSelling = true; // Showroom dang mo ban hay luu tru

    @Column(length = 500)
    private String mainImage;

    @Column(columnDefinition = "TEXT")
    private String listImages; // Danh sach URL anh JSON

    @Column(length = 500)
    private String videoUrl; // Video review tieng po xe / goc nhin 360

    @Builder.Default
    @Column(nullable = false)
    private Double rating = 5.0;

    @Column(length = 255)
    private String engineCapacity; // e.g., 1103cc, 998cc, 1868cc

    @Column(length = 255)
    private String color;

    @Column(length = 100)
    private String brand; // Ducati, Kawasaki, BMW, Harley-Davidson, Honda

    @Builder.Default
    @ManyToMany
    @JoinTable(
        name = "product_style_values",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "style_value_id")
    )
    private List<StyleValue> styleValues = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public BigDecimal getEffectivePrice() {
        if (promotionalPrice != null && promotionalPrice.compareTo(BigDecimal.ZERO) > 0 && promotionalPrice.compareTo(price) < 0) {
            return promotionalPrice;
        }
        return price;
    }
}
