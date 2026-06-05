package servlet.client;

import beans.User;
import beans.shipping.Address;
import constants.SessionConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AddressService;

import java.io.IOException;
import java.util.List;

@WebServlet("/addressBook")
public class AddressBookViewServlet extends HttpServlet {
    private final AddressService service = new AddressService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute(SessionConstants.CURRENT_USER);
            if(user != null){
                List<Address> list = service.getAddressesByUserId(user.getId());
                request.setAttribute("addressList", list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/WEB-INF/views/addressBookView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
