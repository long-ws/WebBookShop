package dto;

import java.sql.Timestamp;

import beans.common.Gender;
import beans.common.UserStatus;

public class AdminUserListDTO {
	private long id;
	private String username;
	private String fullname;
	private String email;
	private String phoneNumber;
	private Gender gender;
	private String role;
	private UserStatus status;
	private Timestamp createdAt;

	public AdminUserListDTO() {
	}

	public AdminUserListDTO(long id, String username, String fullname, String email, String phoneNumber, Gender gender,
			String role, UserStatus status, Timestamp createdAt) {
		this.id = id;
		this.username = username;
		this.fullname = fullname;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.gender = gender;
		this.role = role;
		this.status = status;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}
