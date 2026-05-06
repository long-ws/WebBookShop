package servlet.client;

import java.io.IOException;

import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.UserService;
import utils.HashingUtils;

@WebServlet(name = "ChangePassword", value = "/changePassword")
public class ChangePasswordServlet extends HomeServlet {

	private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/changePasswordView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/signin");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String newPasswordAgain = request.getParameter("newPasswordAgain");

        boolean success = false;

        if (currentPassword != null && newPassword != null && newPasswordAgain != null) {
            String hashedCurrent = HashingUtils.hash(currentPassword);

            if (hashedCurrent.equals(user.getPassword()) && newPassword.equals(newPasswordAgain)) {
                String hashedNew = HashingUtils.hash(newPassword);
                try {
                    userService.changePassword(user.getId(), hashedNew);
                    user.setPassword(hashedNew); // Cập nhật luôn trong session
                    session.setAttribute("currentUser", user);
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (success) {
            request.setAttribute("successMessage", "Đổi mật khẩu thành công!");
        } else {
            request.setAttribute("errorMessage", "Đổi mật khẩu thất bại! Vui lòng kiểm tra lại thông tin.");
        }

        request.getRequestDispatcher("/WEB-INF/views/changePasswordView.jsp").forward(request, response);
    }
}
