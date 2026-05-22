package servlet.client;

import beans.Order;
import beans.OrderItem;
import beans.Product;
import beans.Shipment;
import beans.User;
import beans.vnpay.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.OrderItemService;
import service.OrderService;
import service.PaymentService;
import service.ProductService;
import service.ShipmentService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "InvoiceServlet", value = "/invoice")
public class InvoiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();
    private final ProductService productService = new ProductService();
    private final PaymentService paymentService = new PaymentService();
    private final ShipmentService shipmentService = new ShipmentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("currentUser");

        long orderId = 0;
        try {
            orderId = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            System.out.println("[InvoiceServlet] Invalid order ID: " + request.getParameter("id"));
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        System.out.println("[InvoiceServlet] Loading invoice for orderId: " + orderId);

        Order order = null;
        List<OrderItem> orderItems = new ArrayList<>();
        User user = null;
        Payment payment = null;
        Shipment shipment = null;
        double subtotal = 0;

        try {
            order = orderService.getById(orderId);
            System.out.println("[InvoiceServlet] Order found: " + (order != null ? "yes" : "no"));

            if (order != null) {
                System.out.println("[InvoiceServlet] Order userId: " + order.getUserId() + ", sessionUserId: " + (sessionUser != null ? sessionUser.getId() : "null"));

                if (sessionUser != null && sessionUser.getId() == order.getUserId()) {
                    user = sessionUser;
                }

                orderItems = orderItemService.getByOrderId(orderId);
                System.out.println("[InvoiceServlet] Order items count: " + orderItems.size());

                for (OrderItem item : orderItems) {
                    try {
                        Product product = productService.getById(item.getProductId());
                        item.setProduct(product);
                    } catch (Exception e) {
                        item.setProduct(new Product());
                    }

                    double itemPrice = item.getDiscount() > 0
                            ? item.getPrice() * (100 - item.getDiscount()) / 100.0
                            : item.getPrice();
                    subtotal += itemPrice * item.getQuantity();
                }

                payment = paymentService.getInitPaymentByOrderId(orderId);
                System.out.println("[InvoiceServlet] Payment found: " + (payment != null ? payment.getVnpTxnRef() : "null"));

                // Calculate total = subtotal + deliveryPrice
                double totalOrderPrice = subtotal + order.getDeliveryPrice();
                request.setAttribute("totalOrderPrice", totalOrderPrice);

                shipment = shipmentService.getByOrderIdWithDetails(orderId);
                System.out.println("[InvoiceServlet] Shipment found: " + (shipment != null ? shipment.getTrackingCode() : "null"));

                // Convert LocalDateTime to Date for JSP fmt:formatDate
                if (order.getCreatedAt() != null) {
                    request.setAttribute("orderCreatedAt", Date.from(order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
                }
                if (payment != null && payment.getCreatedAt() != null) {
                    request.setAttribute("paymentCreatedAt", new Date(payment.getCreatedAt().getTime()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (order == null) {
            System.out.println("[InvoiceServlet] Order not found, redirecting");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.setAttribute("order", order);
        request.setAttribute("orderItems", orderItems);
        request.setAttribute("user", user);
        request.setAttribute("payment", payment);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("shipment", shipment);
        request.getRequestDispatcher("/WEB-INF/views/invoiceView.jsp").forward(request, response);
    }
}
