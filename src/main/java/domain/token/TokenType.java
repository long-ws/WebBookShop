package domain.token;

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
