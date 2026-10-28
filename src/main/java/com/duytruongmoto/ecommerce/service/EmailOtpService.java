package com.duytruongmoto.ecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailOtpService {

    private final JavaMailSender mailSender;
    private static final SecureRandom random = new SecureRandom();

    @Value("${spring.mail.username:duytq4906@gmail.com}")
    private String fromEmail;

    @Value("${brevo.api.key:${BREVO_API_KEY:}}")
    private String brevoApiKey;

    @Value("${resend.api.key:${RESEND_API_KEY:}}")
    private String resendApiKey;

    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Async
    public void sendOtpEmail(String toEmail, String otpCode, String purpose) {
        log.info("==================================================");
        log.info(">>> YÊU CẦU GỬI MÃ OTP ĐẾN EMAIL [{}]: {}", toEmail, otpCode);
        log.info(">>> Mục đích xác thực: {}", purpose);
        log.info("==================================================");

        // 1. Ưu tiên gửi qua Brevo HTTPS API nếu có khóa (Giải pháp tối ưu trên môi trường Render chặn SMTP)
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            boolean sent = sendViaBrevoApi(toEmail, otpCode, purpose);
            if (sent) return;
        }

        // 2. Dự phòng gửi qua Resend HTTPS API nếu có khóa
        if (resendApiKey != null && !resendApiKey.isBlank()) {
            boolean sent = sendViaResendApi(toEmail, otpCode, purpose);
            if (sent) return;
        }

        // 3. Gửi qua SMTP truyền thống (Hoạt động tốt ở Localhost)
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
            log.info(">>> [THÀNH CÔNG] Đã chuyển mã OTP {} đến hộp thư {} qua SMTP", otpCode, toEmail);
        } catch (Exception e) {
            log.warn(">>> [LƯU Ý GỬI EMAIL] SMTP không khả dụng (Cổng gửi bị chặn bởi môi trường Cloud): {}", e.getMessage());
            log.info(">>> Mã OTP hợp lệ dùng để kích hoạt: {}", otpCode);
        }
    }

    private boolean sendViaBrevoApi(String toEmail, String otpCode, String purpose) {
        try {
            String jsonPayload = String.format(
                    "{\"sender\":{\"name\":\"Duy Truong Motor\",\"email\":\"%s\"},\"to\":[{\"email\":\"%s\"}],\"subject\":\"[Duy Truong Motor] Mã xác thực OTP: %s\",\"htmlContent\":\"<div style='font-family:Arial,sans-serif;padding:20px;background:#0f172a;color:#f8fafc;border-radius:12px;'><h2 style='color:#d4af37;'>DUY TRUONG MOTOR LUXURY</h2><p>Kính chào Quý khách,</p><p>Mã xác thực OTP của bạn cho thao tác <b>%s</b> là:</p><div style='font-size:32px;font-weight:bold;letter-spacing:6px;color:#d4af37;padding:12px 0;'>%s</div><p style='color:#94a3b8;font-size:13px;'>Mã có hiệu lực trong 5 phút. Tuyệt đối không cung cấp mã cho người khác.</p></div>\"}",
                    fromEmail, toEmail, otpCode, purpose, otpCode
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("api-key", brevoApiKey.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                log.info(">>> [Brevo API] Đã gửi mã OTP {} đến {} qua Brevo HTTPS API thành công!", otpCode, toEmail);
                return true;
            } else {
                log.warn(">>> [Brevo API] Thất bại mã HTTP {}: {}", resp.statusCode(), resp.body());
                return false;
            }
        } catch (Exception ex) {
            log.error(">>> [Brevo API Exception] {}", ex.getMessage());
            return false;
        }
    }

    private boolean sendViaResendApi(String toEmail, String otpCode, String purpose) {
        try {
            String jsonPayload = String.format(
                    "{\"from\":\"Duy Truong Motor <onboarding@resend.dev>\",\"to\":[\"%s\"],\"subject\":\"[Duy Truong Motor] Mã OTP: %s\",\"html\":\"<p>Mã xác thực OTP: <b>%s</b> (Hiệu lực 5 phút)</p>\"}",
                    toEmail, otpCode, otpCode
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey.trim())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception ex) {
            log.error(">>> [Resend API Exception] {}", ex.getMessage());
            return false;
        }
    }
}
