package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.Delivery;
import com.duytruongmoto.ecommerce.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public List<Delivery> getAllActiveDeliveries() {
        return deliveryRepository.findByIsDeletedFalse();
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public Optional<Delivery> findById(Long id) {
        return deliveryRepository.findById(id);
    }

    @Transactional
    public Delivery createDelivery(String name, String description, BigDecimal price) {
        Delivery delivery = Delivery.builder()
                .name(name)
                .description(description)
                .price(price)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public void deleteDelivery(Long id) {
        deliveryRepository.findById(id).ifPresent(d -> {
            d.setIsDeleted(true);
            d.setUpdatedAt(LocalDateTime.now());
            deliveryRepository.save(d);
        });
    }
}
