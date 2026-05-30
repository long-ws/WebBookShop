package validator.user;

import constants.RequestParamConstants;
import constants.SystemConstants;
import dto.user.LocalUserRegistrationRequest;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class LocalUserRegistrationValidator extends BaseValidator<LocalUserRegistrationRequest> {

	public LocalUserRegistrationValidator() {
	}

	@Override
	protected void validateFormat(LocalUserRegistrationRequest dto, ValidationResult result) {
		String username = dto.getUsername();
		if (username == null || username.trim().isEmpty()) {
			result.addError(RequestParamConstants.User.USERNAME, "Tên đăng nhập không được để trống");
		} else if (!username.equals(username.trim())) {
			result.addError(RequestParamConstants.User.USERNAME, "Tên đăng nhập không có dấu cách ở hai đầu");
		} else if (username.length() > SystemConstants.Validation.USERNAME_MAX_LENGTH) {
			result.addError(RequestParamConstants.User.USERNAME, "Tên đăng nhập tối đa " + SystemConstants.Validation.USERNAME_MAX_LENGTH + " ký tự");
		} else if (!username.matches("^[a-zA-Z0-9_]+$")) {
			result.addError(RequestParamConstants.User.USERNAME, "Tên đăng nhập chỉ chứa chữ, số và gạch dưới");
		}

		String password = dto.getPassword();
		if (password == null || password.trim().isEmpty()) {
			result.addError(RequestParamConstants.User.PASSWORD, "Mật khẩu không được để trống");
		} else if (!password.equals(password.trim())) {
			result.addError(RequestParamConstants.User.PASSWORD, "Mật khẩu không có dấu cách ở hai đầu");
		} else if (password.length() < SystemConstants.Validation.PASSWORD_MIN_LENGTH) {
			result.addError(RequestParamConstants.User.PASSWORD, "Mật khẩu phải có ít nhất " + SystemConstants.Validation.PASSWORD_MIN_LENGTH + " ký tự");
		} else if (password.length() > SystemConstants.Validation.PASSWORD_MAX_LENGTH) {
			result.addError(RequestParamConstants.User.PASSWORD, "Mật khẩu tối đa " + SystemConstants.Validation.PASSWORD_MAX_LENGTH + " ký tự");
		} else {
			boolean hasUppercase = !password.equals(password.toLowerCase());
			boolean hasLowercase = !password.equals(password.toUpperCase());
			boolean hasDigit = password.matches(".*\\d.*");
			boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
			int complexityScore = (hasUppercase ? 1 : 0) + (hasLowercase ? 1 : 0) + (hasDigit ? 1 : 0)
					+ (hasSpecial ? 1 : 0);
			if (complexityScore < 3) {
				result.addError(RequestParamConstants.User.PASSWORD,
						"Mật khẩu phải chứa ít nhất 3 trong 4 loại: chữ hoa, chữ thường, số, ký tự đặc biệt");
			}
		}

		String fullname = dto.getFullname();
		if (fullname == null || fullname.trim().isEmpty()) {
			result.addError(RequestParamConstants.User.FULLNAME, "Họ và tên không được để trống");
		} else if (fullname.length() > 100) {
			result.addError(RequestParamConstants.User.FULLNAME, "Họ và tên tối đa 100 ký tự");
		}

		String email = dto.getEmail();
		if (email == null || email.trim().isEmpty()) {
			result.addError(RequestParamConstants.User.EMAIL, "Email không được để trống");
		} else if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
			result.addError(RequestParamConstants.User.EMAIL, "Email không hợp lệ");
		}
	}
}
