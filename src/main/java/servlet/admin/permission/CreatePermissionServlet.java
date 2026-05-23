package servlet.admin.permission;

import java.io.IOException;
import java.util.Map;

import constants.FormConstants;
import constants.RequestParamConstants;
import constants.ViewAttributeConstants;
import dto.permission.PermissionCreateRequest;
import exception.BusinessException;
import helpers.MessageHelper;
import helpers.SessionPermissionCache;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.PermissionService;
import service.PermissionServiceImpl;

@WebServlet(name = "CreatePermissionServlet", urlPatterns = "/admin/permission/create")
public class CreatePermissionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final PermissionService permissionService = new PermissionServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			request.setAttribute(ViewAttributeConstants.Permission.MODULES, permissionService.getAllModules());
			request.setAttribute(ViewAttributeConstants.Permission.PERMISSION,
					new PermissionCreateRequest.Builder().build());
			MessageHelper.cleanupFlashMessages(request.getSession());
			request.getRequestDispatcher("/WEB-INF/views/permissionManagerCreateView.jsp").forward(request, response);
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
		PermissionCreateRequest dto = new PermissionCreateRequest.Builder()
				.code(normalizeCode(request.getParameter(RequestParamConstants.CODE)))
				.name(request.getParameter(RequestParamConstants.NAME))
				.description(request.getParameter(RequestParamConstants.DESCRIPTION))
				.module(moduleParam != null ? moduleParam.toUpperCase() : null)
				.isSystem(false)
				.isActive(true)
				.build();

		try {
			permissionService.createPermission(dto);
			SessionPermissionCache.clear(request.getSession());
			MessageHelper.setSuccessMessage(request.getSession(), "Đã tạo quyền: " + dto.getName());
			response.sendRedirect(request.getContextPath() + "/admin/permission");
		} catch (BusinessException e) {
			Map<String, String> errors = e.getErrors();
			if (errors == null || errors.isEmpty()) {
				errors = Map.of(FormConstants.ERROR_GLOBAL, e.getMessage());
			}
			request.setAttribute(ViewAttributeConstants.Permission.PERMISSION, dto);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.setAttribute(ViewAttributeConstants.Permission.MODULES, permissionService.getAllModules());
			request.getRequestDispatcher("/WEB-INF/views/permissionManagerCreateView.jsp").forward(request, response);
		}
	}

	private String normalizeCode(String codeParam) {
		if (codeParam == null) {
			return null;
		}
		return codeParam.toUpperCase().trim().replace(" ", "_");
	}
}
