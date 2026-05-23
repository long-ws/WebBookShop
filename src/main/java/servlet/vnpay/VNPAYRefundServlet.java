package servlet.vnpay;

import beans.vnpay.Refund;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import beans.vnpay.Payment;
import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderService;
import service.PaymentService;
import service.RefundService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

@WebServlet("/vnpay/refund")
public class VNPAYRefundServlet extends HttpServlet {
    private final PaymentService paymentService = new PaymentService();
    private final OrderService orderService = new OrderService();
    private final RefundService refundService = new RefundService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            long oId = Long.parseLong(request.getParameter("oId"));
            long pId = Long.parseLong(request.getParameter("pId"));

            User u = (User) request.getSession().getAttribute(constants.SessionConstants.CURRENT_USER);
            boolean checkUser;
            if (u != null) {
                checkUser = orderService.checkUser(oId, u.getId());
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            if (!checkUser) {
                response.sendRedirect(request.getContextPath() + "/error");
                return;
            }
            Payment p = paymentService.getPaymentById(pId);
            if (p == null || p.getStatus() != 1) {
                response.sendRedirect(request.getContextPath() + "/error");
                return;
            }
            String vnp_RequestId = String.valueOf(System.currentTimeMillis());
            String vnp_Version = "2.1.0";
            String vnp_Command = "refund";
            String vnp_TmnCode = VNPConfig.vnp_TmnCode;
            String vnp_TransactionType = "02";
            String vnp_TxnRef = p.getVnpTxnRef();
            String vnp_Amount = String.valueOf(p.getVnpAmount());
            String vnp_OrderInfo = "Hoan tien don hang: " + vnp_TxnRef;
            String vnp_TransactionNo = p.getVnpTransactionNo();
            String vnp_TransactionDate = VNPConfig.formatVnpayDate(p.getCreatedAt());
            String vnp_CreateBy = String.valueOf(u.getId());

            Timestamp now = new Timestamp(System.currentTimeMillis());
            String vnp_CreateDate = VNPConfig.formatVnpayDate(now);
            String vnp_IpAddr = VNPConfig.getIpAddress(request);

            Refund r = new Refund();
            r.setOrderId(oId);
            r.setUserId(u.getId());
            r.setCreateAt(now);
            r.setVnpRequestId(vnp_RequestId);
            r.setVnpTransactionType(vnp_TransactionType);
            r.setVnpTxnRef(vnp_TxnRef);
            r.setAmount(p.getAmount());
            long rId = refundService.createRefund(r);
            if(rId == -1){
                response.sendRedirect(request.getContextPath() + "/error");
                return;
            }
            r.setId(rId);

            Map<String, String> paramsMap = new LinkedHashMap<>();
            paramsMap.put("vnp_RequestId", vnp_RequestId);
            paramsMap.put("vnp_Version", vnp_Version);
            paramsMap.put("vnp_Command", vnp_Command);
            paramsMap.put("vnp_TmnCode", vnp_TmnCode);
            paramsMap.put("vnp_TransactionType", vnp_TransactionType);
            paramsMap.put("vnp_TxnRef", vnp_TxnRef);
            paramsMap.put("vnp_Amount", vnp_Amount);
            paramsMap.put("vnp_TransactionNo", vnp_TransactionNo);
            paramsMap.put("vnp_TransactionDate", vnp_TransactionDate);
            paramsMap.put("vnp_CreateBy", vnp_CreateBy);
            paramsMap.put("vnp_CreateDate", vnp_CreateDate);
            paramsMap.put("vnp_IpAddr", vnp_IpAddr);
            paramsMap.put("vnp_OrderInfo", vnp_OrderInfo);
            String vnp_SecureHash = VNPConfig.hashRefundFields(paramsMap);
            paramsMap.put("vnp_SecureHash", vnp_SecureHash);

            JsonObject vnp_Params = new Gson().toJsonTree(paramsMap).getAsJsonObject();

            URL url = new URL(VNPConfig.vnp_ApiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(vnp_Params.toString());
                wr.flush();
            }
            StringBuilder res = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String output;
                while ((output = in.readLine()) != null) {
                    res.append(output);
                }
            }
            Map<String, String> responseData = VNPConfig.parseJsonToMap(res.toString());

            String responseCode = responseData.get("vnp_ResponseCode");
            String transactionNo = responseData.get("vnp_TransactionNo");
            String bankCode = responseData.get("vnp_BankCode");
            String payDateStr = responseData.get("vnp_PayDate");
            String transactionStatus = responseData.get("vnp_TransactionStatus");
            if (responseCode != null && !responseCode.isEmpty()) {
                r.setVnpResponseCode(responseCode);
            }
            if (transactionNo != null && !transactionNo.isEmpty()) {
                r.setVnpTransactionNo(transactionNo);
            }
            if (bankCode != null && !bankCode.isEmpty()) {
                r.setBankCode(bankCode);
            }
            if (payDateStr != null && !payDateStr.isEmpty()) {
                r.setPayDate(VNPConfig.parseVnpayDate(payDateStr));
            }
            if (transactionStatus != null && !transactionStatus.isEmpty()) {
                r.setVnpTransactionStatus(transactionStatus);
            }
            boolean rs = refundService.updateRefundResult(r);
            if(!rs || !"00".equals(responseCode)){
                response.sendRedirect(request.getContextPath() + "/error");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/orderDetail?id=" + oId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}