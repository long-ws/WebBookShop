package servlet.client.productreview;

import java.io.IOException;

import beans.ProductReview;
import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.ProductReviewService;
import service.ProductService;
import service.UserService;

@WebServlet(name = "EditProductReviewServlet", value = "/editProductReview")
public class EditProductReviewServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final ProductReviewService productReviewService = new ProductReviewService();
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        ProductReview productReview = productReviewService.getById(id);
        if (productReview == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        User reviewUser = userService.getById(productReview.getUserId());
        productReview.setUser(reviewUser);
        productReview.setProduct(productService.getById(productReview.getProductId()));

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getId() != productReview.getUserId()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.setAttribute("productReview", productReview);
        request.getRequestDispatcher("/WEB-INF/views/editProductReviewView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        ProductReview productReview = productReviewService.getById(id);
        if (productReview == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getId() != productReview.getUserId()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        int ratingScore;
        String content = request.getParameter("content");
        try {
            ratingScore = Integer.parseInt(request.getParameter("ratingScore"));
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Điểm đánh giá không hợp lệ!");
            request.setAttribute("productReview", productReview);
            request.getRequestDispatcher("/WEB-INF/views/editProductReviewView.jsp").forward(request, response);
            return;
        }

        if (content == null || content.trim().length() < 10) {
            request.setAttribute("errorMessage", "Nội dung đánh giá phải từ 10 ký tự trở lên!");
            request.setAttribute("productReview", productReview);
            request.getRequestDispatcher("/WEB-INF/views/editProductReviewView.jsp").forward(request, response);
            return;
        }

        productReview.setRatingScore(ratingScore);
        productReview.setContent(content);

        try {
            productReviewService.update(productReview);
            request.setAttribute("successMessage", "Đã sửa đánh giá thành công!");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Đã có lỗi truy vấn!");
        }

        request.setAttribute("productReview", productReview);
        request.getRequestDispatcher("/WEB-INF/views/editProductReviewView.jsp").forward(request, response);
    }
}
