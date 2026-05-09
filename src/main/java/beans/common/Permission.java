package beans.common;

import java.sql.Timestamp;

public class Permission {
	private int id;
	private String code;
	private String name;
	private String description;
	private String module;
	private boolean isSystem;
	private boolean isActive;
	private Timestamp createdAt;

	public Permission() {
	}

	public Permission(int id, String code, String name, String description, String module, boolean isSystem,
			boolean isActive, Timestamp createdAt) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.description = description;
		this.module = module;
		this.isSystem = isSystem;
		this.isActive = isActive;
		this.createdAt = createdAt;
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

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
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
}
