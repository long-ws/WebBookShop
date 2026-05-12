package servlet.client;

import beans.vnpay.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/checkoutSuccess")
public class CheckoutSuccessServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, IOException {
        HttpSession session = req.getSession();
        Payment p = (Payment) session.getAttribute("latestPayment");

        if (p == null) {
            res.sendRedirect(req.getContextPath() + "/");
            return;
        }

        req.setAttribute("payment", p);
        req.getRequestDispatcher("/WEB-INF/views/checkoutSuccess.jsp").forward(req, res);
    }
}
