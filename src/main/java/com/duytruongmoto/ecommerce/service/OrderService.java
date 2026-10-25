package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.*;
import com.duytruongmoto.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(Long userId, String recipientName, String phone, String address,
                            Long deliveryId, String paymentMethod, String note) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Giỏ hàng của bạn đang trống!"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Vui lòng chọn ít nhất 1 sản phẩm môtô hoặc phụ kiện vào giỏ hàng!");
        }

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElse(Delivery.builder().name("Giao hàng tiêu chuẩn").price(BigDecimal.valueOf(500000)).build());

        // Group by Store (Order per store in chain)
        Store store = cart.getItems().get(0).getProduct().getStore();

        BigDecimal subtotal = cart.getItems().stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = subtotal.add(delivery.getPrice());

        // Hoa hồng sàn (ví dụ 5% hoặc theo cấu hình commission của store)
        BigDecimal commissionRate = (store.getCommission() != null) ? store.getCommission().getCost() : BigDecimal.valueOf(5.0);
        BigDecimal amountToGD = subtotal.multiply(commissionRate).divide(BigDecimal.valueOf(100));
        BigDecimal amountToStore = totalAmount.subtract(amountToGD);

        String orderCode = "DTM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .orderCode(orderCode)
                .user(cart.getUser())
                .store(store)
                .delivery(delivery)
                .recipientName(recipientName)
                .phone(phone)
                .address(address)
                .status(OrderStatus.NOT_PROCESSED)
                .isPaidBefore("VNPAY".equalsIgnoreCase(paymentMethod) || "MOMO".equalsIgnoreCase(paymentMethod))
                .paymentMethod(paymentMethod)
                .amountFromUser(totalAmount)
                .amountToStore(amountToStore)
                .amountToGD(amountToGD)
                .note(note)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(item.getProduct())
                    .count(item.getCount())
                    .price(item.getPrice())
                    .selectedStyle(item.getProduct().getColor())
                    .build();
            orderItemRepository.save(orderItem);

            // Cập nhật số lượng bán và tồn kho
            Product product = item.getProduct();
            product.setSold(product.getSold() + item.getCount());
            product.setQuantity(Math.max(0, product.getQuantity() - item.getCount()));
            productRepository.save(product);
        }

        // Xóa giỏ hàng sau khi đặt thành công
        cartItemRepository.deleteByCartId(cart.getId());

        return savedOrder;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Transactional
    public void assignShipper(Long orderId, Long shipperId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        User shipper = userRepository.findById(shipperId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên giao nhận không tồn tại"));
        order.setShipper(shipper);
        order.setStatus(OrderStatus.PICKED_UP);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(Long userId, OrderStatus status) {
        if (status != null) {
            return orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Order> getOrdersByStore(Long storeId, OrderStatus status) {
        if (status != null) {
            return orderRepository.findByStoreIdAndStatusOrderByCreatedAtDesc(storeId, status);
        }
        return orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId);
    }

    public List<Order> getOrdersByShipper(Long shipperId, OrderStatus status) {
        if (status != null) {
            return orderRepository.findByShipperIdAndStatusOrderByCreatedAtDesc(shipperId, status);
        }
        return orderRepository.findByShipperIdOrderByCreatedAtDesc(shipperId);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> findByOrderCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
