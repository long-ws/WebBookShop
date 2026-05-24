package beans;

import dto.CategoryDTO;
import dto.ProductDTO;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Voucher implements Serializable {
    private long id;
    private String code;
    private String name;
    private String description;
    private int calculationMethod;
    private int applyTo;
    private Timestamp startDate;
    private Timestamp endDate;
    private double value;
    private double minPurchase;
    private double maxDiscount;
    private int usageLimit;
    private int perUserLimit;
    private int usedCount;
    private boolean active;
    private List<ProductDTO> products;
    private List<CategoryDTO> categories;

    public Voucher() {
        categories =  new ArrayList<>();
        products = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCalculationMethod() {
        return calculationMethod;
    }

    public void setCalculationMethod(int caculationMethod) {
        this.calculationMethod = caculationMethod;
    }

    public int getApplyTo() {
        return applyTo;
    }

    public void setApplyTo(int applyTo) {
        this.applyTo = applyTo;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMinPurchase() {
        return minPurchase;
    }

    public void setMinPurchase(double minPurchase) {
        this.minPurchase = minPurchase;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public int getPerUserLimit() {
        return perUserLimit;
    }

    public void setPerUserLimit(int perUserLimit) {
        this.perUserLimit = perUserLimit;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public boolean setCategory(CategoryDTO category) {
        return this.categories.add(category);
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public boolean setProduct(ProductDTO product) {
        return this.products.add(product);
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
    public List<Long> getCategoryIds() {
        return categories.stream().map(CategoryDTO::getId).collect(Collectors.toList());
    }

    public List<Long> getProductIds() {
        return products.stream().map(ProductDTO::getId).collect(Collectors.toList());
    }
}
