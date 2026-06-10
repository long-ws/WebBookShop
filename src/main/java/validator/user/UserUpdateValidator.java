package validator.user;

import constants.RequestParamConstants;
import dto.user.UserUpdateRequest;
import domain.user.UserIds;
import domain.user.UserValidation;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class UserUpdateValidator extends BaseValidator<UserUpdateRequest> {

	public UserUpdateValidator() {
	}

	@Override
	protected void validateFormat(UserUpdateRequest dto, ValidationResult result) {
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

		String password = dto.getPassword();
		if (password != null && !password.trim().isEmpty()) {
			if (!password.equals(password.trim())) {
				result.addError(RequestParamConstants.User.PASSWORD, "Mật khẩu không có dấu cách ở hai đầu");
			} else if (password.length() < UserValidation.PASSWORD_MIN_LENGTH) {
				result.addError(RequestParamConstants.User.PASSWORD,
						"Mật khẩu phải có ít nhất " + UserValidation.PASSWORD_MIN_LENGTH + " ký tự");
			} else if (password.length() > UserValidation.PASSWORD_MAX_LENGTH) {
				result.addError(RequestParamConstants.User.PASSWORD, "Mật khẩu tối đa " + UserValidation.PASSWORD_MAX_LENGTH + " ký tự");
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
