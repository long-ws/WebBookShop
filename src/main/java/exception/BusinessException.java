package exception;

import java.util.HashMap;
import java.util.Map;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final Map<String, String> errors;

	public BusinessException(String message) {
		super(message);
		this.errors = new HashMap<>();
		this.errors.put("general", message);
	}

	public BusinessException(Map<String, String> errors) {
		super("Validation failed");
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
