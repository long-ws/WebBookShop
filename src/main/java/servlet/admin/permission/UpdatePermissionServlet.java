package servlet.admin.permission;

import java.io.IOException;
import java.util.Map;

import constants.RequestParamConstants;
import constants.ViewAttributeConstants;
import constants.system.SystemKeys;
import dto.permission.PermissionDetailResponse;
import dto.permission.PermissionUpdateRequest;
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

@WebServlet(name = "UpdatePermissionServlet", urlPatterns = "/admin/permission/update")
public class UpdatePermissionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final PermissionService permissionService = new PermissionServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Integer permissionId = RequestParamHelper.parseInteger(request.getParameter(RequestParamConstants.ID));
		if (permissionId == null) {
			response.sendRedirect(request.getContextPath() + "/admin/permission");
			return;
		}

		try {
			PermissionDetailResponse detail = permissionService.getPermissionById(permissionId);
			if (detail == null) {
				response.sendRedirect(request.getContextPath() + "/admin/permission");
				return;
			}

			request.setAttribute(ViewAttributeConstants.Permission.PERMISSION, permissionService.toUpdateDTO(detail));
			request.setAttribute(ViewAttributeConstants.Permission.MODULES, permissionService.getAllModules());
			request.getRequestDispatcher("/WEB-INF/views/permissionManagerEditView.jsp").forward(request, response);
		} catch (BusinessException e) {
			MessageHelper.setErrorMessage(request.getSession(), e.getMessage());
			response.sendRedirect(request.getContextPath() + "/admin/permission");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String moduleParam = request.getParameter(RequestParamConstants.MODULE);
		PermissionUpdateRequest dto = new PermissionUpdateRequest.Builder()
				.id(RequestParamHelper.parseInteger(request.getParameter(RequestParamConstants.ID)))
				.code(normalizeCode(request.getParameter(RequestParamConstants.CODE)))
				.name(request.getParameter(RequestParamConstants.NAME))
				.description(request.getParameter(RequestParamConstants.DESCRIPTION))
				.module(moduleParam != null ? moduleParam.toUpperCase() : null)
				.isSystem(RequestParamHelper.isCheckboxChecked(request, RequestParamConstants.IS_SYSTEM))
				.isActive(RequestParamHelper.isCheckboxChecked(request, RequestParamConstants.IS_ACTIVE))
				.build();

		try {
			permissionService.updatePermission(dto);
			SessionPermissionCache.clear(request.getSession());
			MessageHelper.setSuccessMessage(request.getSession(), "Đã cập nhật quyền: " + dto.getCode());
			response.sendRedirect(request.getContextPath() + "/admin/permission/update?" + RequestParamConstants.ID + "=" + dto.getId());
		} catch (BusinessException e) {
			Map<String, String> errors = e.getErrors();
			if (errors == null || errors.isEmpty()) {
				errors = Map.of(SystemKeys.ERROR_GLOBAL, e.getMessage());
			}
			request.setAttribute(ViewAttributeConstants.Permission.PERMISSION, dto);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.setAttribute(ViewAttributeConstants.Permission.MODULES, permissionService.getAllModules());
			request.getRequestDispatcher("/WEB-INF/views/permissionManagerEditView.jsp").forward(request, response);
		}
	}

	private String normalizeCode(String codeParam) {
		if (codeParam == null) {
			return null;
		}
		return codeParam.trim().toLowerCase();
	}
}
