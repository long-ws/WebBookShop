package beans.common;

import java.sql.Timestamp;
import java.util.List;

public class Role {
	private int id;
	private String code;
	private String name;
	private String description;
	private boolean isSystem;
	private boolean isActive;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	private List<Permission> permissions;

	public Role() {
	}

	public Role(int id, String code, String name, String description, boolean isSystem, boolean isActive,
			Timestamp createdAt, Timestamp updatedAt) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.description = description;
		this.isSystem = isSystem;
		this.isActive = isActive;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean system) {
		isSystem = system;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
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

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
}
