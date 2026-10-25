package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.Order;
import com.duytruongmoto.ecommerce.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);
    List<Order> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    List<Order> findByStoreIdAndStatusOrderByCreatedAtDesc(Long storeId, OrderStatus status);
    List<Order> findByShipperIdOrderByCreatedAtDesc(Long shipperId);
    List<Order> findByShipperIdAndStatusOrderByCreatedAtDesc(Long shipperId, OrderStatus status);
}
