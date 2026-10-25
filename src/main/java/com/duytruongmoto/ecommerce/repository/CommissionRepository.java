package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByIsDeletedFalse();
}
