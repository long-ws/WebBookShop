package servlet.client;

import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderService;

import java.io.IOException;

@WebServlet("/cancelOrder")
public class CancelOrderServlet extends HttpServlet {
     private OrderService orderService = new OrderService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            long oId = Long.parseLong(request.getParameter("oId"));
            long pId = Long.parseLong(request.getParameter("pId"));
            User u = (User) request.getSession().getAttribute(constants.SessionConstants.CURRENT_USER);
            boolean checkUser;
            if(u != null){
                checkUser = orderService.checkUser(oId, u.getId());
            }else{
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            if(!checkUser){
                response.sendRedirect(request.getContextPath() + "/error");
                return;
            }
            boolean isCancelled = orderService.cancelOrder(oId, pId);
            if (!isCancelled) {
                response.sendRedirect(request.getContextPath() + "/error");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/orderDetail?id=" + oId);
        }catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}