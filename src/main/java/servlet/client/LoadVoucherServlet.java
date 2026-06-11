package servlet.client;

import beans.User;
import constants.SessionConstants;
import dto.getUsableVouchers.VoucherDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.VoucherService;

import java.io.IOException;
import java.util.Map;

@WebServlet("/loadVoucher")
public class LoadVoucherServlet extends HttpServlet {
    private VoucherService voucherService = new VoucherService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SessionConstants.CURRENT_USER);

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            long userId = user.getId();
            long cartId = Long.parseLong(request.getParameter("cartId"));
            Map<VoucherDTO, Boolean> usableVouchers = voucherService.getUsableVouchers(userId, cartId);
            request.setAttribute("usableVouchers", usableVouchers);

            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            request.getRequestDispatcher("/WEB-INF/fragments/listVoucher.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}