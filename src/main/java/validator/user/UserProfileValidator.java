package validator.user;

import constants.RequestParamConstants;
import dto.user.UserProfileRequest;
import domain.user.UserIds;
import domain.user.UserValidation;
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
		} else if (fullname.length() > UserValidation.FULLNAME_MAX_LENGTH) {
			result.addError(RequestParamConstants.User.FULLNAME, "Họ và tên tối đa " + UserValidation.FULLNAME_MAX_LENGTH + " ký tự");
		}

		String email = dto.getEmail();
		if (email == null || email.trim().isEmpty()) {
			result.addError(RequestParamConstants.User.EMAIL, "Email không được để trống");
		} else if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
			result.addError(RequestParamConstants.User.EMAIL, "Email không hợp lệ");
		}

		String phone = dto.getPhoneNumber();
		if (phone != null && !phone.trim().isEmpty()) {
			if (!phone.matches("^\\d{" + UserValidation.PHONE_MIN_LENGTH + ","
					+ UserValidation.PHONE_MAX_LENGTH + "}$")) {
				result.addError(RequestParamConstants.User.PHONE_NUMBER, "Số điện thoại không hợp lệ");
			}
		}

		if (dto.getGender() != null) {
			Integer genderId = dto.getGender().getId();
			if (genderId != null && genderId != UserIds.Gender.MALE && genderId != UserIds.Gender.FEMALE) {
				result.addError(RequestParamConstants.User.GENDER, "Giới tính không hợp lệ");
			}
		}
	}
}
