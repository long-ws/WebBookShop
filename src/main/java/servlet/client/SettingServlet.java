package servlet.client;

import java.io.IOException;

import beans.User;
import beans.common.Gender;
import dto.AdminUserDetailDTO;
import dto.UserCreateUpdateFormDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.UserService;
import service.UserServiceImpl;

@WebServlet(name = "SettingServlet", value = "/setting")
public class SettingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");
		if (user != null) {
			AdminUserDetailDTO detail = userService.getUserById(user.getId());
			if (detail != null) {
				UserCreateUpdateFormDTO form = userService.toFormDTO(detail);
				request.setAttribute("user", form);
			}
		}
		request.getRequestDispatcher("WEB-INF/views/settingView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if (currentUser == null) {
			response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		UserCreateUpdateFormDTO dto = new UserCreateUpdateFormDTO();
		dto.setId(currentUser.getId());
		dto.setUsername(request.getParameter("username"));
		dto.setFullname(request.getParameter("fullname"));
		dto.setEmail(request.getParameter("email"));
		dto.setPhoneNumber(request.getParameter("phoneNumber"));
		String genderStr = request.getParameter("gender");
		if (genderStr != null) {
			try {
				int genderId = Integer.parseInt(genderStr);
				Gender gender = new Gender();
				gender.setId(genderId);
				dto.setGender(gender);
			} catch (Exception e) {
			}
		}

		UserCreateUpdateFormDTO result = userService.updateUser(dto);

		if (result.hasErrors()) {
			request.setAttribute("errors", result.getErrors());
			request.setAttribute("user", dto);
			request.setAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin");
		} else {
			User updatedUser = userService.getById(dto.getId());
			if (updatedUser != null) {
				session.setAttribute("currentUser", updatedUser);
			}
			request.setAttribute("successMessage", "Cập nhật thành công!");
			AdminUserDetailDTO detail = userService.getUserById(dto.getId());
			if (detail != null) {
				UserCreateUpdateFormDTO form = userService.toFormDTO(detail);
				request.setAttribute("user", form);
			}
		}

		request.getRequestDispatcher("WEB-INF/views/settingView.jsp").forward(request, response);
	}
}
