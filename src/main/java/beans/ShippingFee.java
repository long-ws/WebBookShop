package beans;

import java.time.LocalDateTime;

public class ShippingFee {
    private long id;
    private long shippingMethodId;
    private String zoneType;
    private double weightMin;
    private double weightMax;
    private double baseFee;
    private double feePerKg;
    private double pricePerVolume;
    private int volumetricRatio;
    private double surchargeMultiplier;
    private int estimatedDaysMin;
    private int estimatedDaysMax;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShippingFee() {
        this.volumetricRatio = 5000;
        this.surchargeMultiplier = 1.0;
        this.isActive = true;
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

    public double getWeightMin() {
        return weightMin;
    }

    public void setWeightMin(double weightMin) {
        this.weightMin = weightMin;
    }

    public double getWeightMax() {
        return weightMax;
    }

    public void setWeightMax(double weightMax) {
        this.weightMax = weightMax;
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

    public double getPricePerVolume() {
        return pricePerVolume;
    }

    public void setPricePerVolume(double pricePerVolume) {
        this.pricePerVolume = pricePerVolume;
    }

    public int getVolumetricRatio() {
        return volumetricRatio;
    }

    public void setVolumetricRatio(int volumetricRatio) {
        this.volumetricRatio = volumetricRatio;
    }

    public double getSurchargeMultiplier() {
        return surchargeMultiplier;
    }

    public void setSurchargeMultiplier(double surchargeMultiplier) {
        this.surchargeMultiplier = surchargeMultiplier;
    }

    public int getEstimatedDaysMin() {
        return estimatedDaysMin;
    }

    public void setEstimatedDaysMin(int estimatedDaysMin) {
        this.estimatedDaysMin = estimatedDaysMin;
    }

    public int getEstimatedDaysMax() {
        return estimatedDaysMax;
    }

    public void setEstimatedDaysMax(int estimatedDaysMax) {
        this.estimatedDaysMax = estimatedDaysMax;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
        return weight >= weightMin && weight <= weightMax;
    }

    public double calculateFee(double chargeableWeight) {
        return baseFee + (feePerKg * chargeableWeight);
    }

    public double calculateVolumetricWeight(double volumeCm3) {
        if (volumeCm3 <= 0 || volumetricRatio <= 0) {
            return 0;
        }
        return volumeCm3 / volumetricRatio;
    }
}
