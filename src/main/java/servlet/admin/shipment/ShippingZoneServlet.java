package servlet.admin.shipment;

import java.io.IOException;
import java.time.LocalDateTime;

import beans.ShippingZone;
import dao.ShippingZoneDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShippingZoneServlet", value = "/admin/shippingZone")
public class ShippingZoneServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ShippingZoneDAO zoneDAO = new ShippingZoneDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        switch (action) {
        case "create":
            createZone(request);
            break;

        case "update":
            updateZone(request);
            break;

        case "delete":
            deleteZone(request);
            break;
        }

        response.sendRedirect(request.getContextPath() + "/admin/shippingMethod");
    }

    private void createZone(HttpServletRequest request) {
        try {
            ShippingZone zone = new ShippingZone();
            zone.setZoneName(request.getParameter("zoneName"));
            zone.setZoneType(request.getParameter("zoneType"));
            zone.setDescription(request.getParameter("description"));
            
            String baseFee = request.getParameter("baseFee");
            zone.setBaseFee(baseFee != null && !baseFee.isEmpty() ? Double.parseDouble(baseFee) : 0);
            
            String pricePerKg = request.getParameter("pricePerKg");
            zone.setPricePerKg(pricePerKg != null && !pricePerKg.isEmpty() ? Double.parseDouble(pricePerKg) : 0);
            
            zone.setPricePerVolume(0);
            
            String daysMin = request.getParameter("estimatedDaysMin");
            zone.setEstimatedDaysMin(daysMin != null && !daysMin.isEmpty() ? Integer.parseInt(daysMin) : 1);
            
            String daysMax = request.getParameter("estimatedDaysMax");
            zone.setEstimatedDaysMax(daysMax != null && !daysMax.isEmpty() ? Integer.parseInt(daysMax) : 5);
            
            zone.setStatus(1);
            zone.setCreatedAt(LocalDateTime.now());
            zone.setUpdatedAt(LocalDateTime.now());

            zoneDAO.insert(zone);
            request.getSession().setAttribute("successMessage", "Đã thêm khu vực vận chuyển mới!");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Thêm khu vực thất bại: " + e.getMessage());
        }
    }

    private void updateZone(HttpServletRequest request) {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            ShippingZone zone = zoneDAO.getById(id);

            if (zone != null) {
                zone.setZoneName(request.getParameter("zoneName"));
                zone.setZoneType(request.getParameter("zoneType"));
                zone.setDescription(request.getParameter("description"));
                
                String baseFee = request.getParameter("baseFee");
                if (baseFee != null && !baseFee.isEmpty()) {
                    zone.setBaseFee(Double.parseDouble(baseFee));
                }
                
                String pricePerKg = request.getParameter("pricePerKg");
                if (pricePerKg != null && !pricePerKg.isEmpty()) {
                    zone.setPricePerKg(Double.parseDouble(pricePerKg));
                }
                
                String daysMin = request.getParameter("estimatedDaysMin");
                if (daysMin != null && !daysMin.isEmpty()) {
                    zone.setEstimatedDaysMin(Integer.parseInt(daysMin));
                }
                
                String daysMax = request.getParameter("estimatedDaysMax");
                if (daysMax != null && !daysMax.isEmpty()) {
                    zone.setEstimatedDaysMax(Integer.parseInt(daysMax));
                }
                
                zone.setUpdatedAt(LocalDateTime.now());

                zoneDAO.update(zone);
                request.getSession().setAttribute("successMessage", "Đã cập nhật khu vực vận chuyển!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
        }
    }

    private void deleteZone(HttpServletRequest request) {
        try {
            long id = Long.parseLong(request.getParameter("id"));
            zoneDAO.delete(id);
            request.getSession().setAttribute("successMessage", "Đã xóa khu vực vận chuyển!");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Xóa thất bại!");
        }
    }
}
