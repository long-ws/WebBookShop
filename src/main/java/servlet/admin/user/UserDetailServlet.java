package servlet.admin.user;

import java.io.IOException;

import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;

@WebServlet(name = "UserDetailServlet", value = "/admin/userManager/detail")
public class UserDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long id = 0;

		try {
			id = Long.parseLong(request.getParameter("id"));
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/admin/userManager");
			return;
		}

		User user = null;
		try {
			user = userService.getById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (user != null) {
			request.setAttribute("user", user);
			request.getRequestDispatcher("/WEB-INF/views/userDetailView.jsp").forward(request, response);
		} else {
			response.sendRedirect(request.getContextPath() + "/admin/userManager");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}
}
