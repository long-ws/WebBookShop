package servlet.admin.user;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;
import service.UserServiceImpl;

@WebServlet(name = "DeleteUserServlet", value = "/admin/userManager/delete")
public class DeleteUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		long id;

		try {
			id = Long.parseLong(request.getParameter("id"));
		} catch (NumberFormatException e) {
			response.sendRedirect(request.getContextPath() + "/admin/userManager");
			return;
		}

		try {
			boolean deleted = userService.deleteUser(id);
			if (deleted) {
				request.getSession().setAttribute("successMessage", "Xóa người dùng #" + id + " thành công!");
			} else {
				request.getSession().setAttribute("errorMessage", "Xóa người dùng #" + id + " thất bại!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("errorMessage", "Xóa người dùng #" + id + " thất bại!");
		}

		response.sendRedirect(request.getContextPath() + "/admin/userManager");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/admin/userManager");
	}
}
