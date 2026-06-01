package filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import beans.User;
import beans.common.Permission;
import constants.SessionConstants;
import constants.UserConstants;
import constants.ViewAttributeConstants;
import context.UserPermissionContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthorizationService;
import service.AuthorizationServiceImpl;

@WebFilter(filterName = "PermissionFilter", value = "/admin/*")
public class PermissionFilter implements Filter {

	private final AuthorizationService authorizationService = new AuthorizationServiceImpl();

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession(false);

		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();

		String path = requestURI.substring(contextPath.length());

		if (path.equals("/admin/403") || path.equals("/admin/401")) {
			chain.doFilter(req, res);
			return;
		}

		if (session == null) {
			chain.doFilter(req, res);
			return;
		}

		User currentUser = (User) session.getAttribute(SessionConstants.CURRENT_USER);

		if (currentUser != null) {

			Set<String> sessionPermissions = getSessionPermissions(session);
			boolean isSuperAdmin;

			if (sessionPermissions == null) {

				long userId = currentUser.getId();
				isSuperAdmin = authorizationService.isSuperAdmin(userId);
				List<Permission> userPermissionsList = authorizationService.getPermissionsByUserId(userId);
				Set<String> permissionSet = new HashSet<String>();

				for (Permission permission : userPermissionsList) {

					if (permission != null) {
						String permissionCode = permission.getCode();

						if (permissionCode != null) {
							permissionSet.add(permissionCode);
						}
					}
				}

				session.setAttribute(SessionConstants.USER_PERMISSIONS, permissionSet);
				session.setAttribute(SessionConstants.IS_SUPER_ADMIN, isSuperAdmin);
				sessionPermissions = permissionSet;

			} else {

				Boolean isSuperAdminObj = (Boolean) session.getAttribute(SessionConstants.IS_SUPER_ADMIN);

				if (isSuperAdminObj != null) {
					isSuperAdmin = isSuperAdminObj;
				} else {
					isSuperAdmin = false;
				}
			}

			UserPermissionContext securityContext = new UserPermissionContext(sessionPermissions, isSuperAdmin);

			request.setAttribute(SessionConstants.IS_SUPER_ADMIN, isSuperAdmin);
			request.setAttribute(SessionConstants.USER_PERMISSIONS, sessionPermissions);
			request.setAttribute(ViewAttributeConstants.Security.SECURITY_CONTEXT, securityContext);
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_USERS, securityContext.isCanViewUser());
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_ROLES, securityContext.isCanViewRole());
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_PERMISSIONS,
					securityContext.isCanViewPermission());
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_CATEGORIES,
					securityContext.isCanViewCategory());
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_PRODUCTS, securityContext.isCanViewProduct());
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_REVIEWS, securityContext.isCanViewReview());
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_ORDERS, securityContext.isCanViewOrder());
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_VOUCHERS, securityContext.isCanViewVoucher());
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_SHIPMENTS, securityContext.isCanViewShipment());
			request.setAttribute(ViewAttributeConstants.Security.CAN_VIEW_SHIPPING_CONFIGS, securityContext.isCanViewShippingConfig());

			String primaryRole = UserConstants.Role.CUSTOMER;

			if (currentUser.getRole() != null) {

				String roleCode = currentUser.getRole().getCode();

				if (roleCode != null) {
					primaryRole = roleCode;
				}
			}

			request.setAttribute(SessionConstants.CURRENT_ROLE, primaryRole);

			if (path.startsWith("/admin")) {

				boolean hasAdminPermission = securityContext.hasAnyAdminPermission()
					|| securityContext.isCanViewShipment()
					|| securityContext.isCanViewShippingConfig();

				if (!hasAdminPermission) {
					// Sử dụng redirect thay vì sendError để hiển thị trang 403 tùy chỉnh
					response.sendRedirect(request.getContextPath() + "/admin/403");
					return;
				}
			}
		}

		chain.doFilter(req, res);
	}

	private Set<String> getSessionPermissions(HttpSession session) {

		Object attr = session.getAttribute(SessionConstants.USER_PERMISSIONS);

		if (attr instanceof Set<?>) {

			Set<?> rawSet = (Set<?>) attr;

			Set<String> permissions = new HashSet<String>();

			for (Object obj : rawSet) {

				if (obj instanceof String) {

					String permission = (String) obj;

					permissions.add(permission);
				}
			}

			return permissions;
		}

		return null;
	}
}