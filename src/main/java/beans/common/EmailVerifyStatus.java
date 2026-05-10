package beans.common;

import java.sql.Timestamp;

public class EmailVerifyStatus {
	private int id;
	private String code;
	private String description;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public EmailVerifyStatus() {
	}

	public EmailVerifyStatus(int id, String code, String description, Timestamp createdAt, Timestamp updatedAt) {
		this.id = id;
		this.code = code;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
}
