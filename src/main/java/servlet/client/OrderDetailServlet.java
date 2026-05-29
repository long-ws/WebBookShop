package servlet.client;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import beans.Order;
import beans.OrderItem;
import beans.Product;
import beans.Shipment;
import beans.ShipmentTracking;
import beans.vnpay.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderItemService;
import service.OrderService;
import service.PaymentService;
import service.ProductService;
import service.ShipmentService;
import servlet.vnpay.VNPConfig;

@WebServlet(name = "OrderDetailServlet", value = "/orderDetail")
public class OrderDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();
    private final ProductService productService = new ProductService();
    private final ShipmentService shipmentService = new ShipmentService();
    private final PaymentService  paymentService = new PaymentService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        paymentService.isPaymentExpired(id);

        Order order = null;
        try {
            order = orderService.getById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (order != null) {
            List<OrderItem> orderItems = new ArrayList<>();
            try {
                orderItems = orderItemService.getByOrderId(id);
            } catch (Exception e) {
                e.printStackTrace();
            }

            double tempPrice = 0;

            for (OrderItem orderItem : orderItems) {
                if (orderItem.getDiscount() == 0) {
                    tempPrice += orderItem.getPrice() * orderItem.getQuantity();
                } else {
                    tempPrice += (orderItem.getPrice() * (100 - orderItem.getDiscount()) / 100.0) * orderItem.getQuantity();
                }

                try {
                    Product product = productService.getById(orderItem.getProductId());
                    if (product != null) {
                        orderItem.setProduct(product);
                    } else {
                        orderItem.setProduct(new Product());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    orderItem.setProduct(new Product());
                }
            }

            Shipment shipment = null;
            List<ShipmentTracking> trackingHistory = new ArrayList<>();
            try {
                shipment = shipmentService.getByOrderIdWithDetails(id);
                if (shipment != null) {
                    trackingHistory = shipmentService.getTrackingHistory(shipment.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Payment p = null;
            try{
                p = paymentService.getPaymentByOrderId(id);
            }catch(Exception e){
                e.printStackTrace();
            }
            if(p != null){
                String vnpMessage = VNPConfig.getResponseMessage(p.getVnpResponseCode());
                boolean isRetryAble = VNPConfig.isRetryAble(p.getVnpResponseCode());
                request.setAttribute("payment", p);
                request.setAttribute("isRetryAble",  isRetryAble);
                request.setAttribute("vnpMessage", vnpMessage);
            }else{
                response.sendRedirect(request.getContextPath() + "/error");
                return;
            }
            request.setAttribute("order", order);
            request.setAttribute("createdAt", order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));
            request.setAttribute("tempPrice", tempPrice);
            request.setAttribute("orderItems", orderItems);
            request.setAttribute("shipment", shipment);
            request.setAttribute("trackingHistory", trackingHistory);

            int statusStep = getStatusStep(order.getStatus());
            request.setAttribute("statusStep", statusStep);

            String cancelledAt = null;
            if (order.getStatus() == 7 && order.getUpdatedAt() != null) { // CANCELLED = 7
                cancelledAt = order.getUpdatedAt().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
            }
            request.setAttribute("cancelledAt", cancelledAt);

            request.getRequestDispatcher("/WEB-INF/views/orderDetailView.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = Long.parseLong(request.getParameter("id"));
        String action = request.getParameter("action");

        if("requestCancel".equals(action)) {
            request.setAttribute("confirmCancel", true);
            doGet(request, response);
        } else if("confirmCancel".equals(action)) {
            try {
                orderService.cancelOrder(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/orderDetail?id=" + id);
        } else if("cancelCancel".equals(action)) {
            doGet(request, response);
        }
    }

    private int getStatusStep(int status) {
        switch (status) {
            case 1: return 1; // PENDING
            case 2: return 2; // CONFIRMED
            case 3: return 3; // PICKED_UP
            case 4: return 4; // SHIPPING
            case 5: return 5; // DELIVERING
            case 6: return 6; // DELIVERED
            default: return 1;
        }
    }

}
