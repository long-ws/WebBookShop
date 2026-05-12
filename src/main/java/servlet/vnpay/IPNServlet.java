package servlet.vnpay;

import beans.vnpay.Payment;
import com.google.gson.JsonObject; // Đạt nhớ thêm thư viện Gson vào project nhé
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.PaymentService;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "IPNServlet", urlPatterns = {"/vnpay/ipn"})
public class IPNServlet extends HttpServlet {
    PaymentService service = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = req.getParameter(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = req.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");


        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        try {

            boolean isValidSignature = VNPConfig.verifySignature(fields, vnp_SecureHash);
            if (!isValidSignature) {
                jsonResponse.addProperty("RspCode", "97");
                jsonResponse.addProperty("Message", "Invalid Checksum");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            String vnp_TxnRef = req.getParameter("vnp_TxnRef");
            long vnp_Amount = Long.parseLong(req.getParameter("vnp_Amount"));
            String responseCode = req.getParameter("vnp_ResponseCode");


            Payment p = service.getInitPayment(vnp_TxnRef);


            if (p == null) {
                jsonResponse.addProperty("RspCode", "01");
                jsonResponse.addProperty("Message", "Order not found");
            }

            else if ((long)(p.getAmount() * 100) != vnp_Amount) {
                jsonResponse.addProperty("RspCode", "04");
                jsonResponse.addProperty("Message", "Invalid Amount");
            }
            else if (p.getStatus() == 1) {
                jsonResponse.addProperty("RspCode", "02");
                jsonResponse.addProperty("Message", "Order already confirmed");
            }

            else {
                p.setVnpResponseCode(responseCode);
                p.setVnpTransactionNo(req.getParameter("vnp_TransactionNo"));
                p.setBankCode(req.getParameter("vnp_BankCode"));
                p.setPayDate(VNPConfig.parseVnpayDate(req.getParameter("vnp_PayDate")));

                if ("00".equals(responseCode)) {
                    p.setStatus(1);
                } else {
                    p.setStatus(2);
                }

                service.updatePaymentResult(p);

                jsonResponse.addProperty("RspCode", "00");
                jsonResponse.addProperty("Message", "Confirm Success");
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("RspCode", "99");
            jsonResponse.addProperty("Message", "Unknown error");
        }

        resp.getWriter().write(jsonResponse.toString());
    }
}