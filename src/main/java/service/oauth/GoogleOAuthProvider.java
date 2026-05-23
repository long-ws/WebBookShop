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
import dto.oauth.OAuthUserResponse;
import mapper.user.OAuthUserMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GoogleOAuthProvider implements OAuthProvider {
    private final Properties oauthProps;
    private final Gson gson;
    private final mapper.user.OAuthUserMapper oauthUserMapper;

    public GoogleOAuthProvider() {
        this.oauthProps = loadOAuthProperties();
        this.gson = new Gson();
        this.oauthUserMapper = new mapper.user.OAuthUserMapper();
    }

    public GoogleOAuthProvider(mapper.user.OAuthUserMapper oauthUserMapper) {
        this.oauthProps = loadOAuthProperties();
        this.gson = new Gson();
        this.oauthUserMapper = oauthUserMapper;
    }

    private Properties loadOAuthProperties() {
        Properties props = new Properties();
        try (InputStream input = GoogleOAuthProvider.class.getClassLoader().getResourceAsStream("oauth.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    private OAuth20Service buildService(String callbackUrl) {
        return new ServiceBuilder(oauthProps.getProperty("google.client.id"))
                .apiSecret(oauthProps.getProperty("google.client.secret"))
                .callback(callbackUrl)
                .defaultScope(oauthProps.getProperty("google.scope"))
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
                throw new RuntimeException("Lấy thông tin user thất bại: " + response.getBody());
            }

            JsonObject json = gson.fromJson(response.getBody(), JsonObject.class);
            return oauthUserMapper.toOAuthUserResponse(json, "google");
        }
    }
}
