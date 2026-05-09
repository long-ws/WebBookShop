package servlet.client;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

@WebServlet(name = "AdvancedSearchServlet", value = "/advancedSearch")
public class AdvancedSearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    private static final int PRODUCTS_PER_PAGE = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryService.getAll();
        request.setAttribute("categories", categories);

        String keyword = request.getParameter("q");
        String categoryIdParam = request.getParameter("categoryId");
        String author = request.getParameter("author");
        String publisher = request.getParameter("publisher");
        String minPriceParam = request.getParameter("minPrice");
        String maxPriceParam = request.getParameter("maxPrice");
        String minYearParam = request.getParameter("minYear");
        String maxYearParam = request.getParameter("maxYear");
        String sortParam = request.getParameter("sort");

        String sortBy = "totalBuy";
        String sortDir = "DESC";
        if (sortParam != null && sortParam.contains("-")) {
            String[] parts = sortParam.split("-", 2);
            sortBy = parts[0];
            sortDir = parts[1];
        }

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        conditions.add("p.isDeleted = 0");

        if (keyword != null && !keyword.trim().isEmpty()) {
            String[] keywords = keyword.trim().split("\\s+");
            for (String kw : keywords) {
                if (!kw.isEmpty()) {
                    conditions.add("(p.name LIKE ? OR p.author LIKE ? OR p.publisher LIKE ? OR p.description LIKE ?)");
                    String likeKw = "%" + kw + "%";
                    params.add(likeKw);
                    params.add(likeKw);
                    params.add(likeKw);
                    params.add(likeKw);
                }
            }
        }

        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            try {
                long catId = Long.parseLong(categoryIdParam);
                conditions.add("EXISTS (SELECT 1 FROM product_category pc WHERE pc.productId = p.id AND pc.categoryId = ?)");
                params.add(catId);
            } catch (NumberFormatException ignored) {}
        }

        if (author != null && !author.trim().isEmpty()) {
            conditions.add("p.author LIKE ?");
            params.add("%" + author.trim() + "%");
        }

        if (publisher != null && !publisher.trim().isEmpty()) {
            conditions.add("p.publisher LIKE ?");
            params.add("%" + publisher.trim() + "%");
        }

        int minPrice = 0;
        int maxPrice = Integer.MAX_VALUE;
        try {
            if (minPriceParam != null && !minPriceParam.trim().isEmpty()) {
                minPrice = Integer.parseInt(minPriceParam.trim());
            }
            if (maxPriceParam != null && !maxPriceParam.trim().isEmpty()) {
                maxPrice = Integer.parseInt(maxPriceParam.trim());
            }
        } catch (NumberFormatException ignored) {}

        if (minPrice > 0 || maxPrice < Integer.MAX_VALUE) {
            conditions.add("p.price BETWEEN ? AND ?");
            params.add(minPrice);
            params.add(maxPrice);
        }

        int minYear = 0;
        int maxYear = 2100;
        try {
            if (minYearParam != null && !minYearParam.trim().isEmpty()) {
                minYear = Integer.parseInt(minYearParam.trim());
            }
            if (maxYearParam != null && !maxYearParam.trim().isEmpty()) {
                maxYear = Integer.parseInt(maxYearParam.trim());
            }
        } catch (NumberFormatException ignored) {}

        if (minYear > 0 || maxYear < 2100) {
            conditions.add("p.yearPublishing BETWEEN ? AND ?");
            params.add(minYear);
            params.add(maxYear);
        }

        String whereClause = String.join(" AND ", conditions);

        int totalProducts = productService.countByAdvancedSearch(whereClause, params);

        int totalPages = totalProducts / PRODUCTS_PER_PAGE;
        if (totalProducts % PRODUCTS_PER_PAGE != 0) {
            totalPages++;
        }

        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                page = Integer.parseInt(pageParam.trim());
            }
        } catch (NumberFormatException ignored) {}
        if (page < 1 || page > totalPages) {
            page = 1;
        }

        int offset = (page - 1) * PRODUCTS_PER_PAGE;

        List<Product> products = productService.getAdvancedSearch(whereClause, params, sortBy, sortDir, PRODUCTS_PER_PAGE, offset);

        request.setAttribute("products", products);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("keyword", keyword);
        request.setAttribute("selectedCategoryId", categoryIdParam);
        request.setAttribute("author", author);
        request.setAttribute("publisher", publisher);
        request.setAttribute("minPrice", minPriceParam);
        request.setAttribute("maxPrice", maxPriceParam);
        request.setAttribute("minYear", minYearParam);
        request.setAttribute("maxYear", maxYearParam);
        request.setAttribute("sort", sortParam != null ? sortParam : "totalBuy-DESC");

        StringBuilder paramBuilder = new StringBuilder();
        if (keyword != null && !keyword.isEmpty()) {
            paramBuilder.append("&q=").append(URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        }
        if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
            paramBuilder.append("&categoryId=").append(categoryIdParam);
        }
        if (author != null && !author.isEmpty()) {
            paramBuilder.append("&author=").append(URLEncoder.encode(author, StandardCharsets.UTF_8));
        }
        if (publisher != null && !publisher.isEmpty()) {
            paramBuilder.append("&publisher=").append(URLEncoder.encode(publisher, StandardCharsets.UTF_8));
        }
        if (minPriceParam != null && !minPriceParam.isEmpty()) {
            paramBuilder.append("&minPrice=").append(minPriceParam);
        }
        if (maxPriceParam != null && !maxPriceParam.isEmpty()) {
            paramBuilder.append("&maxPrice=").append(maxPriceParam);
        }
        if (minYearParam != null && !minYearParam.isEmpty()) {
            paramBuilder.append("&minYear=").append(minYearParam);
        }
        if (maxYearParam != null && !maxYearParam.isEmpty()) {
            paramBuilder.append("&maxYear=").append(maxYearParam);
        }
        if (sortParam != null) {
            paramBuilder.append("&sort=").append(sortParam);
        }
        request.setAttribute("baseQueryString", paramBuilder.toString());

        request.getRequestDispatcher("/WEB-INF/views/advancedSearchView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
