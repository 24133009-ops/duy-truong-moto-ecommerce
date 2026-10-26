package com.duytruongmoto.ecommerce.controller.api;

import com.duytruongmoto.ecommerce.dto.OrderDto;
import com.duytruongmoto.ecommerce.entity.*;
import com.duytruongmoto.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final DeliveryRepository deliveryRepository;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Long shipperId,
            @RequestParam(required = false) String status) {

        List<Order> orders;
        if (userId != null) {
            orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else if (storeId != null) {
            orders = orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
        } else if (shipperId != null) {
            orders = orderRepository.findByShipperIdOrderByCreatedAtDesc(shipperId);
        } else {
            orders = orderRepository.findAll().stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            try {
                OrderStatus os = OrderStatus.valueOf(status.toUpperCase());
                orders = orders.stream().filter(o -> o.getStatus() == os).collect(Collectors.toList());
            } catch (Exception ignored) {}
        }

        List<OrderDto> dtos = orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{orderCode}")
    public ResponseEntity<OrderDto> getOrderByCode(@PathVariable String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .map(o -> ResponseEntity.ok(OrderDto.fromEntity(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> req) {
        try {
            String recipientName = (String) req.getOrDefault("recipientName", "Khách Hàng Biker");
            String phone = (String) req.getOrDefault("phone", "0901234567");
            String address = (String) req.getOrDefault("address", "Showroom Duy Trương Motor");
            String paymentMethod = (String) req.getOrDefault("paymentMethod", "COD");
            Boolean isPaid = (Boolean) req.getOrDefault("isPaid", false);
            BigDecimal totalAmount = new BigDecimal(req.getOrDefault("totalAmount", "0").toString());

            // Default fallback entities
            User customer = userRepository.findByEmail("customer@duytruongmoto.com")
                    .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));
            Store store = storeRepository.findById(1L)
                    .orElseGet(() -> storeRepository.findAll().stream().findFirst().orElse(null));
            Delivery delivery = deliveryRepository.findAll().stream().findFirst().orElse(null);

            String orderCode = "DTM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Order order = Order.builder()
                    .orderCode(orderCode)
                    .user(customer)
                    .store(store)
                    .delivery(delivery)
                    .recipientName(recipientName)
                    .phone(phone)
                    .address(address)
                    .paymentMethod(paymentMethod)
                    .isPaidBefore(isPaid)
                    .status(OrderStatus.NOT_PROCESSED)
                    .amountFromUser(totalAmount)
                    .amountToGD(totalAmount.multiply(BigDecimal.valueOf(0.05)))
                    .amountToStore(totalAmount.multiply(BigDecimal.valueOf(0.95)))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Order savedOrder = orderRepository.save(order);

            // Handle items
            Object rawItems = req.get("items");
            if (rawItems instanceof List) {
                List<?> list = (List<?>) rawItems;
                for (Object itemObj : list) {
                    if (itemObj instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) itemObj;
                        Long prodId = null;
                        if (map.containsKey("productId")) {
                            prodId = Long.parseLong(map.get("productId").toString());
                        } else if (map.containsKey("product") && map.get("product") instanceof Map) {
                            Map<?, ?> pMap = (Map<?, ?>) map.get("product");
                            prodId = Long.parseLong(pMap.get("id").toString());
                        }

                        if (prodId != null) {
                            Optional<Product> prodOpt = productRepository.findById(prodId);
                            if (prodOpt.isPresent()) {
                                Product p = prodOpt.get();
                                int count = map.containsKey("count") ? Integer.parseInt(map.get("count").toString()) : 1;
                                OrderItem oi = OrderItem.builder()
                                        .order(savedOrder)
                                        .product(p)
                                        .price(p.getEffectivePrice())
                                        .count(count)
                                        .build();
                                orderItemRepository.save(oi);
                            }
                        }
                    }
                }
            }

            log.info(">>> [API] Placed new order: {} (Total: {})", savedOrder.getOrderCode(), savedOrder.getTotalAmount());
            return ResponseEntity.status(HttpStatus.CREATED).body(OrderDto.fromEntity(savedOrder));
        } catch (Exception e) {
            log.error("Error creating order: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{orderCode}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String orderCode,
            @RequestBody Map<String, String> body) {

        Optional<Order> opt = orderRepository.findByOrderCode(orderCode);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String newStatus = body.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu status"));
        }

        try {
            OrderStatus status = OrderStatus.valueOf(newStatus.toUpperCase());
            Order o = opt.get();
            o.setStatus(status);
            if (status == OrderStatus.DELIVERED) {
                o.setIsPaidBefore(true);
            }
            o.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(o);
            log.info(">>> [API] Order {} updated status to {}", orderCode, status);
            return ResponseEntity.ok(OrderDto.fromEntity(o));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Trạng thái không hợp lệ"));
        }
    }
}
