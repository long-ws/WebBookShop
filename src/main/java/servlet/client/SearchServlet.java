package servlet.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import beans.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private final Gson gson = new GsonBuilder().create();

    private static final int PRODUCTS_PER_PAGE = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processSearchRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processSearchRequest(request, response);
    }

    private void processSearchRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String queryStr = request.getParameter("q");
        String ajaxParam = request.getParameter("ajax");
        boolean isAjax = "true".equalsIgnoreCase(ajaxParam);

        if (queryStr == null || queryStr.trim().isEmpty()) {
            if (isAjax) {
                sendJsonResponse(response, new SearchResponse(0, new ArrayList<>(), ""));
                return;
            }
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        queryStr = queryStr.trim();

        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                page = Math.max(1, Integer.parseInt(pageParam.trim()));
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int totalProducts = 0;
        List<Product> products = new ArrayList<>();

        try {
            totalProducts = productService.countByAdvancedQuery(queryStr);

            int totalPages = (totalProducts + PRODUCTS_PER_PAGE - 1) / PRODUCTS_PER_PAGE;
            if (totalPages == 0) totalPages = 1;
            page = Math.min(page, totalPages);

            int offset = (page - 1) * PRODUCTS_PER_PAGE;
            products = productService.getByAdvancedQuery(queryStr, PRODUCTS_PER_PAGE, offset);

        } catch (Exception e) {
            System.err.println("Error during search operation: " + e.getMessage());
            e.printStackTrace();
            totalProducts = 0;
            products = new ArrayList<>();
        }

        if (isAjax) {
            SearchResponse searchResponse = new SearchResponse(totalProducts, products, queryStr);
            sendJsonResponse(response, searchResponse);
            return;
        }

        request.setAttribute("query", queryStr);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalPages", (totalProducts + PRODUCTS_PER_PAGE - 1) / (PRODUCTS_PER_PAGE == 0 ? 1 : PRODUCTS_PER_PAGE));
        request.setAttribute("page", page);
        request.setAttribute("products", products);

        request.getRequestDispatcher("/WEB-INF/views/searchView.jsp").forward(request, response);
    }

    private void sendJsonResponse(HttpServletResponse response, SearchResponse searchResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        gson.toJson(searchResponse, out);
    }

    public static class SearchResponse {
        private int totalProducts;
        private List<ProductJson> products;
        private String query;
        private int currentPage;
        private int totalPages;
        private int productsPerPage;

        public SearchResponse(int totalProducts, List<Product> products, String query) {
            this.totalProducts = totalProducts;
            this.query = query;
            this.currentPage = 1;
            this.totalPages = (totalProducts + PRODUCTS_PER_PAGE - 1) / (PRODUCTS_PER_PAGE == 0 ? 1 : PRODUCTS_PER_PAGE);
            this.productsPerPage = PRODUCTS_PER_PAGE;
            this.products = new ArrayList<>();

            List<String> keywords = new ArrayList<>();
            if (query != null && !query.trim().isEmpty()) {
                for (String kw : query.trim().split("\\s+")) {
                    if (!kw.isEmpty() && kw.length() <= 50) {
                        keywords.add(kw);
                    }
                }
            }

            if (products != null) {
                for (Product p : products) {
                    this.products.add(new ProductJson(p, keywords));
                }
            }
        }

        public int getTotalProducts() { return totalProducts; }
        public List<ProductJson> getProducts() { return products; }
        public String getQuery() { return query; }
        public int getCurrentPage() { return currentPage; }
        public int getTotalPages() { return totalPages; }
        public int getProductsPerPage() { return productsPerPage; }
    }

    public static class ProductJson {
        private long id;
        private String name;
        private double price;
        private double discount;
        private String author;
        private String publisher;
        private String imageName;
        private String highlightedName;
        private String highlightedAuthor;

        public ProductJson(Product p, List<String> keywords) {
            this.id = p.getId();
            this.name = p.getName();
            this.price = p.getPrice();
            this.discount = p.getDiscount();
            this.author = p.getAuthor();
            this.publisher = p.getPublisher();
            this.imageName = p.getImageName();
            this.highlightedName = highlightMatches(p.getName(), keywords);
            this.highlightedAuthor = highlightMatches(p.getAuthor(), keywords);
        }

        private static String highlightMatches(String text, List<String> keywords) {
            if (text == null || text.isEmpty()) return text;
            String result = text;
            for (String kw : keywords) {
                if (kw.isEmpty()) continue;
                String lowerText = result.toLowerCase();
                String lowerKw = kw.toLowerCase();
                int idx = lowerText.indexOf(lowerKw);
                if (idx >= 0) {
                    String before = result.substring(0, idx);
                    String match = result.substring(idx, idx + kw.length());
                    String after = result.substring(idx + kw.length());
                    result = before + "\u0000" + match + "\u0001" + after;
                }
            }
            return result.replace("\u0000", "<mark class=\"search-highlight\">").replace("\u0001", "</mark>");
        }

        public long getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public double getDiscount() { return discount; }
        public String getAuthor() { return author; }
        public String getPublisher() { return publisher; }
        public String getImageName() { return imageName; }
        public String getHighlightedName() { return highlightedName; }
        public String getHighlightedAuthor() { return highlightedAuthor; }

        public double getFinalPrice() {
            if (discount > 0) {
                return price * (100 - discount) / 100;
            }
            return price;
        }
    }
}
