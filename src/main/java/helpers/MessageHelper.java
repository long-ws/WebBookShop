package helpers;

import constants.SessionConstants;
import jakarta.servlet.http.HttpSession;

public final class MessageHelper {

	private MessageHelper() {
	}

	public static void setSuccessMessage(HttpSession session, String message) {
		session.setAttribute(SessionConstants.SUCCESS_MESSAGE, message);
	}

	public static void setErrorMessage(HttpSession session, String message) {
		session.setAttribute(SessionConstants.ERROR_MESSAGE, message);
	}

	public static void cleanupFlashMessages(HttpSession session) {
		if (session == null) {
			return;
		}
		session.removeAttribute(SessionConstants.SUCCESS_MESSAGE);
		session.removeAttribute(SessionConstants.ERROR_MESSAGE);
	}
}
