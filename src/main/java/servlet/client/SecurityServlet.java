package servlet.client;

import java.io.IOException;

import beans.User;
import constants.SessionConstants;
import dto.user.ChangePasswordRequest;
import exception.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.PasswordService;
import service.PasswordServiceImpl;
import service.UserProfileService;
import service.UserProfileServiceImpl;

@WebServlet(name = "SecurityServlet", value = "/security")
public class SecurityServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final PasswordService passwordService = new PasswordServiceImpl();
    private final UserProfileService userProfileService = new UserProfileServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/views/securityView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute(SessionConstants.CURRENT_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/signin");
            return;
        }

        handleChangePassword(request, response, currentUser);
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, User currentUser) throws ServletException, IOException {
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String newPasswordAgain = request.getParameter("newPasswordAgain");

        boolean success = false;

        // Thay thế constructor cũ bằng Builder Pattern bất biến
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest.Builder()
                .currentPassword(currentPassword)
                .newPassword(newPassword)
                .confirmPassword(newPasswordAgain)
                .build();
        
        try {
            passwordService.changePassword(currentUser.getId(), changePasswordRequest);
            User updatedUser = userProfileService.getById(currentUser.getId());
            if (updatedUser != null) {
                request.getSession().setAttribute(SessionConstants.CURRENT_USER, updatedUser);
            }
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

        request.getRequestDispatcher("WEB-INF/views/securityView.jsp").forward(request, response);
    }
}