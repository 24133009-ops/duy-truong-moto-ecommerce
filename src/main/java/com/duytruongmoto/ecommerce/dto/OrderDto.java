package com.duytruongmoto.ecommerce.dto;

import com.duytruongmoto.ecommerce.entity.Order;
import com.duytruongmoto.ecommerce.entity.OrderItem;
import lombok.*;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private String id; // orderCode e.g. DTM-9824A1B2
    private Long orderId; // Database numeric ID
    private String customerName;
    private String phone;
    private String address;
    private String productName;
    private String productImage;
    private String storeName;
    private String deliveryName;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private Boolean isPaid;
    private String status;
    private String date;
    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        private Long productId;
        private String name;
        private String image;
        private BigDecimal price;
        private Integer count;
    }

    public static OrderDto fromEntity(Order o) {
        if (o == null) return null;

        String custName = o.getRecipientName();
        if (custName == null && o.getUser() != null) {
            custName = o.getUser().getFullName();
        }

        String stName = o.getStore() != null ? o.getStore().getName() : "Duy Trương Motor";
        String delName = o.getDelivery() != null ? o.getDelivery().getName() : "Giao xe sàn trượt VIP";

        String firstProdName = "Siêu xe Mô Tô PKL";
        String firstProdImg = "/images/ktm_300_exc.jpg";

        List<OrderItemDto> itemDtos = new ArrayList<>();
        if (o.getItems() != null && !o.getItems().isEmpty()) {
            OrderItem first = o.getItems().get(0);
            if (first.getProduct() != null) {
                firstProdName = first.getProduct().getName();
                firstProdImg = first.getProduct().getMainImage();
            }
            for (OrderItem it : o.getItems()) {
                if (it.getProduct() != null) {
                    itemDtos.add(OrderItemDto.builder()
                            .productId(it.getProduct().getId())
                            .name(it.getProduct().getName())
                            .image(it.getProduct().getMainImage())
                            .price(it.getPrice())
                            .count(it.getCount())
                            .build());
                }
            }
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = o.getCreatedAt() != null ? o.getCreatedAt().format(fmt) : "";

        return OrderDto.builder()
                .id(o.getOrderCode() != null ? o.getOrderCode() : ("DTM-" + o.getId()))
                .orderId(o.getId())
                .customerName(custName != null ? custName : "Quý Khách Hàng")
                .phone(o.getPhone())
                .address(o.getAddress())
                .productName(firstProdName)
                .productImage(firstProdImg)
                .storeName(stName)
                .deliveryName(delName)
                .totalAmount(o.getAmountFromUser())
                .paymentMethod(o.getPaymentMethod() != null ? o.getPaymentMethod() : "COD")
                .isPaid(o.getIsPaidBefore() != null ? o.getIsPaidBefore() : false)
                .status(o.getStatus() != null ? o.getStatus().name() : "NOT_PROCESSED")
                .date(formattedDate)
                .items(itemDtos)
                .build();
    }
}
