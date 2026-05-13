package servlet.admin.user;

import java.io.IOException;

import dto.UserCreateUpdateFormDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;
import service.UserServiceImpl;

@WebServlet(name = "CreateUserServlet", value = "/admin/userManager/create")
public class CreateUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/createUserView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		UserCreateUpdateFormDTO dto = new UserCreateUpdateFormDTO();
		dto.setUsername(request.getParameter("username"));
		dto.setPassword(request.getParameter("password"));
		dto.setFullname(request.getParameter("fullname"));
		dto.setEmail(request.getParameter("email"));
		dto.setPhoneNumber(request.getParameter("phoneNumber"));
		String genderStr = request.getParameter("gender");
		if (genderStr != null) {
			try {
				int genderId = Integer.parseInt(genderStr);
				beans.common.Gender gender = new beans.common.Gender();
				gender.setId(genderId);
				dto.setGender(gender);
			} catch (Exception e) {
			}
		}
		String roleStr = request.getParameter("role");
		if (roleStr != null) {
			beans.common.Role role = new beans.common.Role();
			role.setCode(roleStr);
			dto.setRole(role);
		}

		UserCreateUpdateFormDTO result = userService.createUser(dto);

		if (result.hasErrors()) {
			request.setAttribute("errors", result.getErrors());
			request.setAttribute("user", dto);
			request.setAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
		} else {
			request.setAttribute("successMessage", "Thêm người dùng thành công!");
			request.setAttribute("user", null);
		}

		request.getRequestDispatcher("/WEB-INF/views/createUserView.jsp").forward(request, response);
	}
}
