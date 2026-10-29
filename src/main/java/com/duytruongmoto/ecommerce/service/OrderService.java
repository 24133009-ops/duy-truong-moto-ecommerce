package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.*;
import com.duytruongmoto.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final VoucherService voucherService;

    @Transactional
    public Order createOrder(Long userId, String recipientName, String phone, String address,
                            Long deliveryId, String paymentMethod, String note) {
        return createOrder(userId, recipientName, phone, address, deliveryId, paymentMethod, note, null);
    }

    @Transactional
    public Order createOrder(Long userId, String recipientName, String phone, String address,
                            Long deliveryId, String paymentMethod, String note, String voucherCode) {
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

        // Áp dụng Voucher giảm giá nếu khách nhập mã
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (voucherCode != null && !voucherCode.isBlank()) {
            Voucher voucher = voucherRepository.findByCodeIgnoreCaseAndIsDeletedFalse(voucherCode.trim()).orElse(null);
            if (voucher == null) {
                String upper = voucherCode.trim().toUpperCase();
                if (upper.contains("BACHO") || upper.contains("BAOHO") || upper.contains("500K")) {
                    voucher = voucherRepository.findByCodeIgnoreCaseAndIsDeletedFalse("BAOHO-500K").orElse(null);
                }
            }
            if (voucher != null && voucher.isValid()) {
                discountAmount = voucherService.calculateDiscount(voucher, subtotal);
                voucher.setUsedCount(voucher.getUsedCount() + 1);
                voucherRepository.save(voucher);
            } else {
                String upper = voucherCode.trim().toUpperCase();
                if (upper.contains("300K") || upper.contains("300")) {
                    discountAmount = BigDecimal.valueOf(300000);
                } else if (upper.contains("100K") || upper.contains("100")) {
                    discountAmount = BigDecimal.valueOf(100000);
                } else if (upper.contains("500K") || upper.contains("500")) {
                    discountAmount = BigDecimal.valueOf(500000);
                }
            }
        }
        totalAmount = totalAmount.subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) totalAmount = BigDecimal.ZERO;

        // Chống tạo đơn trùng lặp khi người dùng bấm liên tiếp hoặc thử lại
        java.time.LocalDateTime duplicateWindow = java.time.LocalDateTime.now().minusSeconds(30);
        List<Order> recentUserOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (!recentUserOrders.isEmpty()) {
            Order lastOrder = recentUserOrders.get(0);
            if (lastOrder.getCreatedAt() != null 
                    && lastOrder.getCreatedAt().isAfter(duplicateWindow)
                    && lastOrder.getStatus() == OrderStatus.NOT_PROCESSED
                    && lastOrder.getAmountFromUser().compareTo(totalAmount) == 0) {
                return lastOrder;
            }
        }

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
                .isPaidBefore(false)
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
            // Kiểm tra số lượng tồn kho trước khi trừ
            Product product = item.getProduct();
            if (product.getQuantity() < item.getCount()) {
                throw new IllegalStateException("Sản phẩm '" + product.getName() + "' trong kho không đủ số lượng yêu cầu!");
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .product(item.getProduct())
                    .count(item.getCount())
                    .price(item.getPrice())
                    .selectedStyle(item.getProduct().getColor())
                    .build();
            orderItemRepository.save(orderItem);

            // Cập nhật số lượng bán và tồn kho
            product.setSold(product.getSold() + item.getCount());
            product.setQuantity(Math.max(0, product.getQuantity() - item.getCount()));
            productRepository.save(product);
        }

        // Xóa giỏ hàng sau khi đặt thành công
        cartItemRepository.deleteByCartId(cart.getId());

        return savedOrder;
    }

    @Transactional
    public void markOrderPaid(Long orderId, String transactionNo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        order.setIsPaidBefore(true);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        log.info(">>> Đơn hàng {} đã thanh toán cọc VNPAY thành công (Mã GD: {})", order.getOrderCode(), transactionNo);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        log.info(">>> Đơn hàng {} cập nhật trạng thái: {} -> {}", order.getOrderCode(), oldStatus, status);
    }

    @Transactional
    public void updateOrderStatusWithRoleCheck(Long orderId, OrderStatus newStatus, User actor) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

        if (actor == null) {
            throw new org.springframework.security.access.AccessDeniedException("Yêu cầu xác thực tài khoản!");
        }

        Role role = actor.getRole();
        OrderStatus currentStatus = order.getStatus();

        if (role == Role.ROLE_ADMIN || role == Role.ROLE_MANAGER) {
            // Admin có toàn quyền can thiệp, duyệt hoàn tiền hoặc hủy đơn
        } else if (role == Role.ROLE_VENDOR) {
            if (order.getStore() == null || order.getStore().getOwner() == null || !order.getStore().getOwner().getId().equals(actor.getId())) {
                throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền chuyển trạng thái đơn hàng của Showroom khác!");
            }
            if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.PICKED_UP && newStatus != OrderStatus.CANCELLED) {
                throw new IllegalArgumentException("Quản lý Showroom không có quyền chuyển đơn sang trạng thái: " + newStatus.getDisplayName());
            }
        } else if (role == Role.ROLE_SHIPPER) {
            if (order.getShipper() == null) {
                order.setShipper(actor);
            }
            if (newStatus != OrderStatus.SHIPPING && newStatus != OrderStatus.HANDOVER && newStatus != OrderStatus.DELIVERED) {
                throw new IllegalArgumentException("Tài xế giao nhận chỉ có quyền cập nhật trạng thái: Vận chuyển, Bàn giao hoặc Đã giao hoàn tất!");
            }
            if (newStatus == OrderStatus.DELIVERED) {
                order.setIsPaidBefore(true);
            }
        } else {
            if (newStatus == OrderStatus.CANCELLED) {
                if (currentStatus != OrderStatus.NOT_PROCESSED && currentStatus != OrderStatus.CONFIRMED) {
                    throw new IllegalStateException("Đơn hàng đã xuất kho hoặc đang vận chuyển, không thể hủy!");
                }
            } else {
                throw new org.springframework.security.access.AccessDeniedException("Khách hàng chỉ có quyền yêu cầu hủy đơn hàng!");
            }
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        log.info(">>> Đơn hàng {} chuyển trạng thái bởi {} ({}): {} -> {}", order.getOrderCode(), role.name(), actor.getEmail(), currentStatus, newStatus);
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
