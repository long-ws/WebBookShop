package validator.user;

import constants.UserConstants;
import dto.user.UserProfileRequest;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class UserProfileValidator extends BaseValidator<UserProfileRequest> {

	public UserProfileValidator() {
	}

	@Override
	protected void validateFormat(UserProfileRequest dto, ValidationResult result) {
		String fullname = dto.getFullname();
		if (fullname == null || fullname.trim().isEmpty()) {
			result.addError("fullname", "Họ và tên không được để trống");
		} else if (fullname.length() > 100) {
			result.addError("fullname", "Họ và tên tối đa 100 ký tự");
		}

		String email = dto.getEmail();
		if (email == null || email.trim().isEmpty()) {
			result.addError("email", "Email không được để trống");
		} else if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
			result.addError("email", "Email không hợp lệ");
		}

		String phone = dto.getPhoneNumber();
		if (phone != null && !phone.trim().isEmpty()) {
			if (!phone.matches("^\\d{" + UserConstants.Validation.PHONE_MIN_LENGTH + ","
					+ UserConstants.Validation.PHONE_MAX_LENGTH + "}$")) {
				result.addError("phoneNumber", "Số điện thoại không hợp lệ");
			}
		}

		if (dto.getGender() != null) {
			Integer genderId = dto.getGender().getId();
			if (genderId != null && genderId != 0 && genderId != 1) {
				result.addError("gender", "Giới tính không hợp lệ");
			}
		}
	}
}
