package beans;

import java.time.LocalDateTime;

public class ShippingZone {
    private long id;
    private String zoneName;
    private String zoneType;
    private String description;
    private double baseFee;
    private double pricePerKg;
    private double pricePerVolume;
    private int estimatedDaysMin;
    private int estimatedDaysMax;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShippingZone() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getZoneType() {
        return zoneType;
    }

    public void setZoneType(String zoneType) {
        this.zoneType = zoneType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(double baseFee) {
        this.baseFee = baseFee;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public double getPricePerVolume() {
        return pricePerVolume;
    }

    public void setPricePerVolume(double pricePerVolume) {
        this.pricePerVolume = pricePerVolume;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getEstimatedDeliveryDisplay() {
        if (estimatedDaysMin == estimatedDaysMax) {
            return estimatedDaysMin + " ngày";
        }
        return estimatedDaysMin + "-" + estimatedDaysMax + " ngày";
    }
}
