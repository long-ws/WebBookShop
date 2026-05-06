package servlet.admin.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ProductService;

@WebServlet(name = "ProductManagerServlet", value = "/admin/productManager")
public class ProductManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ProductService productService = new ProductService();
	private static final int PRODUCTS_PER_PAGE = 5;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int totalProducts = 0;
		try {
			totalProducts = productService.count();
		} catch (Exception e) {
			totalProducts = 0;
		}

		int totalPages = totalProducts / PRODUCTS_PER_PAGE;
		if (totalProducts % PRODUCTS_PER_PAGE != 0) {
			totalPages++;
		}
		if (totalPages == 0) {
			totalPages = 1;
		}

		int page = 1;
		String pageParam = request.getParameter("page");
		if (pageParam != null) {
			try {
				page = Integer.parseInt(pageParam);
			} catch (Exception e) {
				page = 1;
			}
		}

		if (page < 1 || page > totalPages) {
			page = 1;
		}

		int offset = (page - 1) * PRODUCTS_PER_PAGE;

		List<Product> products = new ArrayList<>();
		try {
			products = productService.getOrderedPart(PRODUCTS_PER_PAGE, offset, "id", "DESC");
		} catch (Exception e) {
			products = new ArrayList<>();
		}

		request.setAttribute("totalPages", totalPages);
		request.setAttribute("page", page);
		request.setAttribute("products", products);

		request.getRequestDispatcher("/WEB-INF/views/productManagerView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String idParam = request.getParameter("id");

		if ("delete".equalsIgnoreCase(action) && idParam != null) {
			try {
				int id = Integer.parseInt(idParam);

				productService.delete(id);

				request.getSession().setAttribute("successMessage", "Xóa sản phẩm thành công!");
			} catch (NumberFormatException e) {
				request.getSession().setAttribute("errorMessage", "ID sản phẩm không hợp lệ!");
			} catch (Exception e) {
				request.getSession().setAttribute("errorMessage", "Xóa sản phẩm thất bại!");
			}
		}

		String currentPage = request.getParameter("page");
		if (currentPage == null)
			currentPage = "1";

		response.sendRedirect(request.getContextPath() + "/admin/productManager?page=" + currentPage);
	}

}
