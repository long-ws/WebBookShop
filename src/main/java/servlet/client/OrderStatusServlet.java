package servlet.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import beans.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderService;

@WebServlet(name = "OrderStatusServlet", value = "/api/order-status")
public class OrderStatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrderService orderService = new OrderService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        PrintWriter out = response.getWriter();

        String orderIdParam = request.getParameter("id");

        if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
            out.print("{\"error\":\"Missing order id\"}");
            return;
        }

        try {
            long orderId = Long.parseLong(orderIdParam);
            Order order = orderService.getById(orderId);

            if (order == null) {
                out.print("{\"error\":\"Order not found\"}");
                return;
            }

            String statusText = getStatusText(order.getStatus());
            String statusClass = getStatusClass(order.getStatus());
            String statusIcon = getStatusIcon(order.getStatus());
            String lastUpdated = order.getUpdatedAt() != null
                    ? order.getUpdatedAt().format(DATE_FORMATTER)
                    : order.getCreatedAt().format(DATE_FORMATTER);

            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"id\":").append(order.getId()).append(",");
            json.append("\"status\":").append(order.getStatus()).append(",");
            json.append("\"statusText\":\"").append(escapeJson(statusText)).append("\",");
            json.append("\"statusClass\":\"").append(escapeJson(statusClass)).append("\",");
            json.append("\"statusIcon\":\"").append(escapeJson(statusIcon)).append("\",");
            json.append("\"lastUpdated\":\"").append(escapeJson(lastUpdated)).append("\"");
            json.append("}");

            out.print(json.toString());

        } catch (NumberFormatException e) {
            out.print("{\"error\":\"Invalid order id\"}");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"Server error\"}");
        }
    }

    private String getStatusText(int status) {
        switch (status) {
            case 1: return "Đã đặt hàng";
            case 2: return "Đã xác nhận";
            case 3: return "Đã lấy hàng";
            case 4: return "Đang vận chuyển";
            case 5: return "Đang giao hàng";
            case 6: return "Đã giao thành công";
            case 7: return "Đã hủy";
            default: return "Không xác định";
        }
    }

    private String getStatusClass(int status) {
        switch (status) {
            case 1: return "warning";
            case 2: case 3: case 4: return "primary";
            case 5: return "info";
            case 6: return "success";
            case 7: return "danger";
            default: return "secondary";
        }
    }

    private String getStatusIcon(int status) {
        switch (status) {
            case 1: return "bag-check";
            case 2: return "check2-all";
            case 3: return "box-seam";
            case 4: return "truck";
            case 5: return "geo-alt";
            case 6: return "check-circle";
            case 7: return "x-circle";
            default: return "question-circle";
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
