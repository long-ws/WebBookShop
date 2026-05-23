package servlet.vnpay;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import service.PaymentService;

import java.io.IOException;


@WebServlet("/updateExpiredPayment")
public class UpdateExpiredPaymentServlet extends HttpServlet {
private final PaymentService service =  new PaymentService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String param = request.getParameter("orderId");

        if (param != null) {
            try {
                long oId =  Long.parseLong(param);
                service.isPaymentExpired(oId);
                response.setStatus(HttpServletResponse.SC_OK);

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
