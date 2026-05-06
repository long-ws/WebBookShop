package servlet.admin.category;

import java.io.IOException;

import beans.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import utils.TextUtils;

@WebServlet(name = "CategoryDetailServlet", value = "/admin/categoryManager/detail")
public class CategoryDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        long id;

        try {
            id = Long.parseLong(idParam);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/categoryManager");
            return;
        }

        Category category;
        try {
            category = categoryService.getById(id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/categoryManager");
            return;
        }

        if (category == null) {
            response.sendRedirect(request.getContextPath() + "/admin/categoryManager");
            return;
        }

        String description = category.getDescription();
        category.setDescription(
                TextUtils.toParagraph(description == null ? "" : description)
        );

        request.setAttribute("category", category);
        request.getRequestDispatcher("/WEB-INF/views/categoryDetailView.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}
