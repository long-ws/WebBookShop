package servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OAuthService;
import service.oauth.OAuthFactory;
import java.io.IOException;

/**
 * Servlet for initiating OAuth login flow. Simplified to only handle 3rd party
 * login.
 */
@WebServlet(name = "OAuthLoginServlet", value = "/oauth-login")
public class OAuthLoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("[OAuthLoginServlet]: Bắt đầu thực hiện đăng nhập bằng Oauth");

		String provider = request.getParameter("provider");
		System.out.println("[OAuthLoginServlet]: Lấy parameter provider: " + provider);

		if (provider == null || provider.trim().isEmpty()) {
			System.out.println("[OAuthLoginServlet]: Provider null hoặc trống");
			response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		try {
			OAuthService oauthService = OAuthFactory.get(provider);
			System.out.println("[OAuthLoginServlet]: Thực thi lấy service theo: " + provider);

			String callbackUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath() + "/oauth-callback";
			System.out.println("[OAuthLoginServlet]: callbackUrl: " + callbackUrl);

			String authUrl = oauthService.getAuthorizationUrl(provider, callbackUrl, provider.toUpperCase());
			System.out.println("[OAuthLoginServlet]: Lấy url để chuyển hướng đăng nhập " + provider + " authUrl: " + authUrl);

			response.sendRedirect(authUrl);
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("oauthError", "Lỗi khởi tạo OAuth: " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/signin");
		}
	}
}