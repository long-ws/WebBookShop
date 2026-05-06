package servlet.admin.user;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;

@WebServlet(name = "DeleteUserServlet", value = "/admin/userManager/delete")
public class DeleteUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long id;

        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/userManager");
            return;
        }

        try {
            userService.delete(id);

            request.getSession().setAttribute(
                "successMessage",
                "Xóa người dùng #" + id + " thành công!"
            );

        } catch (Exception e) {

            e.printStackTrace();

            String errorMessage = "Xóa người dùng #" + id + " thất bại!";

            String dbMessage = e.getMessage().toLowerCase();

            if (dbMessage.contains("foreign key")) {
                errorMessage = "Không thể xóa người dùng id: " + id + " vì đang được sử dụng";
            } else if (dbMessage.contains("no rows affected")) {
                errorMessage = "Người dùng " + id + " không tồn tại";
            }

            request.getSession().setAttribute("errorMessage", errorMessage);
        }

        response.sendRedirect(request.getContextPath() + "/admin/userManager");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/admin/userManager");
    }
}