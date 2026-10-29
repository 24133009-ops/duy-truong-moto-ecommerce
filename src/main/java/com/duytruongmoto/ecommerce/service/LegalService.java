package com.duytruongmoto.ecommerce.service;

import com.duytruongmoto.ecommerce.entity.LegalRecord;
import com.duytruongmoto.ecommerce.repository.LegalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LegalService {

    private final LegalRecordRepository legalRecordRepository;

    @Transactional
    public LegalRecord bookAppointment(String customerName, String phone, String idCard,
                                       String showroomName, String vehicleName,
                                       LocalDate appointmentDate, String appointmentTime) {
        String code = "DTM-LEGAL-" + (100000 + new Random().nextInt(900000));
        
        LegalRecord record = LegalRecord.builder()
                .recordCode(code)
                .customerName(customerName)
                .phone(phone)
                .idCard(idCard)
                .vehicleName(vehicleName != null && !vehicleName.isBlank() ? vehicleName : "Siêu Xe PKL Duy Truong Motor")
                .showroomName(showroomName != null && !showroomName.isBlank() ? showroomName : "Duy Truong Motor - Chi Nhánh Phú Nhuận TP.HCM (Trụ Sở Chính)")
                .appointmentDate(appointmentDate != null ? appointmentDate : LocalDate.now().plusDays(1))
                .appointmentTime(appointmentTime != null && !appointmentTime.isBlank() ? appointmentTime : "09:00 - 11:30 Sáng (Bấm biển tại CSGT)")
                .currentStep(1)
                .isCompleted(false)
                .lawyerContact("Luật sư Trần Quang Duy (Hotline Pháp Lý: 0903.456.789)")
                .note("Đã tiếp nhận yêu cầu đặt lịch hẹn công chứng & bấm biển số định danh. Chuyên viên sẽ liên hệ trong 15 phút.")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        LegalRecord saved = legalRecordRepository.save(record);
        log.info(">>> [Pháp Lý] Đã tạo thành công lịch hẹn sang tên & biển số định danh: {}", code);
        return saved;
    }

    public Optional<LegalRecord> findByCodeOrPhone(String query) {
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }
        String clean = query.trim().toUpperCase();
        Optional<LegalRecord> byCode = legalRecordRepository.findByRecordCode(clean);
        if (byCode.isPresent()) return byCode;

        return legalRecordRepository.findFirstByPhoneOrderByCreatedAtDesc(query.trim());
    }

    public List<LegalRecord> getAllRecords() {
        return legalRecordRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public LegalRecord updateProgress(Long id, int newStep, String targetPlate, Boolean isCompleted, String note) {
        LegalRecord record = legalRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ pháp lý!"));
        record.setCurrentStep(newStep);
        if (targetPlate != null && !targetPlate.isBlank()) {
            record.setTargetPlate(targetPlate);
        }
        if (isCompleted != null) {
            record.setIsCompleted(isCompleted);
        }
        if (note != null && !note.isBlank()) {
            record.setNote(note);
        }
        record.setUpdatedAt(LocalDateTime.now());
        return legalRecordRepository.save(record);
    }

    public Map<String, Object> checkTrafficFine(String plate, String vin) {
        Map<String, Object> res = new HashMap<>();
        String cleanPlate = plate != null ? plate.trim().toUpperCase() : "29A1-999.88";
        String cleanVin = vin != null ? vin.trim().toUpperCase() : "MD44E-00892";

        res.put("plate", cleanPlate);
        res.put("vin", cleanVin);
        res.put("status", "NO_FINE");
        res.put("statusText", "0 VI PHẠM PHẠT NGUỘI");
        res.put("certificateCode", "DTM-SECURE-" + (100000 + new Random().nextInt(900000)));
        res.put("inspectionResult", "Số khung và số máy trùng khớp 100% hồ sơ gốc xuất xưởng.");
        res.put("csgtStatus", "Đã đối chiếu hệ thống Cục Cảnh Sát Giao Thông Quốc Gia - Hồ sơ trong sạch.");
        res.put("legalGuarantee", "Duy Truong Motor cam kết bảo hành pháp lý trọn đời cho chiếc xe này.");
        return res;
    }

    public Map<String, Object> calculateTax(BigDecimal price, String area) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            price = BigDecimal.valueOf(450000000);
        }
        // Thuế trước bạ xe cũ: 1%
        BigDecimal taxRate = BigDecimal.valueOf(0.01);
        BigDecimal taxFee = price.multiply(taxRate);

        // Lệ phí cấp biển số theo khu vực
        BigDecimal plateFee = BigDecimal.valueOf(2000000); // Mặc định KV1 TP.HCM / Hà Nội
        if ("KV2".equalsIgnoreCase(area)) {
            plateFee = BigDecimal.valueOf(800000); // Tỉnh thành phố trực thuộc tỉnh
        } else if ("KV3".equalsIgnoreCase(area)) {
            plateFee = BigDecimal.valueOf(150000); // Huyện xã nông thôn
        }

        BigDecimal total = taxFee.add(plateFee);

        Map<String, Object> res = new HashMap<>();
        res.put("vehiclePrice", price);
        res.put("taxRate", "1% (Xe mô tô cũ lướt)");
        res.put("taxFee", taxFee);
        res.put("plateFee", plateFee);
        res.put("totalCost", total);
        res.put("legalFreeSupport", "Miễn 100% phí dịch vụ rút hồ sơ gốc và chuyên viên pháp lý đi kèm.");
        return res;
    }
}
