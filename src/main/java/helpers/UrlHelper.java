package helpers;

import jakarta.servlet.http.HttpServletRequest;

public final class UrlHelper {

	private UrlHelper() {
	}

	public static String buildBaseUrl(final HttpServletRequest request) {
		if (request == null) {
			return "";
		}

		String scheme = request.getScheme();
		String host = request.getServerName();
		int port = request.getServerPort();
		String contextPath = request.getContextPath();

		if (scheme == null) {
			scheme = "http";
		}
		if (host == null) {
			host = "localhost";
		}
		if (contextPath == null) {
			contextPath = "";
		}

		boolean isHttpDefault = "http".equalsIgnoreCase(scheme) && port == 80;
		boolean isHttpsDefault = "https".equalsIgnoreCase(scheme) && port == 443;

		if (isHttpDefault || isHttpsDefault) {
			return scheme + "://" + host + contextPath;
		}
		return scheme + "://" + host + ":" + port + contextPath;
	}
}
