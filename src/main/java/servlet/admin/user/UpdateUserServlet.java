package servlet.admin.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;
import utils.HashingUtils;

@WebServlet(name = "UpdateUserServlet", value = "/admin/userManager/update")
public class UpdateUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = 0;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/userManager");
            return;
        }

        User user = null;
        try {
            user = userService.getById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user != null) {
            user.setPassword(""); // Không hiển thị password cũ
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/updateUserView.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/userManager");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User();

        try {
            user.setId(Long.parseLong(request.getParameter("id")));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/userManager");
            return;
        }

        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));
        user.setFullname(request.getParameter("fullname"));
        user.setEmail(request.getParameter("email"));
        user.setPhoneNumber(request.getParameter("phoneNumber"));
        try {
            user.setGender(Integer.parseInt(request.getParameter("gender")));
        } catch (Exception e) {
            user.setGender(0);
        }
        user.setAddress(request.getParameter("address"));
        user.setRole(request.getParameter("role"));

        Map<String, List<String>> violations = new HashMap<>();

        List<String> usernameViolations = new ArrayList<>();
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            usernameViolations.add("Không để trống");
        } else if (!user.getUsername().equals(user.getUsername().trim())) {
            usernameViolations.add("Không có dấu cách ở hai đầu");
        } else if (user.getUsername().length() > 25) {
            usernameViolations.add("Chỉ được có nhiều nhất là 25 ký tự");
        } else {
            User exist = null;
            try {
                exist = userService.getByUsername(user.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (exist != null && exist.getId() != user.getId()) {
                usernameViolations.add("Tên đăng nhập đã tồn tại");
            }
        }
        violations.put("usernameViolations", usernameViolations);

        List<String> passwordViolations = new ArrayList<>();
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            if (!user.getPassword().equals(user.getPassword().trim())) {
                passwordViolations.add("Không có dấu cách ở hai đầu");
            }
            if (user.getPassword().length() > 32) {
                passwordViolations.add("Chỉ được có nhiều nhất là 32 ký tự");
            }
        }
        violations.put("passwordViolations", passwordViolations);

        List<String> fullnameViolations = new ArrayList<>();
        if (user.getFullname() == null || user.getFullname().trim().isEmpty()) {
            fullnameViolations.add("Không để trống");
        } else if (!user.getFullname().equals(user.getFullname().trim())) {
            fullnameViolations.add("Không có dấu cách ở hai đầu");
        }
        violations.put("fullnameViolations", fullnameViolations);

        List<String> emailViolations = new ArrayList<>();
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            emailViolations.add("Không để trống");
        } else if (!user.getEmail().equals(user.getEmail().trim())) {
            emailViolations.add("Không có dấu cách ở hai đầu");
        } else if (!user.getEmail().matches("^[^@]+@[^@]+\\.[^@]+$")) {
            emailViolations.add("Phải đúng dạng email");
        } else {
            User exist = null;
            try {
                exist = userService.getByEmail(user.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (exist != null && exist.getId() != user.getId()) {
                emailViolations.add("Email đã tồn tại");
            }
        }
        violations.put("emailViolations", emailViolations);

        List<String> phoneViolations = new ArrayList<>();
        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            phoneViolations.add("Không để trống");
        } else if (!user.getPhoneNumber().equals(user.getPhoneNumber().trim())) {
            phoneViolations.add("Không có dấu cách ở hai đầu");
        } else if (!user.getPhoneNumber().matches("^\\d{10,11}$")) {
            phoneViolations.add("Phải là số điện thoại hợp lệ (10-11 chữ số)");
        } else {
            User exist = null;
            try {
                exist = userService.getByPhoneNumber(user.getPhoneNumber());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (exist != null && exist.getId() != user.getId()) {
                phoneViolations.add("Số điện thoại đã tồn tại");
            }
        }
        violations.put("phoneNumberViolations", phoneViolations);

        List<String> genderViolations = new ArrayList<>();
        if (user.getGender() != 0 && user.getGender() != 1) {
            genderViolations.add("Phải chọn giới tính");
        }
        violations.put("genderViolations", genderViolations);

        List<String> addressViolations = new ArrayList<>();
        if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
            addressViolations.add("Không để trống");
        } else if (!user.getAddress().equals(user.getAddress().trim())) {
            addressViolations.add("Không có dấu cách ở hai đầu");
        }
        violations.put("addressViolations", addressViolations);

        List<String> roleViolations = new ArrayList<>();
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            roleViolations.add("Không để trống");
        }
        violations.put("roleViolations", roleViolations);

        int totalViolations = 0;
        for (List<String> list : violations.values()) {
            totalViolations += list.size();
        }

        String successMessage = "Sửa thành công!";
        String errorMessage = "Sửa thất bại!";

        if (totalViolations == 0) {
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                User oldUser = null;
                try {
                    oldUser = userService.getById(user.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (oldUser != null) {
                    user.setPassword(oldUser.getPassword());
                }
            } else {
                user.setPassword(HashingUtils.hash(user.getPassword()));
            }

            try {
                userService.update(user);
                user.setPassword("");
                request.setAttribute("user", user);
                request.setAttribute("successMessage", successMessage);
            } catch (Exception e) {
                e.printStackTrace();
                user.setPassword("");
                request.setAttribute("user", user);
                request.setAttribute("errorMessage", errorMessage);
            }
        } else {
            request.setAttribute("user", user);
            request.setAttribute("violations", violations);
        }

        request.getRequestDispatcher("/WEB-INF/views/updateUserView.jsp").forward(request, response);
    }
}
