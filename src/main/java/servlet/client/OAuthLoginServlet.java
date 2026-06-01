package servlet.client;

import java.io.IOException;

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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String provider = request.getParameter("provider");

		if (provider == null || provider.trim().isEmpty()) {
			response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		try {
			OAuthProvider oauthProvider = OAuthFactory.get(provider);
			String callbackUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/oauth-callback";
			String authUrl = oauthProvider.getAuthorizationUrl(callbackUrl, provider.toUpperCase());
			response.sendRedirect(authUrl);
		} catch (Exception e) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.setAttribute(SessionConstants.OAUTH_ERROR, "Lỗi khởi tạo OAuth. Vui lòng thử lại.");
			}
			response.sendRedirect(request.getContextPath() + "/signin");
		}
	}
}
