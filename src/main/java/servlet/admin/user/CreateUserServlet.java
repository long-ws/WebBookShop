package servlet.admin.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.common.Gender;
import beans.common.Language;
import beans.common.Role;
import constants.RequestParamConstants;
import constants.ViewAttributeConstants;
import constants.system.SystemKeys;
import dto.user.UserCreateRequest;
import domain.user.UserDefaults;
import exception.BusinessException;
import helpers.MessageHelper;
import helpers.SessionPermissionCache;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.LanguageService;
import service.LanguageServiceImpl;
import service.RoleService;
import service.RoleServiceImpl;
import service.UserManagementService;
import service.UserManagementServiceImpl;

@WebServlet(name = "CreateUserServlet", urlPatterns = "/admin/user/create")
public class CreateUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserManagementService userManagementService = new UserManagementServiceImpl();
	private final RoleService roleService = new RoleServiceImpl();
	private final LanguageService languageService = new LanguageServiceImpl();

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		loadDropdownData(request);
		request.getRequestDispatcher("/WEB-INF/views/createUserView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		final String username = request.getParameter(RequestParamConstants.User.USERNAME);
		final String fullname = request.getParameter(RequestParamConstants.User.FULLNAME);
		final String email = request.getParameter(RequestParamConstants.User.EMAIL);
		final String phoneNumber = request.getParameter(RequestParamConstants.User.PHONE_NUMBER);
		final String genderStr = request.getParameter(RequestParamConstants.User.GENDER);
		final String roleStr = request.getParameter(RequestParamConstants.User.ROLE);
		final String preferredLanguageIdStr = request.getParameter(RequestParamConstants.User.PREFERRED_LANGUAGE_ID);

		final Map<String, String> values = new HashMap<>();
		values.put(RequestParamConstants.User.USERNAME, username);
		values.put(RequestParamConstants.User.FULLNAME, fullname);
		values.put(RequestParamConstants.User.EMAIL, email);
		values.put(RequestParamConstants.User.PHONE_NUMBER, phoneNumber);
		values.put(RequestParamConstants.User.GENDER, genderStr);
		values.put(RequestParamConstants.User.ROLE, roleStr);
		values.put(RequestParamConstants.User.PREFERRED_LANGUAGE_ID, preferredLanguageIdStr);

		final Map<String, String> errors = new HashMap<>();

		Gender gender = null;
		if (genderStr != null && !genderStr.trim().isEmpty()) {
			try {
				gender = new Gender();
				gender.setId(Integer.parseInt(genderStr));
			} catch (NumberFormatException e) {
				errors.put(RequestParamConstants.User.GENDER, "Định dạng giới tính không hợp lệ.");
			}
		}

		Role role = null;
		if (roleStr != null && !roleStr.trim().isEmpty()) {
			role = new Role();
			role.setCode(roleStr);
		}

		Language language = null;
		if (preferredLanguageIdStr != null && !preferredLanguageIdStr.trim().isEmpty()) {
			try {
				language = new Language();
				language.setId(Integer.parseInt(preferredLanguageIdStr));
			} catch (NumberFormatException e) {
				errors.put(RequestParamConstants.User.PREFERRED_LANGUAGE_ID, "Định dạng ngôn ngữ không hợp lệ.");
			}
		}

		final UserCreateRequest dto = new UserCreateRequest.Builder().username(username).password(request.getParameter(RequestParamConstants.User.PASSWORD)).fullname(fullname).email(email)
				.phoneNumber(phoneNumber).gender(gender).role(role).preferredLanguage(language).build();

		if (errors.isEmpty()) {
			try {
				userManagementService.createUser(dto);
			} catch (BusinessException e) {
				final Map<String, String> businessErrors = e.getErrors();
				if (businessErrors != null && !businessErrors.isEmpty()) {
					errors.putAll(businessErrors);
				} else {
					errors.put(SystemKeys.ERROR_GLOBAL, e.getMessage());
				}
			}
		}

		if (!errors.isEmpty()) {
			loadDropdownData(request);
			request.setAttribute(ViewAttributeConstants.VALUES, values);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/WEB-INF/views/createUserView.jsp").forward(request, response);
			return;
		}

		SessionPermissionCache.clear(request.getSession());
		MessageHelper.setSuccessMessage(request.getSession(), "Đã tạo người dùng: " + dto.getUsername());
		response.sendRedirect(request.getContextPath() + "/admin/user/create");
	}

	private void loadDropdownData(final HttpServletRequest request) {
		final List<Role> roles = roleService.getAllActiveRoles();
		final List<Role> filtered = new ArrayList<Role>();

		for (int i = 0; i < roles.size(); i++) {
			Role role = roles.get(i);
			if (role != null && !role.isSystem()) {
				filtered.add(role);
			}
		}

		request.setAttribute(ViewAttributeConstants.User.ALL_ROLES, filtered);
		request.setAttribute(ViewAttributeConstants.User.LANGUAGES, languageService.getAllActiveLanguages());
		request.setAttribute("defaultRoleCode", UserDefaults.DEFAULT_ROLE_CODE);
	}
}
