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
    private static final int expireTime = 30;

    private static final Map<String, String> vnp_response;
    private static final Set<String> retryableCodes;

    static {
        vnp_response = new HashMap<>();

        vnp_response.put("00", "Giao dịch thành công");
        vnp_response.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).");
        vnp_response.put("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
        vnp_response.put("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
        vnp_response.put("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        vnp_response.put("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
        vnp_response.put("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.");
        vnp_response.put("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch");
        vnp_response.put("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        vnp_response.put("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        vnp_response.put("75", "Ngân hàng thanh toán đang bảo trì.");
        vnp_response.put("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch");
        vnp_response.put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)");

        retryableCodes = new HashSet<>();
        retryableCodes.add("09");
        retryableCodes.add("10");
        retryableCodes.add("12");
        retryableCodes.add("13");
        retryableCodes.add("24");
        retryableCodes.add("51");
        retryableCodes.add("65");
        retryableCodes.add("75");
        retryableCodes.add("79");
    }
    public static String getResponseMessage(String responseCode) {
        return vnp_response.get(responseCode);
    }
    public static boolean isRetryAble(String responseCode) {
        return retryableCodes.contains(responseCode);
    }

    public static String vnp_ReturnUrl(String path){
        return path + "vnpay/result";
    }

    public static String formatVnpayDate(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return timestamp.toLocalDateTime().format(formatter);
    }
    public static String getFormatedExpireTime(Timestamp createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return getExpireTime(createdAt).toLocalDateTime().format(formatter);
    }
    public static Timestamp getExpireTime(Timestamp createdAt) {
        return Timestamp.valueOf(createdAt.toLocalDateTime().plusMinutes(expireTime));
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm");
        String datePart = formatter.format(time);

        return String.format("ORDER_%d_%d_%s", userId, orderId, datePart);
    }
    public static boolean verifySignature(Map<String, String> fields, String vnp_SecureHash) {
        String signValue = hashAllFields(fields);
        return signValue.equalsIgnoreCase(vnp_SecureHash);
    }
}

