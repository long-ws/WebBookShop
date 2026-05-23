package servlet.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import beans.User;
import constants.FormConstants;
import constants.RequestParamConstants;
import constants.SessionConstants;
import constants.ViewAttributeConstants;
import dto.user.SigninRequest;
import exception.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthenticationService;
import service.AuthenticationServiceImpl;

@WebServlet(name = "SigninServlet", value = "/signin")
public class SigninServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AuthenticationService authenticationService = new AuthenticationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/signinView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final String username = request.getParameter(RequestParamConstants.User.USERNAME);
        final String password = request.getParameter(RequestParamConstants.User.PASSWORD);

        final Map<String, String> values = new HashMap<>();
        values.put(RequestParamConstants.User.USERNAME, username);
        values.put(RequestParamConstants.User.PASSWORD, password);

        final Map<String, String> errors = new HashMap<>();
        User userFromServer = null;

        try {
            // Thay thế constructor cũ bằng Builder Pattern bất biến
            final SigninRequest signinRequest = new SigninRequest.Builder()
                    .username(username)
                    .password(password)
                    .build();

            userFromServer = authenticationService.authenticate(signinRequest);
        } catch (BusinessException e) {
            final Map<String, String> businessErrors = e.getErrors();
            if (businessErrors != null && !businessErrors.isEmpty()) {
                errors.putAll(businessErrors);
            } else {
                errors.put(FormConstants.ERROR_GLOBAL, e.getMessage());
            }
        }

        if (!errors.isEmpty() || userFromServer == null) {
            request.setAttribute(ViewAttributeConstants.VALUES, values);
            request.setAttribute(ViewAttributeConstants.ERRORS, errors);
            request.getRequestDispatcher("/WEB-INF/views/signinView.jsp").forward(request, response);
            return;
        }

        request.getSession().invalidate();
        final HttpSession newSession = request.getSession(true);
        newSession.setAttribute(SessionConstants.CURRENT_USER, userFromServer);

        final String role = userFromServer.getRole() != null ? userFromServer.getRole().getCode() : null;
        if (role != null) {
            newSession.setAttribute(SessionConstants.USER_ROLE, role);
        }

        response.sendRedirect(request.getContextPath() + "/");
    }
}