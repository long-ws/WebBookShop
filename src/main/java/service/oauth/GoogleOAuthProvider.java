package service.oauth;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import config.oauth.OAuthConfig;
import dto.oauth.OAuthUserResponse;
import exception.BusinessException;
import mapper.user.OAuthUserMapper;

public class GoogleOAuthProvider implements OAuthProvider {
    private final Gson gson;
    private final OAuthUserMapper oauthUserMapper;

    public GoogleOAuthProvider() {
        this.gson = new Gson();
        this.oauthUserMapper = new OAuthUserMapper();
    }

    public GoogleOAuthProvider(OAuthUserMapper oauthUserMapper) {
        this.gson = new Gson();
        this.oauthUserMapper = oauthUserMapper;
    }

    private OAuth20Service buildService(String callbackUrl) {
        return new ServiceBuilder(OAuthConfig.googleClientId())
                .apiSecret(OAuthConfig.googleClientSecret())
                .callback(callbackUrl)
                .defaultScope(OAuthConfig.googleScope())
                .build(GoogleApi20.instance());
    }

    @Override
    public String getAuthorizationUrl(String callbackUrl, String state) {
        OAuth20Service service = buildService(callbackUrl);
        String authUrl = service.getAuthorizationUrl();
        if (state != null) {
            authUrl += "&state=" + state;
        }
        return authUrl;
    }

    @Override
    public OAuthUserResponse getUser(String code, String callbackUrl) throws Exception {
        OAuth20Service service = buildService(callbackUrl);
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        OAuth2AccessToken accessToken = service.getAccessToken(code);
        OAuthRequest request = new OAuthRequest(Verb.GET, userInfoUrl);
        service.signRequest(accessToken, request);

        try (Response response = service.execute(request)) {
            if (!response.isSuccessful()) {
                throw new BusinessException("Xác thực thông tin user thất bại");
            }

            JsonObject json = gson.fromJson(response.getBody(), JsonObject.class);
            return oauthUserMapper.toOAuthUserResponse(json, "google");
        }
    }
}
