package dto.getUsableVouchers;

import beans.Voucher;

import java.io.Serializable;
import java.sql.Timestamp;

public class VoucherDTO implements Serializable {
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
    private String productIdsCsv;
    private String categoryIdsCsv;

    public VoucherDTO() {}

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

    public void setCalculationMethod(int calculationMethod) {
        this.calculationMethod = calculationMethod;
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

    public String getProductIdsCsv() {
        return productIdsCsv;
    }

    public void setProductIdsCsv(String productIdsCsv) {
        this.productIdsCsv = productIdsCsv;
    }

    public String getCategoryIdsCsv() {
        return categoryIdsCsv;
    }

    public void setCategoryIdsCsv(String categoryIdsCsv) {
        this.categoryIdsCsv = categoryIdsCsv;
    }
}
