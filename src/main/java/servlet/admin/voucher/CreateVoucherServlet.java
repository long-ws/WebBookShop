package servlet.admin.voucher;

import beans.Voucher;
import com.google.gson.Gson;
import dto.CategoryDTO;
import dto.ProductDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoryService;
import service.ProductService;
import service.VoucherService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/voucherManager/create")
public class CreateVoucherServlet extends HttpServlet {
    private final VoucherService voucherService = new VoucherService();
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        String query = request.getParameter("query");

        if (type != null) {
            Object results = null;
            if ("product".equals(type)) {
                results = productService.searchDTOByName(query);
            } else if ("category".equals(type)) {
                results = categoryService.searchDTOByName(query);
            }

            String json = new Gson().toJson(results);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
            return;
        }

        request.setAttribute("title", "Thêm voucher mới");
        request.setAttribute("mode", "add");
        request.getRequestDispatcher("/WEB-INF/views/admin/voucherDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String code = request.getParameter("code");
            String name = request.getParameter("name");
            String applyToParam = request.getParameter("applyTo");
            String description = request.getParameter("description");
            String calculationMethodParam = request.getParameter("calculationMethod");
            String valueParam = request.getParameter("value");
            boolean isActive = request.getParameter("isActive") != null;
            String minPurchaseParam = request.getParameter("minPurchase");
            String maxDiscountParam = request.getParameter("maxDiscount");
            String usageLimitParam = request.getParameter("usageLimit");
            String perUserLimitParam = request.getParameter("perUserLimit");
            Timestamp startDate = Timestamp.valueOf(java.time.LocalDateTime.parse(request.getParameter("startDate")));
            Timestamp endDate = Timestamp.valueOf(java.time.LocalDateTime.parse(request.getParameter("endDate")));
            if (code != null) code = code.toUpperCase().trim();

            int calculationMethod = 0;
            if (calculationMethodParam != null) {
                calculationMethod = Integer.parseInt(calculationMethodParam);
            }

            int applyTo = 0;
            if (applyToParam != null) {
                applyTo = Integer.parseInt(applyToParam);
            }

            double value = 0;
            if (valueParam != null && !valueParam.trim().isEmpty()) {
                value = Double.parseDouble(valueParam);
            }
            double minPurchase = 0;
            if (minPurchaseParam != null && !minPurchaseParam.trim().isEmpty()) {
                minPurchase = Double.parseDouble(minPurchaseParam);
            }
            double maxDiscount = 0;
            if (maxDiscountParam != null && !maxDiscountParam.trim().isEmpty()) {
                maxDiscount = Double.parseDouble(maxDiscountParam);
            }
            int usageLimit = 0;
            if (usageLimitParam != null && !usageLimitParam.trim().isEmpty()) {
                usageLimit = Integer.parseInt(usageLimitParam);
            }
            int perUserLimit = 0;
            if (perUserLimitParam != null && !perUserLimitParam.trim().isEmpty()) {
                perUserLimit = Integer.parseInt(perUserLimitParam);
            }

            if (calculationMethod == 0) {
                if (value > 100) {
                    value = 100;
                } else if (value < 0) {
                    value = 0;
                }
            } else if (calculationMethod == 1) {
                maxDiscount = value;
            }

            if (endDate.before(startDate)) {
                request.getSession().setAttribute("errorMessage", "EndDate phải sau startDate!");
                response.sendRedirect(request.getContextPath() + "/admin/voucherManager/create");
                return;
            }
            List<CategoryDTO> selectedCategories = new ArrayList<>();
            List<ProductDTO> selectedProducts = new ArrayList<>();
            String[] categoryIdsParam = request.getParameterValues("categoryIds");
            String[] productIdsParam = request.getParameterValues("productIds");

            if (applyTo == 1) {
                if (productIdsParam != null) {
                    for (String pId : productIdsParam) {
                        ProductDTO dto = new ProductDTO();
                        dto.setId(Long.parseLong(pId));
                        selectedProducts.add(dto);
                    }
                }
            } else if (applyTo == 2) {
                if (categoryIdsParam != null) {
                    for (String cId : categoryIdsParam) {
                        CategoryDTO dto = new CategoryDTO();
                        dto.setId(Long.parseLong(cId));
                        selectedCategories.add(dto);
                    }
                }
            }
            Voucher v = new Voucher();
            v.setCode(code);
            v.setName(name);
            v.setApplyTo(applyTo);
            v.setDescription(description);
            v.setCalculationMethod(calculationMethod);
            v.setValue(value);
            v.setActive(isActive);
            v.setMinPurchase(minPurchase);
            v.setMaxDiscount(maxDiscount);
            v.setUsageLimit(usageLimit);
            v.setPerUserLimit(perUserLimit);
            v.setStartDate(startDate);
            v.setEndDate(endDate);
            v.setCategories(selectedCategories);
            v.setProducts(selectedProducts);

            boolean isCreated = voucherService.createVoucher(v);

            if (isCreated) {
                request.getSession().setAttribute("successMessage", "Thêm voucher thành công!");
                response.sendRedirect(request.getContextPath() + "/admin/voucherManager/view");
            } else {
                request.getSession().setAttribute("errorMessage", "Thêm voucher thất bại!");
                response.sendRedirect(request.getContextPath() + "/admin/voucherManager/create");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/voucherManager/create");
        }
    }
}