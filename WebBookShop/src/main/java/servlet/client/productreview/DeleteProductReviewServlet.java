package servlet.client.productreview;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ProductReviewService;

@WebServlet(name = "DeleteProductReviewServlet", value = "/deleteProductReview")
public class DeleteProductReviewServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final ProductReviewService productReviewService = new ProductReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long productReviewId = 0L;
        String productId = request.getParameter("productId");

        try {
            productReviewId = Long.parseLong(request.getParameter("productReviewId"));
            productReviewService.delete(productReviewId);
            request.getSession().setAttribute("successMessage", "Đã xóa đánh giá thành công!");
        } catch (Exception e) {
            request.getSession().setAttribute("errorDeleteReviewMessage", "Đã có lỗi truy vấn!");
        }

        response.sendRedirect(request.getContextPath() + "/product?id=" + productId + "#review");
    }

}
