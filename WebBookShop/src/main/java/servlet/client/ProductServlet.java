package servlet.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.Category;
import beans.Product;
import beans.ProductReview;
import beans.User;
import beans.WishlistItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.CategoryService;
import service.ProductReviewService;
import service.ProductService;
import service.WishlistItemService;
import utils.TextUtils;

@WebServlet(name = "ProductServlet", value = "/product")
public class ProductServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();
    private final ProductReviewService productReviewService = new ProductReviewService();
    private final WishlistItemService wishlistItemService = new WishlistItemService();

    private static final int PRODUCT_REVIEWS_PER_PAGE = 2;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy ID sản phẩm
        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) { }

        if (id <= 0) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Lấy sản phẩm
        Product product = productService.getById(id);
        if (product == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Lấy danh mục
        Category category = categoryService.getByProductId(id);
        if (category == null) category = new Category();

        // Chuẩn hóa mô tả
        if (product.getDescription() == null) product.setDescription("");
        product.setDescription(TextUtils.toParagraph(product.getDescription()));

        // Lấy đánh giá
        int totalProductReviews = productReviewService.countByProductId(id);
        int sumRatingScores = productReviewService.sumRatingScoresByProductId(id);
        int averageRatingScore = (totalProductReviews == 0) ? 0 : sumRatingScores / totalProductReviews;

        int pageReview = 1;
        String pageReviewParam = request.getParameter("pageReview");
        try { pageReview = Integer.parseInt(pageReviewParam); } catch (Exception e) { pageReview = 1; }

        int totalPagesOfProductReviews = (totalProductReviews + PRODUCT_REVIEWS_PER_PAGE - 1) / PRODUCT_REVIEWS_PER_PAGE;
        if (pageReview < 1 || pageReview > totalPagesOfProductReviews) pageReview = 1;

        int offset = (pageReview - 1) * PRODUCT_REVIEWS_PER_PAGE;
        List<ProductReview> productReviews = productReviewService.getOrderedPartByProductId(
                PRODUCT_REVIEWS_PER_PAGE, offset, "createdAt", "DESC", id
        );
        for (ProductReview r : productReviews) {
            if (r.getContent() == null) r.setContent("");
            r.setContent(TextUtils.toParagraph(r.getContent()));
        }

        // Sản phẩm liên quan
        List<Product> relatedProducts = productService.getRandomPartByCategoryId(4, 0, category.getId());

        // Wishlist
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        List<WishlistItem> wishlistItems = new ArrayList<>();
        if (currentUser != null) {
            wishlistItems = wishlistItemService.getByUserId(currentUser.getId());
        }

        // Set tất cả dữ liệu cho JSP
        request.setAttribute("product", product);
        request.setAttribute("category", category);
        request.setAttribute("totalProductReviews", totalProductReviews);
        request.setAttribute("averageRatingScore", averageRatingScore);
        request.setAttribute("productReviews", productReviews);
        request.setAttribute("pageReview", pageReview);
        request.setAttribute("totalPagesOfProductReviews", totalPagesOfProductReviews);
        request.setAttribute("relatedProducts", relatedProducts);
        request.setAttribute("wishlistItems", wishlistItems);

        request.getRequestDispatcher("/WEB-INF/views/productView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
