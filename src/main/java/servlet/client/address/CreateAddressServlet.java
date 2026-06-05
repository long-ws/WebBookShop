package servlet.client.address;

import beans.User;
import beans.shipping.Address;
import constants.SessionConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AddressService;

import java.io.IOException;

@WebServlet("/address/create")
public class CreateAddressServlet extends HttpServlet {
    private final AddressService service = new AddressService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(SessionConstants.CURRENT_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/addressBook");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/fragments/addressForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = (User) request.getSession().getAttribute(SessionConstants.CURRENT_USER);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/addressBook");
                return;
            }

            String fullname = request.getParameter("fullname");
            String phone = request.getParameter("phone");
            String provinceName = request.getParameter("provinceName");
            String districtName = request.getParameter("districtName");
            String wardName = request.getParameter("wardName");
            String detail = request.getParameter("detail");

            if (fullname == null || fullname.trim().isEmpty() ||
                    phone == null || phone.trim().isEmpty() ||
                    provinceName == null || provinceName.trim().isEmpty() ||
                    districtName == null || districtName.trim().isEmpty() ||
                    wardName == null || wardName.trim().isEmpty() ||
                    detail == null || detail.trim().isEmpty()) {

                response.sendRedirect(request.getContextPath() + "/addressBook?error=missing_fields");
                return;
            }

            String phoneRegex = "^0\\d{8,10}$";
            if (!phone.trim().matches(phoneRegex)) {
                response.sendRedirect(request.getContextPath() + "/addressBook?error=invalid_phone");
                return;
            }

            Address address = new Address();
            address.setUserId(user.getId());
            address.setFullname(fullname.trim());
            address.setPhone(phone.trim());
            address.setProvince(provinceName.trim());
            address.setDistrict(districtName.trim());
            address.setWard(wardName.trim());
            address.setAddressDetail(detail.trim());

            if (!service.createAddress(address)) {
                response.sendRedirect(request.getContextPath() + "/error");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/addressBook");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
