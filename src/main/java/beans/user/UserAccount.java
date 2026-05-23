package beans.user;

import java.sql.Timestamp;
import beans.common.UserStatus;

public class UserAccount {
	private long id;
	private UserStatus status;
	private int tokenVersion;
	private Timestamp lastLoginAt;
	private String rememberToken;
	private Timestamp rememberExpiresAt;
	private Timestamp deletedAt;
	private Long deletedBy;
	private Timestamp deletionScheduledAt;
	private Timestamp createdAt;
	private Timestamp updatedAt;

	public UserAccount() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public int getStatusId() {
		return status != null ? status.getId() : 0;
	}

	public void setStatusId(int statusId) {
		if (this.status == null) {
			this.status = new UserStatus();
		}
		this.status.setId(statusId);
	}

	public int getTokenVersion() {
		return tokenVersion;
	}

	public void setTokenVersion(int tokenVersion) {
		this.tokenVersion = tokenVersion;
	}

	public Timestamp getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(Timestamp lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public String getRememberToken() {
		return rememberToken;
	}

	public void setRememberToken(String rememberToken) {
		this.rememberToken = rememberToken;
	}

	public Timestamp getRememberExpiresAt() {
		return rememberExpiresAt;
	}

	public void setRememberExpiresAt(Timestamp rememberExpiresAt) {
		this.rememberExpiresAt = rememberExpiresAt;
	}

	public Timestamp getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Timestamp deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Long getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(Long deletedBy) {
		this.deletedBy = deletedBy;
	}

	public Timestamp getDeletionScheduledAt() {
		return deletionScheduledAt;
	}

	public void setDeletionScheduledAt(Timestamp deletionScheduledAt) {
		this.deletionScheduledAt = deletionScheduledAt;
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
