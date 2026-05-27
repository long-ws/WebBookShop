package servlet.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import beans.User;
import constants.FormConstants;
import constants.RequestParamConstants;
import constants.SessionConstants;
import constants.ViewAttributeConstants;
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
        MessageHelper.cleanupFlashMessages(request.getSession());
        request.getRequestDispatcher("/WEB-INF/views/securityView.jsp").forward(request, response);
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
        String currentPassword = request.getParameter(RequestParamConstants.User.CURRENT_PASSWORD);
        String newPassword = request.getParameter(RequestParamConstants.User.NEW_PASSWORD);
        String confirmPassword = request.getParameter(RequestParamConstants.User.CONFIRM_PASSWORD);

        final Map<String, String> values = new HashMap<>();
        values.put(RequestParamConstants.User.CURRENT_PASSWORD, currentPassword);
        values.put(RequestParamConstants.User.NEW_PASSWORD, newPassword);
        values.put(RequestParamConstants.User.CONFIRM_PASSWORD, confirmPassword);

        final Map<String, String> errors = new HashMap<>();
        boolean success = false;

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest.Builder()
                .currentPassword(currentPassword)
                .newPassword(newPassword)
                .confirmPassword(confirmPassword)
                .build();
        
        try {
            authenticationService.changePassword(currentUser.getId(), changePasswordRequest);
            User updatedUser = userProfileService.getById(currentUser.getId());
            if (updatedUser != null) {
                request.getSession().setAttribute(SessionConstants.CURRENT_USER, updatedUser);
            }
            success = true;
            MessageHelper.setSuccessMessage(request.getSession(), "Đổi mật khẩu thành công!");
        } catch (BusinessException e) {
            final Map<String, String> businessErrors = e.getErrors();
            if (businessErrors != null && !businessErrors.isEmpty()) {
                errors.putAll(businessErrors);
            } else {
                errors.put(FormConstants.ERROR_GLOBAL, e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.put(FormConstants.ERROR_GLOBAL, "Đổi mật khẩu thất bại! Vui lòng kiểm tra lại thông tin.");
        }

        if (!errors.isEmpty()) {
            request.setAttribute(ViewAttributeConstants.VALUES, values);
            request.setAttribute(ViewAttributeConstants.ERRORS, errors);
        }

        request.getRequestDispatcher("/WEB-INF/views/securityView.jsp").forward(request, response);
    }
}
