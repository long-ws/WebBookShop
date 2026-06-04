package servlet.client;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import constants.RequestParamConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import constants.SessionConstants;
import service.oauth.OAuthFactory;
import service.oauth.OAuthProvider;

@WebServlet(name = "OAuthLoginServlet", value = "/oauth-login")
public class OAuthLoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String provider = request.getParameter(RequestParamConstants.OAuth.PROVIDER);

		if (provider == null || provider.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		try {
			String providerLower = provider.trim().toLowerCase();
			HttpSession session = request.getSession(true);
			String state = generateState();
			session.setAttribute(SessionConstants.OAUTH_STATE, state);
			session.setAttribute(SessionConstants.OAUTH_STATE_CREATED_AT, Long.valueOf(System.currentTimeMillis()));

			OAuthProvider oauthProvider = OAuthFactory.get(providerLower);
			String callbackUrl = request.getScheme()
					+ "://"
					+ request.getServerName()
					+ ":"
					+ request.getServerPort()
					+ request.getContextPath()
					+ "/oauth-callback"
					+ "?"
					+ RequestParamConstants.OAuth.PROVIDER
					+ "="
					+ providerLower;
			String authUrl = oauthProvider.getAuthorizationUrl(callbackUrl, state);
			response.sendRedirect(authUrl);
		} catch (Exception e) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.setAttribute(SessionConstants.OAUTH_ERROR, "Lỗi khởi tạo OAuth. Vui lòng thử lại.");
			}
			response.sendRedirect(request.getContextPath() + "/signin");
			e.printStackTrace();		}
	}

	private static String generateState() {
		byte[] bytes = new byte[32];
		SECURE_RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}
