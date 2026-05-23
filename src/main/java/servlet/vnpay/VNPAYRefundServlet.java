package servlet.vnpay;

import beans.vnpay.Refund;
import com.google.gson.JsonObject;
import beans.vnpay.Payment;
import beans.User;
import com.google.gson.JsonParser;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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

            JsonObject vnp_Params = new JsonObject();
            vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
            vnp_Params.addProperty("vnp_Version", vnp_Version);
            vnp_Params.addProperty("vnp_Command", vnp_Command);
            vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
            vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.addProperty("vnp_Amount", vnp_Amount);
            vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);
            vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
            vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
            vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);
            String hash_Data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" +
                    vnp_TransactionType + "|" + vnp_TxnRef + "|" + vnp_Amount + "|" + vnp_TransactionNo + "|" +
                    vnp_TransactionDate + "|" + vnp_CreateBy + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo;

            String vnp_SecureHash = VNPConfig.hmacSHA512(VNPConfig.secretKey, hash_Data);
            vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

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
            JsonObject jsonResponse = JsonParser.parseString(res.toString()).getAsJsonObject();
            if (jsonResponse.has("vnp_ResponseCode") && !jsonResponse.get("vnp_ResponseCode").isJsonNull()) {
                r.setVnpResponseCode(jsonResponse.get("vnp_ResponseCode").getAsString());
            }
            if (jsonResponse.has("vnp_TransactionNo") && !jsonResponse.get("vnp_TransactionNo").isJsonNull()) {
                r.setVnpTransactionNo(jsonResponse.get("vnp_TransactionNo").getAsString());
            }
            if (jsonResponse.has("vnp_BankCode") && !jsonResponse.get("vnp_BankCode").isJsonNull()) {
                r.setBankCode(jsonResponse.get("vnp_BankCode").getAsString());
            }
            if (jsonResponse.has("vnp_PayDate") && !jsonResponse.get("vnp_PayDate").isJsonNull()) {
                String payDateStr = jsonResponse.get("vnp_PayDate").getAsString();
                if (!payDateStr.isEmpty()) {
                    r.setPayDate(VNPConfig.parseVnpayDate(payDateStr));
                }
            }
            if (jsonResponse.has("vnp_TransactionStatus") && !jsonResponse.get("vnp_TransactionStatus").isJsonNull()) {
                r.setVnpTransactionStatus(jsonResponse.get("vnp_TransactionStatus").getAsString());
            }
            boolean rs = refundService.updateRefundResult(r);
            if(!rs){
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