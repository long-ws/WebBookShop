package filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.User;
import constants.PermissionConstants;
import constants.SessionConstants;
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

@WebFilter(filterName = "AuthorizationFilter", value = "/admin/*")
public class AuthorizationFilter implements Filter {

	private static final Map<String, List<String>> PERMISSION_MAP = new HashMap<>();
	private final AuthorizationService authorizationService = new AuthorizationServiceImpl();

	static {
		PERMISSION_MAP.put("/admin/user", Arrays.asList(PermissionConstants.USER_VIEW));
		PERMISSION_MAP.put("/admin/user/detail", Arrays.asList(PermissionConstants.USER_VIEW));
		PERMISSION_MAP.put("/admin/user/create", Arrays.asList(PermissionConstants.USER_CREATE));
		PERMISSION_MAP.put("/admin/user/update", Arrays.asList(PermissionConstants.USER_EDIT));
		PERMISSION_MAP.put("/admin/user/delete", Arrays.asList(PermissionConstants.USER_DELETE));

		PERMISSION_MAP.put("/admin/role", Arrays.asList(PermissionConstants.ROLE_VIEW));
		PERMISSION_MAP.put("/admin/role/create", Arrays.asList(PermissionConstants.ROLE_CREATE));
		PERMISSION_MAP.put("/admin/role/update", Arrays.asList(PermissionConstants.ROLE_EDIT));
		PERMISSION_MAP.put("/admin/role/delete", Arrays.asList(PermissionConstants.ROLE_DELETE));

		PERMISSION_MAP.put("/admin/permission", Arrays.asList(PermissionConstants.PERMISSION_VIEW));
		PERMISSION_MAP.put("/admin/permission/create", Arrays.asList(PermissionConstants.PERMISSION_CREATE));
		PERMISSION_MAP.put("/admin/permission/update", Arrays.asList(PermissionConstants.PERMISSION_EDIT));
		PERMISSION_MAP.put("/admin/permission/delete", Arrays.asList(PermissionConstants.PERMISSION_DELETE));

		PERMISSION_MAP.put("/admin/categoryManager/view", Arrays.asList(PermissionConstants.CATEGORY_VIEW));
		PERMISSION_MAP.put("/admin/categoryManager/create", Arrays.asList(PermissionConstants.CATEGORY_CREATE));
		PERMISSION_MAP.put("/admin/categoryManager/update", Arrays.asList(PermissionConstants.CATEGORY_EDIT));
		PERMISSION_MAP.put("/admin/categoryManager/delete", Arrays.asList(PermissionConstants.CATEGORY_DELETE));

		PERMISSION_MAP.put("/admin/productManager/view", Arrays.asList(PermissionConstants.PRODUCT_VIEW));
		PERMISSION_MAP.put("/admin/productManager/create", Arrays.asList(PermissionConstants.PRODUCT_CREATE));
		PERMISSION_MAP.put("/admin/productManager/update", Arrays.asList(PermissionConstants.PRODUCT_EDIT));
		PERMISSION_MAP.put("/admin/productManager/delete", Arrays.asList(PermissionConstants.PRODUCT_DELETE));

		PERMISSION_MAP.put("/admin/reviewManager/view", Arrays.asList(PermissionConstants.REVIEW_VIEW));
		PERMISSION_MAP.put("/admin/reviewManager/update", Arrays.asList(PermissionConstants.REVIEW_MODERATE));

		PERMISSION_MAP.put("/admin/orderManager/view", Arrays.asList(PermissionConstants.ORDER_VIEW));
		PERMISSION_MAP.put("/admin/orderManager/update", Arrays.asList(PermissionConstants.ORDER_EDIT));
		PERMISSION_MAP.put("/admin/orderManager/delete", Arrays.asList(PermissionConstants.ORDER_DELETE));

		PERMISSION_MAP.put("/admin/voucherManager/view", Arrays.asList(PermissionConstants.VOUCHER_VIEW));
		PERMISSION_MAP.put("/admin/voucherManager/create", Arrays.asList(PermissionConstants.VOUCHER_CREATE));
		PERMISSION_MAP.put("/admin/voucherManager/update", Arrays.asList(PermissionConstants.VOUCHER_EDIT));
		PERMISSION_MAP.put("/admin/voucherManager/delete", Arrays.asList(PermissionConstants.VOUCHER_DELETE));
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);

		String loginURI = request.getContextPath() + "/admin/signin";
		String signoutURI = request.getContextPath() + "/admin/signout";
		String admin403URI = request.getContextPath() + "/admin/403";
		String adminHomeURI = request.getContextPath() + "/admin";

		User currentUser = null;
		if (session != null) {
			currentUser = (User) session.getAttribute(SessionConstants.CURRENT_USER);
		}

		boolean loginRequest = request.getRequestURI().equals(loginURI);
		boolean signoutRequest = request.getRequestURI().equals(signoutURI);
		boolean adminHomeRequest = request.getRequestURI().equals(adminHomeURI);

		if (currentUser == null) {
			if (loginRequest) {
				chain.doFilter(request, response);
			} else {
				response.sendRedirect(loginURI);
			}
			return;
		}

		if (loginRequest) {
			response.sendRedirect(adminHomeURI);
			return;
		}

		if (signoutRequest || adminHomeRequest) {
			chain.doFilter(request, response);
			return;
		}

		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String path = requestURI.substring(contextPath.length());

		boolean hasPermission = false;
		List<String> sortedKeys = new java.util.ArrayList<>(PERMISSION_MAP.keySet());
		sortedKeys.sort((a, b) -> Integer.compare(b.length(), a.length()));
		
		for (String key : sortedKeys) {
			if (path.startsWith(key)) {
				for (String permission : PERMISSION_MAP.get(key)) {
					if (authorizationService.hasPermission(currentUser.getId(), permission)) {
						hasPermission = true;
						break;
					}
				}
				if (hasPermission) {
					break;
				}
			}
		}

		if (hasPermission) {
			chain.doFilter(request, response);
		} else {
			response.sendRedirect(admin403URI);
		}
	}
}
