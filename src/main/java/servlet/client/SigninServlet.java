package servlet.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.User;
import constants.SessionConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.UserService;
import service.UserServiceImpl;
import utils.HashingUtils;

@WebServlet(name = "SigninServlet", value = "/signin")
public class SigninServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/signinView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		Map<String, String> values = new HashMap<>();
		values.put("username", username);
		values.put("password", password);

		Map<String, List<String>> violations = new HashMap<>();
		violations.put("usernameViolations", new ArrayList<>());
		violations.put("passwordViolations", new ArrayList<>());

		if (username == null || username.trim().isEmpty()) {
			violations.get("usernameViolations").add("Tên đăng nhập không được để trống");
		}

		if (password == null || password.trim().isEmpty()) {
			violations.get("passwordViolations").add("Mật khẩu không được để trống");
		}

		User userFromServer = null;
		if (violations.get("usernameViolations").isEmpty() && violations.get("passwordViolations").isEmpty()) {
			try {
				userFromServer = userService.getUserEntityByUsername(username);

				if (userFromServer == null) {
					violations.get("usernameViolations").add("Tên đăng nhập hoặc mật khẩu không đúng");
				} else {
					if (!HashingUtils.verify(password, userFromServer.getPasswordHash())) {
						violations.get("passwordViolations").add("Mật khẩu không đúng");
						userFromServer = null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				violations.get("usernameViolations").add("Lỗi hệ thống");
			}
		}

		boolean hasErrors = false;
		for (List<String> list : violations.values()) {
			if (!list.isEmpty()) {
				hasErrors = true;
				break;
			}
		}

		if (!hasErrors) {
			HttpSession session = request.getSession(true);

			session.setAttribute(SessionConstants.CURRENT_USER, userFromServer);

			String role = userFromServer.getRole() != null ? userFromServer.getRole().getCode() : null;
			if (role != null) {
				session.setAttribute(SessionConstants.USER_ROLE, role);
			}

			response.sendRedirect(request.getContextPath() + "/");
		} else {
			request.setAttribute("values", values);
			request.setAttribute("violations", violations);
			request.getRequestDispatcher("/WEB-INF/views/signinView.jsp").forward(request, response);
		}
	}
}