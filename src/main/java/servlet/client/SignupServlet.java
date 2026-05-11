package servlet.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import beans.common.Gender;
import beans.common.Role;
import dto.UserFormDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.user.UserService;
import service.user.impl.UserServiceImpl;


@WebServlet(name = "SignupServlet", value = "/signup")
public class SignupServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String genderStr = request.getParameter("gender");
        String policy = request.getParameter("policy");

        Map<String, String> errors = new HashMap<>();
        Map<String, String> values = new HashMap<>();
        values.put("username", username);
        values.put("fullname", fullname);
        values.put("email", email);
        values.put("phoneNumber", phoneNumber);
        values.put("gender", genderStr);

        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "Tên đăng nhập không được để trống");
        } else if (!username.equals(username.trim())) {
            errors.put("username", "Tên đăng nhập không có dấu cách ở hai đầu");
        } else if (username.length() > 50) { // Default max length
            errors.put("username", "Tên đăng nhập tối đa 50 ký tự");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Mật khẩu không được để trống");
        } else if (!password.equals(password.trim())) {
            errors.put("password", "Mật khẩu không có dấu cách ở hai đầu");
        } else if (password.length() < 6) {
            errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự");
        }

        if (fullname == null || fullname.trim().isEmpty()) {
            errors.put("fullname", "Họ và tên không được để trống");
        }

        if (email == null || !email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
            errors.put("email", "Email không hợp lệ");
        }

        int gender = 0; // Nam
        if (genderStr == null) {
            errors.put("gender", "Vui lòng chọn giới tính");
        } else {
            try {
                gender = Integer.parseInt(genderStr);
            } catch (NumberFormatException e) {
                errors.put("gender", "Giới tính không hợp lệ");
            }
        }

        if (policy == null) {
            errors.put("policy", "Bạn phải đồng ý với chính sách");
        }

        if (errors.isEmpty()) {
            if (userService.isUsernameExists(username)) {
                errors.put("username", "Tên đăng nhập đã tồn tại!");
            }
        }

        if (errors.isEmpty()) {
            if (userService.isEmailExists(email)) {
                errors.put("email", "Email đã được sử dụng!");
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("values", values);
            request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
            return;
        }

        try {
            UserFormDTO dto = new UserFormDTO();
            dto.setUsername(username);
            dto.setPassword(password);
            dto.setFullname(fullname);
            dto.setEmail(email);
            dto.setPhoneNumber(phoneNumber);
            
            Gender genderObj = new Gender();
            genderObj.setId(gender);
            dto.setGender(genderObj);
            
            Role roleObj = new Role();
            roleObj.setCode("CUSTOMER");
            dto.setRole(roleObj);
            
            UserFormDTO result = userService.createUser(dto);
            
            if (result.hasErrors()) {
                for (Map.Entry<String, String> entry : result.getErrors().entrySet()) {
                    errors.put(entry.getKey(), entry.getValue());
                }
                request.setAttribute("errors", errors);
                request.setAttribute("values", values);
                request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
                return;
            }
            
            request.getSession().setAttribute("signupSuccess", "Đăng ký thành công! Vui lòng đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/signin");

        } catch (Exception e) {
            e.printStackTrace();

            String errorMessage = "Đăng ký thất bại, vui lòng thử lại!";
            String dbMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (dbMessage.contains("duplicate") || dbMessage.contains("username")) {
                errorMessage = "Tên đăng nhập đã tồn tại!";
            } else if (dbMessage.contains("email")) {
                errorMessage = "Email đã được sử dụng!";
            }

            request.setAttribute("errorMessage", errorMessage);
            request.setAttribute("values", values);
            request.getRequestDispatcher("/WEB-INF/views/signupView.jsp").forward(request, response);
        }
    }
}