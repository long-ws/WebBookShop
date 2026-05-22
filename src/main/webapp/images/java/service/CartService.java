package service;

import java.sql.SQLException;
import java.util.List;

import beans.Cart;
import dao.CartDAO;

public class CartService {

    private final CartDAO cartDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
    }

    public long insert(Cart cart) throws SQLException {
        return cartDAO.insert(cart);
    }

    public void update(Cart cart) throws SQLException {
        cartDAO.update(cart);
    }

    public void delete(long id) throws SQLException {
        cartDAO.delete(id);
    }

    public Cart getById(long id) {
        return cartDAO.getById(id);
    }

    public List<Cart> getAll() {
        return cartDAO.getAll();
    }

    public List<Cart> getPart(int limit, int offset) {
        return cartDAO.getPart(limit, offset);
    }

    public List<Cart> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return cartDAO.getOrderedPart(limit, offset, orderBy, orderDir);
    }


    public Cart getByUserId(long userId) {
        return cartDAO.getByUserId(userId);
    }

    public int countCartItemQuantityByUserId(long userId) {
        return cartDAO.countCartItemQuantityByUserId(userId);
    }

    public int countOrderByUserId(long userId) {
        return cartDAO.countOrderByUserId(userId);
    }

    public int countOrderDeliverByUserId(long userId) {
        return cartDAO.countOrderDeliverByUserId(userId);
    }

    public int countOrderReceivedByUserId(long userId) {
        return cartDAO.countOrderReceivedByUserId(userId);
    }
}
