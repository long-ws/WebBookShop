package servlet.client;

import java.io.IOException;

import beans.User;
import constants.SessionConstants;
import dto.user.ChangePasswordRequest;
import exception.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthenticationService;
import service.AuthenticationServiceImpl;

@WebServlet(name = "ChangePassword", value = "/changePassword")
public class ChangePasswordServlet extends HomeServlet {

    private static final long serialVersionUID = 1L;
    private final AuthenticationService authenticationService = new AuthenticationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/changePasswordView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SessionConstants.CURRENT_USER);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/signin");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String newPasswordAgain = request.getParameter("newPasswordAgain");

        boolean success = false;

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest.Builder()
                .currentPassword(currentPassword)
                .newPassword(newPassword)
                .confirmPassword(newPasswordAgain)
                .build();
        
        try {
            authenticationService.changePassword(user.getId(), changePasswordRequest);
            success = true;
        } catch (BusinessException e) {
            request.setAttribute(SessionConstants.ERROR_MESSAGE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            request.setAttribute(SessionConstants.SUCCESS_MESSAGE, "Đổi mật khẩu thành công!");
        } else if (request.getAttribute(SessionConstants.ERROR_MESSAGE) == null) {
            request.setAttribute(SessionConstants.ERROR_MESSAGE, "Đổi mật khẩu thất bại! Vui lòng kiểm tra lại thông tin.");
        }

        request.getRequestDispatcher("/WEB-INF/views/changePasswordView.jsp").forward(request, response);
    }
}
