package beans;

import java.time.LocalDateTime;

public class Province {
    private long id;
    private String provinceCode;
    private String provinceName;
    private String provinceType;
    private long shippingZoneId;
    private Boolean metroCity;
    private String region;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private transient ShippingZone shippingZone;

    public Province() {
        this.metroCity = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceType() {
        return provinceType;
    }

    public void setProvinceType(String provinceType) {
        this.provinceType = provinceType;
    }

    public long getShippingZoneId() {
        return shippingZoneId;
    }

    public void setShippingZoneId(long shippingZoneId) {
        this.shippingZoneId = shippingZoneId;
    }

    public Boolean getMetroCity() {
        return metroCity != null ? metroCity : false;
    }

    public void setMetroCity(Boolean metroCity) {
        this.metroCity = metroCity;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public ShippingZone getShippingZone() {
        return shippingZone;
    }

    public void setShippingZone(ShippingZone shippingZone) {
        this.shippingZone = shippingZone;
    }

    public String getZoneType() {
        if (shippingZone != null) {
            return shippingZone.getZoneType();
        }
        return "PROVINCIAL";
    }
}
