package filter;

import java.io.IOException;
import java.util.List;

import beans.Category;
import beans.User;
import dao.CategoryDAO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import utils.CartUtils;

@WebFilter("/*") // Áp dụng cho tất cả URL
public class CategoryFilter implements Filter {

    private CategoryDAO categoryDAO;

    @Override
    public void init(FilterConfig filterConfig) {
        categoryDAO = new CategoryDAO();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Load danh sách category từ database
        List<Category> categories = categoryDAO.getAll();

        // Đưa vào request scope để JSP dùng
        request.setAttribute("categories", categories);

        // Cập nhật cart badge count trong session (cho cả guest và logged-in user)
        if (request instanceof HttpServletRequest httpRequest) {
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                CartUtils.updateCartBadge(session);
            }
        }

        // Tiếp tục chuỗi filter
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
