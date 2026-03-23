package service;

import java.sql.SQLException;
import java.util.List;

import beans.Order;
import beans.OrderItem;
import dao.OrderDAO;

public class OrderService {

    private final OrderDAO orderDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
    }

    public long insert(Order order) throws SQLException {
        return orderDAO.insert(order);
    }

    public void update(Order order) throws SQLException {
        orderDAO.update(order);
    }

    public void delete(long id) throws SQLException {
        orderDAO.delete(id);
    }

    public Order getById(long id) {
        return orderDAO.getById(id);
    }

    public List<Order> getAll() {
        return orderDAO.getAll();
    }

    public List<Order> getPart(int limit, int offset) {
        return orderDAO.getPart(limit, offset);
    }

    public List<Order> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return orderDAO.getOrderedPart(limit, offset, orderBy, orderDir);
    }

    public List<Order> getOrderedPartByUserId(long userId, int limit, int offset) {
        return orderDAO.getOrderedPartByUserId(userId, limit, offset);
    }

    public int countByUserId(long userId) {
        return orderDAO.countByUserId(userId);
    }

    public int count() {
        return orderDAO.count();
    }

    public boolean cancelOrder(long id) {
        return orderDAO.cancelOrder(id);
    }

    public boolean confirm(long id) {
        return orderDAO.confirm(id);
    }

    public boolean cancel(long id) {
        return orderDAO.cancel(id);
    }

    public boolean reset(long id) {
        return orderDAO.reset(id);
    }
    
    public double calculateTotalPrice(List<OrderItem> orderItems, double deliveryPrice) {
        double totalPrice = deliveryPrice;

        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);

            if (item.getDiscount() == 0) {
                totalPrice += item.getPrice() * item.getQuantity();
            } else {
                totalPrice +=
                        (item.getPrice() * (100 - item.getDiscount()) / 100)
                                * item.getQuantity();
            }
        }

        return totalPrice;
    }
}
