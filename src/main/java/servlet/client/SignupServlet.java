package servlet.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;
import utils.HashingUtils;

@WebServlet(name = "SignupServlet", value = "/signup")
public class SignupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String fullname = request.getParameter("fullname");
		String email = request.getParameter("email");
		String phoneNumber = request.getParameter("phoneNumber");
		String genderStr = request.getParameter("gender");
		String address = request.getParameter("address");
		String policy = request.getParameter("policy");

		Map<String, String> errors = new HashMap<>();
		Map<String, String> values = new HashMap<>();
		values.put("username", username);
		values.put("fullname", fullname);
		values.put("email", email);
		values.put("phoneNumber", phoneNumber);
		values.put("gender", genderStr);
		values.put("address", address);

		if (username == null || username.trim().isEmpty()) {
			errors.put("username", "Tên đăng nhập không được để trống");
		} else if (username.length() > 25) {
			errors.put("username", "Tên đăng nhập tối đa 25 ký tự");
		}

		if (password == null || password.trim().isEmpty()) {
			errors.put("password", "Mật khẩu không được để trống");
		} else if (password.length() > 32) {
			errors.put("password", "Mật khẩu tối đa 32 ký tự");
		}

		if (fullname == null || fullname.trim().isEmpty()) {
			errors.put("fullname", "Họ và tên không được để trống");
		}

		if (email == null || !email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
			errors.put("email", "Email không hợp lệ");
		}

		if (phoneNumber == null || !phoneNumber.matches("^\\d{10,11}$")) {
			errors.put("phoneNumber", "Số điện thoại không hợp lệ");
		}

		int gender = 0;
		if (genderStr == null) {
			errors.put("gender", "Vui lòng chọn giới tính");
		} else {
			try {
				gender = Integer.parseInt(genderStr);
			} catch (NumberFormatException e) {
				errors.put("gender", "Giới tính không hợp lệ");
			}
		}

		if (address == null || address.trim().isEmpty()) {
			errors.put("address", "Địa chỉ không được để trống");
		}

		if (policy == null) {
			errors.put("policy", "Bạn phải đồng ý với chính sách");
		}

		if (!errors.isEmpty()) {
			request.setAttribute("errors", errors);
			request.setAttribute("values", values);
			request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
			return;
		}

		try {
			User user = new User();
			user.setUsername(username);
			user.setPassword(HashingUtils.hash(password));
			user.setFullname(fullname);
			user.setEmail(email);
			user.setPhoneNumber(phoneNumber);
			user.setGender(gender);
			user.setAddress(address);
			user.setRole("CUSTOMER");

			userService.insert(user);

			request.setAttribute("successMessage", "Đăng ký thành công!");

		} catch (Exception e) {

			e.printStackTrace();

			String errorMessage = "Đăng ký thất bại, vui lòng thử lại!";
			String dbMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

			if (dbMessage.contains("duplicate") || dbMessage.contains("username")) {
				errorMessage = "Tên đăng nhập đã tồn tại!";
			} else if (dbMessage.contains("email")) {
				errorMessage = "Email đã được sử dụng!";
			}

			request.setAttribute("errorMessage", errorMessage);
			request.setAttribute("values", values);
		}

		request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
	}
}
