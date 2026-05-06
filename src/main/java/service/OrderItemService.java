package service;

import java.sql.SQLException;
import java.util.List;

import beans.OrderItem;
import dao.OrderItemDAO;

public class OrderItemService {

    private final OrderItemDAO orderItemDAO;

    public OrderItemService() {
        this.orderItemDAO = new OrderItemDAO();
    }

    public long insert(OrderItem orderItem) throws SQLException {
        return orderItemDAO.insert(orderItem);
    }

    public void update(OrderItem orderItem) throws SQLException {
        orderItemDAO.update(orderItem);
    }

    public void delete(long id) throws SQLException {
        orderItemDAO.delete(id);
    }

    public OrderItem getById(long id) {
        return orderItemDAO.getById(id);
    }

    public List<OrderItem> getAll() {
        return orderItemDAO.getAll();
    }

    public List<OrderItem> getPart(int limit, int offset) {
        return orderItemDAO.getPart(limit, offset);
    }

    public List<OrderItem> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return orderItemDAO.getOrderedPart(limit, offset, orderBy, orderDir);
    }

    public void bulkInsert(List<OrderItem> orderItems) {
        orderItemDAO.bulkInsert(orderItems);
    }

    public List<String> getProductNamesByOrderId(long orderId) {
        return orderItemDAO.getProductNamesByOrderId(orderId);
    }

    public List<OrderItem> getByOrderId(long orderId) {
        return orderItemDAO.getByOrderId(orderId);
    }
}
