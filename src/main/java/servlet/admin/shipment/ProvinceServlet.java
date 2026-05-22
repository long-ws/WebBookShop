package servlet.admin.shipment;

import java.io.IOException;
import java.time.LocalDateTime;

import beans.Province;
import dao.ProvinceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProvinceServlet", value = "/admin/province")
public class ProvinceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ProvinceDAO provinceDAO = new ProvinceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/admin/shippingMethod");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        switch (action) {
        case "create":
            createProvince(request);
            break;

        case "update":
            updateProvince(request);
            break;

        case "delete":
            deleteProvince(request);
            break;
        }

        response.sendRedirect(request.getContextPath() + "/admin/shippingMethod");
    }

    private void createProvince(HttpServletRequest request) {
        try {
            Province province = new Province();
            province.setProvinceCode(request.getParameter("provinceCode"));
            province.setProvinceName(request.getParameter("provinceName"));
            province.setProvinceType(request.getParameter("provinceType"));
            
            String zoneId = request.getParameter("shippingZoneId");
            if (zoneId != null && !zoneId.isEmpty()) {
                province.setShippingZoneId(Long.parseLong(zoneId));
            }
            
            province.setRegion(request.getParameter("region"));
            
            String metroCity = request.getParameter("isMetroCity");
            province.setMetroCity("true".equals(metroCity));
            
            province.setCreatedAt(LocalDateTime.now());
            province.setUpdatedAt(LocalDateTime.now());

            provinceDAO.insert(province);
            request.getSession().setAttribute("successMessage", "Đã thêm tỉnh/thành mới!");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Thêm tỉnh/thành thất bại: " + e.getMessage());
        }
    }

    private void updateProvince(HttpServletRequest request) {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            Province province = provinceDAO.getById(id);

            if (province != null) {
                province.setProvinceCode(request.getParameter("provinceCode"));
                province.setProvinceName(request.getParameter("provinceName"));
                province.setProvinceType(request.getParameter("provinceType"));

                String zoneId = request.getParameter("shippingZoneId");
                if (zoneId != null && !zoneId.isEmpty()) {
                    province.setShippingZoneId(Long.parseLong(zoneId));
                } else {
                    province.setShippingZoneId(0);
                }

                province.setRegion(request.getParameter("region"));

                String metroCity = request.getParameter("isMetroCity");
                province.setMetroCity("true".equals(metroCity));

                province.setUpdatedAt(LocalDateTime.now());

                provinceDAO.update(province);
                request.getSession().setAttribute("successMessage", "Đã cập nhật tỉnh/thành!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
        }
    }

    private void deleteProvince(HttpServletRequest request) {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            provinceDAO.delete(id);
            request.getSession().setAttribute("successMessage", "Đã xóa tỉnh/thành!");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Xóa thất bại!");
        }
    }
}
