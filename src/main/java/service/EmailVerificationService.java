package service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HexFormat;

import constants.RequestParamConstants;
import dao.user.UserLocalDAO;
import dao.user.UserLocalDAOImpl;
import dao.user.UserTokenDAO;
import dao.user.UserTokenDAOImpl;
import domain.token.TokenType;
import domain.user.UserIds;
import policy.email.EmailVerificationPolicy;
import utils.DbTransaction;
import utils.TransactionCallback;

public class EmailVerificationService {

	public enum SendVerificationStatus {
		SENT,
		RATE_LIMITED,
		TOO_MANY_REQUESTS,
		FAILED
	}

	private static final TokenType TOKEN_TYPE_VERIFY_EMAIL = TokenType.VERIFY_EMAIL;

	private static final SecureRandom RANDOM = new SecureRandom();

	private final UserTokenDAO userTokenDAO;
	private final UserLocalDAO userLocalDAO;
	private final EmailService emailService;

	public EmailVerificationService() {
		this(new UserTokenDAOImpl(), new UserLocalDAOImpl(), new EmailService());
	}

	public EmailVerificationService(UserTokenDAO userTokenDAO, UserLocalDAO userLocalDAO, EmailService emailService) {
		this.userTokenDAO = userTokenDAO;
		this.userLocalDAO = userLocalDAO;
		this.emailService = emailService;
	}

	public SendVerificationStatus sendVerificationEmail(final long userId, final String email, final String baseUrl) throws SQLException {
		if (email == null || email.isBlank()) {
			return SendVerificationStatus.FAILED;
		}

		final String token = generateToken();
		final String tokenHash = hashToken(token);

		final Long tokenInsertResult = DbTransaction.run(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(Connection conn) throws SQLException {
				int countLastHour = userTokenDAO.countCreatedAfterMinutes(conn, userId, TOKEN_TYPE_VERIFY_EMAIL.getCode(), 60);
				if (countLastHour >= EmailVerificationPolicy.MAX_SEND_PER_HOUR) {
					return 0L;
				}

				int countLastInterval = userTokenDAO.countCreatedAfterSeconds(conn, userId, TOKEN_TYPE_VERIFY_EMAIL.getCode(),
						EmailVerificationPolicy.MIN_SEND_INTERVAL_SECONDS);
				if (countLastInterval > 0) {
					return -1L;
				}

				userTokenDAO.expireActiveTokens(conn, userId, TOKEN_TYPE_VERIFY_EMAIL.getCode());
				return userTokenDAO.insertToken(conn, userId, tokenHash, TOKEN_TYPE_VERIFY_EMAIL.getCode(), EmailVerificationPolicy.TOKEN_EXPIRES_MINUTES);
			}
		});

		if (tokenInsertResult == null) {
			return SendVerificationStatus.FAILED;
		}
		if (tokenInsertResult == 0L) {
			return SendVerificationStatus.TOO_MANY_REQUESTS;
		}
		if (tokenInsertResult == -1L) {
			return SendVerificationStatus.RATE_LIMITED;
		}

		final String confirmUrl = baseUrl + "/verify-email/confirm?" + RequestParamConstants.CODE + "=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
		final String subject = "Xác thực email";
		final String content = "Vui lòng xác thực email bằng cách nhấn vào liên kết sau:\n\n" + confirmUrl + "\n\nLiên kết sẽ hết hạn sau " + EmailVerificationPolicy.TOKEN_EXPIRES_MINUTES + " phút.";
		boolean sent = emailService.send(email, subject, content);
		return sent ? SendVerificationStatus.SENT : SendVerificationStatus.FAILED;
	}

	public boolean verifyToken(final long userId, final String token) throws SQLException {
		if (token == null || token.isBlank()) {
			return false;
		}

		final String tokenHash = hashToken(token);

		return DbTransaction.run(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(Connection conn) throws SQLException {
				Long tokenId = userTokenDAO.findActiveTokenId(conn, userId, tokenHash, TOKEN_TYPE_VERIFY_EMAIL.getCode());
				if (tokenId == null) {
					return false;
				}

				boolean marked = userTokenDAO.markTokenUsed(conn, tokenId);
				if (!marked) {
					return false;
				}

				userLocalDAO.updateEmailVerifyStatus(conn, userId, UserIds.EmailVerifyStatus.VERIFIED);
				return true;
			}
		});
	}

	private static String generateToken() {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private static String hashToken(final String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		} catch (Exception e) {
			throw new RuntimeException("Không thể băm token", e);
		}
	}
}
