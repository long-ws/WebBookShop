package servlet.admin.role;

import java.io.IOException;
import java.util.Map;

import constants.FormConstants;
import constants.RequestParamConstants;
import constants.ViewAttributeConstants;
import dto.role.RoleEditFormResponse;
import dto.role.RoleUpdateRequest;
import exception.BusinessException;
import helpers.MessageHelper;
import helpers.RequestParamHelper;
import helpers.SessionPermissionCache;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.RoleService;
import service.RoleServiceImpl;

@WebServlet(name = "UpdateRoleServlet", urlPatterns = "/admin/role/update")
public class UpdateRoleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final RoleService roleService = new RoleServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer roleId = RequestParamHelper.parseInteger(request.getParameter(RequestParamConstants.ID));
		if (roleId == null) {
			response.sendRedirect(request.getContextPath() + "/admin/role");
			return;
		}

		try {
			RoleEditFormResponse form = roleService.getRoleEditForm(roleId);
			if (form == null) {
				response.sendRedirect(request.getContextPath() + "/admin/role");
				return;
			}
			applyEditForm(request, form);
			MessageHelper.cleanupFlashMessages(request.getSession());
			request.getRequestDispatcher("/WEB-INF/views/roleManagerEditView.jsp").forward(request, response);
		} catch (BusinessException e) {
			MessageHelper.setErrorMessage(request.getSession(), e.getMessage());
			response.sendRedirect(request.getContextPath() + "/admin/role");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		RoleUpdateRequest dto = new RoleUpdateRequest.Builder()
				.id(RequestParamHelper.parseInteger(request.getParameter(RequestParamConstants.ID)))
				.code(normalizeCode(request.getParameter(RequestParamConstants.CODE)))
				.name(request.getParameter(RequestParamConstants.NAME))
				.description(request.getParameter(RequestParamConstants.DESCRIPTION))
				.isSystem(RequestParamHelper.isCheckboxChecked(request, RequestParamConstants.IS_SYSTEM))
				.isActive(RequestParamHelper.isCheckboxChecked(request, RequestParamConstants.IS_ACTIVE))
				.permissionIds(RequestParamHelper
						.parseIntegerList(request.getParameterValues(RequestParamConstants.Role.PERMISSION_IDS)))
				.assignedRoleIds(RequestParamHelper
						.parseIntegerList(request.getParameterValues(RequestParamConstants.Role.ASSIGNED_ROLE_IDS)))
				.build();

		try {
			roleService.updateRole(dto);
			SessionPermissionCache.clear(request.getSession());
			MessageHelper.setSuccessMessage(request.getSession(), "Đã cập nhật vai trò: " + dto.getName());
			response.sendRedirect(request.getContextPath() + "/admin/role");
		} catch (BusinessException e) {
			Map<String, String> errors = e.getErrors();
			if (errors == null || errors.isEmpty()) {
				errors = Map.of(FormConstants.ERROR_GLOBAL, e.getMessage());
			}
			if (dto.getId() != null) {
				try {
					RoleEditFormResponse form = roleService.getRoleEditForm(dto.getId());
					if (form != null) {
						applyEditForm(request, form);
					}
				} catch (BusinessException loadError) {
					// Giữ form tối thiểu với dữ liệu người dùng vừa gửi
				}
			}
			request.setAttribute(ViewAttributeConstants.Role.ROLE, dto);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/WEB-INF/views/roleManagerEditView.jsp").forward(request, response);
		}
	}

	private void applyEditForm(HttpServletRequest request, RoleEditFormResponse form) {
		request.setAttribute(ViewAttributeConstants.Role.ROLE, form.getRole());
		request.setAttribute(ViewAttributeConstants.Role.ALL_PERMISSIONS, form.getAllPermissions());
		request.setAttribute(ViewAttributeConstants.Role.ALL_ROLES, form.getAllRoles());
		request.setAttribute(ViewAttributeConstants.Role.ROLE_PERMISSIONS, form.getRolePermissions());
		request.setAttribute(ViewAttributeConstants.Role.PERMISSION_ROLE_MAP, form.getPermissionRoleMap());
	}

	private String normalizeCode(String codeParam) {
		if (codeParam == null) {
			return null;
		}
		return codeParam.toUpperCase().trim().replace(" ", "_");
	}
}
