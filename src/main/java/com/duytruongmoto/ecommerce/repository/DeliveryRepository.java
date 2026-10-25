package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByIsDeletedFalse();
}
