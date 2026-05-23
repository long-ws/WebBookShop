package validator.permission;

import constants.RequestParamConstants;
import dto.permission.PermissionUpdateRequest;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class PermissionUpdateValidator extends BaseValidator<PermissionUpdateRequest> {

	public PermissionUpdateValidator() {
	}

	@Override
	protected void validateFormat(PermissionUpdateRequest dto, ValidationResult result) {
		if (dto.getId() == null) {
			result.addError(RequestParamConstants.ID, "ID quyền không được để trống");
		}

		if (dto.getName() == null || dto.getName().trim().isEmpty()) {
			result.addError(RequestParamConstants.NAME, "Tên quyền không được để trống");
		}
	}
}
