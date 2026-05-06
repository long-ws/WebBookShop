package servlet.client.productreview;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import beans.ProductReview;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ProductReviewService;

@WebServlet(name = "AddProductReviewServlet", value = "/addProductReview")
public class AddProductReviewServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final ProductReviewService productReviewService = new ProductReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> values = new HashMap<>();
        values.put("userId", request.getParameter("userId"));
        values.put("productId", request.getParameter("productId"));
        values.put("ratingScore", request.getParameter("ratingScore"));
        values.put("content", request.getParameter("content"));

        String successMessage = "Đã đánh giá thành công!";
        String errorAddReviewMessage = "Đã có lỗi truy vấn!";
        String redirectAnchor = "";

        boolean valid = true;

        if (values.get("ratingScore") == null || values.get("ratingScore").trim().isEmpty()) {
            valid = false;
        }

        if (values.get("content") == null || values.get("content").trim().length() < 10) {
            valid = false;
        }

        if (valid) {
            ProductReview productReview = new ProductReview();
            productReview.setId(0L);
            productReview.setUserId(Long.parseLong(values.get("userId")));
            productReview.setProductId(Long.parseLong(values.get("productId")));
            productReview.setRatingScore(Integer.parseInt(values.get("ratingScore")));
            productReview.setContent(values.get("content"));
            productReview.setCreatedAt(LocalDateTime.now());
            productReview.setUpdatedAt(null);

            try {
                productReviewService.insert(productReview);
                request.getSession().setAttribute("successMessage", successMessage);
                redirectAnchor = "#review";
            } catch (Exception e) {
                request.getSession().setAttribute("values", values);
                request.getSession().setAttribute("errorAddReviewMessage", errorAddReviewMessage);
                redirectAnchor = "#review-form";
            }
        } else {
            request.getSession().setAttribute("values", values);
            redirectAnchor = "#review-form";
        }

        response.sendRedirect(request.getContextPath() + "/product?id=" + values.get("productId") + redirectAnchor);
    }
}
