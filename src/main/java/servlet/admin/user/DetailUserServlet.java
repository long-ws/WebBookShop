package servlet.admin.user;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import constants.FormConstants;
import constants.RequestParamConstants;
import constants.ViewAttributeConstants;
import dto.user.UserDetailResponse;
import exception.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserManagementService;
import service.UserManagementServiceImpl;

@WebServlet(name = "DetailUserServlet", urlPatterns = "/admin/user/detail")
public class DetailUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserManagementService userManagementService = new UserManagementServiceImpl();

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		final Map<String, String> errors = new HashMap<>();
		final String idStr = request.getParameter(RequestParamConstants.ID);
		long id = 0;

		if (idStr == null || idStr.trim().isEmpty()) {
			errors.put(FormConstants.ERROR_GLOBAL, "Yêu cầu ID người dùng để xem chi tiết.");
		} else {
			try {
				id = Long.parseLong(idStr.trim());
			} catch (NumberFormatException e) {
				errors.put(FormConstants.ERROR_GLOBAL, "Định dạng ID người dùng không hợp lệ.");
			}
		}

		if (!errors.isEmpty()) {
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/admin/user").forward(request, response);
			return;
		}

		UserDetailResponse user = null;

		try {
			user = userManagementService.getUserById(id);
			if (user == null) {
				errors.put(FormConstants.ERROR_GLOBAL, "Người dùng không tồn tại trên hệ thống.");
			}
		} catch (BusinessException e) {
			final Map<String, String> businessErrors = e.getErrors();
			if (businessErrors != null && !businessErrors.isEmpty()) {
				errors.putAll(businessErrors);
			} else {
				errors.put(FormConstants.ERROR_GLOBAL, e.getMessage());
			}
		} catch (Exception e) {
			errors.put(FormConstants.ERROR_GLOBAL, "Không thể tải thông tin người dùng do sự cố hệ thống.");
		}

		if (!errors.isEmpty()) {
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/admin/user").forward(request, response);
			return;
		}

		request.setAttribute(ViewAttributeConstants.User.USER, user);
		request.getRequestDispatcher("/WEB-INF/views/userDetailView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
	}
}
