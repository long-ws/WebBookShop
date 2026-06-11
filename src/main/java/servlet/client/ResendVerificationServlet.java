package servlet.client;

import java.io.IOException;
import java.sql.SQLException;

import constants.SessionConstants;
import helpers.MessageHelper;
import helpers.UrlHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.EmailVerificationService;
import service.EmailVerificationService.SendVerificationStatus;

@WebServlet(name = "ResendVerificationServlet", value = "/verify-email/resend")
public class ResendVerificationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final EmailVerificationService emailVerificationService = new EmailVerificationService();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		Long userId = session != null ? (Long) session.getAttribute(SessionConstants.PENDING_VERIFICATION_USER_ID) : null;
		String email = session != null ? (String) session.getAttribute(SessionConstants.PENDING_VERIFICATION_EMAIL) : null;

		if (userId == null || email == null) {
			response.sendRedirect(request.getContextPath() + "/signin");
			return;
		}

		String baseUrl = UrlHelper.buildBaseUrl(request);

		try {
			SendVerificationStatus status = emailVerificationService.sendVerificationEmail(userId, email, baseUrl);
			if (status == SendVerificationStatus.SENT) {
				MessageHelper.setSuccessMessage(session, "Đã gửi lại email xác thực. Vui lòng kiểm tra hộp thư.");
			} else if (status == SendVerificationStatus.RATE_LIMITED) {
				MessageHelper.setErrorMessage(session, "Vui lòng thử lại sau 60 giây.");
			} else if (status == SendVerificationStatus.TOO_MANY_REQUESTS) {
				MessageHelper.setErrorMessage(session, "Bạn đã yêu cầu gửi email xác thực quá nhiều lần trong ngày. Vui lòng thử lại ngày sau.");
			} else {
				MessageHelper.setErrorMessage(session, "Không thể gửi email xác thực. Vui lòng thử lại sau.");
			}
		} catch (SQLException e) {
			MessageHelper.setErrorMessage(session, "Lỗi hệ thống khi gửi email xác thực.");
		}

		response.sendRedirect(request.getContextPath() + "/verify-email");
	}
}
