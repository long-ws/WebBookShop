package dto;

import beans.common.Gender;
import beans.common.Language;
import beans.common.Role;

import java.util.HashMap;
import java.util.Map;

public class UserFormDTO {
	private Long id;
	private String username;
	private String password;
	private String fullname;
	private String email;
	private String phoneNumber;
	private Gender gender;
	private Language preferredLanguage;
	private String avatarUrl;
	private Role role;

	private Map<String, String> errors = new HashMap<>();

	public UserFormDTO() {
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public void addError(String field, String message) {
		errors.put(field, message);
	}

	public String getError(String field) {
		return errors.get(field);
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	// New getters/setters for object references
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Language getPreferredLanguage() {
		return preferredLanguage;
	}

	public void setPreferredLanguage(Language preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

}
