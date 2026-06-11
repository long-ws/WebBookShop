package servlet.admin.role;

import java.io.IOException;
import java.util.Map;

import constants.RequestParamConstants;
import constants.ViewAttributeConstants;
import constants.system.SystemKeys;
import dto.role.RoleCreateRequest;
import exception.BusinessException;
import helpers.MessageHelper;
import helpers.RequestParamHelper;
import helpers.SessionPermissionCache;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.PermissionService;
import service.PermissionServiceImpl;
import service.RoleService;
import service.RoleServiceImpl;

@WebServlet(name = "CreateRoleServlet", urlPatterns = "/admin/role/create")
public class CreateRoleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final RoleService roleService = new RoleServiceImpl();
	private final PermissionService permissionService = new PermissionServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setAttribute(ViewAttributeConstants.Role.ALL_PERMISSIONS, permissionService.getAllPermissions());
			request.setAttribute(ViewAttributeConstants.Role.ROLE, new RoleCreateRequest.Builder().build());
			request.getRequestDispatcher("/WEB-INF/views/roleManagerCreateView.jsp").forward(request, response);
		} catch (BusinessException e) {
			MessageHelper.setErrorMessage(request.getSession(), e.getMessage());
			response.sendRedirect(request.getContextPath() + "/admin/role");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		RoleCreateRequest dto = new RoleCreateRequest.Builder().code(normalizeCode(request.getParameter(RequestParamConstants.CODE))).name(request.getParameter(RequestParamConstants.NAME))
				.description(request.getParameter(RequestParamConstants.DESCRIPTION)).isSystem(false).isActive(true)
				.permissionIds(RequestParamHelper.parseIntegerList(request.getParameterValues(RequestParamConstants.Role.PERMISSION_IDS))).build();

		try {
			roleService.createRole(dto);
			SessionPermissionCache.clear(request.getSession());
			MessageHelper.setSuccessMessage(request.getSession(), "Đã tạo vai trò: " + dto.getCode());
			response.sendRedirect(request.getContextPath() + "/admin/role/create");
		} catch (BusinessException e) {
			Map<String, String> errors = e.getErrors();
			if (errors == null || errors.isEmpty()) {
				errors = Map.of(SystemKeys.ERROR_GLOBAL, e.getMessage());
			}
			request.setAttribute(ViewAttributeConstants.Role.ROLE, dto);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.setAttribute(ViewAttributeConstants.Role.ALL_PERMISSIONS, permissionService.getAllPermissions());
			request.getRequestDispatcher("/WEB-INF/views/roleManagerCreateView.jsp").forward(request, response);
		}
	}

	private String normalizeCode(String codeParam) {
		if (codeParam == null) {
			return null;
		}
		return codeParam.toUpperCase().trim().replace(" ", "_");
	}
}
