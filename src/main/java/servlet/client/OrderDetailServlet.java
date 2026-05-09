package servlet.client;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Order;
import beans.OrderItem;
import beans.OrderNote;
import beans.Product;
import beans.ProductReview;
import beans.Shipment;
import beans.ShipmentTracking;
import beans.ShipperMessage;
import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderItemService;
import service.OrderNoteService;
import service.OrderService;
import service.ProductReviewService;
import service.ProductService;
import service.ShipmentService;
import service.ShipperMessageService;

@WebServlet(name = "OrderDetailServlet", value = "/orderDetail")
public class OrderDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final OrderService orderService = new OrderService();
	private final OrderItemService orderItemService = new OrderItemService();
	private final ProductService productService = new ProductService();
	private final OrderNoteService orderNoteService = new OrderNoteService();
	private final ProductReviewService productReviewService = new ProductReviewService();
	private final ShipmentService shipmentService = new ShipmentService();
	private final ShipperMessageService shipperMessageService = new ShipperMessageService();

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

            // Order notes
            List<OrderNote> orderNotes = new ArrayList<>();
            try {
                orderNotes = orderNoteService.getNotesByOrderId(order.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.setAttribute("orderNotes", orderNotes);

            // Mark all notes as read when customer views
            try {
                orderNoteService.markAllRead(order.getId());
            } catch (Exception e) {
                // ignore
            }

            // Unread note count
            int unreadNoteCount = 0;
            try {
                unreadNoteCount = orderNoteService.countUnread(order.getId());
            } catch (Exception e) {
                // ignore
            }
            request.setAttribute("unreadNoteCount", unreadNoteCount);
            Map<Long, ProductReview> existingReviews = new HashMap<>();
            try {
                for (OrderItem item : orderItems) {
                    ProductReview review = productReviewService.getByUserAndProduct(order.getUserId(), item.getProductId());
                    if (review != null) {
                        existingReviews.put(item.getProductId(), review);
                    }
                }
            } catch (Exception e) {
                // ignore
            }
            request.setAttribute("existingReviews", existingReviews);

            // Shipment info
            beans.Shipment shipment = null;
            try {
                shipment = shipmentService.getByOrderId(order.getId());
                request.setAttribute("shipment", shipment);
                if (shipment != null) {
                    List<beans.ShipmentTracking> trackingHistory = shipmentService.getTrackingHistory(shipment.getId());
                    request.setAttribute("trackingHistory", trackingHistory);
                    int unreadMsgCount = shipperMessageService.countUnreadFromShipper(shipment.getId());
                    request.setAttribute("unreadMsgCount", unreadMsgCount);
                }
            } catch (Exception e) {
                request.setAttribute("shipment", shipment);
            }

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
