package servlet.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import beans.common.Role;
import constants.RequestParamConstants;
import constants.SessionConstants;
import constants.ViewAttributeConstants;
import constants.system.SystemKeys;
import domain.user.UserDefaults;
import dto.user.UserCreateRequest;
import dto.user.UserDetailResponse;
import exception.BusinessException;
import helpers.MessageHelper;
import helpers.UrlHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthenticationService;
import service.AuthenticationServiceImpl;
import service.EmailVerificationService;
import service.EmailVerificationService.SendVerificationStatus;

@WebServlet(name = "SignupServlet", value = "/signup")
public class SignupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final AuthenticationService authenticationService = new AuthenticationServiceImpl();
	private final EmailVerificationService emailVerificationService = new EmailVerificationService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final String username = request.getParameter(RequestParamConstants.User.USERNAME);
		final String password = request.getParameter(RequestParamConstants.User.PASSWORD);
		final String fullname = request.getParameter(RequestParamConstants.User.FULLNAME);
		final String email = request.getParameter(RequestParamConstants.User.EMAIL);

		final Map<String, String> values = new HashMap<>();
		values.put(RequestParamConstants.User.USERNAME, username);
		values.put(RequestParamConstants.User.FULLNAME, fullname);
		values.put(RequestParamConstants.User.EMAIL, email);

		final Map<String, String> errors = new HashMap<>();

		final Role roleObj = new Role();
		roleObj.setCode(UserDefaults.DEFAULT_ROLE_CODE);

		final UserCreateRequest dto = new UserCreateRequest.Builder().username(username).password(password).fullname(fullname).email(email).role(roleObj).build();

		try {
			final UserDetailResponse createdUser = authenticationService.signupUser(dto);
			final Long userId = createdUser != null ? createdUser.getId() : null;
			final String createdEmail = createdUser != null ? createdUser.getEmail() : null;

			if (userId == null || createdEmail == null || createdEmail.isBlank()) {
				errors.put(SystemKeys.ERROR_GLOBAL, "Đăng ký thành công nhưng không thể khởi tạo xác thực email.");
				request.setAttribute(ViewAttributeConstants.VALUES, values);
				request.setAttribute(ViewAttributeConstants.ERRORS, errors);
				request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
				return;
			}

			final HttpSession session = request.getSession(true);
			session.setAttribute(SessionConstants.PENDING_VERIFICATION_USER_ID, userId);
			session.setAttribute(SessionConstants.PENDING_VERIFICATION_EMAIL, createdEmail);

			final String baseUrl = UrlHelper.buildBaseUrl(request);
			SendVerificationStatus status = emailVerificationService.sendVerificationEmail(userId, createdEmail, baseUrl);
			if (status == SendVerificationStatus.SENT) {
				MessageHelper.setSuccessMessage(session, "Email xác thực đã được gửi. Vui lòng kiểm tra hộp thư.");
			} else if (status == SendVerificationStatus.RATE_LIMITED) {
				MessageHelper.setErrorMessage(session, "Bạn gửi yêu cầu quá nhanh. Vui lòng thử lại sau.");
			} else if (status == SendVerificationStatus.TOO_MANY_REQUESTS) {
				MessageHelper.setErrorMessage(session, "Bạn đã yêu cầu gửi email xác thực quá nhiều lần. Vui lòng thử lại sau.");
			} else {
				MessageHelper.setErrorMessage(session, "Không thể gửi email xác thực lúc này. Bạn có thể thử gửi lại.");
			}

			response.sendRedirect(request.getContextPath() + "/verify-email");
		} catch (BusinessException e) {
			final Map<String, String> businessErrors = e.getErrors();
			if (businessErrors != null && !businessErrors.isEmpty()) {
				errors.putAll(businessErrors);
			} else {
				errors.put(SystemKeys.ERROR_GLOBAL, e.getMessage());
			}

			request.setAttribute(ViewAttributeConstants.VALUES, values);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
		} catch (Exception e) {
			errors.put(SystemKeys.ERROR_GLOBAL, "Đăng ký thất bại do sự cố hệ thống.");
			request.setAttribute(ViewAttributeConstants.VALUES, values);
			request.setAttribute(ViewAttributeConstants.ERRORS, errors);
			request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
		}
	}
}
