package service.oauth;

import dto.oauth.OAuthUserResponse;

public interface OAuthProvider {
    String getAuthorizationUrl(String callbackUrl, String state);
    OAuthUserResponse getUser(String code, String callbackUrl) throws Exception;
}
