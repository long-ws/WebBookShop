package validator.role;

import constants.RequestParamConstants;
import dto.role.RoleCreateRequest;
import validator.core.BaseValidator;
import validator.core.ValidationResult;

public class RoleCreateValidator extends BaseValidator<RoleCreateRequest> {

    public RoleCreateValidator() {
    }

    @Override
    protected void validateFormat(RoleCreateRequest dto, ValidationResult result) {
        if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
            result.addError(RequestParamConstants.CODE, "Mã vai trò không được để trống");
        } else if (!dto.getCode().matches("^[A-Z_]+$")) {
            result.addError(RequestParamConstants.CODE, "Mã vai trò phải chỉ chứa chữ hoa và gạch dưới");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            result.addError(RequestParamConstants.NAME, "Tên vai trò không được để trống");
        }
    }
}
