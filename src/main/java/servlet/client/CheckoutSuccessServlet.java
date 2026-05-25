package servlet.client;

import beans.Order;
import beans.OrderItem;
import beans.Product;
import beans.Shipment;
import beans.ShippingContact;
import beans.User;
import beans.vnpay.Payment;
import dao.ShippingContactDAO;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/checkoutSuccess")
public class CheckoutSuccessServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();
    private final ProductService productService = new ProductService();
    private final PaymentService paymentService = new PaymentService();
    private final ShipmentService shipmentService = new ShipmentService();
    private final ShippingContactDAO shippingContactDAO = new ShippingContactDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // Lay tu session truoc
        Payment payment = (Payment) session.getAttribute("latestPayment");
        Long orderIdFromSession = (Long) session.getAttribute("latestOrderId");
        User user = (User) session.getAttribute("currentUser");

        System.out.println("========== [CheckoutSuccess] START ==========");
        System.out.println("[CheckoutSuccess] Payment from session: " + (payment != null ? payment.getVnpTxnRef() : "null"));
        System.out.println("[CheckoutSuccess] OrderId from session: " + orderIdFromSession);
        System.out.println("[CheckoutSuccess] User from session: " + (user != null ? user.getEmail() : "null"));

        // Lay orderId tu URL parameter (uu tien cao nhat vi session co the bi mat khi redirect)
        String orderIdParam = req.getParameter("orderId");
        System.out.println("[CheckoutSuccess] OrderId from URL: " + orderIdParam);

        Long orderIdFromUrl = null;
        if (orderIdParam != null && !orderIdParam.trim().isEmpty()) {
            try {
                orderIdFromUrl = Long.parseLong(orderIdParam.trim());
                System.out.println("[CheckoutSuccess] Parsed orderId from URL: " + orderIdFromUrl);
            } catch (NumberFormatException e) {
                System.out.println("[CheckoutSuccess] Invalid orderId param: " + orderIdParam);
            }
        }

        // Neu khong co payment tu session, thu lay tu database
        if (payment == null) {
            Long targetOrderId = orderIdFromUrl != null ? orderIdFromUrl : orderIdFromSession;
            if (targetOrderId != null) {
                System.out.println("[CheckoutSuccess] Payment not in session, trying to fetch from DB with orderId: " + targetOrderId);
                payment = paymentService.getInitPaymentByOrderId(targetOrderId);
                System.out.println("[CheckoutSuccess] Payment from DB: " + (payment != null ? payment.getVnpTxnRef() : "null"));
            }
        }

        // Xac dinh orderId de su dung
        long orderId = 0;
        if (payment != null) {
            orderId = payment.getOrderId();
        } else if (orderIdFromUrl != null) {
            orderId = orderIdFromUrl;
        } else if (orderIdFromSession != null) {
            orderId = orderIdFromSession;
        }

        System.out.println("[CheckoutSuccess] Final orderId to use: " + orderId);

        // Neu van khong co orderId, thu lay tu database bang orderService
        if (orderId == 0 && user != null) {
            System.out.println("[CheckoutSuccess] No orderId found, trying to find latest order for user: " + user.getId());
            try {
                List<Order> userOrders = orderService.getByUserId(user.getId());
                if (userOrders != null && !userOrders.isEmpty()) {
                    // Lay order moi nhat
                    Order latestOrder = userOrders.get(0);
                    for (Order o : userOrders) {
                        if (o.getCreatedAt() != null && latestOrder.getCreatedAt() != null
                                && o.getCreatedAt().isAfter(latestOrder.getCreatedAt())) {
                            latestOrder = o;
                        }
                    }
                    orderId = latestOrder.getId();
                    System.out.println("[CheckoutSuccess] Found latest order from DB: " + orderId);
                    payment = paymentService.getInitPaymentByOrderId(orderId);
                }
            } catch (Exception e) {
                System.out.println("[CheckoutSuccess] Error finding latest order: " + e.getMessage());
            }
        }

        if (orderId == 0) {
            System.out.println("[CheckoutSuccess] No orderId found!");
            System.out.println("[CheckoutSuccess] User: " + (user != null ? user.getId() : "NULL"));
            System.out.println("[CheckoutSuccess] Session attributes:");
            java.util.Enumeration<String> attrNames = session.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String name = attrNames.nextElement();
                System.out.println("  - " + name + ": " + session.getAttribute(name));
            }
            // Luu thong bao loi vao session truoc khi redirect
            session.setAttribute("errorMessage", "Không tìm thấy đơn hàng. Vui lòng kiểm tra lại lịch sử đặt hàng.");
            if (user != null) {
                res.sendRedirect(req.getContextPath() + "/order");
            } else {
                res.sendRedirect(req.getContextPath() + "/signin");
            }
            return;
        }

        System.out.println("[CheckoutSuccess] Using orderId: " + orderId);

        Order order = null;
        List<OrderItem> orderItems = new ArrayList<>();
        Shipment shipment = null;
        double subtotal = 0;

        try {
            order = orderService.getById(orderId);
            System.out.println("[CheckoutSuccess] Order retrieved: " + (order != null ? "YES - ID: " + order.getId() : "NO"));

            if (order != null) {
                orderItems = orderItemService.getByOrderId(order.getId());
                System.out.println("[CheckoutSuccess] Order items count: " + orderItems.size());

                subtotal = 0;
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

                double deliveryPrice = order.getDeliveryPrice() > 0 ? order.getDeliveryPrice() : 0;
                double totalOrderPrice = subtotal + deliveryPrice;
                order.setTotalPrice(totalOrderPrice);

                shipment = shipmentService.getByOrderIdWithDetails(order.getId());
                System.out.println("[CheckoutSuccess] Shipment found: " + (shipment != null ? "YES - Code: " + shipment.getTrackingCode() : "NO"));
            } else {
                System.out.println("[CheckoutSuccess] Order is NULL! Trying to find by user...");
                // Thu tim order theo user
                if (user != null) {
                    List<Order> userOrders = orderService.getByUserId(user.getId());
                    if (userOrders != null && !userOrders.isEmpty()) {
                        order = userOrders.get(0);
                        System.out.println("[CheckoutSuccess] Found order by user: " + order.getId());
                        
                        orderItems = orderItemService.getByOrderId(order.getId());
                        subtotal = 0;
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
                        double deliveryPrice = order.getDeliveryPrice() > 0 ? order.getDeliveryPrice() : 0;
                        order.setTotalPrice(subtotal + deliveryPrice);
                        shipment = shipmentService.getByOrderIdWithDetails(order.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Xoa session attributes sau khi su dung
        session.removeAttribute("latestPayment");
        session.removeAttribute("latestOrderId");

        req.setAttribute("payment", payment);
        req.setAttribute("order", order);
        req.setAttribute("orderItems", orderItems);
        req.setAttribute("subtotal", subtotal);
        req.setAttribute("totalOrderPrice", order != null ? order.getTotalPrice() : 0);
        req.setAttribute("user", user);
        req.setAttribute("shipment", shipment);
        
        // Fetch shipping contact info
        ShippingContact shippingContact = null;
        if (shipment != null) {
            java.util.List<ShippingContact> contacts = shippingContactDAO.getByShipmentId(shipment.getId());
            if (contacts != null && !contacts.isEmpty()) {
                shippingContact = contacts.get(0);
            }
        }
        req.setAttribute("shippingContact", shippingContact);
        System.out.println("[CheckoutSuccess] ShippingContact found: " + (shippingContact != null ? "YES - " + shippingContact.getContactName() : "NO"));
        
        System.out.println("[CheckoutSuccess] Forwarding to checkoutSuccess.jsp");
        System.out.println("========== [CheckoutSuccess] END ==========");
        req.getRequestDispatcher("/WEB-INF/views/checkoutSuccess.jsp").forward(req, res);
    }
}
