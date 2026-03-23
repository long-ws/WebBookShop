package servlet.admin.productreview;

import java.io.IOException;

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
import utils.TextUtils;

@WebServlet(name = "ProductReviewDetailServlet", value = "/admin/reviewManager/detail")
public class ProductReviewDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final ProductReviewService productReviewService = new ProductReviewService();
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long id;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/reviewManager");
            return;
        }

        ProductReview productReview = productReviewService.getById(id);
        if (productReview == null) {
            response.sendRedirect(request.getContextPath() + "/admin/reviewManager");
            return;
        }

        productReview.setContent(
                TextUtils.toParagraph(
                        productReview.getContent() == null ? "" : productReview.getContent()
                )
        );

        User user = userService.getById(productReview.getUserId());
        if (user != null) {
            productReview.setUser(user);
        }

        Product product = productService.getById(productReview.getProductId());
        if (product != null) {
            productReview.setProduct(product);
        }

        request.setAttribute("productReview", productReview);
        request.getRequestDispatcher("/WEB-INF/views/productReviewDetailView.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}
