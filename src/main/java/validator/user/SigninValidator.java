package validator.user;

import dto.user.SigninRequest;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class SigninValidator extends BaseValidator<SigninRequest> {

	public SigninValidator() {
	}

	@Override
	protected void validateFormat(SigninRequest dto, ValidationResult result) {
		String username = dto.getUsername();
		if (username == null || username.trim().isEmpty()) {
			result.addError("username", "Tên đăng nhập không được để trống");
		}

		String password = dto.getPassword();
		if (password == null || password.trim().isEmpty()) {
			result.addError("password", "Mật khẩu không được để trống");
		}
	}
}
