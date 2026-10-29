package com.duytruongmoto.ecommerce.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailOtpService {

    private final JavaMailSender mailSender;
    private static final SecureRandom random = new SecureRandom();

    @Value("${spring.mail.username:duytq4906@gmail.com}")
    private String fromEmail;

    @Value("${spring.mail.password:lnxgxrhmotbwdpmp}")
    private String mailPassword;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

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

        // 1. Ưu tiên gửi qua Brevo HTTPS API nếu có khóa (Giải pháp tối ưu trên môi trường Cloud)
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            boolean sent = sendViaBrevoApi(toEmail, otpCode, purpose);
            if (sent) return;
        }

        // 2. Dự phòng gửi qua Resend HTTPS API nếu có khóa
        if (resendApiKey != null && !resendApiKey.isBlank()) {
            boolean sent = sendViaResendApi(toEmail, otpCode, purpose);
            if (sent) return;
        }

        // 3. Gửi qua Spring mailSender mặc định
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            String senderAddr = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail : "duytq4906@gmail.com";
            helper.setFrom(new InternetAddress(senderAddr, "Duy Truong Motor Luxury"));
            helper.setTo(toEmail);
            helper.setSubject("[Duy Trường Motor] Mã Xác Thực OTP: " + otpCode);
            helper.setText(buildHtmlEmail(otpCode, purpose), true);

            mailSender.send(mimeMessage);
            log.info(">>> [THÀNH CÔNG] Đã chuyển mã OTP {} đến hộp thư {} qua Spring JavaMailSender", otpCode, toEmail);
            return;
        } catch (Exception e) {
            log.warn(">>> [LƯU Ý] mailSender mặc định gặp lỗi: {}. Thử gửi trực tiếp qua Gmail SSL 465...", e.getMessage());
        }

        // 4. Dự phòng trực tiếp qua Gmail SMTP SSL (Cổng 465)
        boolean fallback465 = tryDirectGmailSmtp(toEmail, otpCode, purpose, 465);
        if (fallback465) return;

        // 5. Dự phòng trực tiếp qua Gmail SMTP TLS (Cổng 587)
        boolean fallback587 = tryDirectGmailSmtp(toEmail, otpCode, purpose, 587);
        if (fallback587) return;

        log.error(">>> [LỖI] Không thể kết nối tới Gmail SMTP. Mã OTP nội bộ: {}", otpCode);
    }

    private boolean tryDirectGmailSmtp(String toEmail, String otpCode, String purpose, int port) {
        try {
            JavaMailSenderImpl directSender = new JavaMailSenderImpl();
            directSender.setHost(mailHost != null && !mailHost.isBlank() ? mailHost : "smtp.gmail.com");
            directSender.setPort(port);
            directSender.setUsername(fromEmail != null && !fromEmail.isBlank() ? fromEmail : "duytq4906@gmail.com");
            String pwd = (mailPassword != null && !mailPassword.isBlank()) ? mailPassword.trim() : "lnxgxrhmotbwdpmp";
            directSender.setPassword(pwd);

            Properties props = directSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            if (port == 465) {
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            } else {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            }
            props.put("mail.smtp.connectiontimeout", "8000");
            props.put("mail.smtp.timeout", "8000");
            props.put("mail.smtp.writetimeout", "8000");

            MimeMessage mime = directSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(new InternetAddress(directSender.getUsername(), "Duy Truong Motor Luxury"));
            helper.setTo(toEmail);
            helper.setSubject("[Duy Trường Motor] Mã Xác Thực OTP: " + otpCode);
            helper.setText(buildHtmlEmail(otpCode, purpose), true);

            directSender.send(mime);
            log.info(">>> [Port {} SSL] Gửi thành công mã OTP {} đến {}", port, otpCode, toEmail);
            return true;
        } catch (Exception ex) {
            log.warn(">>> [Port {} SSL] Thất bại: {}", port, ex.getMessage());
            return false;
        }
    }

    private String buildHtmlEmail(String otpCode, String purpose) {
        return "<div style=\"font-family:'Segoe UI',Roboto,Helvetica,Arial,sans-serif;max-width:600px;margin:0 auto;background:#0b0e14;border:1px solid #d4af37;border-radius:16px;overflow:hidden;color:#ffffff;\">"
                + "<div style=\"background:linear-gradient(135deg, #1a1e29 0%, #0b0e14 100%);padding:30px 24px;text-align:center;border-bottom:1px solid rgba(212,175,55,0.3);\">"
                + "<h1 style=\"margin:0;color:#d4af37;font-size:26px;letter-spacing:2px;font-weight:900;\">DUY TRƯỜNG MOTOR</h1>"
                + "<div style=\"color:#94a3b8;font-size:13px;margin-top:6px;letter-spacing:1px;\">CHUỖI SIÊU THỊ MÔ TÔ PHÂN KHỐI LỚN &amp; ĐỒ BẢO HỘ CHÍNH HÃNG</div>"
                + "</div>"
                + "<div style=\"padding:36px 30px;text-align:center;\">"
                + "<h2 style=\"margin:0 0 12px;color:#f8fafc;font-size:20px;\">MÃ XÁC THỰC EMAIL (OTP)</h2>"
                + "<p style=\"color:#cbd5e1;font-size:14px;margin:0 0 24px;line-height:1.6;\">Kính chào Quý khách,<br>Hệ thống vừa nhận được yêu cầu <strong>" + purpose + "</strong> cho tài khoản của bạn. Vui lòng nhập mã bảo mật 6 số dưới đây để hoàn tất:</p>"
                + "<div style=\"background:#131822;border:2px dashed #d4af37;border-radius:12px;padding:18px 24px;margin:0 auto 24px;display:inline-block;\">"
                + "<span style=\"font-size:36px;font-weight:900;letter-spacing:8px;color:#f59e0b;font-family:monospace;\">" + otpCode + "</span>"
                + "</div>"
                + "<div style=\"color:#ef4444;font-size:13px;font-weight:bold;margin-bottom:8px;\">⏰ Mã có hiệu lực trong vòng 5 phút (kể từ lúc gửi)</div>"
                + "<div style=\"color:#64748b;font-size:12px;\">Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email hoặc liên hệ với CSKH để bảo vệ tài khoản.</div>"
                + "</div>"
                + "<div style=\"background:#07090e;padding:18px 24px;text-align:center;border-top:1px solid rgba(255,255,255,0.06);font-size:12px;color:#64748b;\">"
                + "Hotline hỗ trợ: <strong>1900 6868</strong> | Trụ sở chính: 123 Hoàng Văn Thụ, P.8, Q. Phú Nhuận, TP.HCM<br>"
                + "© 2026 Duy Truong Motor - All rights reserved."
                + "</div>"
                + "</div>";
    }

    private boolean sendViaBrevoApi(String toEmail, String otpCode, String purpose) {
        try {
            String jsonPayload = String.format(
                    "{\"sender\":{\"name\":\"Duy Truong Motor\",\"email\":\"%s\"},\"to\":[{\"email\":\"%s\"}],\"subject\":\"[Duy Truong Motor] Mã xác thực OTP: %s\",\"htmlContent\":\"%s\"}",
                    fromEmail, toEmail, otpCode, buildHtmlEmail(otpCode, purpose).replace("\"", "\\\"")
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
                    "{\"from\":\"Duy Truong Motor <onboarding@resend.dev>\",\"to\":[\"%s\"],\"subject\":\"[Duy Truong Motor] Mã OTP: %s\",\"html\":\"%s\"}",
                    toEmail, otpCode, buildHtmlEmail(otpCode, purpose).replace("\"", "\\\"")
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
