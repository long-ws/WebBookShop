package dto.user;

import beans.common.Gender;
import beans.common.Language;

public class UserProfileRequest {
    private final String fullname;
    private final String email;
    private final String phoneNumber;
    private final String avatarUrl;
    private final Gender gender;
    private final Language preferredLanguage;

    private UserProfileRequest(Builder builder) {
        this.fullname = builder.fullname;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.avatarUrl = builder.avatarUrl;
        this.gender = builder.gender;
        this.preferredLanguage = builder.preferredLanguage;
    }

    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAvatarUrl() { return avatarUrl; }
    public Gender getGender() { return gender; }
    public Language getPreferredLanguage() { return preferredLanguage; }

    public static class Builder {
        private String fullname;
        private String email;
        private String phoneNumber;
        private String avatarUrl;
        private Gender gender;
        private Language preferredLanguage;

        public Builder fullname(String fullname) {
            this.fullname = fullname;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder preferredLanguage(Language preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
            return this;
        }

        public UserProfileRequest build() {
            return new UserProfileRequest(this);
        }
    }
}