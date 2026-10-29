package com.duytruongmoto.ecommerce.controller.api;

import com.duytruongmoto.ecommerce.entity.LegalRecord;
import com.duytruongmoto.ecommerce.service.LegalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class LegalApiController {

    private final LegalService legalService;

    @PostMapping("/appointment")
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, Object> req) {
        String name = (String) req.getOrDefault("name", "Khách Hàng Biker");
        String phone = (String) req.getOrDefault("phone", "");
        String idCard = (String) req.getOrDefault("idCard", "");
        String showroom = (String) req.getOrDefault("showroom", "");
        String vehicleName = (String) req.getOrDefault("vehicleName", "Siêu Xe PKL Duy Truong Motor");
        String dateStr = (String) req.get("date");
        String time = (String) req.getOrDefault("time", "09:00 - 11:30 Sáng (Bấm biển tại CSGT)");

        LocalDate date = LocalDate.now().plusDays(1);
        if (dateStr != null && !dateStr.isBlank()) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception ignored) {}
        }

        LegalRecord record = legalService.bookAppointment(name, phone, idCard, showroom, vehicleName, date, time);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đặt lịch hẹn công chứng & bấm biển số định danh thành công!",
                "recordCode", record.getRecordCode(),
                "customerName", record.getCustomerName(),
                "phone", record.getPhone(),
                "showroom", record.getShowroomName(),
                "appointmentDate", record.getAppointmentDate(),
                "appointmentTime", record.getAppointmentTime()
        ));
    }

    @GetMapping("/tracking")
    public ResponseEntity<?> trackRecord(@RequestParam(required = false) String query) {
        if (query == null || query.isBlank()) {
            query = "DTM-LEGAL-9833BD"; // Default demo
        }
        Optional<LegalRecord> recordOpt = legalService.findByCodeOrPhone(query);
        if (recordOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy hồ sơ pháp lý với mã hoặc số điện thoại: " + query
            ));
        }

        LegalRecord record = recordOpt.get();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "record", record
        ));
    }

    @GetMapping("/check-traffic-fine")
    public ResponseEntity<?> checkTrafficFine(@RequestParam(required = false) String plate,
                                              @RequestParam(required = false) String vin) {
        Map<String, Object> result = legalService.checkTrafficFine(plate, vin);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/calculate-tax")
    public ResponseEntity<?> calculateTax(@RequestBody Map<String, Object> req) {
        BigDecimal price = BigDecimal.valueOf(450000000);
        if (req.containsKey("price")) {
            try {
                price = new BigDecimal(req.get("price").toString());
            } catch (Exception ignored) {}
        }
        String area = (String) req.getOrDefault("area", "KV1");
        Map<String, Object> res = legalService.calculateTax(price, area);
        return ResponseEntity.ok(res);
    }
}
