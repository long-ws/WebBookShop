package beans.user;

import java.sql.Timestamp;

import beans.common.EmailVerifyStatus;

public class UserLocalAuth {
	private long userId;
	private String username;
	private String passwordHash;
	private String email;
	private EmailVerifyStatus emailVerifyStatus;
	private int failedAttempts;
	private Timestamp lockedUntil;

	public UserLocalAuth() {
	}

	public UserLocalAuth(long userId, String username, String passwordHash, String email,
			EmailVerifyStatus emailVerifyStatus, int failedAttempts, Timestamp lockedUntil) {
		this.userId = userId;
		this.username = username;
		this.passwordHash = passwordHash;
		this.email = email;
		this.emailVerifyStatus = emailVerifyStatus;
		this.failedAttempts = failedAttempts;
		this.lockedUntil = lockedUntil;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public EmailVerifyStatus getEmailVerifyStatus() {
		return emailVerifyStatus;
	}

	public void setEmailVerifyStatus(EmailVerifyStatus emailVerifyStatus) {
		this.emailVerifyStatus = emailVerifyStatus;
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public Timestamp getLockedUntil() {
		return lockedUntil;
	}

	public void setLockedUntil(Timestamp lockedUntil) {
		this.lockedUntil = lockedUntil;
	}
}
