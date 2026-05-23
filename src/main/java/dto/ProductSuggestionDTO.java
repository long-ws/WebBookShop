package dto;

import java.util.List;

public class ProductSuggestionDTO {
	private final long id;
	private final String name;
	private final String author;
	private final double price;
	private final double discount;
	private final String imageName;
	private final String highlightedName;
	private final String highlightedAuthor;
	private final List<String> matchedKeywords;

	public ProductSuggestionDTO(long id, String name, String author, double price, double discount, String imageName,
			String highlightedName, String highlightedAuthor, List<String> matchedKeywords) {
		this.id = id;
		this.name = name;
		this.author = author;
		this.price = price;
		this.discount = discount;
		this.imageName = imageName;
		this.highlightedName = highlightedName;
		this.highlightedAuthor = highlightedAuthor;
		this.matchedKeywords = matchedKeywords;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	public double getPrice() {
		return price;
	}

	public double getDiscount() {
		return discount;
	}

	public String getImageName() {
		return imageName;
	}

	public String getHighlightedName() {
		return highlightedName;
	}

	public String getHighlightedAuthor() {
		return highlightedAuthor;
	}

	public List<String> getMatchedKeywords() {
		return matchedKeywords;
	}

	public double getFinalPrice() {
		return discount > 0 ? price * (100 - discount) / 100 : price;
	}
}
