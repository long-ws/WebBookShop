package beans.user;

import beans.common.Gender;
import beans.common.Language;

import java.sql.Timestamp;

public class UserProfile {
	private long userId;
	private String fullname;
	private String phoneNumber;
	private String email;
	private Gender gender;
	private Language preferredLanguage;
	private String avatarUrl;
	private Timestamp updatedAt;

	public UserProfile() {
	}

	public UserProfile(long userId, String fullname, String phoneNumber, String email, Gender gender, Language preferredLanguage,
			Timestamp updatedAt) {
		this.userId = userId;
		this.fullname = fullname;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.gender = gender;
		this.preferredLanguage = preferredLanguage;
		this.updatedAt = updatedAt;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
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

	public Language getPreferredLanguage() {
		return preferredLanguage;
	}

	public void setPreferredLanguage(Language preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
}
