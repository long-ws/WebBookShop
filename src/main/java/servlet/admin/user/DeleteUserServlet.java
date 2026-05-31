package servlet.admin.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beans.User;
import constants.PermissionConstants;
import constants.RequestParamConstants;
import constants.SessionConstants;
import exception.BusinessException;
import helpers.MessageHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthorizationService;
import service.AuthorizationServiceImpl;
import service.UserManagementService;
import service.UserManagementServiceImpl;

@WebServlet(name = "DeleteUserServlet", urlPatterns = "/admin/user/delete")
public class DeleteUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserManagementService userManagementService = new UserManagementServiceImpl();
	private final AuthorizationService authorizationService = new AuthorizationServiceImpl();

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Phương thức GET không được hỗ trợ cho hành động xóa.");
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		final HttpSession session = request.getSession(false);
		final User currentUser = (session != null) ? (User) session.getAttribute(SessionConstants.CURRENT_USER) : null;

		if (currentUser == null) {
			response.sendRedirect(request.getContextPath() + "/admin/signin");
			return;
		}

		if (!authorizationService.hasPermission(currentUser.getId(), PermissionConstants.USER_DELETE)) {
			response.sendRedirect(request.getContextPath() + "/admin/403");
			return;
		}

		final String[] userIds = request.getParameterValues(RequestParamConstants.User.USER_IDS);
		final String singleId = request.getParameter(RequestParamConstants.ID);
		final List<Long> idsToDelete = new ArrayList<>();

		if (userIds != null && userIds.length > 0) {
			for (final String idStr : userIds) {
				if (idStr != null && !idStr.trim().isEmpty()) {
					try {
						idsToDelete.add(Long.parseLong(idStr.trim()));
					} catch (NumberFormatException e) {
					}
				}
			}
		} else if (singleId != null && !singleId.trim().isEmpty()) {
			try {
				idsToDelete.add(Long.parseLong(singleId.trim()));
			} catch (NumberFormatException e) {
				MessageHelper.setErrorMessage(request.getSession(), "ID người dùng không hợp lệ.");
				response.sendRedirect(request.getContextPath() + "/admin/user");
				return;
			}
		}

		if (idsToDelete.isEmpty()) {
			MessageHelper.setErrorMessage(request.getSession(), "Không có người dùng hợp lệ nào được chọn để xóa.");
			response.sendRedirect(request.getContextPath() + "/admin/user");
			return;
		}

		try {
			final boolean isDeleted = userManagementService.deleteUsers(idsToDelete);

			if (isDeleted) {
				StringBuilder idsBuilder = new StringBuilder();
				for (int i = 0; i < idsToDelete.size(); i++) {
					if (i > 0) {
						idsBuilder.append(", ");
					}
					idsBuilder.append(idsToDelete.get(i));
				}
				MessageHelper.setSuccessMessage(request.getSession(), "Đã xóa người dùng thành công: id " + idsBuilder);
			} else {
				MessageHelper.setErrorMessage(request.getSession(), "Xóa dữ liệu thất bại hoặc người dùng không tồn tại.");
			}
		} catch (BusinessException e) {
			final String message = (e.getErrors() != null && !e.getErrors().isEmpty())
					? e.getErrors().getOrDefault(RequestParamConstants.ID, e.getMessage())
					: e.getMessage();
			MessageHelper.setErrorMessage(request.getSession(), message);
		} catch (Exception e) {
			MessageHelper.setErrorMessage(request.getSession(), "Đã xảy ra sự cố hệ thống trong quá trình xóa.");
		}

		response.sendRedirect(request.getContextPath() + "/admin/user");
	}
}
