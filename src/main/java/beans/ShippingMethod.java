package beans;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class ShippingMethod {
	private long id;
	private String name;
	private int estimatedDays;
	private double pricePerKg;
	private String supportPhone;
	private String supportEmail;
	private int status;
	private String providerType;
	private String apiKey;
	private String apiSecret;
	private String webhookToken;

	// GHN specific fields
	private int ghnServiceId;      // GHN service ID (2 = Giao hàng nhanh)
	private String ghnFromDistrictId;  // District ID gửi hàng
	private String ghnFromWardCode;    // Ward code gửi hàng
	private String ghnShopId;            // Shop ID GHN
	private String ghnToken;             // Token GHN

	// Pricing configuration
	private boolean isExpress;           // TRUE = Giao nhanh, FALSE = Giao tieu chuan
	private double expressSurcharge;     // He so nhan (VD: 1.5 = gap 50%)
	private double minWeightKg;         // Trong luong toi thieu
	private double maxWeightKg;          // Trong luong toi da
	private double freeShippingThreshold; // Neu tong don > N thi mien phi ship

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public ShippingMethod() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEstimatedDays() {
		return estimatedDays;
	}

	public void setEstimatedDays(int estimatedDays) {
		this.estimatedDays = estimatedDays;
	}

	public double getPricePerKg() {
		return pricePerKg;
	}

	public void setPricePerKg(double pricePerKg) {
		this.pricePerKg = pricePerKg;
	}

	public String getSupportPhone() {
		return supportPhone;
	}

	public void setSupportPhone(String supportPhone) {
		this.supportPhone = supportPhone;
	}

	public String getSupportEmail() {
		return supportEmail;
	}

	public void setSupportEmail(String supportEmail) {
		this.supportEmail = supportEmail;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiSecret() {
		return apiSecret;
	}

	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}

	public String getWebhookToken() {
		return webhookToken;
	}

	public void setWebhookToken(String webhookToken) {
		this.webhookToken = webhookToken;
	}

	public int getGhnServiceId() {
		return ghnServiceId;
	}

	public void setGhnServiceId(int ghnServiceId) {
		this.ghnServiceId = ghnServiceId;
	}

	public String getGhnFromDistrictId() {
		return ghnFromDistrictId;
	}

	public void setGhnFromDistrictId(String ghnFromDistrictId) {
		this.ghnFromDistrictId = ghnFromDistrictId;
	}

	public String getGhnFromWardCode() {
		return ghnFromWardCode;
	}

	public void setGhnFromWardCode(String ghnFromWardCode) {
		this.ghnFromWardCode = ghnFromWardCode;
	}

	public String getGhnShopId() {
		return ghnShopId;
	}

	public void setGhnShopId(String ghnShopId) {
		this.ghnShopId = ghnShopId;
	}

	public String getGhnToken() {
		return ghnToken;
	}

	public void setGhnToken(String ghnToken) {
		this.ghnToken = ghnToken;
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

	public boolean isExpress() {
		return isExpress;
	}

	public void setExpress(boolean express) {
		isExpress = express;
	}

	public double getExpressSurcharge() {
		return expressSurcharge;
	}

	public void setExpressSurcharge(double expressSurcharge) {
		this.expressSurcharge = expressSurcharge;
	}

	public double getMinWeightKg() {
		return minWeightKg;
	}

	public void setMinWeightKg(double minWeightKg) {
		this.minWeightKg = minWeightKg;
	}

	public double getMaxWeightKg() {
		return maxWeightKg;
	}

	public void setMaxWeightKg(double maxWeightKg) {
		this.maxWeightKg = maxWeightKg;
	}

	public double getFreeShippingThreshold() {
		return freeShippingThreshold;
	}

	public void setFreeShippingThreshold(double freeShippingThreshold) {
		this.freeShippingThreshold = freeShippingThreshold;
	}

	public String getServiceTypeName() {
		return isExpress ? "Giao hàng nhanh" : "Giao hàng tiêu chuẩn";
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ShippingMethod.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("name=" + name)
				.add("providerType=" + providerType)
				.add("ghnServiceId=" + ghnServiceId)
				.add("status=" + status)
				.toString();
	}
}
