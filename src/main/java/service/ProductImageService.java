package service;

import java.time.LocalDateTime;
import java.util.List;

import beans.ProductImage;
import dao.ProductImageDAO;

public class ProductImageService {
    private final ProductImageDAO productImageDAO = new ProductImageDAO();

    public long insert(ProductImage img) throws Exception {
        return productImageDAO.insert(img);
    }

    public void update(ProductImage img) throws Exception {
        productImageDAO.update(img);
    }

    public void delete(long id) throws Exception {
        productImageDAO.delete(id);
    }

    public ProductImage getById(long id) {
        return productImageDAO.getById(id);
    }

    public List<ProductImage> getByProductId(long productId) {
        return productImageDAO.getByProductId(productId);
    }

    public ProductImage getPrimaryByProductId(long productId) {
        return productImageDAO.getPrimaryByProductId(productId);
    }

    public int countByProductId(long productId) {
        return productImageDAO.countByProductId(productId);
    }

    public void setPrimary(long imageId, long productId) {
        productImageDAO.setPrimary(imageId, productId);
    }

    public void deleteAllByProductId(long productId) {
        productImageDAO.deleteAllByProductId(productId);
    }

    public long insertNew(long productId, String imageName, int isPrimary) throws Exception {
        ProductImage img = new ProductImage();
        img.setProductId(productId);
        img.setImageName(imageName);
        img.setIsPrimary(isPrimary);
        img.setSortOrder(0);
        img.setCreatedAt(LocalDateTime.now());
        return productImageDAO.insert(img);
    }
}
