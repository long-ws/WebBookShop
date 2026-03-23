package servlet.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.Category;
import beans.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import service.ProductService;

@WebServlet(name = "HomeServlet", value = "")
public class HomeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();

    private static final int LIMIT = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> categories = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        try {
            categories = categoryService.getPart(LIMIT, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            products = productService.getOrderedPart(LIMIT, 0, "createdAt", "DESC");
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("categories", categories);
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/views/homeView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
