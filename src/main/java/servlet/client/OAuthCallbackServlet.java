package servlet.client;

import beans.oauth.OAuthUser;
import beans.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.UserRepositoryImpl;
import repository.OAuthAuthRepositoryImpl;
import service.OAuthService;
import service.OAuthUserService;
import service.oauth.OAuthFactory;
import java.io.IOException;

@WebServlet(name = "OAuthCallbackServlet", value = "/oauth-callback")
public class OAuthCallbackServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final OAuthUserService oauthUserService = new OAuthUserService(new UserRepositoryImpl(),
			new OAuthAuthRepositoryImpl());

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

			OAuthService oauthService = OAuthFactory.get(state);

			OAuthUser oauthUser = oauthService.getUser(code, state, callbackUrl);
			User user = oauthUserService.handleOAuthCallback(oauthUser, state);

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