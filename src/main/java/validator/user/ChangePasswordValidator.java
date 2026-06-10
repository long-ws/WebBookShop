package validator.user;

import constants.RequestParamConstants;
import dto.user.ChangePasswordRequest;
import domain.user.UserValidation;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class ChangePasswordValidator extends BaseValidator<ChangePasswordRequest> {

	public ChangePasswordValidator() {
	}

	@Override
	protected void validateFormat(ChangePasswordRequest dto, ValidationResult result) {
		String currentPassword = dto.getCurrentPassword();
		if (currentPassword == null || currentPassword.trim().isEmpty()) {
			result.addError(RequestParamConstants.User.CURRENT_PASSWORD, "Mật khẩu hiện tại không được để trống");
		}

		String newPassword = dto.getNewPassword();
		if (newPassword == null || newPassword.trim().isEmpty()) {
			result.addError(RequestParamConstants.User.NEW_PASSWORD, "Mật khẩu mới không được để trống");
		} else if (!newPassword.equals(newPassword.trim())) {
			result.addError(RequestParamConstants.User.NEW_PASSWORD, "Mật khẩu mới không có dấu cách ở hai đầu");
		} else if (newPassword.length() < UserValidation.PASSWORD_MIN_LENGTH) {
			result.addError(RequestParamConstants.User.NEW_PASSWORD,
					"Mật khẩu mới phải có ít nhất " + UserValidation.PASSWORD_MIN_LENGTH + " ký tự");
		} else if (newPassword.length() > UserValidation.PASSWORD_MAX_LENGTH) {
			result.addError(RequestParamConstants.User.NEW_PASSWORD,
					"Mật khẩu mới tối đa " + UserValidation.PASSWORD_MAX_LENGTH + " ký tự");
		} else {
			boolean hasUppercase = !newPassword.equals(newPassword.toLowerCase());
			boolean hasLowercase = !newPassword.equals(newPassword.toUpperCase());
			boolean hasDigit = newPassword.matches(".*\\d.*");
			boolean hasSpecial = newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
			int complexityScore = (hasUppercase ? 1 : 0) + (hasLowercase ? 1 : 0) + (hasDigit ? 1 : 0)
					+ (hasSpecial ? 1 : 0);
			if (complexityScore < 3) {
				result.addError(RequestParamConstants.User.NEW_PASSWORD, "Mật khẩu mới phải chứa ít nhất 3 trong 4 loại: chữ hoa, chữ thường, số, ký tự đặc biệt");
			}
		}

		String confirmPassword = dto.getConfirmPassword();
		if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
			result.addError(RequestParamConstants.User.CONFIRM_PASSWORD, "Xác nhận mật khẩu không được để trống");
		} else if (newPassword != null && !newPassword.equals(confirmPassword)) {
			result.addError(RequestParamConstants.User.CONFIRM_PASSWORD, "Xác nhận mật khẩu không khớp với mật khẩu mới");
		}
	}
}
