package helpers;

import constants.SessionConstants;
import jakarta.servlet.http.HttpSession;

public final class SessionPermissionCache {

	private SessionPermissionCache() {
	}

	public static void clear(HttpSession session) {
		if (session == null) {
			return;
		}
		session.removeAttribute(SessionConstants.USER_PERMISSIONS);
		session.removeAttribute(SessionConstants.IS_SUPER_ADMIN);
	}
}
