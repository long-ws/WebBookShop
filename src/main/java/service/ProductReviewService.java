package service;

import java.sql.SQLException;
import java.util.List;

import beans.ProductReview;
import dao.ProductReviewDAO;

public class ProductReviewService {

    private final ProductReviewDAO dao;

    public ProductReviewService() {
        this.dao = new ProductReviewDAO();
    }

    public long insert(ProductReview review) throws SQLException {
        return dao.insert(review);
    }

    public void update(ProductReview review) throws SQLException {
        dao.update(review);
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
    }

    public ProductReview getById(long id) {
        return dao.getById(id);
    }

    public List<ProductReview> getAll() {
        return dao.getAll();
    }

    public List<ProductReview> getOrderedPartByProductId(int limit, int offset, String orderBy, String orderDir, long productId) {
        return dao.getOrderedPartByProductId(limit, offset, orderBy, orderDir, productId);
    }

    public int countByProductId(long productId) {
        return dao.countByProductId(productId);
    }

    public int sumRatingScoresByProductId(long productId) {
        return dao.sumRatingScoresByProductId(productId);
    }

    public int count() {
        return dao.count();
    }

    public void hide(long id) {
        dao.hide(id);
    }

    public void show(long id) {
        dao.show(id);
    }

	public List<ProductReview> getOrderedPart(int productReviewsPerPage, int offset, String orderBy, String orderDir) {
		return dao.getOrderedPart(productReviewsPerPage, offset, orderBy, orderDir);
	}
}
