package servlet.admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import constants.system.SystemKeys;
import constants.ViewAttributeConstants;
import exception.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import service.OrderService;
import service.ProductService;
import service.UserManagementService;
import service.UserManagementServiceImpl;

@WebServlet(name = "AdminServlet", value = "/admin")
public class AdminServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserManagementService userManagementService = new UserManagementServiceImpl();
	private final CategoryService categoryService = new CategoryService();
	private final ProductService productService = new ProductService();
	private final OrderService orderService = new OrderService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		long totalUsers = 0;
		int totalCategories = 0;
		int totalProducts = 0;
		int totalOrders = 0;

		final Map<String, String> errors = new HashMap<>();

		try {
			totalUsers = userManagementService.countUsers();
			totalCategories = categoryService.count();
			totalProducts = productService.count();
			totalOrders = orderService.count();
		} catch (BusinessException e) {
			final Map<String, String> businessErrors = e.getErrors();
			if (businessErrors != null && !businessErrors.isEmpty()) {
				errors.putAll(businessErrors);
			} else {
				errors.put(SystemKeys.ERROR_GLOBAL, e.getMessage());
			}
		} catch (Exception e) {
			errors.put(SystemKeys.ERROR_GLOBAL, "Không thể tải dữ liệu dashboard do sự cố hệ thống.");
		}

		request.setAttribute(ViewAttributeConstants.Dashboard.TOTAL_USERS, totalUsers);
		request.setAttribute(ViewAttributeConstants.Dashboard.TOTAL_CATEGORIES, totalCategories);
		request.setAttribute(ViewAttributeConstants.Dashboard.TOTAL_PRODUCTS, totalProducts);
		request.setAttribute(ViewAttributeConstants.Dashboard.TOTAL_ORDERS, totalOrders);
		request.setAttribute(ViewAttributeConstants.ERRORS, errors);

		request.getRequestDispatcher("/WEB-INF/views/adminView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}
