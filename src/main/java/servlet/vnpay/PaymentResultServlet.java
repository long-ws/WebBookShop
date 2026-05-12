package servlet.vnpay;

import beans.vnpay.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.PaymentService;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "PaymentResultServlet", urlPatterns = {"/vnpay/result"})
public class PaymentResultServlet extends HttpServlet {
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

        boolean isValidSignature = VNPConfig.verifySignature(fields, vnp_SecureHash);

        String vnp_TxnRef =  req.getParameter("vnp_TxnRef");
        Payment p = service.getInitPayment(vnp_TxnRef);
        if (p == null) {
            resp.sendRedirect(req.getContextPath() + "/error");
        }

        p.setVnpTxnRef(vnp_TxnRef);
        p.setVnpResponseCode(req.getParameter("vnp_ResponseCode"));
        p.setVnpTransactionNo(req.getParameter("vnp_TransactionNo"));
        p.setBankCode(req.getParameter("vnp_BankCode"));

        String amountRaw = req.getParameter("vnp_Amount");
        if (amountRaw != null) {
            p.setAmount((double) Long.parseLong(amountRaw) / 100);
        }

        p.setPayDate(VNPConfig.parseVnpayDate(req.getParameter("vnp_PayDate")));

        String message;
        boolean isSuccess = false;

        if (isValidSignature) {
            if ("00".equals(p.getVnpResponseCode())) {
                message = "Payment thành công!";
                isSuccess = true;
                p.setStatus(1);
            } else {
                message = "Payment thất bại " + p.getVnpResponseCode();
                p.setStatus(2);
            }
        } else {
            message = "Sai chữ ký!";
            p.setStatus(-1);
        }

        req.setAttribute("payment", p);
        req.setAttribute("isSuccess", isSuccess);
        req.setAttribute("message", message);

        req.getRequestDispatcher("/WEB-INF/views/vnpay/paymentResult.jsp").forward(req, resp);
    }
}
