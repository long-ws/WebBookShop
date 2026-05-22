package servlet.client;

import java.io.IOException;

import beans.Category;
import dto.AdvancedSearchRequest;
import dto.AdvancedSearchResponse;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("categories", categoryService.getAll());

        AdvancedSearchRequest searchRequest = buildSearchRequest(request);
        String sortParam = request.getParameter("sort");

        if (searchRequest.getCategoryId() != null) {
            Category category = categoryService.getById(searchRequest.getCategoryId());
            if (category != null) {
                searchRequest.setSelectedCategoryName(category.getName());
            }
        }

        AdvancedSearchResponse searchResponse = productService.advancedSearch(searchRequest);
        searchResponse.setBaseQueryString(productService.buildBaseQueryString(searchRequest));

        if (sortParam != null) {
            searchResponse.setBaseQueryString(searchResponse.getBaseQueryString()
                    .replace("&sort=" + searchResponse.getSortBy() + "-" + searchResponse.getSortDir(), "")
                    + "&sort=" + sortParam);
        }

        request.setAttribute("products", searchResponse.getProducts());
        request.setAttribute("totalProducts", searchResponse.getTotalProducts());
        request.setAttribute("totalPages", searchResponse.getTotalPages());
        request.setAttribute("page", searchResponse.getCurrentPage());
        request.setAttribute("keyword", searchResponse.getKeyword());
        request.setAttribute("selectedCategoryId", searchResponse.getCategoryId());
        request.setAttribute("selectedCategoryName", searchResponse.getSelectedCategoryName());
        request.setAttribute("author", searchResponse.getAuthor());
        request.setAttribute("publisher", searchResponse.getPublisher());
        request.setAttribute("minPrice", searchResponse.getMinPrice());
        request.setAttribute("maxPrice", searchResponse.getMaxPrice());
        request.setAttribute("minYear", searchResponse.getMinYear());
        request.setAttribute("maxYear", searchResponse.getMaxYear());
        request.setAttribute("sort", (searchResponse.getSortBy() != null ? searchResponse.getSortBy() : "totalBuy")
                + "-" + (searchResponse.getSortDir() != null ? searchResponse.getSortDir() : "DESC"));
        request.setAttribute("baseQueryString", searchResponse.getBaseQueryString());

        request.getRequestDispatcher("/WEB-INF/views/advancedSearchView.jsp").forward(request, response);
    }

    private AdvancedSearchRequest buildSearchRequest(HttpServletRequest request) {
        AdvancedSearchRequest searchRequest = new AdvancedSearchRequest();

        String keyword = request.getParameter("q");
        String categoryIdParam = request.getParameter("categoryId");
        String author = request.getParameter("author");
        String publisher = request.getParameter("publisher");
        String minPriceParam = request.getParameter("minPrice");
        String maxPriceParam = request.getParameter("maxPrice");
        String minYearParam = request.getParameter("minYear");
        String maxYearParam = request.getParameter("maxYear");
        String sortParam = request.getParameter("sort");
        String pageParam = request.getParameter("page");

        searchRequest.setKeyword(keyword);

        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            try {
                searchRequest.setCategoryId(Long.parseLong(categoryIdParam.trim()));
            } catch (NumberFormatException ignored) {}
        }

        searchRequest.setAuthor(author);
        searchRequest.setPublisher(publisher);

        if (minPriceParam != null && !minPriceParam.trim().isEmpty()) {
            try {
                searchRequest.setMinPrice(Integer.parseInt(minPriceParam.trim()));
            } catch (NumberFormatException ignored) {}
        }
        if (maxPriceParam != null && !maxPriceParam.trim().isEmpty()) {
            try {
                searchRequest.setMaxPrice(Integer.parseInt(maxPriceParam.trim()));
            } catch (NumberFormatException ignored) {}
        }

        if (minYearParam != null && !minYearParam.trim().isEmpty()) {
            try {
                searchRequest.setMinYear(Integer.parseInt(minYearParam.trim()));
            } catch (NumberFormatException ignored) {}
        }
        if (maxYearParam != null && !maxYearParam.trim().isEmpty()) {
            try {
                searchRequest.setMaxYear(Integer.parseInt(maxYearParam.trim()));
            } catch (NumberFormatException ignored) {}
        }

        if (sortParam != null && sortParam.contains("-")) {
            String[] parts = sortParam.split("-", 2);
            searchRequest.setSortBy(parts[0]);
            searchRequest.setSortDir(parts[1]);
        }

        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                searchRequest.setPage(Integer.parseInt(pageParam.trim()));
            } catch (NumberFormatException ignored) {}
        }

        return searchRequest;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
