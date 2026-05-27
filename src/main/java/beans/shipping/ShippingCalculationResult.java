package beans.shipping;

public class ShippingCalculationResult {
    private long methodId;
    private String methodName;
    private boolean isExpress;
    private String provinceCode;
    private String provinceName;
    private String zoneType;
    private double weightKg;
    private double volumeCm3;
    private double volumetricWeight;
    private double chargeableWeight;
    private double baseFee;
    private double feePerKg;
    private double pricePerVolume;
    private double surchargeMultiplier;
    private double shippingFee;
    private int estimatedDaysMin;
    private int estimatedDaysMax;
    private boolean freeShipping;
    private double freeShippingThreshold;

    public ShippingCalculationResult() {
    }

    public long getMethodId() { return methodId; }
    public String getMethodName() { return methodName; }
    public boolean isIsExpress() { return isExpress; }
    public String getProvinceCode() { return provinceCode; }
    public String getProvinceName() { return provinceName; }
    public String getZoneType() { return zoneType; }
    public double getWeightKg() { return weightKg; }
    public double getVolumeCm3() { return volumeCm3; }
    public double getVolumetricWeight() { return volumetricWeight; }
    public double getChargeableWeight() { return chargeableWeight; }
    public double getBaseFee() { return baseFee; }
    public double getFeePerKg() { return feePerKg; }
    public double getPricePerVolume() { return pricePerVolume; }
    public double getSurchargeMultiplier() { return surchargeMultiplier; }
    public double getShippingFee() { return shippingFee; }
    public int getEstimatedDaysMin() { return estimatedDaysMin; }
    public int getEstimatedDaysMax() { return estimatedDaysMax; }
    public boolean isFreeShipping() { return freeShipping; }
    public double getFreeShippingThreshold() { return freeShippingThreshold; }

    public void setMethodId(long methodId) { this.methodId = methodId; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public void setIsExpress(boolean isExpress) { this.isExpress = isExpress; }
    public void setProvinceCode(String provinceCode) { this.provinceCode = provinceCode; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public void setVolumeCm3(double volumeCm3) { this.volumeCm3 = volumeCm3; }
    public void setVolumetricWeight(double volumetricWeight) { this.volumetricWeight = volumetricWeight; }
    public void setChargeableWeight(double chargeableWeight) { this.chargeableWeight = chargeableWeight; }
    public void setBaseFee(double baseFee) { this.baseFee = baseFee; }
    public void setFeePerKg(double feePerKg) { this.feePerKg = feePerKg; }
    public void setPricePerVolume(double pricePerVolume) { this.pricePerVolume = pricePerVolume; }
    public void setSurchargeMultiplier(double surchargeMultiplier) { this.surchargeMultiplier = surchargeMultiplier; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public void setEstimatedDaysMin(int estimatedDaysMin) { this.estimatedDaysMin = estimatedDaysMin; }
    public void setEstimatedDaysMax(int estimatedDaysMax) { this.estimatedDaysMax = estimatedDaysMax; }
    public void setFreeShipping(boolean freeShipping) { this.freeShipping = freeShipping; }
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
            default: return zoneType;
        }
    }

    public String getServiceTypeName() {
        return isExpress ? "Giao hàng nhanh" : "Giao hàng tiêu chuẩn";
    }

    public String getFeeBreakdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("Phi co ban: ").append(String.format("%,.0f", baseFee)).append("d\n");
        sb.append("Trong luong tinh cuoc: ").append(String.format("%.2f", chargeableWeight)).append(" kg\n");
        sb.append("Phi/kg: ").append(String.format("%,.0f", feePerKg)).append("d\n");
        if (isExpress) {
            sb.append("He so: ").append(String.format("%.1f", surchargeMultiplier)).append("x\n");
        }
        return sb.toString();
    }
}
