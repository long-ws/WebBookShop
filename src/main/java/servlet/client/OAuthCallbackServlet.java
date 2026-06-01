package servlet.client;

import beans.User;
import constants.SessionConstants;
import dto.oauth.OAuthUserResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
		String code = request.getParameter("code");
		String state = request.getParameter("state"); // Provider
		String callbackUrl = request.getRequestURL().toString();

		try {
			if (code == null || state == null) {
				response.sendRedirect(request.getContextPath() + "/signin");
				return;
			}

			OAuthProvider oauthProvider = OAuthFactory.get(state);

			OAuthUserResponse oauthUser = oauthProvider.getUser(code, callbackUrl);
			User user = oauthOrchestratorService.handleOAuthCallback(oauthUser, state);

			HttpSession session = request.getSession(true);
			session.setAttribute(SessionConstants.CURRENT_USER, user);

			if (user.getRole() != null) {
				session.setAttribute(SessionConstants.USER_ROLE, user.getRole().getCode());
			}

			response.sendRedirect(request.getContextPath() + "/");
		} catch (Exception e) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.setAttribute(SessionConstants.OAUTH_ERROR, "Đăng nhập OAuth thất bại. Vui lòng thử lại.");
			}
			response.sendRedirect(request.getContextPath() + "/signin?error=oauth_failed");
		}
	}
}
