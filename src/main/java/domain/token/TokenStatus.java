package domain.token;

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
