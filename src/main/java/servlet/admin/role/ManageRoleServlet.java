package servlet.admin.role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import constants.ViewAttributeConstants;
import context.UserPermissionContext;
import dto.role.ManageRoleResponse;
import exception.BusinessException;
import helpers.MessageHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.RoleService;
import service.RoleServiceImpl;

@WebServlet(name = "ManageRoleServlet", urlPatterns = "/admin/role")
public class ManageRoleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final RoleService roleService = new RoleServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<ManageRoleResponse> roles;
		try {
			roles = roleService.getRoles();
		} catch (BusinessException e) {
			roles = new ArrayList<>();
			MessageHelper.setErrorMessage(request.getSession(), e.getMessage());
		}

		UserPermissionContext securityContext = (UserPermissionContext) request
				.getAttribute(ViewAttributeConstants.Security.SECURITY_CONTEXT);
		if (securityContext != null) {
			request.setAttribute(ViewAttributeConstants.Role.HAS_CREATE, securityContext.isCanCreateRole());
			request.setAttribute(ViewAttributeConstants.Role.HAS_EDIT, securityContext.isCanEditRole());
			request.setAttribute(ViewAttributeConstants.Role.HAS_DELETE, securityContext.isCanDeleteRole());
		}

		request.setAttribute(ViewAttributeConstants.Role.ROLES, roles);
		request.getRequestDispatcher("/WEB-INF/views/roleManagerView.jsp").forward(request, response);
	}
}
