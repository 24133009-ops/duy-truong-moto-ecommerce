package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(length = 100)
    private String selectedStyle; // e.g. "Màu Đỏ Rosso Corsa"

    @Column(nullable = false)
    private Integer count = 1;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(count));
    }
}
