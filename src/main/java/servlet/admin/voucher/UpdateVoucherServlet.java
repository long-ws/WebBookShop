package servlet.admin.voucher;

import beans.Voucher;
import dto.CategoryDTO;
import dto.ProductDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.VoucherService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/voucherManager/update")
public class UpdateVoucherServlet extends HttpServlet {
    private final VoucherService service = new VoucherService();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long vId = Long.parseLong(request.getParameter("id"));
            Voucher voucher = service.getVoucherWithRelations(vId);

            if (voucher != null) {
                request.setAttribute("title", "Update voucher");
                request.setAttribute("mode", "edit");
                request.setAttribute("voucher", voucher);
                request.getRequestDispatcher("/WEB-INF/views/admin/voucherDetail.jsp").forward(request, response);
                return;
            }
            request.getSession().setAttribute("errorMessage", "Không tìm thấy voucher!");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Id không hợp lệ!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/vouchers");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            String code = request.getParameter("code");
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            int applyTo = Integer.parseInt(request.getParameter("applyTo"));
            int calculationMethod = Integer.parseInt(request.getParameter("calculationMethod"));
            double value = Double.parseDouble(request.getParameter("value"));
            double minPurchase = Double.parseDouble(request.getParameter("minPurchase"));
            double maxDiscount = Double.parseDouble(request.getParameter("maxDiscount"));
            int usageLimit = Integer.parseInt(request.getParameter("usageLimit"));
            int perUserLimit = Integer.parseInt(request.getParameter("perUserLimit"));
            boolean isActive = request.getParameter("isActive") != null;
            Timestamp startDate = Timestamp.valueOf(LocalDateTime.parse(request.getParameter("startDate"), FORMATTER));
            Timestamp endDate = Timestamp.valueOf(LocalDateTime.parse(request.getParameter("endDate"), FORMATTER));

            if (code != null) code = code.toUpperCase().trim();

            if (calculationMethod == 1) {
                maxDiscount = value;
            } else if (calculationMethod == 0) {
                if (value > 100) value = 100;
                else if (value < 0) value = 0;
            }

            if (endDate.before(startDate)) {
                request.getSession().setAttribute("errorMessage", "Ngày kết thúc phải sau ngày bắt đầu!");
                response.sendRedirect(request.getContextPath() + "/admin/voucherManager/update?id=" + id);
                return;
            }
            List<CategoryDTO> categories = new ArrayList<>();
            List<ProductDTO> products = new ArrayList<>();

            String[] categoryIdsArr = request.getParameterValues("categoryIds");
            String[] productIdsArr = request.getParameterValues("productIds");
            if (applyTo == 1) {
                if (productIdsArr != null) {
                    for (String prodId : productIdsArr) {
                        ProductDTO dto = new ProductDTO();
                        dto.setId(Long.parseLong(prodId));
                        products.add(dto);
                    }
                }
            } else if (applyTo == 2) {
                if (categoryIdsArr != null) {
                    for (String catId : categoryIdsArr) {
                        CategoryDTO dto = new CategoryDTO();
                        dto.setId(Long.parseLong(catId));
                        categories.add(dto);
                    }
                }
            }
            Voucher voucher = new Voucher();
            voucher.setId(id);
            voucher.setCode(code);
            voucher.setName(name);
            voucher.setDescription(description);
            voucher.setApplyTo(applyTo);
            voucher.setCalculationMethod(calculationMethod);
            voucher.setValue(value);
            voucher.setMinPurchase(minPurchase);
            voucher.setMaxDiscount(maxDiscount);
            voucher.setUsageLimit(usageLimit);
            voucher.setPerUserLimit(perUserLimit);
            voucher.setActive(isActive);
            voucher.setStartDate(startDate);
            voucher.setEndDate(endDate);
            voucher.setCategories(categories);
            voucher.setProducts(products);

            boolean isUpdated = service.updateVoucher(voucher);
            if (isUpdated) {
                request.getSession().setAttribute("successMessage", "Update voucher thành công!");
                response.sendRedirect(request.getContextPath() + "/admin/voucherManager/view");
                return;
            } else {
                request.getSession().setAttribute("errorMessage", "Update lỗi tại database!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Xảy ra lỗi trong quá trình update!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/voucherManager/update?id=" + request.getParameter("id"));
    }
}