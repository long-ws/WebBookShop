package filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import beans.User;
import constants.SessionConstants;
import constants.ViewAttributeConstants;
import context.UserPermissionContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class PermissionFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession(false);

		if (session != null && session.getAttribute(SessionConstants.CURRENT_USER) instanceof User) {

			boolean isSuperAdmin = Boolean.TRUE.equals(session.getAttribute(SessionConstants.IS_SUPER_ADMIN));

			Set<String> permissions = isSuperAdmin ? Collections.emptySet() : getPermissionsFromSession(session);

			UserPermissionContext context = new UserPermissionContext(permissions, isSuperAdmin);
			request.setAttribute(ViewAttributeConstants.Security.USER_PERMISSION_CONTEXT, context);
		}

		chain.doFilter(req, res);
	}

	@SuppressWarnings("unchecked")
	private Set<String> getPermissionsFromSession(HttpSession session) {
		Object attr = session.getAttribute(SessionConstants.USER_PERMISSIONS);
		return (attr instanceof Set<?>) ? (Set<String>) attr : Collections.emptySet();
	}
}