package servlet.client;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import beans.Order;
import beans.OrderItem;
import beans.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderItemService;
import service.OrderService;
import service.ProductService;

@WebServlet(name = "OrderDetailServlet", value = "/orderDetail")
public class OrderDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

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

            request.setAttribute("order", order);
            request.setAttribute("createdAt", order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));
            request.setAttribute("tempPrice", tempPrice);
            request.setAttribute("orderItems", orderItems);
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
            // Chỉ đánh dấu trạng thái xác nhận, không hủy
            request.setAttribute("confirmCancel", true);
            doGet(request, response); // render lại trang
        } else if("confirmCancel".equals(action)) {
            try {
                orderService.cancelOrder(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/orderDetail?id=" + id);
        } else if("cancelCancel".equals(action)) {
            // Hủy thao tác, quay về trạng thái ban đầu
            doGet(request, response);
        }
    }

}
