package servlet.admin.shipment;

import java.io.IOException;
import java.util.List;

import beans.Province;
import beans.ShippingZone;
import dao.ProvinceDAO;
import dao.ShippingZoneDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProvinceServlet", value = "/admin/province")
public class ProvinceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final ShippingZoneDAO zoneDAO = new ShippingZoneDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Province> provinces = provinceDAO.getAll();
        List<ShippingZone> zones = zoneDAO.getAll();

        request.setAttribute("provinces", provinces);
        request.setAttribute("zones", zones);
        request.getRequestDispatcher("/WEB-INF/views/admin/provinceManagerView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String redirectUrl = request.getContextPath() + "/admin/province";

        if ("create".equals(action)) {
            try {
                Province province = new Province();
                province.setProvinceCode(request.getParameter("provinceCode"));
                province.setProvinceName(request.getParameter("provinceName"));
                province.setRegion(request.getParameter("region"));

                String metroCity = request.getParameter("isMetroCity");
                province.setMetroCity("true".equals(metroCity) || "1".equals(metroCity));

                String zoneId = request.getParameter("zoneId");
                if (zoneId != null && !zoneId.isEmpty()) {
                    province.setShippingZoneId(Long.parseLong(zoneId));
                }

                province.setCreatedAt(java.time.LocalDateTime.now());
                province.setUpdatedAt(java.time.LocalDateTime.now());

                provinceDAO.insert(province);
                request.getSession().setAttribute("successMessage", "Da them tinh/thanh moi!");
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "Them that bai: " + e.getMessage());
            }
        } else if ("update".equals(action)) {
            try {
                long id = Long.parseLong(request.getParameter("id"));
                Province province = provinceDAO.getById(id);

                if (province != null) {
                    province.setProvinceName(request.getParameter("provinceName"));
                    province.setRegion(request.getParameter("region"));

                    String metroCity = request.getParameter("isMetroCity");
                    province.setMetroCity("true".equals(metroCity) || "1".equals(metroCity));

                    String zoneId = request.getParameter("zoneId");
                    if (zoneId != null && !zoneId.isEmpty()) {
                        province.setShippingZoneId(Long.parseLong(zoneId));
                    }

                    province.setUpdatedAt(java.time.LocalDateTime.now());

                    provinceDAO.update(province);
                    request.getSession().setAttribute("successMessage", "Da cap nhat tinh/thanh!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "Cap nhat that bai!");
            }
        } else if ("delete".equals(action)) {
            try {
                long id = Long.parseLong(request.getParameter("id"));
                provinceDAO.delete(id);
                request.getSession().setAttribute("successMessage", "Da xoa tinh/thanh!");
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "Xoa that bai!");
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
