package beans.shipping;

public class ShippingInfo {
    private long methodId;
    private String methodName;
    private boolean isExpress;
    private String provinceCode;
    private String provinceName;
    private String zoneType;
    private double weightKg;
    private double volumeCm3;
    private double shippingFee;
    private int estimatedDaysMin;
    private int estimatedDaysMax;
    private boolean freeShipping;
    private double freeShippingThreshold;

    public ShippingInfo() {
    }

    public long getMethodId() { return methodId; }
    public void setMethodId(long methodId) { this.methodId = methodId; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public boolean isExpress() { return isExpress; }
    public void setIsExpress(boolean isExpress) { this.isExpress = isExpress; }
    public String getProvinceCode() { return provinceCode; }
    public void setProvinceCode(String provinceCode) { this.provinceCode = provinceCode; }
    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public double getVolumeCm3() { return volumeCm3; }
    public void setVolumeCm3(double volumeCm3) { this.volumeCm3 = volumeCm3; }
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public int getEstimatedDaysMin() { return estimatedDaysMin; }
    public void setEstimatedDaysMin(int estimatedDaysMin) { this.estimatedDaysMin = estimatedDaysMin; }
    public int getEstimatedDaysMax() { return estimatedDaysMax; }
    public void setEstimatedDaysMax(int estimatedDaysMax) { this.estimatedDaysMax = estimatedDaysMax; }
    public boolean isFreeShipping() { return freeShipping; }
    public void setFreeShipping(boolean freeShipping) { this.freeShipping = freeShipping; }
    public double getFreeShippingThreshold() { return freeShippingThreshold; }
    public void setFreeShippingThreshold(double freeShippingThreshold) { this.freeShippingThreshold = freeShippingThreshold; }

    public String getEstimatedDeliveryDisplay() {
        if (estimatedDaysMin == estimatedDaysMax) {
            return estimatedDaysMin + " ngay";
        }
        return estimatedDaysMin + "-" + estimatedDaysMax + " ngày";
    }

    public String getZoneDisplayName() {
        switch (zoneType) {
            case "INNER": return "Nội thành";
            case "PROVINCIAL": return "Tỉnh lẻ";
            case "REMOTE": return "Vùng xa";
            default: return zoneType != null ? zoneType : "Không xác định";
        }
    }
}
