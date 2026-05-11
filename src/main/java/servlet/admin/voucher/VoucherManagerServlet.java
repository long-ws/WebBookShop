package servlet.admin.voucher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.VoucherService;

@WebServlet(name = "VoucherManagerServlet", value = "/admin/voucherManager")
public class VoucherManagerServlet extends HttpServlet {
    private final VoucherService voucherService = new VoucherService();
    private static final int VOUCHERS_PER_PAGE = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int totalVouchers;
        try {
            totalVouchers = voucherService.count();
        } catch (Exception e) {
            e.printStackTrace();
            totalVouchers = 0;
        }

        int totalPages = (int) Math.ceil((double) totalVouchers / VOUCHERS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        int page = 1;
        String pageParam = request.getParameter("page");
        try {
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException ignored) {}

        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int offset = (page - 1) * VOUCHERS_PER_PAGE;

        List<Voucher> vouchers = new ArrayList<>();
        try {
            vouchers = voucherService.getOrderedPart(VOUCHERS_PER_PAGE, offset, "id", "DESC");
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("vouchers", vouchers);

        request.getRequestDispatcher("/WEB-INF/views/admin/voucherManagerView.jsp").forward(request, response);
    }
}