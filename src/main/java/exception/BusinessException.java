package exception;

import java.util.HashMap;
import java.util.Map;

import constants.FormConstants;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final Map<String, String> errors;

	public BusinessException(String message) {
		super(message);
		this.errors = new HashMap<>();
		this.errors.put(FormConstants.ERROR_GLOBAL, message);
	}

	public BusinessException(Map<String, String> errors) {
		super("Validation failed: " + errors.keySet());
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
