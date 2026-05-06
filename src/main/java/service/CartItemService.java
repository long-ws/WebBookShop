package service;

import java.sql.SQLException;
import java.util.List;

import beans.CartItem;
import dao.CartItemDAO;

public class CartItemService {

    private final CartItemDAO cartItemDAO;

    public CartItemService() {
        this.cartItemDAO = new CartItemDAO();
    }

    public long insert(CartItem cartItem) throws SQLException {
        return cartItemDAO.insert(cartItem);
    }

    public void update(CartItem cartItem) throws SQLException {
        cartItemDAO.update(cartItem);
    }

    public void delete(long id) throws SQLException {
        cartItemDAO.delete(id);
    }

    public CartItem getById(long id) {
        return cartItemDAO.getById(id);
    }

    public List<CartItem> getAll() {
        return cartItemDAO.getAll();
    }

    public List<CartItem> getPart(int limit, int offset) {
        return cartItemDAO.getPart(limit, offset);
    }

    public List<CartItem> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return cartItemDAO.getOrderedPart(limit, offset, orderBy, orderDir);
    }

    public List<CartItem> getByCartId(long cartId) {
        return cartItemDAO.getByCartId(cartId);
    }

    public CartItem getByCartIdAndProductId(long cartId, long productId) {
        return cartItemDAO.getByCartIdAndProductId(cartId, productId);
    }

    public int sumQuantityByUserId(long userId) {
        return cartItemDAO.sumQuantityByUserId(userId);
    }
}
