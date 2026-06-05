package constants;

public final class TokenConstants {

	private TokenConstants() {
	}

	public enum TokenType {
		VERIFY_EMAIL("VERIFY_EMAIL"),
		RESET_PASSWORD("RESET_PASSWORD");

		private final String code;

		TokenType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	public enum TokenStatus {
		ACTIVE("ACTIVE"),
		USED("USED"),
		EXPIRED("EXPIRED");

		private final String code;

		TokenStatus(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}
}
