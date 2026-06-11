package servlet.client;

import java.io.IOException;
import java.sql.SQLException;

import constants.RequestParamConstants;
import constants.SessionConstants;
import helpers.MessageHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.EmailVerificationService;

@WebServlet(name = "EmailVerificationConfirmServlet", value = "/verify-email/confirm")
public class EmailVerificationConfirmServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final EmailVerificationService emailVerificationService = new EmailVerificationService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		Long userId = session != null ? (Long) session.getAttribute(SessionConstants.PENDING_VERIFICATION_USER_ID) : null;
		String email = session != null ? (String) session.getAttribute(SessionConstants.PENDING_VERIFICATION_EMAIL) : null;

		if (userId == null || email == null) {
			response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		String token = request.getParameter(RequestParamConstants.CODE);
		if (token == null || token.isBlank()) {
			MessageHelper.setErrorMessage(session, "Liên kết xác thực không hợp lệ.");
			response.sendRedirect(request.getContextPath() + "/verify-email");
			return;
		}

		try {
			boolean verified = emailVerificationService.verifyToken(userId, token);
			if (verified) {
				session.removeAttribute(SessionConstants.PENDING_VERIFICATION_USER_ID);
				session.removeAttribute(SessionConstants.PENDING_VERIFICATION_EMAIL);
				session.setAttribute(SessionConstants.SIGNUP_SUCCESS, "Xác thực email thành công! Vui lòng đăng nhập.");
				response.sendRedirect(request.getContextPath() + "/signin");
				return;
			}
			MessageHelper.setErrorMessage(session, "Liên kết xác thực không hợp lệ hoặc đã hết hạn.");
			response.sendRedirect(request.getContextPath() + "/verify-email");
		} catch (SQLException e) {
			MessageHelper.setErrorMessage(session, "Lỗi hệ thống khi xác thực email.");
			response.sendRedirect(request.getContextPath() + "/verify-email");
		}
	}
}
