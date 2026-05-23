package servlet.admin.category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import utils.ImageUtils;

@WebServlet(name = "UpdateCategoryServlet", value = "/admin/categoryManager/update")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 5,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class UpdateCategoryServlet extends HttpServlet {

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

        request.setAttribute("category", category);
        request.getRequestDispatcher("/WEB-INF/views/updateCategoryView.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        long id;

        try {
            id = Long.parseLong(idParam);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/categoryManager");
            return;
        }

        String name = request.getParameter("name");
        String descriptionParam = request.getParameter("description");
        String imageNameParam = request.getParameter("imageName");
        String deleteImage = request.getParameter("deleteImage");

        Category category = new Category();
        category.setId(id);
        category.setName(name);

        if (descriptionParam == null || descriptionParam.trim().isEmpty()) {
            category.setDescription(null);
        } else {
            category.setDescription(descriptionParam.trim());
        }

        if (imageNameParam == null || imageNameParam.trim().isEmpty()) {
            category.setImageName(null);
        } else {
            category.setImageName(imageNameParam.trim());
        }

        // ===== VALIDATION =====
        Map<String, List<String>> violations = new HashMap<String, List<String>>();

        // name
        List<String> nameViolations = new ArrayList<String>();
        if (name == null || name.trim().isEmpty()) {
            nameViolations.add("Không để trống tên danh mục");
        } else {
            if (!name.equals(name.trim())) {
                nameViolations.add("Không có dấu cách ở hai đầu");
            }
            if (name.length() > 100) {
                nameViolations.add("Tên danh mục tối đa 100 ký tự");
            }
        }
        violations.put("nameViolations", nameViolations);

        List<String> descriptionViolations = new ArrayList<String>();
        if (category.getDescription() != null && category.getDescription().length() > 350) {
            descriptionViolations.add("Mô tả tối đa 350 ký tự");
        }
        violations.put("descriptionViolations", descriptionViolations);

        int totalViolations = 0;
        for (List<String> list : violations.values()) {
            totalViolations += list.size();
        }

        if (totalViolations == 0) {

            try {
                String oldImageName = category.getImageName();

                if (deleteImage != null && oldImageName != null) {
                    ImageUtils.delete(oldImageName);
                    category.setImageName(null);
                }

                String newImageName = ImageUtils.upload(request);
                if (newImageName != null) {
                    if (oldImageName != null) {
                        ImageUtils.delete(oldImageName);
                    }
                    category.setImageName(newImageName);
                }

                categoryService.update(category);

                request.setAttribute("category", category);
                request.setAttribute("successMessage", "Sửa thành công!");

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("category", category);
                request.setAttribute("errorMessage", "Sửa thất bại!");
            }

        } else {
            request.setAttribute("category", category);
            request.setAttribute("violations", violations);
            request.setAttribute("deleteImage", deleteImage);
        }

        request.getRequestDispatcher("/WEB-INF/views/updateCategoryView.jsp")
                .forward(request, response);
    }
}
