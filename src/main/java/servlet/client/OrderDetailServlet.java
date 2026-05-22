package servlet.client;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import beans.Order;
import beans.OrderItem;
import beans.Product;
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
import servlet.vnpay.VNPConfig;

@WebServlet(name = "OrderDetailServlet", value = "/orderDetail")
public class OrderDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();
    private final ProductService productService = new ProductService();
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
            request.getRequestDispatcher("/WEB-INF/views/orderDetailView.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
