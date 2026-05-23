package servlet.admin.product;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Category;
import beans.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import service.ProductService;
import utils.ImageUtils;

@WebServlet(name = "UpdateProductServlet", value = "/admin/productManager/update")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 5,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class UpdateProductServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/productManager");
            return;
        }

        Product product = productService.getById(id);
        if (product == null) {
            response.sendRedirect(request.getContextPath() + "/admin/productManager");
            return;
        }

        List<Category> categories = categoryService.getAll();
        Category category = categoryService.getByProductId(id);

        request.setAttribute("product", product);
        request.setAttribute("categories", categories);
        if (category != null) {
            request.setAttribute("categoryId", category.getId());
        }

        request.getRequestDispatcher("/WEB-INF/views/updateProductView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Product product = new Product();
        Map<String, List<String>> violations = new HashMap<>();

        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/productManager");
            return;
        }
        product.setId(id);

        product.setName(request.getParameter("name"));
        product.setAuthor(request.getParameter("author"));
        product.setPublisher(request.getParameter("publisher"));
        product.setDescription(emptyToNull(request.getParameter("description")));
        product.setImageName(emptyToNull(request.getParameter("imageName")));
        product.setUpdatedAt(LocalDateTime.now());

        product.setPrice(parseDouble(request.getParameter("price")));
        product.setDiscount(parseDouble(request.getParameter("discount")));
        product.setQuantity(parseInt(request.getParameter("quantity")));
        product.setTotalBuy(parseInt(request.getParameter("totalBuy")));
        product.setPages(parseInt(request.getParameter("pages")));
        product.setYearPublishing(parseInt(request.getParameter("yearPublishing")));
        product.setShop(parseInt(request.getParameter("shop")));

        product.setStartsAt(parseDate(request.getParameter("startsAt")));
        product.setEndsAt(parseDate(request.getParameter("endsAt")));

        long categoryId = 0;
        try {
            categoryId = Long.parseLong(request.getParameter("category"));
        } catch (Exception ignored) {}

        if (categoryId == 0) {
            addViolation(violations, "categoryViolations", "Phải chọn thể loại cho sản phẩm");
        }

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            addViolation(violations, "nameViolations", "Tên sản phẩm không được để trống");
        }
        if (product.getPrice() <= 0) {
            addViolation(violations, "priceViolations", "Giá phải lớn hơn 0");
        }
        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            addViolation(violations, "discountViolations", "Khuyến mãi không hợp lệ");
        }

        String deleteImage = request.getParameter("deleteImage");

        if (violations.isEmpty()) {
            String currentImage = product.getImageName();

            if (deleteImage != null && currentImage != null) {
                ImageUtils.delete(currentImage);
                product.setImageName(null);
            }

            String newImage = ImageUtils.upload(request);
            if (newImage != null) {
                if (currentImage != null) {
                    ImageUtils.delete(currentImage);
                }
                product.setImageName(newImage);
            }

            try {
                productService.update(product);

                Category oldCategory = categoryService.getByProductId(id);
                if (oldCategory != null) {
                    productService.updateProductCategory(id, categoryId);
                } else {
                    productService.insertProductCategory(id, categoryId);
                }

                request.setAttribute("successMessage", "Sửa thành công!");
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Sửa thất bại!");
            }
        } else {
            request.setAttribute("violations", violations);
            request.setAttribute("deleteImage", deleteImage);
        }

        request.setAttribute("product", product);
        request.setAttribute("categories", categoryService.getAll());
        request.setAttribute("categoryId", categoryId);

        request.getRequestDispatcher("/WEB-INF/views/updateProductView.jsp").forward(request, response);
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s;
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private LocalDateTime parseDate(String s) {
        try {
            return (s == null || s.trim().isEmpty()) ? null : LocalDateTime.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private void addViolation(Map<String, List<String>> map, String key, String message) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(message);
    }
}
