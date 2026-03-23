package servlet.admin.productreview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.Product;
import beans.ProductReview;
import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ProductReviewService;
import service.ProductService;
import service.UserService;

@WebServlet(name = "ProductReviewManagerServlet", value = "/admin/reviewManager")
public class ProductReviewManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ProductReviewService productReviewService = new ProductReviewService();
	private final UserService userService = new UserService();
	private final ProductService productService = new ProductService();

	private static final int PRODUCT_REVIEWS_PER_PAGE = 5;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int totalProductReviews = productReviewService.count();
		int totalPages = totalProductReviews / PRODUCT_REVIEWS_PER_PAGE;
		if (totalProductReviews % PRODUCT_REVIEWS_PER_PAGE != 0) {
			totalPages++;
		}

		int page = 1;
		try {
			page = Integer.parseInt(request.getParameter("page"));
		} catch (Exception ignored) {
		}

		if (page < 1 || page > totalPages) {
			page = 1;
		}

		int offset = (page - 1) * PRODUCT_REVIEWS_PER_PAGE;

		List<ProductReview> productReviews = productReviewService.getOrderedPart(PRODUCT_REVIEWS_PER_PAGE, offset, "id",
				"DESC");

		if (productReviews == null) {
			productReviews = new ArrayList<>();
		}

		for (int i = 0; i < productReviews.size(); i++) {
			ProductReview review = productReviews.get(i);

			User user = userService.getById(review.getUserId());
			if (user != null) {
				review.setUser(user);
			}

			Product product = productService.getById(review.getProductId());
			if (product != null) {
				review.setProduct(product);
			}
		}

		request.setAttribute("totalPages", totalPages);
		request.setAttribute("page", page);
		request.setAttribute("productReviews", productReviews);

		request.getRequestDispatcher("/WEB-INF/views/productReviewManagerView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    String action = request.getParameter("action");
	    int id = Integer.parseInt(request.getParameter("id"));

	    if ("HIDE".equals(action)) {
	        productReviewService.hide(id);
	        request.getSession().setAttribute("successMessage", "Đã ẩn đánh giá #" + id);
	    } else if ("SHOW".equals(action)) {
	        productReviewService.show(id);
	        request.getSession().setAttribute("successMessage", "Đã hiện đánh giá #" + id);
	    }

	    String pageParam = request.getParameter("page");
	    if (pageParam == null || pageParam.isEmpty()) {
	        pageParam = "1";
	    }

	    response.sendRedirect(
	        request.getContextPath() + "/admin/reviewManager?page=" + pageParam
	    );
	}


}
