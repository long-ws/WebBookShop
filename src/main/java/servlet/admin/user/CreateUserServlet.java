package servlet.admin.user;

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

@WebServlet(name = "CreateUserServlet", value = "/admin/userManager/create")
public class CreateUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/createUserView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		User user = new User();
		Map<String, List<String>> violations = new HashMap<>();

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String fullname = request.getParameter("fullname");
		String email = request.getParameter("email");
		String phoneNumber = request.getParameter("phoneNumber");
		String genderStr = request.getParameter("gender");
		String address = request.getParameter("address");
		String role = request.getParameter("role");

		user.setUsername(username);
		user.setPassword(password);
		user.setFullname(fullname);
		user.setEmail(email);
		user.setPhoneNumber(phoneNumber);
		user.setAddress(address);
		user.setRole(role);

		Integer gender = null;
		try {
			gender = Integer.parseInt(genderStr);
			user.setGender(gender);
		} catch (Exception e) {
			addViolation(violations, "genderViolations", "Giới tính không hợp lệ");
		}

		if (isBlank(username)) {
			addViolation(violations, "usernameViolations", "Tên đăng nhập không để trống");
		} else if (username.length() > 25) {
			addViolation(violations, "usernameViolations", "Tên đăng nhập tối đa 25 ký tự");
		} else if (userService.getByUsername(username) != null) {
			addViolation(violations, "usernameViolations", "Tên đăng nhập đã tồn tại");
		}

		if (isBlank(password)) {
			addViolation(violations, "passwordViolations", "Mật khẩu không để trống");
		} else if (password.length() > 32) {
			addViolation(violations, "passwordViolations", "Mật khẩu tối đa 32 ký tự");
		}

		if (isBlank(fullname)) {
			addViolation(violations, "fullnameViolations", "Họ tên không để trống");
		}

		if (isBlank(email)) {
			addViolation(violations, "emailViolations", "Email không để trống");
		} else if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
			addViolation(violations, "emailViolations", "Email không đúng định dạng");
		} else if (userService.getByEmail(email) != null) {
			addViolation(violations, "emailViolations", "Email đã tồn tại");
		}

		if (isBlank(phoneNumber)) {
			addViolation(violations, "phoneNumberViolations", "Số điện thoại không để trống");
		} else if (!phoneNumber.matches("^\\d{10,11}$")) {
			addViolation(violations, "phoneNumberViolations", "Số điện thoại không hợp lệ");
		} else if (userService.getByPhoneNumber(phoneNumber) != null) {
			addViolation(violations, "phoneNumberViolations", "Số điện thoại đã tồn tại");
		}

		if (gender == null) {
			addViolation(violations, "genderViolations", "Vui lòng chọn giới tính");
		}

		if (isBlank(address)) {
			addViolation(violations, "addressViolations", "Địa chỉ không để trống");
		}

		if (isBlank(role)) {
			addViolation(violations, "roleViolations", "Vai trò không để trống");
		}

		if (violations.isEmpty()) {
			try {
				user.setPassword(HashingUtils.hash(password));
				userService.insert(user);
				request.setAttribute("successMessage", "Thêm thành công!");
			} catch (Exception e) {
				request.setAttribute("errorMessage", "Thêm thất bại!");
				request.setAttribute("user", user);
			}
		} else {
			request.setAttribute("user", user);
			request.setAttribute("violations", violations);
		}

		request.getRequestDispatcher("/WEB-INF/views/createUserView.jsp").forward(request, response);
	}

	private boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	private void addViolation(Map<String, List<String>> map, String key, String message) {
		List<String> list = map.get(key);
		if (list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		list.add(message);
	}
}
