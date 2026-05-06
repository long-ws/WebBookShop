package servlet.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;
import utils.HashingUtils;

@WebServlet(name = "SigninAdminServlet", value = "/admin/signin")
public class SigninAdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/signinAdminView.jsp").forward(request, response);
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
		} else {
			if (!username.equals(username.trim())) {
				violations.get("usernameViolations").add("Tên đăng nhập không có dấu cách ở hai đầu");
			}
			if (username.length() > 25) {
				violations.get("usernameViolations").add("Tên đăng nhập tối đa 25 ký tự");
			}
		}

		if (password == null || password.trim().isEmpty()) {
			violations.get("passwordViolations").add("Mật khẩu không được để trống");
		} else {
			if (!password.equals(password.trim())) {
				violations.get("passwordViolations").add("Mật khẩu không có dấu cách ở hai đầu");
			}
			if (password.length() > 32) {
				violations.get("passwordViolations").add("Mật khẩu tối đa 32 ký tự");
			}
		}

		User userFromServer = null;
		if (violations.get("usernameViolations").isEmpty()) {
			try {
				userFromServer = userService.getByUsername(username);
				if (userFromServer == null) {
					violations.get("usernameViolations").add("Tên đăng nhập không tồn tại");
				}
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("errorMessage", "Lỗi hệ thống, vui lòng thử lại sau");
			}
		}

		if (userFromServer != null && violations.get("passwordViolations").isEmpty()) {
			String hashedInputPassword = HashingUtils.hash(password);
			if (!hashedInputPassword.equals(userFromServer.getPassword())) {
				violations.get("passwordViolations").add("Mật khẩu không đúng");
			}
		}

		int totalViolations = violations.values().stream().mapToInt(List::size).sum();

		if (totalViolations == 0 && userFromServer != null) {
			if ("ADMIN".equals(userFromServer.getRole()) || "EMPLOYEE".equals(userFromServer.getRole())) {

				request.getSession().setAttribute("currentUser", userFromServer);
				response.sendRedirect(request.getContextPath() + "/admin");
				return;
			} else {
				request.setAttribute("errorMessage", "Người dùng không có quyền đăng nhập Admin");
			}
		}

		request.setAttribute("values", values);
		request.setAttribute("violations", violations);
		request.getRequestDispatcher("/WEB-INF/views/signinAdminView.jsp").forward(request, response);
	}
}
