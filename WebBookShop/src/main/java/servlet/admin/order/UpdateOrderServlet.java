package servlet.admin.order;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderService;

@WebServlet(name = "UpdateOrderServlet", value = "/admin/orderManager/update")
public class UpdateOrderServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/admin/orderManager");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long id;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "ID đơn hàng không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/admin/orderManager");
            return;
        }

        String action = request.getParameter("action");
        String errorMessage = "Đã có lỗi khi xử lý đơn hàng!";

        try {
            if ("CONFIRM".equals(action)) {
                orderService.confirm(id);
                request.getSession().setAttribute(
                        "successMessage",
                        "Đã xác nhận giao đơn hàng #" + id + " thành công!"
                );

            } else if ("CANCEL".equals(action)) {
                orderService.cancel(id);
                request.getSession().setAttribute(
                        "successMessage",
                        "Đã hủy đơn hàng #" + id + " thành công!"
                );

            } else if ("RESET".equals(action)) {
                orderService.reset(id);
                request.getSession().setAttribute(
                        "successMessage",
                        "Đã đặt lại trạng thái đang giao cho đơn hàng #" + id + " thành công!"
                );

            } else {
                request.getSession().setAttribute(
                        "errorMessage",
                        "Hành động không hợp lệ!"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", errorMessage);
        }

        response.sendRedirect(request.getContextPath() + "/admin/orderManager");
    }
}
