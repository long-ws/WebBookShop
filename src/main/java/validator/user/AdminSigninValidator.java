package validator.user;

import constants.RequestParamConstants;
import dto.user.AdminSigninRequest;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class AdminSigninValidator extends BaseValidator<AdminSigninRequest> {

	public AdminSigninValidator() {
	}

	@Override
	protected void validateFormat(AdminSigninRequest dto, ValidationResult result) {
		String username = dto.getUsername();
		if (username == null || username.trim().isEmpty()) {
			result.addError(RequestParamConstants.Auth.USERNAME, "Tên đăng nhập không được để trống");
		} else if (!username.equals(username.trim())) {
			result.addError(RequestParamConstants.Auth.USERNAME, "Tên đăng nhập không có dấu cách ở hai đầu");
		} else if (username.length() > 25) {
			result.addError(RequestParamConstants.Auth.USERNAME, "Tên đăng nhập tối đa 25 ký tự");
		}

		String password = dto.getPassword();
		if (password == null || password.trim().isEmpty()) {
			result.addError(RequestParamConstants.Auth.PASSWORD, "Mật khẩu không được để trống");
		} else if (!password.equals(password.trim())) {
			result.addError(RequestParamConstants.Auth.PASSWORD, "Mật khẩu không có dấu cách ở hai đầu");
		} else if (password.length() > 32) {
			result.addError(RequestParamConstants.Auth.PASSWORD, "Mật khẩu tối đa 32 ký tự");
		}
	}
}
