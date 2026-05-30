package servlet.admin.role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.common.Role;
import constants.RequestParamConstants;
import exception.BusinessException;
import helpers.MessageHelper;
import helpers.SessionPermissionCache;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.RoleService;
import service.RoleServiceImpl;

@WebServlet(name = "DeleteRoleServlet", urlPatterns = "/admin/role/delete")
public class DeleteRoleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final RoleService roleService = new RoleServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Phương thức GET không được hỗ trợ cho hành động xóa.");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final HttpSession session = request.getSession();
		final String[] roleIds = request.getParameterValues(RequestParamConstants.Role.ROLE_IDS);
		final List<Integer> idsToDelete = new ArrayList<>();

		if (roleIds != null && roleIds.length > 0) {
			for (final String idStr : roleIds) {
				if (idStr != null && !idStr.trim().isEmpty()) {
					try {
						idsToDelete.add(Integer.parseInt(idStr.trim()));
					} catch (NumberFormatException e) {
					}
				}
			}
		}

		if (idsToDelete.isEmpty()) {
			MessageHelper.setErrorMessage(session, "Không có vai trò hợp lệ nào được chọn để xóa.");
			response.sendRedirect(request.getContextPath() + "/admin/role");
			return;
		}

		try {
			List<String> deletedInfo = new ArrayList<>();
			for (Integer id : idsToDelete) {
				Role role = roleService.getById(id);
				if (role != null) {
					deletedInfo.add(id + " - " + role.getCode());
				}
			}

			boolean success = roleService.deleteRoles(idsToDelete);
			SessionPermissionCache.clear(session);
			if (success) {
				String infoStr = String.join(", ", deletedInfo);
				MessageHelper.setSuccessMessage(session, "Đã xóa vai trò: " + infoStr);
			} else {
				MessageHelper.setErrorMessage(session, "Không thể xóa vai trò (có thể là vai trò hệ thống)");
			}
		} catch (BusinessException e) {
			MessageHelper.setErrorMessage(session, e.getMessage());
		}

		response.sendRedirect(request.getContextPath() + "/admin/role");
	}
}
