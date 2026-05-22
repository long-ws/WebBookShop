package dto;

import java.util.List;

import beans.Product;

public class SearchProductResultDTO {
	private long id;
	private String name;
	private double price;
	private double discount;
	private String author;
	private String publisher;
	private String imageName;
	private String highlightedName;
	private String highlightedAuthor;

	public SearchProductResultDTO() {
	}

	public SearchProductResultDTO(long id, String name, double price, double discount, String author, String publisher,
			String imageName, String highlightedName, String highlightedAuthor) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.discount = discount;
		this.author = author;
		this.publisher = publisher;
		this.imageName = imageName;
		this.highlightedName = highlightedName;
		this.highlightedAuthor = highlightedAuthor;
	}

	private static String highlightMatches(String text, List<String> keywords) {
		if (text == null || text.isEmpty())
			return text;
		String result = text;
		for (String kw : keywords) {
			if (kw.isEmpty())
				continue;
			String lowerText = result.toLowerCase();
			String lowerKw = kw.toLowerCase();
			int idx = lowerText.indexOf(lowerKw);
			if (idx >= 0) {
				String before = result.substring(0, idx);
				String match = result.substring(idx, idx + kw.length());
				String after = result.substring(idx + kw.length());
				result = before + "\u0000" + match + "\u0001" + after;
			}
		}
		return result.replace("\u0000", "<mark class=\"search-highlight\">").replace("\u0001", "</mark>");
	}

	public static SearchProductResultDTO fromProduct(Product p, List<String> keywords) {
		return new SearchProductResultDTO(p.getId(), p.getName(), p.getPrice(), p.getDiscount(), p.getAuthor(), p.getPublisher(),
				p.getImageName(), highlightMatches(p.getName(), keywords), highlightMatches(p.getAuthor(), keywords));
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
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

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getHighlightedName() {
		return highlightedName;
	}

	public void setHighlightedName(String highlightedName) {
		this.highlightedName = highlightedName;
	}

	public String getHighlightedAuthor() {
		return highlightedAuthor;
	}

	public void setHighlightedAuthor(String highlightedAuthor) {
		this.highlightedAuthor = highlightedAuthor;
	}

	public double getFinalPrice() {
		if (discount > 0) {
			return price * (100 - discount) / 100;
		}
		return price;
	}
}
