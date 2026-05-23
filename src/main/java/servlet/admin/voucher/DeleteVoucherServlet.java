package servlet.admin.voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.VoucherDao;
import service.VoucherService;

import java.io.IOException;

@WebServlet("/admin/voucherManager/delete")
public class DeleteVoucherServlet extends HttpServlet {
    private final VoucherService service = new VoucherService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String currentPageParam = request.getParameter("currentPage");

        int currentPage = 1;
        if (currentPageParam != null && !currentPageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(currentPageParam);
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        try {
            if (idParam != null && !idParam.trim().isEmpty()) {
                long vId = Long.parseLong(idParam);
                boolean success = service.deleteVoucher(vId);

                if (success) {
                    request.getSession().setAttribute("successMessage", "Xóa voucher thành công!");
                } else {
                    request.getSession().setAttribute("errorMessage", "Xóa voucher thất bại hoặc voucher không tồn tại!");
                }
            } else {
                request.getSession().setAttribute("errorMessage", "ID không hợp lệ!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Xảy ra lỗi trong quá trình xóa!");
        }

        response.sendRedirect(request.getContextPath() + "/admin/voucherManager/view?page=" + currentPage);
    }
}
