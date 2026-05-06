package servlet.client;

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

@WebServlet(name = "SearchServlet", value = "/search")
public class SearchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final ProductService productService = new ProductService();

    private static final int PRODUCTS_PER_PAGE = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String queryStr = request.getParameter("q");

        if (queryStr != null && !queryStr.trim().isEmpty()) {
            queryStr = queryStr.trim();

            // Lấy tổng số sản phẩm
            int totalProducts = 0;
            try {
                totalProducts = productService.countByQuery(queryStr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int totalPages = totalProducts / PRODUCTS_PER_PAGE;
            if (totalProducts % PRODUCTS_PER_PAGE != 0) {
                totalPages += 1;
            }

            // Lấy trang hiện tại
            int page = 1;
            try {
                String pageParam = request.getParameter("page");
                if (pageParam != null) {
                    page = Integer.parseInt(pageParam);
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
            if (page < 1 || page > totalPages) {
                page = 1;
            }

            int offset = (page - 1) * PRODUCTS_PER_PAGE;

            // Lấy danh sách sản phẩm
            List<Product> products = new ArrayList<>();
            try {
                products = productService.getByQuery(queryStr, PRODUCTS_PER_PAGE, offset);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Product product : products) {
                String name = product.getName();
                product.setName(name);
            }

            request.setAttribute("query", queryStr);
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("page", page);
            request.setAttribute("products", products);

            request.getRequestDispatcher("/WEB-INF/views/searchView.jsp").forward(request, response);
        } else {
            // Nếu query rỗng thì redirect về trang chủ
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
