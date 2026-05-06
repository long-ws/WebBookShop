package beans;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class ProductReview {
	private long id;
	private long userId;
	private long productId;
	private int ratingScore;
	private String content;
	private int isShow;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private User user;
	private Product product;

	public ProductReview() {
	}

	public ProductReview(long id, long userId, long productId, int ratingScore, String content, int isShow,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.userId = userId;
		this.productId = productId;
		this.ratingScore = ratingScore;
		this.content = content;
		this.isShow = isShow;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getRatingScore() {
		return ratingScore;
	}

	public void setRatingScore(int ratingScore) {
		this.ratingScore = ratingScore;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getIsShow() {
		return isShow;
	}

	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ProductReview.class.getSimpleName() + "[", "]").add("id=" + id)
				.add("userId=" + userId).add("productId=" + productId).add("ratingScore=" + ratingScore)
				.add("content='" + content + "'").add("isShow=" + isShow).add("createdAt=" + createdAt)
				.add("updatedAt=" + updatedAt).add("user=" + user).add("product=" + product).toString();
	}
}
