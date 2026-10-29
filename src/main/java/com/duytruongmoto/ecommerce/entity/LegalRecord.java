package com.duytruongmoto.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "legal_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String recordCode; // e.g. DTM-LEGAL-9833BD

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 20)
    private String idCard; // Số CCCD 12 số

    @Column(nullable = false, length = 150)
    private String vehicleName; // e.g. MV Agusta Brutale 1000 RR

    @Column(length = 50)
    private String vinNumber; // Số khung

    @Column(length = 50)
    private String engineNumber; // Số máy

    @Column(length = 30)
    private String originalPlate; // Biển số cũ

    @Column(length = 30)
    private String targetPlate; // Biển số định danh mới (e.g. 29A1-999.88)

    @Column(length = 200)
    private String showroomName;

    private LocalDate appointmentDate;

    @Column(length = 100)
    private String appointmentTime;

    @Builder.Default
    @Column(nullable = false)
    private Integer currentStep = 1; // 1: Kiểm định & Phạt nguội, 2: Công chứng, 3: Rút hồ sơ gốc, 4: Cấp biển số định danh

    @Builder.Default
    @Column(nullable = false)
    private Boolean isCompleted = false;

    @Column(length = 150)
    private String lawyerContact; // Luật sư / Chuyên viên pháp lý phụ trách

    @Column(length = 1000)
    private String note;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
