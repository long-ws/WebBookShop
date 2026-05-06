package service;

import java.sql.SQLException;
import java.util.List;

import beans.WishlistItem;
import dao.WishlistItemDAO;

public class WishlistItemService {

    private final WishlistItemDAO dao;

    public WishlistItemService() {
        this.dao = new WishlistItemDAO();
    }

    public long insert(WishlistItem item) throws SQLException {
        return dao.insert(item);
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
    }

    public List<WishlistItem> getByUserId(long userId) {
        return dao.getByUserId(userId);
    }

    public int countByUserIdAndProductId(long userId, long productId) {
        return dao.countByUserIdAndProductId(userId, productId);
    }
}
