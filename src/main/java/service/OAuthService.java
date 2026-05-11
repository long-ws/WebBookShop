package service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import beans.oauth.OAuthUser;

public class OAuthService {

	private final Properties oauthProps;
	private final Gson gson;

	public OAuthService() {
		this.oauthProps = loadOAuthProperties();
		this.gson = new Gson();
	}

	private Properties loadOAuthProperties() {
		Properties props = new Properties();
		try (InputStream input = OAuthService.class.getClassLoader().getResourceAsStream("oauth.properties")) {
			if (input != null) {
				props.load(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	private OAuth20Service buildGoogleService(String callbackUrl) {
		System.out.println("[OAuthService]: Khởi tạo cấu hình authorization Oauth client cho google");
		return new ServiceBuilder(oauthProps.getProperty("google.client.id"))
				.apiSecret(oauthProps.getProperty("google.client.secret")).callback(callbackUrl)
				.defaultScope(oauthProps.getProperty("google.scope")).build(GoogleApi20.instance());
	}

	public String getAuthorizationUrl(String provider, String callbackUrl, String state) {
		System.out.println("[OAuthService]: Khởi tạo url để chuyển hướng đăng nhập Oauth");
		String providerLower = provider.toLowerCase();
		OAuth20Service service;

		switch (providerLower) {
		case "google":
			service = buildGoogleService(callbackUrl);
			break;
		default:
			throw new IllegalArgumentException("Không hỗ trợ: " + provider);
		}

		String authUrl = service.getAuthorizationUrl();
		if (state != null) {
			authUrl += "&state=" + state;
		}
		return authUrl;
	}

	public OAuthUser getUser(String code, String provider, String callbackUrl) throws Exception {
		System.out.println("[OAuthService]: Lấy thông tin người dùng từ Oauth");
		System.out.println("[OAuthService]: code: " + code);

		String providerLower = provider.toLowerCase();
		OAuth20Service service;
		
		// URL API dùng để lấy thông tin user từ provider
		String userInfoUrl;

		switch (providerLower) {
		case "google":
			service = buildGoogleService(callbackUrl);
			// Đây là endpoint của Google để lấy thông tin user
			userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
			break;
		default:
			throw new IllegalArgumentException("Không hỗ trợ: " + provider);
		}

		// Dùng authorization code để đổi lấy access token và trả về access token nếu hợp lệ
		OAuth2AccessToken accessToken = service.getAccessToken(code);

		// Tạo HTTP GET request tới UserInfo API (Google, ...)
		OAuthRequest request = new OAuthRequest(Verb.GET, userInfoUrl);
		
		// Gắn access token vào request
		service.signRequest(accessToken, request);

		// Thực hiện HTTP request tới API (Google, ...)
		try (Response response = service.execute(request)) {
			if (!response.isSuccessful()) {
				throw new RuntimeException("Lấy thông tin user thất bại: " + response.getBody());
			}

			// Thành công nhận được Json từ API (Google, ...)
			JsonObject json = gson.fromJson(response.getBody(), JsonObject.class);
			
			// Chuyển thông tin nhận được về thành OauthUser
			return parseOAuthUser(json, providerLower);
		}
	}

	private OAuthUser parseOAuthUser(JsonObject json, String provider) {
		OAuthUser user = new OAuthUser();
		switch (provider.toLowerCase()) {
		case "google":
			user.setId(getJsonString(json, "id"));
			user.setEmail(getJsonString(json, "email"));
			user.setName(getJsonString(json, "name"));
			user.setPictureUrl(getJsonString(json, "picture"));
			break;
		}
		return user;
	}

	private String getJsonString(JsonObject json, String key) {
		if (json.has(key) && !json.get(key).isJsonNull()) {
			return json.get(key).getAsString();
		}
		return null;
	}
}
