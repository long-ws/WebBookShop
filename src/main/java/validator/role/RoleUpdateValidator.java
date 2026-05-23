package validator.role;

import constants.RequestParamConstants;
import dto.role.RoleUpdateRequest;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class RoleUpdateValidator extends BaseValidator<RoleUpdateRequest> {

    public RoleUpdateValidator() {
    }

    @Override
    protected void validateFormat(RoleUpdateRequest dto, ValidationResult result) {
        if (dto.getId() == null) {
            result.addError(RequestParamConstants.ID, "ID vai trò không được để trống");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            result.addError(RequestParamConstants.NAME, "Tên vai trò không được để trống");
        }
    }
}
