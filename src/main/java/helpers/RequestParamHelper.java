package helpers;

import java.util.ArrayList;
import java.util.List;

import constants.RequestParamConstants;
import jakarta.servlet.http.HttpServletRequest;

public final class RequestParamHelper {

	private RequestParamHelper() {
	}

	public static List<Integer> parseIntegerList(String[] values) {
		List<Integer> ids = new ArrayList<>();
		if (values == null) {
			return ids;
		}
		for (String value : values) {
			if (value == null || value.trim().isEmpty()) {
				continue;
			}
			try {
				ids.add(Integer.parseInt(value.trim()));
			} catch (NumberFormatException ignored) {
			}
		}
		return ids;
	}

	public static Integer parseInteger(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Long parseLong(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		try {
			return Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static boolean isCheckboxChecked(HttpServletRequest request, String paramName) {
		return RequestParamConstants.CHECKBOX_ON.equals(request.getParameter(paramName));
	}
}
