package servlet.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import beans.User;
import constants.RequestParamConstants;
import constants.SessionConstants;
import constants.ViewAttributeConstants;
import constants.system.SystemKeys;
import dto.user.ChangePasswordRequest;
import exception.BusinessException;
import helpers.MessageHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthenticationService;
import service.AuthenticationServiceImpl;
import service.UserProfileService;
import service.UserProfileServiceImpl;

@WebServlet(name = "SecurityServlet", value = "/security")
public class SecurityServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AuthenticationService authenticationService = new AuthenticationServiceImpl();
    private final UserProfileService userProfileService = new UserProfileServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            MessageHelper.cleanupFlashMessages(session);
        }
        request.getRequestDispatcher("/WEB-INF/views/securityView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(SessionConstants.CURRENT_USER) : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/signin");
            return;
        }

        handleChangePassword(request, response, currentUser);
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, User currentUser) throws ServletException, IOException {
        String currentPassword = request.getParameter(RequestParamConstants.User.CURRENT_PASSWORD);
        String newPassword = request.getParameter(RequestParamConstants.User.NEW_PASSWORD);
        String confirmPassword = request.getParameter(RequestParamConstants.User.CONFIRM_PASSWORD);

        final Map<String, String> errors = new HashMap<>();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest.Builder()
                .currentPassword(currentPassword)
                .newPassword(newPassword)
                .confirmPassword(confirmPassword)
                .build();
        
        try {
            authenticationService.changePassword(currentUser.getId(), changePasswordRequest);
            User updatedUser = userProfileService.getById(currentUser.getId());
            if (updatedUser != null) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.setAttribute(SessionConstants.CURRENT_USER, updatedUser);
                }
            }
            MessageHelper.setSuccessMessage(request.getSession(), "Đổi mật khẩu thành công!");
            response.sendRedirect(request.getContextPath() + "/security");
            return;
        } catch (BusinessException e) {
            final Map<String, String> businessErrors = e.getErrors();
            if (businessErrors != null && !businessErrors.isEmpty()) {
                errors.putAll(businessErrors);
            } else {
                errors.put(SystemKeys.ERROR_GLOBAL, e.getMessage());
            }
        } catch (Exception e) {
            errors.put(SystemKeys.ERROR_GLOBAL, "Đổi mật khẩu thất bại! Vui lòng kiểm tra lại thông tin.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute(ViewAttributeConstants.ERRORS, errors);
        }

        request.getRequestDispatcher("/WEB-INF/views/securityView.jsp").forward(request, response);
    }
}
