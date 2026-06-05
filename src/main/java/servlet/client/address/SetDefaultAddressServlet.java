package servlet.client.address;

import beans.User;
import constants.SessionConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AddressService;

import java.io.IOException;

@WebServlet("/address/setDefault")
public class SetDefaultAddressServlet extends HttpServlet {
    private final AddressService service = new AddressService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute(SessionConstants.CURRENT_USER);
            if(user != null){
                long addressId = Long.parseLong(request.getParameter("addressId"));
                if(!service.setDefaultAddress(user.getId(), addressId)){
                    response.sendRedirect(request.getContextPath() + "/error");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect(request.getContextPath() + "/addressBook");
    }
}
