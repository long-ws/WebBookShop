package servlet.admin.product;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Category;
import beans.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import service.ProductService;
import utils.ImageUtils;

@WebServlet(name = "CreateProductServlet", value = "/admin/productManager/create")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 5, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class CreateProductServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ProductService productService = new ProductService();
	private final CategoryService categoryService = new CategoryService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		List<Category> categories = categoryService.getAll();
		request.setAttribute("categories", categories);
		request.getRequestDispatcher("/WEB-INF/views/createProductView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Product product = new Product();
		Map<String, List<String>> violations = new HashMap<>();

		product.setName(request.getParameter("name"));
		product.setAuthor(request.getParameter("author"));
		product.setPublisher(request.getParameter("publisher"));
		product.setDescription(request.getParameter("description"));
		product.setCreatedAt(LocalDateTime.now());

		double price = parseDouble(request.getParameter("price"));
		double discount = parseDouble(request.getParameter("discount"));
		int quantity = parseInt(request.getParameter("quantity"));
		int totalBuy = parseInt(request.getParameter("totalBuy"));
		int pages = parseInt(request.getParameter("pages"));
		int year = parseInt(request.getParameter("yearPublishing"));
		int shop = parseInt(request.getParameter("shop"));
		long categoryId = parseLong(request.getParameter("category"));

		product.setPrice(price);
		product.setDiscount(discount);
		product.setQuantity(quantity);
		product.setTotalBuy(totalBuy);
		product.setPages(pages);
		product.setYearPublishing(year);
		product.setShop(shop);

		if (product.getName() == null || product.getName().trim().isEmpty()) {
			addViolation(violations, "nameViolations", "Tên không được để trống");
		}

		if (price <= 0) {
			addViolation(violations, "priceViolations", "Giá phải lớn hơn 0");
		}

		if (discount < 0 || discount > 100) {
			addViolation(violations, "discountViolations", "Khuyến mãi từ 0 đến 100%");
		}

		if (quantity < 0) {
			addViolation(violations, "quantityViolations", "Số lượng không hợp lệ");
		}

		if (pages <= 0) {
			addViolation(violations, "pagesViolations", "Số trang phải > 0");
		}

		if (year < 1900 || year > 2100) {
			addViolation(violations, "yearPublishingViolations", "Năm xuất bản không hợp lệ");
		}

		if (categoryId == 0) {
			addViolation(violations, "categoryViolations", "Phải chọn thể loại");
		}

		/* ===== ĐẾM LỖI ===== */
		int errorCount = 0;
		for (List<String> list : violations.values()) {
			errorCount += list.size();
		}

		/* ===== XỬ LÝ ===== */
		if (errorCount == 0) {
			String imageName = ImageUtils.upload(request);
			if (imageName != null) {
				product.setImageName(imageName);
			}

			try {
				long productId = productService.insert(product);
				productService.insertProductCategory(productId, categoryId);
				request.setAttribute("successMessage", "Thêm sản phẩm thành công!");
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("errorMessage", "Thêm sản phẩm thất bại!");
				request.setAttribute("product", product);
			}
		} else {
			request.setAttribute("product", product);
			request.setAttribute("violations", violations);
		}

		request.setAttribute("categories", categoryService.getAll());
		request.getRequestDispatcher("/WEB-INF/views/createProductView.jsp").forward(request, response);
	}

	private double parseDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return 0;
		}
	}

	private int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}

	private long parseLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			return 0;
		}
	}

	private void addViolation(Map<String, List<String>> violations, String key, String message) {
		List<String> list = violations.get(key);
		if (list == null) {
			list = new ArrayList<>();
			violations.put(key, list);
		}
		list.add(message);
	}
}
