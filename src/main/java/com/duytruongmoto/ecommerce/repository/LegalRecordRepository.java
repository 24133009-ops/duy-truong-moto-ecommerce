package com.duytruongmoto.ecommerce.repository;

import com.duytruongmoto.ecommerce.entity.LegalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LegalRecordRepository extends JpaRepository<LegalRecord, Long> {
    Optional<LegalRecord> findByRecordCode(String recordCode);
    List<LegalRecord> findByPhone(String phone);
    Optional<LegalRecord> findFirstByPhoneOrderByCreatedAtDesc(String phone);
    Optional<LegalRecord> findFirstByTargetPlateOrVinNumber(String targetPlate, String vinNumber);
    List<LegalRecord> findAllByOrderByCreatedAtDesc();
}
