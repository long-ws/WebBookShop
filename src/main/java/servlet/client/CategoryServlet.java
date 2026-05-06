package servlet.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

@WebServlet(name = "CategoryServlet", value = "/category")
public class CategoryServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();

    private static final int PRODUCTS_PER_PAGE = 6;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        Category category = categoryService.getById(id);
        if (category == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Lọc nhà xuất bản
        String[] checkedPublishersParam = request.getParameterValues("checkedPublishers");
        List<String> checkedPublishers = (checkedPublishersParam != null) ? Arrays.asList(checkedPublishersParam) : new ArrayList<>();

        // Lọc khoảng giá
        String[] priceRangesParam = request.getParameterValues("priceRanges");
        List<String> priceRanges = (priceRangesParam != null) ? Arrays.asList(priceRangesParam) : new ArrayList<>();

        // Sắp xếp
        String orderParam = request.getParameter("order");
        String orderBy = "totalBuy";
        String orderDir = "DESC";
        if (orderParam != null && orderParam.contains("-")) {
            String[] parts = orderParam.split("-", 2);
            orderBy = parts[0];
            orderDir = parts[1];
        }

        // Tổng hợp tiêu chí lọc
        List<String> filters = new ArrayList<>();
        if (!checkedPublishers.isEmpty()) {
            filters.add(productService.filterByPublishers(checkedPublishers));
        }
        if (!priceRanges.isEmpty()) {
            filters.add(productService.filterByPriceRanges(priceRanges));
        }
        String filtersQuery = productService.createFiltersQuery(filters);

        // Tổng số sản phẩm
        int totalProducts = 0;
        if (filters.isEmpty()) {
            totalProducts = productService.countByCategoryId(id);
        } else {
            totalProducts = productService.countByCategoryIdAndFilters(id, filtersQuery);
        }

        // Tổng số trang
        int totalPages = totalProducts / PRODUCTS_PER_PAGE;
        if (totalProducts % PRODUCTS_PER_PAGE != 0) {
            totalPages++;
        }

        // Trang hiện tại
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException ignored) {}
        if (page < 1 || page > totalPages) {
            page = 1;
        }

        int offset = (page - 1) * PRODUCTS_PER_PAGE;

        // Danh sách sản phẩm
        List<Product> products;
        if (filters.isEmpty()) {
            products = productService.getOrderedPartByCategoryId(PRODUCTS_PER_PAGE, offset, orderBy, orderDir, id);
        } else {
            products = productService.getOrderedPartByCategoryIdAndFilters(PRODUCTS_PER_PAGE, offset, orderBy, orderDir, id, filtersQuery);
        }
        if (products == null) {
            products = new ArrayList<>();
        }

        // Danh sách nhà xuất bản
        List<String> publishers = productService.getPublishersByCategoryId(id);
        if (publishers == null) {
            publishers = new ArrayList<>();
        }

        // Thiết lập attributes
        request.setAttribute("category", category);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("products", products);
        request.setAttribute("publishers", publishers);
        request.setAttribute("checkedPublishers", checkedPublishers);
        request.setAttribute("priceRanges", priceRanges);
        request.setAttribute("order", orderParam != null ? orderParam : "totalBuy-DESC");

        String filterQueryString = request.getQueryString();
        if (filterQueryString != null) {
            filterQueryString = filterQueryString.replaceAll("^id=\\d{1,5}(&page=\\d{1,5}|)", "");
        } else {
            filterQueryString = "";
        }
        request.setAttribute("filterQueryString", filterQueryString);

        request.getRequestDispatcher("/WEB-INF/views/categoryView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
