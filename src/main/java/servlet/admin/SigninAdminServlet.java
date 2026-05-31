package servlet.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import beans.User;
import beans.common.Permission;
import constants.RequestParamConstants;
import constants.SessionConstants;
import constants.SystemConstants;
import constants.ViewAttributeConstants;
import dto.user.AdminSigninRequest;
import exception.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthenticationService;
import service.AuthenticationServiceImpl;
import service.AuthorizationService;
import service.AuthorizationServiceImpl;

@WebServlet(name = "SigninAdminServlet", value = "/admin/signin")
public class SigninAdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final AuthenticationService authenticationService = new AuthenticationServiceImpl();
	private final AuthorizationService authorizationService = new AuthorizationServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/signinAdminView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String username = request.getParameter(RequestParamConstants.Auth.USERNAME);
		final String password = request.getParameter(RequestParamConstants.Auth.PASSWORD);

		final Map<String, String> values = new HashMap<>();
		values.put(RequestParamConstants.Auth.USERNAME, username);

		final Map<String, String> errors = new HashMap<>();

		User userFromServer = null;

		try {
			final AdminSigninRequest adminSigninRequest = new AdminSigninRequest.Builder().username(username).password(password).build();

			userFromServer = authenticationService.authenticateAdmin(adminSigninRequest);
		} catch (BusinessException e) {
			final Map<String, String> businessErrors = e.getErrors();
			if (businessErrors != null && !businessErrors.isEmpty()) {
				errors.putAll(businessErrors);
			} else {
				errors.put(SystemConstants.ERROR_GLOBAL, e.getMessage());
			}
		}

		if (!errors.isEmpty() || userFromServer == null) {
			request.setAttribute(ViewAttributeConstants.VALUES, values);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/WEB-INF/views/signinAdminView.jsp").forward(request, response);
			return;
		}

		HttpSession oldSession = request.getSession(false);
		if (oldSession != null) {
			oldSession.invalidate();
		}
		final HttpSession newSession = request.getSession(true);
		newSession.setAttribute(SessionConstants.CURRENT_USER, userFromServer);

		final boolean isSuperAdmin = SystemConstants.Security.isSuperAdminUsername(userFromServer.getUsername());
		newSession.setAttribute(SessionConstants.IS_SUPER_ADMIN, isSuperAdmin);

		final Set<String> permissionCodes = new HashSet<>();
		if (!isSuperAdmin) {
			for (Permission permission : authorizationService.getPermissionsByUserId(userFromServer.getId())) {
				if (permission != null && permission.getCode() != null && !permission.getCode().isBlank()) {
					permissionCodes.add(permission.getCode());
				}
			}
		}
		newSession.setAttribute(SessionConstants.USER_PERMISSIONS, permissionCodes);

		response.sendRedirect(request.getContextPath() + "/admin");
	}
}
