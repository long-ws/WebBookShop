package servlet.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import beans.User;
import config.security.SecurityConfig;
import constants.RequestParamConstants;
import constants.SessionConstants;
import constants.ViewAttributeConstants;
import constants.system.SystemKeys;
import dto.user.SigninRequest;
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

@WebServlet(name = "SigninServlet", value = "/signin")
public class SigninServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final AuthenticationService authenticationService = new AuthenticationServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/signinView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String username = request.getParameter(RequestParamConstants.User.USERNAME);
		final String password = request.getParameter(RequestParamConstants.User.PASSWORD);

		final Map<String, String> values = new HashMap<>();
		values.put(RequestParamConstants.User.USERNAME, username);

		final Map<String, String> errors = new HashMap<>();
		User userFromServer = null;

		try {
			final SigninRequest signinRequest = new SigninRequest.Builder().username(username).password(password).build();

			userFromServer = authenticationService.authenticate(signinRequest);
		} catch (BusinessException e) {
			final Map<String, String> businessErrors = e.getErrors();
			if (businessErrors != null && !businessErrors.isEmpty()) {
				errors.putAll(businessErrors);
			} else {
				errors.put(SystemKeys.ERROR_GLOBAL, e.getMessage());
			}
		} catch (Exception e) {
			errors.put(SystemKeys.ERROR_GLOBAL, "Đăng nhập thất bại do sự cố hệ thống.");
		}

		if (!errors.isEmpty() || userFromServer == null) {
			request.setAttribute(ViewAttributeConstants.VALUES, values);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/WEB-INF/views/signinView.jsp").forward(request, response);
			return;
		}

		final String roleCode = userFromServer.getRole() != null ? userFromServer.getRole().getCode() : null;
		final boolean isSuperAdmin = SecurityConfig.isSuperAdminUsername(userFromServer.getUsername());

		if (!isSuperAdmin && !userFromServer.isEmailVerified()) {
			final String email = userFromServer.getEmail();
			if (email == null || email.isBlank()) {
				errors.put(SystemKeys.ERROR_GLOBAL, "Email chưa được xác thực.");
				request.setAttribute(ViewAttributeConstants.VALUES, values);
				request.setAttribute(ViewAttributeConstants.ERRORS, errors);
				request.getRequestDispatcher("/WEB-INF/views/signinView.jsp").forward(request, response);
				return;
			}

			HttpSession oldSession = request.getSession(false);
			if (oldSession != null) {
				oldSession.invalidate();
			}
			final HttpSession newSession = request.getSession(true);
			newSession.setAttribute(SessionConstants.PENDING_VERIFICATION_USER_ID, userFromServer.getId());
			newSession.setAttribute(SessionConstants.PENDING_VERIFICATION_EMAIL, email);
			MessageHelper.setErrorMessage(newSession, "Email chưa được xác thực. Vui lòng kiểm tra hộp thư để xác thực.");
			response.sendRedirect(request.getContextPath() + "/verify-email");
			return;
		}

		HttpSession oldSession = request.getSession(false);
		if (oldSession != null) {
			oldSession.invalidate();
		}
		final HttpSession newSession = request.getSession(true);
		newSession.setAttribute(SessionConstants.CURRENT_USER, userFromServer);

		if (roleCode != null) {
			newSession.setAttribute(SessionConstants.USER_ROLE, roleCode);
		}
		if (isSuperAdmin) {
			newSession.setAttribute(SessionConstants.IS_SUPER_ADMIN, Boolean.TRUE);
		}

		response.sendRedirect(request.getContextPath() + "/");
	}
}
