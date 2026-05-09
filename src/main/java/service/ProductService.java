package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Product;
import dao.ProductDAO;
import utils.DBConnection;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO(); // DAO JDBC thuần
    }

    // ================== CRUD cơ bản ==================
    public long insert(Product product) throws SQLException {
        return productDAO.insert(product);
    }

    public void update(Product product) throws SQLException {
        productDAO.update(product);
    }

    public void delete(long id) throws SQLException {
        productDAO.delete(id);
    }

    public Product getById(long id) {
        return productDAO.getById(id);
    }

    public List<Product> getAll() {
        return productDAO.getAll();
    }

    public List<Product> getPart(int limit, int offset) {
        return productDAO.getPart(limit, offset);
    }

    public List<Product> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return productDAO.getOrderedPart(limit, offset, orderBy, orderDir);
    }

    public List<Product> getOrderedPartByCategoryId(int limit, int offset, String orderBy, String orderDir, long categoryId) {
        return productDAO.getOrderedPartByCategoryId(limit, offset, orderBy, orderDir, categoryId);
    }

    public int countByCategoryId(long categoryId) {
        return productDAO.countByCategoryId(categoryId);
    }

    public List<Product> getRandomPartByCategoryId(int limit, int offset, long categoryId) {
        return productDAO.getRandomPartByCategoryId(limit, offset, categoryId);
    }

    public List<String> getPublishersByCategoryId(long categoryId) {
        return productDAO.getPublishersByCategoryId(categoryId);
    }

    public List<String> getAllPublishers() {
        return productDAO.getAllPublishers();
    }

    public List<Integer> getYearsByCategoryId(long categoryId) {
        return productDAO.getYearsByCategoryId(categoryId);
    }

    public List<Integer> getAllYears() {
        return productDAO.getAllYears();
    }

    public int countByFilters(String filters) {
        return productDAO.countByFilters(filters);
    }

    public List<Product> getOrderedPartByFilters(int limit, int offset, String orderBy, String orderDir, String filters) {
        return productDAO.getOrderedPartByFilters(limit, offset, orderBy, orderDir, filters);
    }

    public String filterByYearRange(int minYear, int maxYear) {
        if (minYear > 0 && maxYear > 0) {
            return "yearPublishing BETWEEN " + minYear + " AND " + maxYear;
        } else if (minYear > 0) {
            return "yearPublishing >= " + minYear;
        } else if (maxYear > 0) {
            return "yearPublishing <= " + maxYear;
        }
        return "";
    }

    public int countByCategoryIdAndFilters(long categoryId, String filters) {
        return productDAO.countByCategoryIdAndFilters(categoryId, filters);
    }

    public List<Product> getOrderedPartByCategoryIdAndFilters(int limit, int offset, String orderBy, String orderDir, long categoryId, String filters) {
        return productDAO.getOrderedPartByCategoryIdAndFilters(limit, offset, orderBy, orderDir, categoryId, filters);
    }

    public int count() {
        return productDAO.count();
    }

    public void insertProductCategory(long productId, long categoryId) {
        productDAO.insertProductCategory(productId, categoryId);
    }

    public void updateProductCategory(long productId, long categoryId) {
        productDAO.updateProductCategory(productId, categoryId);
    }

    public void deleteProductCategory(long productId, long categoryId) {
        productDAO.deleteProductCategory(productId, categoryId);
    }

    public List<Product> getByQuery(String query, int limit, int offset) {
        return productDAO.getByQuery(query, limit, offset);
    }

    public int countByQuery(String query) {
        return productDAO.countByQuery(query);
    }

    public List<Product> getByAdvancedQuery(String query, int limit, int offset) {
        return productDAO.getByAdvancedQuery(query, limit, offset);
    }

    public int countByAdvancedQuery(String query) {
        return productDAO.countByAdvancedQuery(query);
    }

    public int countByAdvancedSearch(String whereClause, List<Object> params) {
        String baseSql = "SELECT COUNT(p.id) FROM product p WHERE " + whereClause;
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(baseSql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Product> getAdvancedSearch(String whereClause, List<Object> params, String sortBy, String sortDir, int limit, int offset) {
        List<Product> list = new ArrayList<>();
        String sortColumn = switch (sortBy) {
            case "name" -> "p.name";
            case "price" -> "p.price";
            case "yearPublishing" -> "p.yearPublishing";
            case "totalBuy" -> "p.totalBuy";
            default -> "p.totalBuy";
        };
        String sortDirection = "ASC".equalsIgnoreCase(sortDir) ? "ASC" : "DESC";
        String sql = "SELECT p.* FROM product p WHERE " + whereClause + " ORDER BY " + sortColumn + " " + sortDirection + " LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.setInt(params.size() + 1, limit);
            ps.setInt(params.size() + 2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(productDAO.mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================== Các helper xử lý filter ==================
    public String getFirst(String twopartString) {
        if (twopartString.contains("-")) {
            return twopartString.split("-")[0];
        }
        return "0"; // mặc định 0 nếu không có "-"
    }

    public String getLast(String twopartString) {
        if (twopartString.contains("-")) {
            return twopartString.split("-")[1];
        }
        return "0"; // mặc định 0 nếu không có "-"
    }

    public int getMinPrice(String priceRange) {
        try {
            return Integer.parseInt(getFirst(priceRange));
        } catch (Exception e) {
            return 0;
        }
    }

    public int getMaxPrice(String priceRange) {
        String max = getLast(priceRange);
        if ("infinity".equals(max)) {
            return Integer.MAX_VALUE;
        }
        try {
            return Integer.parseInt(max);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    public String filterByPublishers(List<String> publishers) {
        List<String> quoted = new ArrayList<>();
        for (String p : publishers) {
            quoted.add("'" + p + "'");
        }
        return "p.publisher IN (" + String.join(", ", quoted) + ")";
    }

    public String filterByPriceRanges(List<String> priceRanges) {
        List<String> conditions = new ArrayList<>();
        for (String range : priceRanges) {
            conditions.add("p.price BETWEEN " + getMinPrice(range) + " AND " + getMaxPrice(range));
        }
        return "(" + String.join(" OR ", conditions) + ")";
    }

    public String createFiltersQuery(List<String> filters) {
        return String.join(" AND ", filters);
    }

    public List<dao.ProductDAO.ProductSuggestion> getSearchSuggestions(String query, int limit) {
        return productDAO.getSearchSuggestions(query, limit);
    }
}
