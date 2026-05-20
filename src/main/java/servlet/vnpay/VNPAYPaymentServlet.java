package servlet.vnpay;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import beans.vnpay.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.PaymentService;

@WebServlet("/vnpay/payment")
public class VNPAYPaymentServlet extends HttpServlet {
    PaymentService service =  new PaymentService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String vnpTxnRef = req.getParameter("vnpTxnRef");
            String bankCode = req.getParameter("bankCode");

            if (vnpTxnRef == null) {
                resp.sendRedirect(req.getContextPath() + "/error");
                return;
            }

            Payment p = service.getInitPayment(vnpTxnRef);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", VNPConfig.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(p.getVnpAmount()));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnpTxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnpTxnRef);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");

            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }

            String baseUrl = req.getRequestURL().toString().replace(req.getRequestURI(), req.getContextPath()) + "/";
            vnp_Params.put("vnp_ReturnUrl", VNPConfig.vnp_ReturnUrl(baseUrl));
            vnp_Params.put("vnp_IpAddr", VNPConfig.getIpAddress(req));

            vnp_Params.put("vnp_CreateDate", VNPConfig.formatVnpayDate(p.getCreatedAt()));
            vnp_Params.put("vnp_ExpireDate", VNPConfig.getFormatedExpireTime(p.getCreatedAt()));

            String vnp_SecureHash = VNPConfig.hashAllFields(vnp_Params);

            StringBuilder query = new StringBuilder();
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                    }
                }
            }

            String paymentUrl = VNPConfig.vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;
            resp.sendRedirect(paymentUrl);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/cart?error=system");
        }
    }
}