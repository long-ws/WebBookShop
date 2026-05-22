package servlet.vnpay;

import java.io.IOException;

import beans.User;
import beans.vnpay.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.PaymentService;

@WebServlet("/vnpay/checkout")
public class PaymentViewServlet extends HttpServlet {
    PaymentService service =  new PaymentService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String vnpTxnRef = req.getParameter("vnpTxnRef");

        if (vnpTxnRef == null || vnpTxnRef.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/error");
            return;
        }

        Payment p = null;
        try{
            p = service.getInitPayment(vnpTxnRef);
        }catch(Exception e){
            e.printStackTrace();
        }
        if (p != null) {
            req.setAttribute("payment", p);
            String vnpMessage = VNPConfig.getResponseMessage(p.getVnpResponseCode());
            req.setAttribute("vnpMessage", vnpMessage);
        }else{
            resp.sendRedirect(req.getContextPath() + "/cart?error=not_found");
            return;
        }

        User u = null;
        try{
            u =  (User) req.getSession().getAttribute(constants.SessionConstants.CURRENT_USER);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(u==null || u.getId() != p.getUserId()){
            resp.sendRedirect(req.getContextPath() + "/error");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/vnpay/paymentView.jsp").forward(req, resp);
    }
}