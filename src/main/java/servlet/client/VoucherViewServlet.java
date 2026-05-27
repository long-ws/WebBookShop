package servlet.client;

import beans.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.VoucherService;

import java.io.IOException;
import java.util.List;

@WebServlet("/vouchers")
public class VoucherViewServlet extends HttpServlet {
    private final VoucherService voucherService = new VoucherService();
    private static final int RECORDS_PER_PAGE = 6;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int page = 1;
            if (request.getParameter("page") != null) {
                try {
                    page = Integer.parseInt(request.getParameter("page"));
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            String applyToParam = request.getParameter("applyTo");
            Integer applyTo = null;
            if (applyToParam != null && !applyToParam.trim().isEmpty()) {
                try {
                    applyTo = Integer.parseInt(applyToParam);
                } catch (NumberFormatException e) {
                    applyTo = null;
                }
            }
            int offset = (page - 1) * RECORDS_PER_PAGE;
            List<Voucher> listVouchers = voucherService.getVouchersForUser(applyTo, offset, RECORDS_PER_PAGE);
            int totalRecords = voucherService.getTotalVouchersCountForUser(applyTo);
            int totalPages = (int) Math.ceil((double) totalRecords / RECORDS_PER_PAGE);

            request.setAttribute("vouchers", listVouchers);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải danh sách Voucher!");
        }

        request.getRequestDispatcher("/WEB-INF/views/voucherView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
