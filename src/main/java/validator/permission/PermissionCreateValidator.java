package validator.permission;

import constants.RequestParamConstants;
import dto.permission.PermissionCreateRequest;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class PermissionCreateValidator extends BaseValidator<PermissionCreateRequest> {

	public PermissionCreateValidator() {
	}

	@Override
	protected void validateFormat(PermissionCreateRequest dto, ValidationResult result) {
		if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
			result.addError(RequestParamConstants.CODE, "Mã quyền không được để trống");
		} else if (!dto.getCode().matches("^[A-Z][A-Z_]*$")) {
			result.addError(RequestParamConstants.CODE, "Mã quyền phải bắt đầu bằng chữ hoa và chỉ chứa chữ hoa và gạch dưới");
		}

		if (dto.getName() == null || dto.getName().trim().isEmpty()) {
			result.addError(RequestParamConstants.NAME, "Tên quyền không được để trống");
		}
	}
}
