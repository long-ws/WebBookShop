package validation.strategy;

import constants.UserConstants;
import dto.UserCreateUpdateFormDTO;
import service.UserService;

public class UserValidationStrategy {
    
    private final UserService userService;
    
    public UserValidationStrategy(UserService userService) {
        this.userService = userService;
    }
    
    public void validateCreate(UserCreateUpdateFormDTO dto) {
        validateUsername(dto, true);
        validatePassword(dto, true);
        validateFullname(dto);
        validateEmail(dto, true);
        validatePhoneNumber(dto);
        validateGender(dto);
        validateRole(dto);
    }
    
    public void validateUpdate(UserCreateUpdateFormDTO dto) {
        validateUsername(dto, false);
        validatePassword(dto, false);
        validateFullname(dto);
        validateEmail(dto, false);
        validatePhoneNumber(dto);
        validateGender(dto);
        validateRole(dto);
    }
    
    private void validateUsername(UserCreateUpdateFormDTO dto, boolean required) {
        String username = dto.getUsername();
        
        if (required && (username == null || username.trim().isEmpty())) {
            dto.addError("username", "Tên đăng nhập không được để trống");
            return;
        }
        
        if (username == null) return;
        
        username = username.trim();
        
        if (username.length() > UserConstants.Validation.USERNAME_MAX_LENGTH) {
            dto.addError("username", "Tên đăng nhập tối đa " + UserConstants.Validation.USERNAME_MAX_LENGTH + " ký tự");
            return;
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            dto.addError("username", "Tên đăng nhập chỉ chứa chữ, số và gạch dưới");
            return;
        }
        
        Long id = dto.getId();
        boolean exists = id != null 
            ? userService.isUsernameExists(username, id) 
            : userService.isUsernameExists(username);
            
        if (exists) {
            dto.addError("username", "Tên đăng nhập đã tồn tại");
        }
    }
    
    private void validatePassword(UserCreateUpdateFormDTO dto, boolean required) {
        String password = dto.getPassword();
        
        if (required && (password == null || password.isEmpty())) {
            dto.addError("password", "Mật khẩu không được để trống");
            return;
        }
        
        if (password == null || password.isEmpty()) return;
        
        if (password.length() < UserConstants.Validation.PASSWORD_MIN_LENGTH) {
            dto.addError("password", "Mật khẩu tối thiểu " + UserConstants.Validation.PASSWORD_MIN_LENGTH + " ký tự");
            return;
        }
        
        if (password.length() > UserConstants.Validation.PASSWORD_MAX_LENGTH) {
            dto.addError("password", "Mật khẩu tối đa " + UserConstants.Validation.PASSWORD_MAX_LENGTH + " ký tự");
            return;
        }
        
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        
        int complexityScore = (hasUppercase ? 1 : 0) + (hasLowercase ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);
        
        if (complexityScore < 3) {
            dto.addError("password", "Mật khẩu phải chứa ít nhất 3 trong 4 loại: chữ hoa, chữ thường, số, ký tự đặc biệt");
        }
    }
    
    private void validateFullname(UserCreateUpdateFormDTO dto) {
        String fullname = dto.getFullname();
        
        if (fullname == null || fullname.trim().isEmpty()) {
            dto.addError("fullname", "Họ và tên không được để trống");
            return;
        }
        
        if (fullname.length() > 100) {
            dto.addError("fullname", "Họ và tên tối đa 100 ký tự");
        }
    }
    
    private void validateEmail(UserCreateUpdateFormDTO dto, boolean checkUnique) {
        String email = dto.getEmail();
        
        if (email == null || email.trim().isEmpty()) {
            dto.addError("email", "Email không được để trống");
            return;
        }
        
        if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
            dto.addError("email", "Email không hợp lệ");
            return;
        }
        
        if (checkUnique) {
            Long id = dto.getId();
            boolean exists = id != null 
                ? userService.isEmailExists(email, id)
                : userService.isEmailExists(email);
                
            if (exists) {
                dto.addError("email", "Email đã được sử dụng");
            }
        }
    }
    
    private void validatePhoneNumber(UserCreateUpdateFormDTO dto) {
        String phone = dto.getPhoneNumber();
        
        if (phone == null || phone.trim().isEmpty()) {
            dto.addError("phoneNumber", "Số điện thoại không được để trống");
            return;
        }
        
        if (!phone.matches("^\\d{" + UserConstants.Validation.PHONE_MIN_LENGTH + "," + UserConstants.Validation.PHONE_MAX_LENGTH + "}$")) {
            dto.addError("phoneNumber", "Số điện thoại không hợp lệ");
        }
    }
    
    private void validateGender(UserCreateUpdateFormDTO dto) {
        Integer genderId = null;
        if (dto.getGender() != null) {
            genderId = dto.getGender().getId();
        }
        
        if (genderId == null) {
            dto.addError("gender", "Vui lòng chọn giới tính");
            return;
        }
        
        if (genderId != 0 && genderId != 1) {
            dto.addError("gender", "Giới tính không hợp lệ");
        }
    }
    
    private void validateRole(UserCreateUpdateFormDTO dto) {
        String roleCode = null;
        if (dto.getRole() != null) {
            roleCode = dto.getRole().getCode();
        }

        if (roleCode == null || roleCode.trim().isEmpty()) {
            dto.addError("role", "Vui lòng chọn vai trò");
        }
    }
}
