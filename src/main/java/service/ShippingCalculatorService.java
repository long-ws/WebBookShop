package service;

import java.time.LocalDateTime;
import java.util.List;

import beans.Province;
import beans.ShippingFee;
import beans.ShippingMethod;
import beans.ShippingZone;
import dao.ProvinceDAO;
import dao.ShippingFeeDAO;
import dao.ShippingMethodDAO;
import dao.ShippingZoneDAO;

public class ShippingCalculatorService {

    private final ShippingMethodDAO methodDAO = new ShippingMethodDAO();
    private final ShippingZoneDAO zoneDAO = new ShippingZoneDAO();
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final ShippingFeeDAO shippingFeeDAO = new ShippingFeeDAO();

    private static final double VOLUME_WEIGHT_RATIO = 5000;

    public ShippingCalculatorService() {
    }

    public ShippingCalculationResult calculateFee(long methodId, String provinceCode, double weightKg, double volumeCm3) {
        ShippingMethod method = methodDAO.getById(methodId);
        if (method == null || method.getStatus() != 1) {
            return null;
        }
        return calculateFee(method, provinceCode, weightKg, volumeCm3);
    }

    public ShippingCalculationResult calculateFee(ShippingMethod method, String provinceCode, double weightKg, double volumeCm3) {
        if (method == null || weightKg <= 0) {
            return null;
        }

        Province province = provinceDAO.getByProvinceCode(provinceCode);
        String zoneType = determineZoneType(province);
        ShippingZone zone = zoneDAO.getByZoneType(zoneType);
        double volumetricWeight = volumeCm3 / VOLUME_WEIGHT_RATIO;
        double chargeableWeight = Math.max(weightKg, volumetricWeight);

        ShippingFee shippingFee = null;
        try {
            shippingFee = shippingFeeDAO.getFeeByMethodAndZoneAndWeight(method.getId(), zoneType, chargeableWeight);
        } catch (Exception e) {
            System.err.println("Warning: shipping_fees query failed - table or columns may not exist: " + e.getMessage());
        }

        double baseFee = 0;
        double feePerKg = 0;
        double pricePerVolume = 0;
        int estimatedDaysMin = method.getEstimatedDays();
        int estimatedDaysMax = method.getEstimatedDays();
        double surchargeMultiplier = method.isExpress() ? method.getExpressSurcharge() : 1.0;

        if (shippingFee != null) {
            baseFee = shippingFee.getBaseFee();
            feePerKg = shippingFee.getFeePerKg();
            pricePerVolume = shippingFee.getPricePerVolume();
            estimatedDaysMin = shippingFee.getEstimatedDaysMin();
            estimatedDaysMax = shippingFee.getEstimatedDaysMax();
            surchargeMultiplier = shippingFee.getSurchargeMultiplier();
        } else if (zone != null) {
            baseFee = zone.getBaseFee();
            feePerKg = zone.getPricePerKg();
            pricePerVolume = zone.getPricePerVolume();
            estimatedDaysMin = zone.getEstimatedDaysMin();
            estimatedDaysMax = zone.getEstimatedDaysMax();
        }

        double totalFee = calculateShippingFee(
            method, shippingFee, zone,
            weightKg, volumetricWeight, chargeableWeight,
            baseFee, feePerKg, pricePerVolume, surchargeMultiplier
        );

        boolean isFreeShipping = false;
        if (method.getFreeShippingThreshold() > 0 && totalFee >= method.getFreeShippingThreshold()) {
            totalFee = 0;
            isFreeShipping = true;
        }

        totalFee = Math.round(totalFee / 1000) * 1000;

        ShippingCalculationResult result = new ShippingCalculationResult();
        result.setMethodId(method.getId());
        result.setMethodName(method.getName());
        result.setIsExpress(method.isExpress());
        result.setProvinceCode(provinceCode);
        result.setProvinceName(province != null ? province.getProvinceName() : "");
        result.setZoneType(zoneType);
        result.setWeightKg(weightKg);
        result.setVolumeCm3(volumeCm3);
        result.setVolumetricWeight(volumetricWeight);
        result.setChargeableWeight(chargeableWeight);
        result.setBaseFee(baseFee);
        result.setFeePerKg(feePerKg);
        result.setPricePerVolume(pricePerVolume);
        result.setSurchargeMultiplier(surchargeMultiplier);
        result.setShippingFee(totalFee);
        result.setEstimatedDaysMin(estimatedDaysMin);
        result.setEstimatedDaysMax(estimatedDaysMax);
        result.setFreeShipping(isFreeShipping);
        result.setFreeShippingThreshold(method.getFreeShippingThreshold());

        return result;
    }

