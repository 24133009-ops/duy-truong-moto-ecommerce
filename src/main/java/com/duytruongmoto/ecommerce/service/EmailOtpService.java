package com.duytruongmoto.ecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailOtpService {

    private final JavaMailSender mailSender;
    private static final SecureRandom random = new SecureRandom();

    @Value("${spring.mail.username:duytq4906@gmail.com}")
    private String fromEmail;

    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Async
    public void sendOtpEmail(String toEmail, String otpCode, String purpose) {
        log.info("==================================================");
        log.info(">>> GỬI MÃ OTP ĐẾN EMAIL [{}]: {}", toEmail, otpCode);
        log.info(">>> Mục đích: {}", purpose);
        log.info("==================================================");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[Duy Truong Motor] Mã xác thực OTP: " + otpCode);
            message.setText("Kính chào Quý khách,\n\n" +
                    "Mã xác thực OTP của bạn cho thao tác " + purpose + " tại Hệ thống Siêu Xe Phân Khối Lớn Duy Truong Motor là: " + otpCode + "\n\n" +
                    "Mã có hiệu lực trong vòng 5 phút. Vui lòng tuyệt đối không chia sẻ mã này cho bất kỳ ai!\n\n" +
                    "Trân trọng,\nĐội ngũ CSKH Duy Truong Motor Luxury");
            mailSender.send(message);
            log.info(">>> [THÀNH CÔNG] Đã chuyển mã OTP {} đến hộp thư {}", otpCode, toEmail);
        } catch (Exception e) {
            log.error(">>> [LỖI GỬI EMAIL] Không thể gửi email qua SMTP: {}", e.getMessage());
            log.warn("Mã OTP dự phòng: {}", otpCode);
        }
    }
}
