package filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import beans.User;
import beans.common.Permission;
import config.PermissionRegistry;
import config.security.SecurityConfig;
import constants.SessionConstants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthorizationService;
import service.AuthorizationServiceImpl;
import utils.DBConnection;

public class AuthorizationFilter implements Filter {

	private final AuthorizationService authorizationService = new AuthorizationServiceImpl();

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);

		String currentPath = normalizePath(request);

		if (isPublicPage(currentPath)) {
			chain.doFilter(req, res);
			return;
		}

		User user = (session != null) ? (User) session.getAttribute(SessionConstants.CURRENT_USER) : null;
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/admin/signin");
			return;
		}

		if (isSuperAdmin(session, user)) {
			chain.doFilter(req, res);
			return;
		}

		Set<String> userPermissions = getPermissionsFromSession(session);
		if (userPermissions == null) {
			userPermissions = loadPermissionsFromDatabase(session, user.getId());
		}

		if (isAuthorized(currentPath, userPermissions)) {
			chain.doFilter(req, res);
		} else {
			response.sendRedirect(request.getContextPath() + "/admin/403");
		}
	}

	private String normalizePath(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		return (path.length() > 1 && path.endsWith("/")) ? path.substring(0, path.length() - 1) : path;
	}

	private boolean isPublicPage(String path) {
		return path.equals("/admin/403") || path.equals("/admin/signin");
	}

	private boolean isSuperAdmin(HttpSession session, User user) {
		boolean isSuperAdmin = SecurityConfig.isSuperAdminUsername(user.getUsername());
		session.setAttribute(SessionConstants.IS_SUPER_ADMIN, isSuperAdmin);
		return isSuperAdmin;
	}

	@SuppressWarnings("unchecked")
	private Set<String> getPermissionsFromSession(HttpSession session) {
		Object attr = session.getAttribute(SessionConstants.USER_PERMISSIONS);
		return (attr instanceof Set<?>) ? (Set<String>) attr : null;
	}

	private Set<String> loadPermissionsFromDatabase(HttpSession session, long userId) throws ServletException {
		try (Connection conn = DBConnection.getConnection()) {
			Set<String> permissions = extractCodes(authorizationService.getPermissionsByUserId(conn, userId));
			session.setAttribute(SessionConstants.USER_PERMISSIONS, permissions);
			return permissions;
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	private Set<String> extractCodes(List<Permission> list) {
		Set<String> codes = new HashSet<String>();
		if (list != null) {
			for (Permission p : list) {
				if (p != null && p.getCode() != null && !p.getCode().isBlank()) {
					codes.add(p.getCode());
				}
			}
		}
		return codes;
	}

	private boolean isAuthorized(String path, Set<String> userPermissions) {
		Set<String> requiredPermissions = findRequiredPermissions(path);
		if (requiredPermissions == null || requiredPermissions.isEmpty())
			return false;

		return hasAnyPermission(userPermissions, requiredPermissions);
	}

	private Set<String> findRequiredPermissions(String path) {
		Map<String, Set<String>> findRequiredPermissions = PermissionRegistry.getPermissionMap();
		if (findRequiredPermissions.containsKey(path))
			return findRequiredPermissions.get(path);

		for (String registeredPath : PermissionRegistry.getSortedPathsDescLength()) {
			if (path.startsWith(registeredPath) && isPathSegmentMatch(path, registeredPath)) {
				return findRequiredPermissions.get(registeredPath);
			}
		}
		return null;
	}

	private boolean hasAnyPermission(Set<String> userPermissions, Iterable<String> requiredPermissions) {
		for (String code : requiredPermissions) {
			if (hasPermission(userPermissions, code))
				return true;
		}
		return false;
	}

	private boolean hasPermission(Set<String> userPermissions, String code) {
		String viewPermission = getViewPermission(code);
		if (viewPermission != null && !userPermissions.contains(viewPermission))
			return false;
		return userPermissions.contains(code);
	}

	private String getViewPermission(String code) {
		int lastDot = code.lastIndexOf('.');
		if (lastDot <= 0)
			return null;
		String action = code.substring(lastDot + 1);
		return "view".equals(action) ? null : code.substring(0, lastDot) + ".view";
	}

	private boolean isPathSegmentMatch(String path, String registeredPath) {
		return path.length() == registeredPath.length() || path.charAt(registeredPath.length()) == '/';
	}
}
