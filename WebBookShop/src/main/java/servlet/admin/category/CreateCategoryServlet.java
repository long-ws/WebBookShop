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

@WebServlet(name = "CreateCategoryServlet", value = "/admin/categoryManager/create")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 5, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class CreateCategoryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final CategoryService categoryService = new CategoryService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.getRequestDispatcher("/WEB-INF/views/createCategoryView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String name = request.getParameter("name");
		String descriptionParam = request.getParameter("description");

		Category category = new Category();
		category.setName(name);

		if (descriptionParam == null || descriptionParam.trim().isEmpty()) {
			category.setDescription(null);
		} else {
			category.setDescription(descriptionParam.trim());
		}

		Map<String, List<String>> violations = new HashMap<String, List<String>>();

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
				String imageName = ImageUtils.upload(request);
				if (imageName != null) {
					category.setImageName(imageName);
				}

				categoryService.insert(category);

				request.setAttribute("successMessage", "Thêm thành công!");
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("category", category);
				request.setAttribute("errorMessage", "Thêm thất bại!");
			}
		} else {
			request.setAttribute("category", category);
			request.setAttribute("violations", violations);
		}

		request.getRequestDispatcher("/WEB-INF/views/createCategoryView.jsp").forward(request, response);
	}
}
