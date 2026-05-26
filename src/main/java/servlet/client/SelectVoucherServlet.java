package servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/voucher-cleaner")
public class SelectVoucherServlet extends HttpServlet {

    // 1. Xử lý GET: Trả về danh sách Voucher dạng JSON
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        if ("getActiveVouchers".equals(action)) {
            // Thực hiện truy vấn DB lấy các voucher thỏa mãn: status = active VÀ currentDate nằm giữa startDate và endDate
            // Giả lập mẫu chuỗi JSON gửi về client:
            String jsonResponse = "{"
                    + "\"discountVouchers\": ["
                    + "  {\"id\": \"1\", \"code\": \"ALLPRODUCT50K\", \"calculationMethod\": 1, \"value\": 50000, \"minPurchase\": 200000, \"applyTo\": 1},"
                    + "  {\"id\": \"2\", \"code\": \"TECH10\", \"calculationMethod\": 0, \"value\": 10, \"maxValue\": 30000, \"minPurchase\": 150000, \"applyTo\": 2},"
                    + "  {\"id\": \"3\", \"code\": \"FASHION20\", \"calculationMethod\": 1, \"value\": 20000, \"minPurchase\": 100000, \"applyTo\": 2}"
                    + "],"
                    + "\"shipVouchers\": ["
                    + "  {\"id\": \"4\", \"code\": \"FREESHIPMAX\", \"calculationMethod\": 1, \"value\": 30000, \"minPurchase\": 50000, \"applyTo\": 3}"
                    + "]"
                    + "}";

            out.print(jsonResponse);
            out.flush();
        }
    }

    // 2. Xử lý POST: Nhận ID Voucher để tính ra số tiền thực tế được giảm
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        if ("calculateDiscount".equals(action)) {
            String voucherId = request.getParameter("voucherId");
            String shipVoucherId = request.getParameter("shipVoucherId");
            long tempTotal = Long.parseLong(request.getParameter("tempTotal"));

            long discountAmount = 0;
            long shipDiscountAmount = 0;

            // --- ĐOẠN XỬ LÝ LOGIC TRÊN SERVER ---
            // Truy vấn DB dựa trên voucherId và shipVoucherId của khách hàng gửi lên:
            // Thí dụ tìm thấy voucherId = 1 giảm giá thẳng 50.000đ:
            if ("1".equals(voucherId) && tempTotal >= 200000) {
                discountAmount = 50000;
            } else if ("2".equals(voucherId) && tempTotal >= 150000) {
                // Giảm 10%
                discountAmount = (long) (tempTotal * 0.1);
                if(discountAmount > 30000) discountAmount = 30000; // chặn max value
            }

            // Thí dụ tìm thấy shipVoucherId = 4 (Freeship tối đa 30k)
            if ("4".equals(shipVoucherId)) {
                shipDiscountAmount = 30000;
            }

            // Trả về JSON chứa kết quả giảm giá thực tế cho Client
            String jsonResult = "{"
                    + "\"discountAmount\": " + discountAmount + ","
                    + "\"shipDiscountAmount\": " + shipDiscountAmount
                    + "}";

            out.print(jsonResult);
            out.flush();
        }
    }
}