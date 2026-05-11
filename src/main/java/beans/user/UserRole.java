package beans.user;

import java.sql.Timestamp;

public class UserRole {
	private long userId;
	private int roleId;
	private Timestamp assignedAt;
	private Long assignedBy;

	public UserRole() {
	}

	public UserRole(long userId, int roleId, Timestamp assignedAt, Long assignedBy) {
		this.userId = userId;
		this.roleId = roleId;
		this.assignedAt = assignedAt;
		this.assignedBy = assignedBy;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public Timestamp getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(Timestamp assignedAt) {
		this.assignedAt = assignedAt;
	}

	public Long getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Long assignedBy) {
		this.assignedBy = assignedBy;
	}
}
