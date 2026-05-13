package dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import beans.Product;

public class SearchResponse {
	private int totalProducts;
	private List<SearchProductResultDTO> products;
	private String query;
	private int currentPage;
	private int totalPages;
	private int productsPerPage;

	public SearchResponse() {
	}

	public SearchResponse(int totalProducts, List<Product> products, String query) {
		this.totalProducts = totalProducts;
		this.query = query;
		this.currentPage = 1;
		this.totalPages = products != null && products.size() > 0 ? totalProducts / products.size() : 0;
		this.productsPerPage = products != null ? products.size() : 0;
		this.products = convertToProductJson(products, query);
	}

	public SearchResponse(int totalProducts, List<SearchProductResultDTO> products, String query, int currentPage, int totalPages,
			int productsPerPage) {
		this.totalProducts = totalProducts;
		this.products = products;
		this.query = query;
		this.currentPage = currentPage;
		this.totalPages = totalPages;
		this.productsPerPage = productsPerPage;
	}

	private List<String> parseKeywords(String query) {
		List<String> keywords = new ArrayList<>();
		if (query != null && !query.trim().isEmpty()) {
			for (String kw : query.trim().split("\\s+")) {
				if (!kw.isEmpty() && kw.length() <= 50) {
					keywords.add(kw);
				}
			}
		}
		return keywords;
	}

	private List<SearchProductResultDTO> convertToProductJson(List<Product> products, String query) {
		if (products == null)
			return new ArrayList<>();
		List<String> keywords = parseKeywords(query);
		return products.stream().map(p -> SearchProductResultDTO.fromProduct(p, keywords)).collect(Collectors.toList());
	}

	public int getTotalProducts() {
		return totalProducts;
	}

	public void setTotalProducts(int totalProducts) {
		this.totalProducts = totalProducts;
	}

	public List<SearchProductResultDTO> getProducts() {
		return products;
	}

	public void setProducts(List<SearchProductResultDTO> products) {
		this.products = products;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getProductsPerPage() {
		return productsPerPage;
	}

	public void setProductsPerPage(int productsPerPage) {
		this.productsPerPage = productsPerPage;
	}
}
