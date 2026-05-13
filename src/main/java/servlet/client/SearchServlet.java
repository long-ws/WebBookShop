package servlet.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import beans.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.SearchResponse;
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processSearchRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processSearchRequest(request, response);
	}

	private void processSearchRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String queryStr = request.getParameter("q");
		String ajaxParam = request.getParameter("ajax");
		boolean isAjax = "true".equalsIgnoreCase(ajaxParam);

		if (queryStr == null || queryStr.trim().isEmpty()) {
			if (isAjax) {
				sendJsonResponse(response, new SearchResponse());
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
			if (totalPages == 0)
				totalPages = 1;
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
		int calcTotalPages = (totalProducts + PRODUCTS_PER_PAGE - 1) / (PRODUCTS_PER_PAGE == 0 ? 1 : PRODUCTS_PER_PAGE);
		request.setAttribute("totalPages", calcTotalPages);
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
}
