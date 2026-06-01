package validator.user;

import constants.RequestParamConstants;
import dto.user.OAuthUserRegistrationRequest;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class OAuthUserRegistrationValidator extends BaseValidator<OAuthUserRegistrationRequest> {

	public OAuthUserRegistrationValidator() {
	}

	@Override
	protected void validateFormat(OAuthUserRegistrationRequest dto, ValidationResult result) {
		if (dto.getProviderId() <= 0) {
			result.addError("providerId", "Provider ID không hợp lệ");
		}

		String providerUserId = dto.getProviderUserId();
		if (providerUserId == null || providerUserId.trim().isEmpty()) {
			result.addError("providerUserId", "Provider User ID không được để trống");
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
