package servlet.admin.voucher;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import service.CategoryService;
import service.ProductService;

import java.io.IOException;

@WebServlet("/admin/voucherManager/search")
public class SearchVoucherTargets extends HttpServlet {
    private ProductService productService = new ProductService();
    private CategoryService categoryService = new CategoryService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        String query = request.getParameter("query");

        Object results = null;
        if ("product".equals(type)) {
            results = productService.searchByName(query);
        } else if ("category".equals(type)) {
            results = categoryService.searchByName(query);
        }

        String json = new Gson().toJson(results);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}