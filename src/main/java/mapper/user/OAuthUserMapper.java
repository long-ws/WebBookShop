package mapper.user;

import com.google.gson.JsonObject;
import dto.oauth.OAuthUserResponse;
import dto.user.OAuthUserRegistrationRequest;

public class OAuthUserMapper {
    public OAuthUserMapper() {
    }

    public OAuthUserResponse toOAuthUserResponse(JsonObject json, String provider) {
        String providerLower = provider.toLowerCase();
        OAuthUserResponse.Builder builder = new OAuthUserResponse.Builder();
        
        switch (providerLower) {
            case "google":
                builder.id(getJsonString(json, "id"))
                       .email(getJsonString(json, "email"))
                       .name(getJsonString(json, "name"))
                       .pictureUrl(getJsonString(json, "picture"))
                       .provider(providerLower);
                break;
        }
        
        return builder.build();
    }

    public OAuthUserRegistrationRequest toOAuthUserRegistrationRequest(OAuthUserResponse oauthUserResponse, int providerId) {
        return new OAuthUserRegistrationRequest.Builder()
                .providerId(providerId)
                .providerUserId(oauthUserResponse.getId())
                .fullname(oauthUserResponse.getName())
                .email(oauthUserResponse.getEmail())
                .avatarUrl(oauthUserResponse.getPictureUrl())
                .build();
    }

    private String getJsonString(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }
}