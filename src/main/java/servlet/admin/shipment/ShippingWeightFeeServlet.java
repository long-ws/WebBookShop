package servlet.admin.shipment;

import java.io.IOException;
import java.time.LocalDateTime;

import beans.ShippingWeightFee;
import dao.ShippingWeightFeeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShippingWeightFeeServlet", value = "/admin/shippingWeightFee")
public class ShippingWeightFeeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ShippingWeightFeeDAO feeDAO = new ShippingWeightFeeDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        switch (action) {
        case "create":
            createFee(request);
            break;

        case "update":
            updateFee(request);
            break;

        case "delete":
            deleteFee(request);
            break;
        }

        response.sendRedirect(request.getContextPath() + "/admin/shippingMethod");
    }

    private void createFee(HttpServletRequest request) {
        try {
            ShippingWeightFee fee = new ShippingWeightFee();
            fee.setShippingMethodId(Long.parseLong(request.getParameter("shippingMethodId")));
            fee.setZoneType(request.getParameter("zoneType"));
            fee.setMinWeight(Double.parseDouble(request.getParameter("minWeight")));
            fee.setMaxWeight(Double.parseDouble(request.getParameter("maxWeight")));
            fee.setBaseFee(Double.parseDouble(request.getParameter("baseFee")));
            fee.setFeePerKg(Double.parseDouble(request.getParameter("feePerKg")));
            fee.setCreatedAt(LocalDateTime.now());
            fee.setUpdatedAt(LocalDateTime.now());

            feeDAO.insert(fee);
            request.getSession().setAttribute("successMessage", "Đã thêm biểu phí mới!");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Thêm biểu phí thất bại: " + e.getMessage());
        }
    }

    private void updateFee(HttpServletRequest request) {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            ShippingWeightFee fee = feeDAO.getById(id);

            if (fee != null) {
                String methodId = request.getParameter("shippingMethodId");
                if (methodId != null && !methodId.isEmpty()) {
                    fee.setShippingMethodId(Long.parseLong(methodId));
                }
                fee.setZoneType(request.getParameter("zoneType"));
                fee.setMinWeight(Double.parseDouble(request.getParameter("minWeight")));
                fee.setMaxWeight(Double.parseDouble(request.getParameter("maxWeight")));
                fee.setBaseFee(Double.parseDouble(request.getParameter("baseFee")));
                fee.setFeePerKg(Double.parseDouble(request.getParameter("feePerKg")));
                fee.setUpdatedAt(LocalDateTime.now());

                feeDAO.update(fee);
                request.getSession().setAttribute("successMessage", "Đã cập nhật biểu phí!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
        }
    }

    private void deleteFee(HttpServletRequest request) {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            feeDAO.delete(id);
            request.getSession().setAttribute("successMessage", "Đã xóa biểu phí!");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Xóa thất bại!");
        }
    }
}
