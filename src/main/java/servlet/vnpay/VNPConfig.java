package servlet.vnpay;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;

public class VNPConfig {
    public static final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String vnp_TmnCode = "4P9P9ZLY";
    public static final String secretKey = "KKRXD30HV5DKBT4Y648121MPBYWPVG1B";
    public static final String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    public static String vnp_ReturnUrl(String path){
        return path + "vnpay/result";
    }

    public static String formatVnpayDate(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return timestamp.toLocalDateTime().format(formatter);
    }
    public static String getVnpayExpireDate(Timestamp createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime expireTime = createdAt.toLocalDateTime().plusMinutes(15);
        return expireTime.format(formatter);

    }
    public static Timestamp parseVnpayDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return new Timestamp(System.currentTimeMillis());
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime ldt = LocalDateTime.parse(dateStr, formatter);
            return Timestamp.valueOf(ldt);
        } catch (Exception e) {
            return new Timestamp(System.currentTimeMillis());
        }
    }
    public static String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                try {
                    sb.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(secretKey, sb.toString());
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
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

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }
    public static String getRandomCode(long orderId, long userId, Timestamp time) {
        SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyMMddHHmm");
        String datePart = formatter.format(time);

        return String.format("ORDER_%d_%d_%s", userId, orderId, datePart);
    }
    public static boolean verifySignature(Map<String, String> fields, String vnp_SecureHash) {
        String signValue = hashAllFields(fields);
        return signValue.equalsIgnoreCase(vnp_SecureHash);
    }
}

