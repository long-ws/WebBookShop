package validator.user;

import constants.RequestParamConstants;
import constants.SystemConstants;
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

		String phone = dto.getPhoneNumber();
		if (phone != null && !phone.trim().isEmpty()) {
			if (!phone.matches("^\\d{" + SystemConstants.Validation.PHONE_MIN_LENGTH + ","
					+ SystemConstants.Validation.PHONE_MAX_LENGTH + "}$")) {
				result.addError(RequestParamConstants.User.PHONE_NUMBER, "Số điện thoại không hợp lệ");
			}
		}

		if (dto.getGender() != null) {
			Integer genderId = dto.getGender().getId();
			if (genderId != null && genderId != 0 && genderId != 1) {
				result.addError(RequestParamConstants.User.GENDER, "Giới tính không hợp lệ");
			}
		}
	}
}
