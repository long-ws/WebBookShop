package dto;

public class AdvancedSearchRequest {
	private String keyword;
	private Long categoryId;
	private String author;
	private String publisher;
	private Integer minPrice;
	private Integer maxPrice;
	private Integer minYear;
	private Integer maxYear;
	private String sortBy;
	private String sortDir;
	private int page;
	private int limit;

	public static final int DEFAULT_LIMIT = 12;
	public static final int DEFAULT_MIN_YEAR = 1000;
	public static final int DEFAULT_MAX_YEAR = java.time.Year.now().getValue() + 10;

	public AdvancedSearchRequest() {
		this.minYear = DEFAULT_MIN_YEAR;
		this.maxYear = DEFAULT_MAX_YEAR;
		this.sortBy = "totalBuy";
		this.sortDir = "DESC";
		this.page = 1;
		this.limit = DEFAULT_LIMIT;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Integer getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public Integer getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Integer getMinYear() {
		return minYear;
	}

	public void setMinYear(Integer minYear) {
		this.minYear = minYear;
	}

	public Integer getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(Integer maxYear) {
		this.maxYear = maxYear;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getSortDir() {
		return sortDir;
	}

	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return (page - 1) * limit;
	}

	public String getSelectedCategoryName() {
		return selectedCategoryName;
	}

	public void setSelectedCategoryName(String selectedCategoryName) {
		this.selectedCategoryName = selectedCategoryName;
	}

	private String selectedCategoryName;

	public boolean hasFilters() {
		return (keyword != null && !keyword.trim().isEmpty()) || categoryId != null
				|| (author != null && !author.trim().isEmpty()) || (publisher != null && !publisher.trim().isEmpty())
				|| minPrice != null || maxPrice != null || (minYear != null && minYear > DEFAULT_MIN_YEAR)
				|| (maxYear != null && maxYear < DEFAULT_MAX_YEAR);
	}
}
