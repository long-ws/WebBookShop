package servlet.client;

import beans.User;
import dto.oauth.OAuthUserResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.oauth.OAuthFactory;
import service.oauth.OAuthProvider;
import service.oauth.OAuthService;
import java.io.IOException;

@WebServlet(name = "OAuthCallbackServlet", value = "/oauth-callback")
public class OAuthCallbackServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final OAuthService oauthOrchestratorService = new OAuthService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("[OAuthCallbackServlet]: Bắt đầu trả Oauth về");
		String code = request.getParameter("code");
		String state = request.getParameter("state"); // Provider
		String callbackUrl = request.getRequestURL().toString();

		System.out.println("[OAuthCallbackServlet]: Thông tin: code: " + code + ", state: " + state + "\ncallbackUrl: "
				+ callbackUrl);

		try {
			if (code == null || state == null) {
				System.out.println("[OAuthCallbackServlet]: " + code + " hoặc " + state + " null");
				response.sendRedirect(request.getContextPath() + "/signin");
				return;
			}

			OAuthProvider oauthProvider = OAuthFactory.get(state);

			OAuthUserResponse oauthUser = oauthProvider.getUser(code, callbackUrl);
			User user = oauthOrchestratorService.handleOAuthCallback(oauthUser, state);

			request.getSession().setAttribute("currentUser", user);

			if (user.getRole() != null) {
				request.getSession().setAttribute("userRole", user.getRole().getCode());
			}
			System.out.println("[OAuthCallbackServlet]: Đăng nhập thành công\nUser: " + user.getId() + ", Role: "
					+ user.getRole().getCode());

			response.sendRedirect(request.getContextPath() + "/");
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("oauthError", "Lỗi đăng nhập: " + e.getMessage());
			response.sendRedirect(request.getContextPath() + "/signin?error=oauth_failed");
		}
	}
}