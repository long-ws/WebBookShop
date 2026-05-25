package beans;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class ShippingContact {
	private long id;
	private long shipmentId;
	private String contactType;
	private String contactRole;
	private String contactName;
	private String contactPhone;
	private LocalDateTime createdAt;

	public ShippingContact() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(long shipmentId) {
		this.shipmentId = shipmentId;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public String getContactRole() {
		return contactRole;
	}

	public void setContactRole(String contactRole) {
		this.contactRole = contactRole;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ShippingContact.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("shipmentId=" + shipmentId)
				.add("contactType=" + contactType)
				.add("contactName=" + contactName)
				.toString();
	}
}
