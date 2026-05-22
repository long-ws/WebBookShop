package beans;

import java.time.LocalDateTime;

public class ProductImage {
    private long id;
    private long productId;
    private String imageName;
    private int isPrimary;
    private int sortOrder;
    private LocalDateTime createdAt;

    public ProductImage() {}

    public ProductImage(long id, long productId, String imageName, int isPrimary, int sortOrder, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.imageName = imageName;
        this.isPrimary = isPrimary;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public int getIsPrimary() { return isPrimary; }
    public void setIsPrimary(int isPrimary) { this.isPrimary = isPrimary; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
