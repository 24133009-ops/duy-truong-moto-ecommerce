package com.duytruongmoto.ecommerce.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class VnPayService {

    @Value("${vnpay.tmn_code:DUYTRUONG}")
    private String vnpTmnCode;

    @Value("${vnpay.hash_secret:ABCDEFGHIJKLMN1234567890OPQRSTUVWXYZ}")
    private String vnpHashSecret;

    @Value("${vnpay.payment_url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpUrl;

    @Value("${vnpay.return_url:http://localhost:8080/checkout/vnpay-return}")
    private String vnpReturnUrl;

    public String createPaymentUrl(String orderCode, BigDecimal amount, String orderInfo, HttpServletRequest request) {
        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String vnpTxnRef = orderCode;
        String vnpIpAddr = getIpAddress(request);

        long amountInVnd = amount.multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amountInVnd));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnpTxnRef);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");

        // Dynamic return URL for Render cloud deployment
        String actualReturnUrl = vnpReturnUrl;
        if (request != null) {
            String serverName = request.getServerName();
            if (serverName != null && (serverName.contains("onrender.com") || (actualReturnUrl != null && actualReturnUrl.contains("localhost")))) {
                String scheme = request.getHeader("X-Forwarded-Proto");
                if (scheme == null) scheme = request.getScheme();
                int serverPort = request.getServerPort();
                if (serverName.contains("onrender.com")) {
                    actualReturnUrl = "https://" + serverName + "/checkout/vnpay-return";
                } else if (!serverName.contains("localhost") && !serverName.contains("127.0.0.1")) {
                    actualReturnUrl = scheme + "://" + serverName + (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort) + "/checkout/vnpay-return";
                }
            }
        }
        vnpParams.put("vnp_ReturnUrl", actualReturnUrl);
        vnpParams.put("vnp_IpAddr", vnpIpAddr);

        // Fix: Use Asia/Ho_Chi_Minh on both Calendar and SimpleDateFormat so VNPAY receives exact Vietnam time (GMT+7)
        TimeZone vnTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar cld = Calendar.getInstance(vnTimeZone);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(vnTimeZone);

        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        cld.add(Calendar.MINUTE, 30);
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding error", e);
        }

        String vnpSecureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnpSecureHash);

        return vnpUrl + "?" + query.toString();
    }

    public boolean validateSignature(Map<String, String> fields) {
        String vnpSecureHash = fields.get("vnp_SecureHash");
        if (vnpSecureHash == null || vnpSecureHash.trim().isEmpty()) {
            return false;
        }

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        fieldNames.remove("vnp_SecureHash");
        fieldNames.remove("vnp_SecureHashType");
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = fields.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("VNPAY signature hash encoding error", e);
            return false;
        }

        String signValue = hmacSHA512(vnpHashSecret, hashData.toString());
        return signValue.equalsIgnoreCase(vnpSecureHash);
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) return "";
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress != null ? ipAddress : "127.0.0.1";
    }
}
