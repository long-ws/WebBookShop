package servlet.admin.user;

import java.io.IOException;
import java.util.List;

import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;

@WebServlet(name = "UserManagerServlet", value = "/admin/userManager")
public class UserManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserService();

	private static final int USERS_PER_PAGE = 5;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int totalUsers = 0;
		try {
			totalUsers = userService.count();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int totalPages = totalUsers / USERS_PER_PAGE;
		if (totalUsers % USERS_PER_PAGE != 0) {
			totalPages++;
		}

		int page = 1;
		try {
			String pageParam = request.getParameter("page");
			if (pageParam != null) {
				page = Integer.parseInt(pageParam);
			}
		} catch (Exception e) {
			page = 1;
		}

		if (page < 1 || page > totalPages) {
			page = 1;
		}

		int offset = (page - 1) * USERS_PER_PAGE;

		List<User> users = null;
		try {
			users = userService.getOrderedPart(USERS_PER_PAGE, offset, "id", "DESC");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (users == null) {
			users = java.util.Collections.emptyList();
		}

		request.setAttribute("totalPages", totalPages);
		request.setAttribute("page", page);
		request.setAttribute("users", users);
		request.getRequestDispatcher("/WEB-INF/views/userManagerView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");

		if ("delete".equals(action)) {

			String idParam = request.getParameter("id");
			String pageParam = request.getParameter("page");

			if (pageParam == null || pageParam.isEmpty()) {
				pageParam = "1";
			}

			try {
				int id = Integer.parseInt(idParam);
				userService.delete(id);
				request.getSession().setAttribute("successMessage", "Đã xóa người dùng id: " + id);
			} catch (Exception e) {
				request.getSession().setAttribute("errorMessage", "Xóa người dùng thất bại!");
			}

			response.sendRedirect(request.getContextPath() + "/admin/userManager?page=" + pageParam);
		}
	}

}
