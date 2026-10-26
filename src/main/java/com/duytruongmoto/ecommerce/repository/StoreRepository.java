package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findBySlug(String slug);
    List<Store> findByOwnerId(Long ownerId);
    Optional<Store> findFirstByOwnerId(Long ownerId);
    List<Store> findByIsActiveTrue();
    List<Store> findByIsActive(Boolean isActive);
}
