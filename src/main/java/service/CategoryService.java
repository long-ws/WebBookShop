package service;

import java.sql.SQLException;
import java.util.List;

import beans.Category;
import dao.CategoryDAO;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public long insert(Category category) throws SQLException {
        return categoryDAO.insert(category);
    }

    public void update(Category category) throws SQLException {
        categoryDAO.update(category);
    }

    public void delete(long id) throws SQLException {
        categoryDAO.delete(id);
    }

    public Category getById(long id) {
        return categoryDAO.getById(id);
    }

    public List<Category> getAll() {
        return categoryDAO.getAll();
    }

    public List<Category> getPart(int limit, int offset) {
        return categoryDAO.getPart(limit, offset);
    }

    public List<Category> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return categoryDAO.getOrderedPart(limit, offset, orderBy, orderDir);
    }

    public Category getByProductId(long productId) {
        return categoryDAO.getByProductId(productId);
    }

    public int count() {
        return categoryDAO.count();
    }
}
