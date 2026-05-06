package servlet.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import service.OrderService;
import service.ProductService;
import service.UserService;

@WebServlet(name = "AdminServlet", value = "/admin")
public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserService userService = new UserService();
	private final CategoryService categoryService = new CategoryService();
	private final ProductService productService = new ProductService();
	private final OrderService orderService = new OrderService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int totalUsers = 0;
		int totalCategories = 0;
		int totalProducts = 0;
		int totalOrders = 0;

		try {
			totalUsers = userService.count();
			totalCategories = categoryService.count();
			totalProducts = productService.count();
			totalOrders = orderService.count();
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", "Không thể tải dữ liệu dashboard");
		}

		request.setAttribute("totalUsers", totalUsers);
		request.setAttribute("totalCategories", totalCategories);
		request.setAttribute("totalProducts", totalProducts);
		request.setAttribute("totalOrders", totalOrders);

		request.getRequestDispatcher("/WEB-INF/views/adminView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}
}