    private double calculateShippingFee(
            ShippingMethod method,
            ShippingFee shippingFee,
            ShippingZone zone,
            double actualWeight,
            double volumetricWeight,
            double chargeableWeight,
            double baseFee,
            double feePerKg,
            double pricePerVolume,
            double surchargeMultiplier) {

        double fee = baseFee + (feePerKg * chargeableWeight);

        if (volumetricWeight > actualWeight && pricePerVolume > 0) {
            double volumeExcess = volumetricWeight - actualWeight;
            fee += volumeExcess * pricePerVolume;
        }

        if (chargeableWeight > method.getMaxWeightKg()) {
            double excessWeight = chargeableWeight - method.getMaxWeightKg();
            double excessFee = excessWeight * feePerKg * 1.5;
            fee += excessFee;
        }

        fee = fee * surchargeMultiplier;

        if (shippingFee == null && zone != null) {
            fee = applyZoneMultiplier(fee, determineZoneType(provinceDAO.getByProvinceCode("")), method.isExpress());
        }

        return fee;
    }

    private double applyZoneMultiplier(double fee, String zoneType, boolean isExpress) {
        double multiplier = 1.0;

        switch (zoneType) {
            case "INNER":
                multiplier = 1.0;
                break;
            case "PROVINCIAL":
                multiplier = isExpress ? 1.2 : 1.3;
                break;
            case "REMOTE":
                multiplier = isExpress ? 1.5 : 1.8;
                break;
            default:
                multiplier = 1.0;
        }

        return fee * multiplier;
    }

    private String determineZoneType(Province province) {
        if (province == null) {
            return "PROVINCIAL";
        }

        if (province.getMetroCity()) {
            return "INNER";
        }

        ShippingZone zone = province.getShippingZone();
        if (zone != null && zone.getZoneType() != null) {
            return zone.getZoneType();
        }

        return "PROVINCIAL";
    }

    public List<ShippingMethod> getAvailableMethods() {
        return methodDAO.getAllActive();
    }

    public List<ShippingCalculationResult> calculateAllMethods(String provinceCode, double weightKg, double volumeCm3) {
        List<ShippingMethod> methods = methodDAO.getAllActive();
        java.util.List<ShippingCalculationResult> results = new java.util.ArrayList<>();

        for (ShippingMethod method : methods) {
            ShippingCalculationResult result = calculateFee(method, provinceCode, weightKg, volumeCm3);
            if (result != null) {
                results.add(result);
            }
        }

        results.sort((a, b) -> Double.compare(a.getShippingFee(), b.getShippingFee()));
        return results;
    }

    public ShippingMethod getDefaultMethod() {
        List<ShippingMethod> methods = methodDAO.getAllActive();
        if (methods.isEmpty()) {
            return null;
        }
        for (ShippingMethod m : methods) {
            if (!m.isExpress()) {
                return m;
            }
        }
        return methods.get(0);
    }

    public String generateTrackingCode(long shipmentId) {
        return "WEB" + String.format("%06d", shipmentId);
    }

    public LocalDateTime calculateEstimatedDelivery(ShippingMethod method, Province province) {
        if (method == null) {
            return LocalDateTime.now().plusDays(5);
        }

        int days = method.getEstimatedDays();

        if (province != null) {
            String zoneType = determineZoneType(province);
            ShippingZone zone = zoneDAO.getByZoneType(zoneType);
            if (zone != null) {
                days = Math.max(days, zone.getEstimatedDaysMin());
                days = Math.max(days, zone.getEstimatedDaysMax());
            }
        }

        return LocalDateTime.now().plusDays(days);
    }

    public ShippingMethod getMethodById(long methodId) {
        return methodDAO.getById(methodId);
    }

    public List<Province> getAllProvinces() {
        return provinceDAO.getAll();
    }

    public List<Province> getMetroCities() {
        return provinceDAO.getMetroCities();
    }

    public static class ShippingCalculationResult {
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
            return estimatedDaysMin + "-" + estimatedDaysMax + " ngay";
        }

        public String getZoneDisplayName() {
            switch (zoneType) {
                case "INNER": return "Noi thanh";
                case "PROVINCIAL": return "Tinh le";
                case "REMOTE": return "Vung xa";
                default: return zoneType;
            }
        }

        public String getServiceTypeName() {
            return isExpress ? "Giao hang nhanh" : "Giao hang tieu chuan";
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
}
