package servlet.admin.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constants.SystemConstants;
import constants.ViewAttributeConstants;
import context.UserPermissionContext;
import dto.user.UserManageResponse;
import exception.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserManagementService;
import service.UserManagementServiceImpl;

@WebServlet(name = "ManageUserServlet", urlPatterns = "/admin/user")
public class ManageUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserManagementService userManagementService = new UserManagementServiceImpl();

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		List<UserManageResponse> rawUsers = null;
		final Map<String, String> errors = new HashMap<>();

		try {
			rawUsers = userManagementService.getUsers();
		} catch (BusinessException e) {
			final Map<String, String> businessErrors = e.getErrors();
			if (businessErrors != null && !businessErrors.isEmpty()) {
				errors.putAll(businessErrors);
			} else {
				errors.put(SystemConstants.ERROR_GLOBAL, e.getMessage());
			}
		} catch (Exception e) {
			errors.put(SystemConstants.ERROR_GLOBAL, "Không thể tải danh sách người dùng do sự cố hệ thống.");
		}

		final List<UserManageResponse> users = rawUsers != null ? rawUsers : new ArrayList<>();

		final UserPermissionContext userPermissionContext = (UserPermissionContext) request.getAttribute(ViewAttributeConstants.Security.USER_PERMISSION_CONTEXT);

		boolean canCreate = false;
		boolean canEdit = false;
		boolean canDelete = false;

		if (userPermissionContext != null) {
			canCreate = userPermissionContext.isCanCreateUser();
			canEdit = userPermissionContext.isCanEditUser();
			canDelete = userPermissionContext.isCanDeleteUser();
		}

		request.setAttribute(ViewAttributeConstants.User.USERS, users);
		request.setAttribute(ViewAttributeConstants.ERRORS, errors);
		request.setAttribute(ViewAttributeConstants.User.HAS_CREATE, canCreate);
		request.setAttribute(ViewAttributeConstants.User.HAS_EDIT, canEdit);
		request.setAttribute(ViewAttributeConstants.User.HAS_DELETE, canDelete);

		request.getRequestDispatcher("/WEB-INF/views/userManagerView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
	}
}
