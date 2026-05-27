package beans;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class Shipment {
	private long id;
	private long orderId;
	private long shippingMethodId;
	private String trackingCode;
	private String receiverName;
	private String receiverPhone;
	private String province;
	private String district;
	private String ward;
	private String addressDetail;
	private double totalWeight;
	private double totalVolume;
	private double shippingFee;
	private String shippingStatus;
	private String sellerNote;
	private String customerNote;
	private String shipperContact;
	private LocalDateTime estimatedDeliveryDate;
	private LocalDateTime shippedAt;
	private LocalDateTime deliveredAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String providerType;
	private String providerOrderCode;
	private String shipperName;
	private String shipperPhone;
	private String shipperAvatar;
	private transient ShippingMethod shippingMethod;

	public Shipment() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public long getShippingMethodId() {
		return shippingMethodId;
	}

	public void setShippingMethodId(long shippingMethodId) {
		this.shippingMethodId = shippingMethodId;
	}

	public String getTrackingCode() {
		return trackingCode;
	}

	public void setTrackingCode(String trackingCode) {
		this.trackingCode = trackingCode;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverPhone() {
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	public String getAddressDetail() {
		return addressDetail;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}

	public double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public double getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(double totalVolume) {
		this.totalVolume = totalVolume;
	}

	public double getShippingFee() {
		return shippingFee;
	}

	public void setShippingFee(double shippingFee) {
		this.shippingFee = shippingFee;
	}

	public String getShippingStatus() {
		return shippingStatus;
	}

	public void setShippingStatus(String shippingStatus) {
		this.shippingStatus = shippingStatus;
	}

	public String getSellerNote() {
		return sellerNote;
	}

	public void setSellerNote(String sellerNote) {
		this.sellerNote = sellerNote;
	}

	public String getCustomerNote() {
		return customerNote;
	}

	public void setCustomerNote(String customerNote) {
		this.customerNote = customerNote;
	}

	public String getShipperContact() {
		return shipperContact;
	}

	public void setShipperContact(String shipperContact) {
		this.shipperContact = shipperContact;
	}

	public LocalDateTime getEstimatedDeliveryDate() {
		return estimatedDeliveryDate;
	}

	public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
		this.estimatedDeliveryDate = estimatedDeliveryDate;
	}

	public LocalDateTime getShippedAt() {
		return shippedAt;
	}

	public void setShippedAt(LocalDateTime shippedAt) {
		this.shippedAt = shippedAt;
	}

	public LocalDateTime getDeliveredAt() {
		return deliveredAt;
	}

	public void setDeliveredAt(LocalDateTime deliveredAt) {
		this.deliveredAt = deliveredAt;
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

	public String getProviderType() {
		return providerType;
	}

	public void setProviderType(String providerType) {
		this.providerType = providerType;
	}

	public String getProviderOrderCode() {
		return providerOrderCode;
	}

	public void setProviderOrderCode(String providerOrderCode) {
		this.providerOrderCode = providerOrderCode;
	}

	public String getShipperName() {
		return shipperName;
	}

	public void setShipperName(String shipperName) {
		this.shipperName = shipperName;
	}

	public String getShipperPhone() {
		return shipperPhone;
	}

	public void setShipperPhone(String shipperPhone) {
		this.shipperPhone = shipperPhone;
	}

	public String getShipperAvatar() {
		return shipperAvatar;
	}

	public void setShipperAvatar(String shipperAvatar) {
		this.shipperAvatar = shipperAvatar;
	}

	public ShippingMethod getShippingMethod() {
		return shippingMethod;
	}

	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Shipment.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("orderId=" + orderId)
				.add("trackingCode=" + trackingCode)
				.add("receiverName=" + receiverName)
				.add("shippingStatus=" + shippingStatus)
				.toString();
	}
}
