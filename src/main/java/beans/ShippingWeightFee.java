package beans;

import java.time.LocalDateTime;

public class ShippingWeightFee {
    private long id;
    private long shippingMethodId;
    private String zoneType; // INNER, PROVINCIAL, REMOTE
    private double minWeight;
    private double maxWeight;
    private double baseFee;
    private double feePerKg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShippingWeightFee() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(long shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public String getZoneType() {
        return zoneType;
    }

    public void setZoneType(String zoneType) {
        this.zoneType = zoneType;
    }

    public double getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(double minWeight) {
        this.minWeight = minWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(double baseFee) {
        this.baseFee = baseFee;
    }

    public double getFeePerKg() {
        return feePerKg;
    }

    public void setFeePerKg(double feePerKg) {
        this.feePerKg = feePerKg;
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

    public boolean isWeightInRange(double weight) {
        return weight >= minWeight && weight <= maxWeight;
    }

    private String methodName;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
