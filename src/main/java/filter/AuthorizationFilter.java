package filter;

import java.io.IOException;

import beans.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(filterName = "AuthorizationFilter", value = "/admin/*")
public class AuthorizationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);

		String loginURI = request.getContextPath() + "/admin/signin";
		String admin401URI = request.getContextPath() + "/admin/401";

		String role = null;
		if (session != null) {
			User currentUser = (User) session.getAttribute("currentUser");
			if (currentUser != null) {
				role = currentUser.getRole();
			}
		}

		boolean isAdmin = "ADMIN".equals(role);
		boolean isEmployee = "EMPLOYEE".equals(role);
		boolean loginRequest = request.getRequestURI().equals(loginURI);

		boolean isNotAccessibleForEmployee = false;
		if (isEmployee) {
			String[] restrictedPaths = { "/admin/userManager" };
			for (String path : restrictedPaths) {
				String fullPath = request.getContextPath() + path;
				if (request.getRequestURI().startsWith(fullPath)) {
					isNotAccessibleForEmployee = true;
					break;
				}
			}
		}

		if (isAdmin || isEmployee || loginRequest) {
			if (isEmployee && isNotAccessibleForEmployee) {
				response.sendRedirect(admin401URI);
			} else {
				chain.doFilter(request, response);
			}
		} else {
			response.sendRedirect(loginURI);
		}
	}
}
