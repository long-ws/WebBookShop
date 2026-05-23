package servlet.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.oauth.OAuthFactory;
import service.oauth.OAuthProvider;
import java.io.IOException;

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
			OAuthProvider oauthProvider = OAuthFactory.get(provider);
			System.out.println("[OAuthLoginServlet]: Thực thi lấy provider theo: " + provider);

			String callbackUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath() + "/oauth-callback";
			System.out.println("[OAuthLoginServlet]: callbackUrl: " + callbackUrl);

			String authUrl = oauthProvider.getAuthorizationUrl(callbackUrl, provider.toUpperCase());
			System.out.println("[OAuthLoginServlet]: Lấy url để chuyển hướng đăng nhập " + provider + " authUrl: " + authUrl);

			response.sendRedirect(authUrl);
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("oauthError", "Lỗi khởi tạo OAuth: " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/signin");
		}
	}
}