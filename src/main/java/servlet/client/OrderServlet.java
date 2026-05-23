package servlet.client;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import beans.Order;
import beans.OrderItem;
import beans.User;
import dto.OrderResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.OrderItemService;
import service.OrderService;

@WebServlet(name = "OrderServlet", value = "/order")
public class OrderServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();

    private static final int ORDERS_PER_PAGE = 3;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/signin");
            return;
        }

        // Lấy tổng số order của user
        int totalOrders = 0;
        try {
            totalOrders = orderService.countByUserId(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tính tổng số trang
        int totalPages = totalOrders / ORDERS_PER_PAGE;
        if (totalOrders % ORDERS_PER_PAGE != 0) {
            totalPages++;
        }

        // Lấy trang hiện tại
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (Exception e) {
                page = 1;
            }
        }
        if (page < 1 || page > totalPages) {
            page = 1;
        }

        int offset = (page - 1) * ORDERS_PER_PAGE;

        // Lấy danh sách order
        List<Order> orders = new ArrayList<>();
        try {
            orders = orderService.getOrderedPartByUserId(user.getId(), ORDERS_PER_PAGE, offset);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {
            List<OrderItem> orderItems = new ArrayList<>();
            try {
                orderItems = orderItemService.getByOrderId(order.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            double total = 0.0;
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getDiscount() == 0) {
                    total += orderItem.getPrice() * orderItem.getQuantity();
                } else {
                    total += (orderItem.getPrice() * (100 - orderItem.getDiscount()) / 100.0) * orderItem.getQuantity();
                }
            }

            List<String> productNames = new ArrayList<>();
            try {
                productNames = orderItemService.getProductNamesByOrderId(order.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            OrderResponse orderResponse = new OrderResponse(
                    order.getId(),
                    order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    formatProductNames(productNames),
                    order.getStatus(),
                    total + order.getDeliveryPrice()
            );

            orderResponses.add(orderResponse);
        }

        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("orders", orderResponses);

        request.getRequestDispatcher("/WEB-INF/views/orderView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    // Helper method để format danh sách sản phẩm
    private String formatProductNames(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        if (list.size() == 1) return list.get(0);
        return list.get(0) + " và " + (list.size() - 1) + " sản phẩm khác";
    }
}
