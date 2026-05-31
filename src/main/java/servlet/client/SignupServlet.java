package servlet.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import beans.common.Role;
import constants.RequestParamConstants;
import constants.SessionConstants;
import constants.SystemConstants;
import constants.ViewAttributeConstants;
import dto.user.UserCreateRequest;
import exception.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AuthenticationService;
import service.AuthenticationServiceImpl;

@WebServlet(name = "SignupServlet", value = "/signup")
public class SignupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final AuthenticationService authenticationService = new AuthenticationServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String username = request.getParameter(RequestParamConstants.User.USERNAME);
		final String password = request.getParameter(RequestParamConstants.User.PASSWORD);
		final String fullname = request.getParameter(RequestParamConstants.User.FULLNAME);
		final String email = request.getParameter(RequestParamConstants.User.EMAIL);

		final Map<String, String> values = new HashMap<>();
		values.put(RequestParamConstants.User.USERNAME, username);
		values.put(RequestParamConstants.User.FULLNAME, fullname);
		values.put(RequestParamConstants.User.EMAIL, email);

		final Map<String, String> errors = new HashMap<>();

		final Role roleObj = new Role();
		roleObj.setCode(SystemConstants.DEFAULT_ROLE_CODE);

		final UserCreateRequest dto = new UserCreateRequest.Builder().username(username).password(password).fullname(fullname).email(email).role(roleObj).build();

		try {
			authenticationService.signupUser(dto);
			request.getSession().setAttribute(SessionConstants.SIGNUP_SUCCESS, "Đăng ký thành công! Vui lòng đăng nhập.");
			response.sendRedirect(request.getContextPath() + "/signin");
		} catch (BusinessException e) {
			final Map<String, String> businessErrors = e.getErrors();
			if (businessErrors != null && !businessErrors.isEmpty()) {
				errors.putAll(businessErrors);
			} else {
				errors.put(SystemConstants.ERROR_GLOBAL, e.getMessage());
			}

			request.setAttribute(ViewAttributeConstants.VALUES, values);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
		} catch (Exception e) {
			errors.put(SystemConstants.ERROR_GLOBAL, "Đăng ký thất bại do sự cố hệ thống.");
			request.setAttribute(ViewAttributeConstants.VALUES, values);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
		}
	}
}
