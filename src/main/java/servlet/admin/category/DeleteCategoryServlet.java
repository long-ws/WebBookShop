package servlet.admin.category;

import java.io.IOException;

import beans.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import utils.ImageUtils;

@WebServlet(name = "DeleteCategoryServlet", value = "/admin/categoryManager/delete")
public class DeleteCategoryServlet extends HttpServlet {

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

        String successMessage = "Xóa thể loại #" + id + " thành công!";
        String errorMessage = "Xóa thể loại #" + id + " thất bại!";

        try {
            categoryService.delete(id);

            String imageName = category.getImageName();
            if (imageName != null && !imageName.trim().isEmpty()) {
                ImageUtils.delete(imageName);
            }

            request.getSession().setAttribute("successMessage", successMessage);

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", errorMessage);
        }

        response.sendRedirect(request.getContextPath() + "/admin/categoryManager");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}
