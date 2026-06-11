package servlet.client;

import beans.Order;
import beans.Shipment;
import beans.ShippingContact;
import beans.vnpay.Payment;
import dao.ShippingContactDAO;
import dto.CheckoutResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/checkoutSuccess")
public class CheckoutSuccessServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final ShippingContactDAO shippingContactDAO = new ShippingContactDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();

        CheckoutResult result = (CheckoutResult) session.getAttribute("result");

        if(result == null) {
            res.sendRedirect(req.getContextPath() + "/cart");
            return;
        }
        Payment payment = result.getPayment();
        Order order = result.getOrder();
        Shipment shipment = result.getShipment();

        req.setAttribute("payment", payment);
        req.setAttribute("order", order);
        req.setAttribute("subtotal", order.getTotalProductPrice());
        req.setAttribute("shipment", shipment);
        req.setAttribute("discountOrderAmount", order.getProductDiscount());
        req.setAttribute("discountShipAmount", order.getShipDiscount());
        // Fetch shipping contact info
        ShippingContact shippingContact = null;
        if (shipment != null) {
            java.util.List<ShippingContact> contacts = shippingContactDAO.getByShipmentId(shipment.getId());
            if (contacts != null && !contacts.isEmpty()) {
                shippingContact = contacts.get(0);
            }
        }
        req.setAttribute("shippingContact", shippingContact);
        session.removeAttribute("result");
        req.getRequestDispatcher("/WEB-INF/views/checkoutSuccess.jsp").forward(req, res);
    }
}
