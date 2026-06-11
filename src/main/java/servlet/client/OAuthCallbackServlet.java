package servlet.client;

import beans.User;
import constants.RequestParamConstants;
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
	private final OAuthService oAuthService = new OAuthService();
	private static final long STATE_TTL_MS = 5 * 60 * 1000L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String provider = request.getParameter(RequestParamConstants.OAuth.PROVIDER);
		String code = request.getParameter(RequestParamConstants.OAuth.CODE);
		String state = request.getParameter(RequestParamConstants.OAuth.STATE);

		try {
			HttpSession session = request.getSession(false);
			if (session == null) {
				session = request.getSession(true);
				session.setAttribute(SessionConstants.OAUTH_ERROR, "Phiên OAuth đã hết hạn hoặc không hợp lệ. Vui lòng thử lại.");
				response.sendRedirect(request.getContextPath() + "/signin?" + RequestParamConstants.ERROR + "=" + RequestParamConstants.ErrorValue.OAUTH_STATE);
				return;
			}

			if (code == null || code.trim().isEmpty() || state == null || state.trim().isEmpty()) {
				session.setAttribute(SessionConstants.OAUTH_ERROR, "Phiên OAuth không hợp lệ. Vui lòng thử lại.");
				response.sendRedirect(request.getContextPath() + "/signin?" + RequestParamConstants.ERROR + "=" + RequestParamConstants.ErrorValue.OAUTH_STATE);
				return;
			}

			if (!isValidState(session, state)) {
				session.setAttribute(SessionConstants.OAUTH_ERROR, "Phiên OAuth không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.");
				response.sendRedirect(request.getContextPath() + "/signin?" + RequestParamConstants.ERROR + "=" + RequestParamConstants.ErrorValue.OAUTH_STATE);
				return;
			}

			if (provider == null || provider.trim().isEmpty()) {
				session.setAttribute(SessionConstants.OAUTH_ERROR, "Không thể xác định OAuth provider. Vui lòng thử lại.");
				response.sendRedirect(request.getContextPath() + "/signin?" + RequestParamConstants.ERROR + "=" + RequestParamConstants.ErrorValue.OAUTH_PROVIDER);
				return;
			}

			String providerLower = provider.trim().toLowerCase();
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
			OAuthProvider oauthProvider = OAuthFactory.get(providerLower);

			OAuthUserResponse oauthUser = oauthProvider.getUser(code, callbackUrl);
			User user = oAuthService.handleOAuthCallback(oauthUser, providerLower);

			request.changeSessionId();
			session = request.getSession(false);
			if (session == null) {
				session = request.getSession(true);
			}

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
			response.sendRedirect(request.getContextPath() + "/signin?" + RequestParamConstants.ERROR + "=" + RequestParamConstants.ErrorValue.OAUTH_FAILED);
		}
	}

	private boolean isValidState(HttpSession session, String requestState) {
		Object expectedObj = session.getAttribute(SessionConstants.OAUTH_STATE);
		Object createdAtObj = session.getAttribute(SessionConstants.OAUTH_STATE_CREATED_AT);

		String expected = (expectedObj instanceof String) ? (String) expectedObj : null;
		Long createdAt = (createdAtObj instanceof Long) ? (Long) createdAtObj : null;

		session.removeAttribute(SessionConstants.OAUTH_STATE);
		session.removeAttribute(SessionConstants.OAUTH_STATE_CREATED_AT);

		if (expected == null || createdAt == null) {
			return false;
		}
		if (!expected.equals(requestState)) {
			return false;
		}

		long now = System.currentTimeMillis();
		long ageMs = now - createdAt.longValue();
		return ageMs >= 0 && ageMs <= STATE_TTL_MS;
	}
}
