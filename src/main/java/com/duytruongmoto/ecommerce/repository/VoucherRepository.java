package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCodeIgnoreCaseAndIsDeletedFalse(String code);
    List<Voucher> findByIsDeletedFalse();
    List<Voucher> findByStoreIdAndIsDeletedFalse(Long storeId);
}
