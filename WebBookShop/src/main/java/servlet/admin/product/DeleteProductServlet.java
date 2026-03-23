package servlet.admin.product;

import java.io.IOException;

import beans.Category;
import beans.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import service.ProductService;
import utils.ImageUtils;

@WebServlet(name = "DeleteProductServlet", value = "/admin/productManager/delete")
public class DeleteProductServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ProductService productService = new ProductService();
	private final CategoryService categoryService = new CategoryService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		long id = 0;
		try {
			id = Long.parseLong(request.getParameter("id"));
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/admin/productManager");
			return;
		}

		Product product = productService.getById(id);
		if (product == null) {
			response.sendRedirect(request.getContextPath() + "/admin/productManager");
			return;
		}

		String successMessage = "Xóa sản phẩm #" + id + " thành công!";
		String errorMessage = "Xóa sản phẩm #" + id + " thất bại!";

		try {
			Category category = categoryService.getByProductId(id);
			if (category != null) {
				productService.deleteProductCategory(id, category.getId());
			}

			if (product.getImageName() != null) {
				ImageUtils.delete(product.getImageName());
			}

			productService.delete(id);

			request.getSession().setAttribute("successMessage", successMessage);
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("errorMessage", errorMessage);
		}

		response.sendRedirect(request.getContextPath() + "/admin/productManager");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}
}
