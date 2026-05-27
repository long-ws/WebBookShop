package servlet.admin.role;

import java.io.IOException;

import constants.RequestParamConstants;
import exception.BusinessException;
import helpers.MessageHelper;
import helpers.RequestParamHelper;
import helpers.SessionPermissionCache;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AssignRolePemissionService;
import service.AssignRolePermissionServiceImpl;

@WebServlet(name = "AssignRolePermissionServlet", urlPatterns = "/admin/role/batchAssignPermission")
public class AssignRolePermissionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final AssignRolePemissionService assignRolePemissionService = new AssignRolePermissionServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer roleId = RequestParamHelper.parseInteger(request.getParameter(RequestParamConstants.Role.ROLE_ID));
		if (roleId == null) {
			response.sendRedirect(request.getContextPath() + "/admin/role");
			return;
		}

		try {
			assignRolePemissionService.assignPermissionsToRole(roleId,
					RequestParamHelper.parseIntegerList(request.getParameterValues(RequestParamConstants.Role.PERMISSION_IDS)));
			SessionPermissionCache.clear(request.getSession());
			MessageHelper.setSuccessMessage(request.getSession(), "Đã thêm quyền thành công!");
		} catch (BusinessException e) {
			MessageHelper.setErrorMessage(request.getSession(), e.getMessage());
		}

		response.sendRedirect(request.getContextPath() + "/admin/role/update?id=" + roleId);
	}
}
