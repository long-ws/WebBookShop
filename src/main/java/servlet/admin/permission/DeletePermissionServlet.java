package servlet.admin.permission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import service.PermissionService;
import service.PermissionServiceImpl;

@WebServlet(name = "DeletePermissionServlet", urlPatterns = "/admin/permission/delete")
public class DeletePermissionServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final PermissionService permissionService = new PermissionServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Phương thức GET không được hỗ trợ cho hành động xóa.");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final HttpSession session = request.getSession();
		final String[] permissionIds = request.getParameterValues(RequestParamConstants.Permission.PERMISSION_IDS);
		final List<Integer> idsToDelete = new ArrayList<>();

		if (permissionIds != null && permissionIds.length > 0) {
			for (final String idStr : permissionIds) {
				if (idStr != null && !idStr.trim().isEmpty()) {
					try {
						idsToDelete.add(Integer.parseInt(idStr.trim()));
					} catch (NumberFormatException e) {
					}
				}
			}
		}

		if (idsToDelete.isEmpty()) {
			MessageHelper.setErrorMessage(session, "Không có quyền hợp lệ nào được chọn để xóa.");
			response.sendRedirect(request.getContextPath() + "/admin/permission");
			return;
		}

		try {
			boolean success = permissionService.deletePermissions(idsToDelete);
			SessionPermissionCache.clear(session);
			if (success) {
				MessageHelper.setSuccessMessage(session, "Đã xóa quyền");
			} else {
				MessageHelper.setErrorMessage(session, "Không thể xóa quyền (có thể là quyền hệ thống)");
			}
		} catch (BusinessException e) {
			MessageHelper.setErrorMessage(session, e.getMessage());
		}

		response.sendRedirect(request.getContextPath() + "/admin/permission");
	}
}
