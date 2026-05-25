package beans;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class ShipmentTracking {
	private long id;
	private long shipmentId;
	private String status;
	private String note;
	private String location;
	private String updatedBy;
	private LocalDateTime updatedAt;

	public ShipmentTracking() {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ShipmentTracking.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("shipmentId=" + shipmentId)
				.add("status=" + status)
				.add("updatedAt=" + updatedAt)
				.toString();
	}
}
