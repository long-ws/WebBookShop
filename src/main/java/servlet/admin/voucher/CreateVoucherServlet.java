package servlet.admin.voucher;

import beans.Voucher;
import beans.VoucherCategory;
import beans.VoucherProduct;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.VoucherCategoryService;
import service.VoucherProductService;
import service.VoucherService;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/admin/voucherManager/create")
public class CreateVoucherServlet extends HttpServlet {
    private final VoucherService service = new VoucherService();
    private final VoucherProductService p =  new VoucherProductService();
    private final VoucherCategoryService  c = new VoucherCategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
            String applyTo =  request.getParameter("applyTo");
            String description = request.getParameter("description");
            String calculationMethod = request.getParameter("calculationMethod");
            String valueParam = request.getParameter("value");
            boolean isActive = request.getParameter("isActive") != null;
            String minPurchaseParam = request.getParameter("minPurchase");
            String maxDiscountParam = request.getParameter("maxDiscount");
            String usageLimitParam = request.getParameter("usageLimit");
            String perUserLimitParam = request.getParameter("perUserLimit");
            LocalDateTime startDate = LocalDateTime.parse(request.getParameter("startDate"));
            LocalDateTime endDate = LocalDateTime.parse(request.getParameter("endDate"));

            if (code != null) code = code.toUpperCase().trim();
            if(!calculationMethod.equalsIgnoreCase("PERCENT") && !calculationMethod.equalsIgnoreCase("FIXED")){
                calculationMethod = "FIXED";
            }
            if(!applyTo.equalsIgnoreCase("ORDER") && !applyTo.equalsIgnoreCase("SHIPPING")){
                applyTo = "ORDER";
            }
            double value = 0;
            if(valueParam != null && !valueParam.trim().isEmpty()){
                value =  Double.parseDouble(valueParam);
            }
            double minPurchase = 0;
            if(minPurchaseParam != null && !minPurchaseParam.trim().isEmpty()){
                minPurchase =  Double.parseDouble(minPurchaseParam);
            }
            double maxDiscount = 0;
            if(maxDiscountParam != null && !maxDiscountParam.trim().isEmpty()){
                maxDiscount =  Double.parseDouble(maxDiscountParam);
            }
            int usageLimit = 0;
            if(usageLimitParam != null && !usageLimitParam.trim().isEmpty()){
                usageLimit =  Integer.parseInt(usageLimitParam);
            }
            int perUserLimit = 0;
            if(perUserLimitParam != null && !perUserLimitParam.trim().isEmpty()){
                perUserLimit =  Integer.parseInt(perUserLimitParam);
            }

            if(calculationMethod.equals("PERCENT")){
                if(value > 100){
                    value = 100;
                }else if(value < 0){
                    value = 0;
                }
            }
            if (endDate.isBefore(startDate)) {
                request.getSession().setAttribute("errorMessage", "EndDate phải sau startDate!");
                response.sendRedirect(request.getContextPath() + "/admin/voucherManager/create");
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

            long voucherId =  service.insert(v);

            String[] categoryIds = request.getParameterValues("categoryIds");
            String[] productIds = request.getParameterValues("productIds");


            if (categoryIds != null) {
                for (String cId : categoryIds) {
                    VoucherCategory o =  new VoucherCategory();
                    o.setVoucherId(voucherId);
                    o.setCategoryId(Long.parseLong(cId));
                    c.insert(o);
                }
            }

            if (productIds != null) {
                for (String pId : productIds) {
                    VoucherProduct o =  new VoucherProduct();
                    o.setVoucherId(voucherId);
                    o.setProductId(Long.parseLong(pId));
                    p.insert(o);
                }
            }

            request.getSession().setAttribute("successMessage", "Thêm voucher id " + voucherId + "thành công!");
            response.sendRedirect(request.getContextPath() + "/admin/voucherManager");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/voucherManager/create");
        }
    }
}