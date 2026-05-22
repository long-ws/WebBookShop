package servlet.client;

import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderService;

import java.io.IOException;

@WebServlet("/rebuy")
public class RebuyServlet extends HttpServlet {
    private OrderService orderService = new OrderService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            long oId = Long.parseLong(request.getParameter("oId"));
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
            boolean isRebought = orderService.rebuy(u.getId(), oId);
            if (isRebought) {
                response.sendRedirect(request.getContextPath() + "/cart");
            } else {
                request.setAttribute("mess", "Lỗi khi mua lại đơn!");
                response.sendRedirect(request.getContextPath() + "/error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}