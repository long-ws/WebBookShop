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
import constants.SystemConstants;
import constants.ViewAttributeConstants;
import dto.user.UserDetailResponse;
import dto.user.UserUpdateRequest;
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

@WebServlet(name = "UpdateUserServlet", urlPatterns = "/admin/user/update")
public class UpdateUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final UserManagementService userManagementService = new UserManagementServiceImpl();
	private final RoleService roleService = new RoleServiceImpl();
	private final LanguageService languageService = new LanguageServiceImpl();

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		final String idStr = request.getParameter(RequestParamConstants.ID);
		long id = 0;

		try {
			if (idStr != null) {
				id = Long.parseLong(idStr.trim());
			}
		} catch (NumberFormatException e) {
			final Map<String, String> errors = new HashMap<>();
			errors.put(SystemConstants.ERROR_GLOBAL, "ID người dùng không hợp lệ.");
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/admin/user").forward(request, response);
			return;
		}

		final Map<String, String> errors = new HashMap<>();
		UserDetailResponse user = null;

		try {
			user = userManagementService.getUserById(id);
			if (user == null) {
				errors.put(SystemConstants.ERROR_GLOBAL, "Người dùng không tồn tại trên hệ thống.");
			}
		} catch (BusinessException e) {
			final Map<String, String> businessErrors = e.getErrors();
			if (businessErrors != null && !businessErrors.isEmpty()) {
				errors.putAll(businessErrors);
			} else {
				errors.put(SystemConstants.ERROR_GLOBAL, e.getMessage());
			}
		} catch (Exception e) {
			errors.put(SystemConstants.ERROR_GLOBAL, "Không thể tải thông tin người dùng do sự cố hệ thống.");
		}

		if (!errors.isEmpty()) {
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/admin/user").forward(request, response);
			return;
		}

		final UserUpdateRequest formDTO = userManagementService.toUpdateDTO(user);
		final boolean isSystemUser = user.getRole() != null && user.getRole().isSystem();

		final Map<String, String> values = new HashMap<>();
		values.put(RequestParamConstants.ID, String.valueOf(formDTO.getId()));
		values.put(RequestParamConstants.User.USERNAME, formDTO.getUsername());
		values.put(RequestParamConstants.User.FULLNAME, formDTO.getFullname());
		values.put(RequestParamConstants.User.EMAIL, formDTO.getEmail());
		values.put(RequestParamConstants.User.PHONE_NUMBER, formDTO.getPhoneNumber());
		values.put(RequestParamConstants.User.GENDER, formDTO.getGender() != null ? String.valueOf(formDTO.getGender().getId()) : "");
		values.put(RequestParamConstants.User.ROLE, formDTO.getRole() != null ? formDTO.getRole().getCode() : "");
		values.put(RequestParamConstants.User.PREFERRED_LANGUAGE_ID, formDTO.getPreferredLanguage() != null ? String.valueOf(formDTO.getPreferredLanguage().getId()) : "");

		loadDropdownData(request);
		request.setAttribute(ViewAttributeConstants.VALUES, values);
		request.setAttribute(ViewAttributeConstants.User.IS_SYSTEM, isSystemUser);
		request.getRequestDispatcher("/WEB-INF/views/updateUserView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		final Map<String, String> errors = new HashMap<>();
		final String idStr = request.getParameter(RequestParamConstants.ID);
		long id = 0;

		try {
			if (idStr != null) {
				id = Long.parseLong(idStr.trim());
			}
		} catch (NumberFormatException e) {
			errors.put(RequestParamConstants.ID, "ID người dùng không hợp lệ.");
		}

		final String username = request.getParameter(RequestParamConstants.User.USERNAME);
		final String fullname = request.getParameter(RequestParamConstants.User.FULLNAME);
		final String email = request.getParameter(RequestParamConstants.User.EMAIL);
		final String phoneNumber = request.getParameter(RequestParamConstants.User.PHONE_NUMBER);
		final String genderStr = request.getParameter(RequestParamConstants.User.GENDER);
		final String roleStr = request.getParameter(RequestParamConstants.User.ROLE);
		final String preferredLanguageIdStr = request.getParameter(RequestParamConstants.User.PREFERRED_LANGUAGE_ID);

		final Map<String, String> values = new HashMap<>();
		values.put(RequestParamConstants.ID, String.valueOf(id));
		values.put(RequestParamConstants.User.USERNAME, username);
		values.put(RequestParamConstants.User.FULLNAME, fullname);
		values.put(RequestParamConstants.User.EMAIL, email);
		values.put(RequestParamConstants.User.PHONE_NUMBER, phoneNumber);
		values.put(RequestParamConstants.User.GENDER, genderStr);
		values.put(RequestParamConstants.User.ROLE, roleStr);
		values.put(RequestParamConstants.User.PREFERRED_LANGUAGE_ID, preferredLanguageIdStr);

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

		final UserUpdateRequest dto = new UserUpdateRequest.Builder().id(id).username(username).password(request.getParameter(RequestParamConstants.User.PASSWORD)).fullname(fullname).email(email)
				.phoneNumber(phoneNumber).gender(gender).role(role).preferredLanguage(language).build();

		if (errors.isEmpty()) {
			try {
				userManagementService.updateUser(dto);
			} catch (BusinessException e) {
				final Map<String, String> businessErrors = e.getErrors();
				if (businessErrors != null && !businessErrors.isEmpty()) {
					errors.putAll(businessErrors);
				} else {
					errors.put(SystemConstants.ERROR_GLOBAL, e.getMessage());
				}
			} catch (Exception e) {
				errors.put(SystemConstants.ERROR_GLOBAL, "Sự cố hệ thống không thể cập nhật dữ liệu.");
			}
		}

		if (!errors.isEmpty()) {
			loadDropdownData(request);
			request.setAttribute(ViewAttributeConstants.VALUES, values);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/WEB-INF/views/updateUserView.jsp").forward(request, response);
			return;
		}

		SessionPermissionCache.clear(request.getSession());
		MessageHelper.setSuccessMessage(request.getSession(), "Đã cập nhật người dùng: " + dto.getUsername());
		response.sendRedirect(request.getContextPath() + "/admin/user/update?" + RequestParamConstants.ID + "=" + dto.getId());
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
	}
}
