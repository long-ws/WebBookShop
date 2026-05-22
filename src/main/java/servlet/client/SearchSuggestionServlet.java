package servlet.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.ProductDAO;
import dto.ProductSuggestionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "SearchSuggestionServlet", value = "/searchSuggestion")
public class SearchSuggestionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final ProductDAO productDAO = new ProductDAO();
    private final Gson gson = new GsonBuilder().create();

    private static final int DEFAULT_LIMIT = 8;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("q");
        String limitParam = request.getParameter("limit");

        int limit = DEFAULT_LIMIT;
        try {
            if (limitParam != null && !limitParam.trim().isEmpty()) {
                int parsed = Integer.parseInt(limitParam.trim());
                limit = Math.max(1, Math.min(20, parsed));
            }
        } catch (NumberFormatException ignored) {}

        List<ProductSuggestionDTO> suggestions;
        try {
            suggestions = productDAO.getSearchSuggestions(query, limit);
        } catch (Exception e) {
            e.printStackTrace();
            suggestions = List.of();
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        gson.toJson(suggestions, out);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
