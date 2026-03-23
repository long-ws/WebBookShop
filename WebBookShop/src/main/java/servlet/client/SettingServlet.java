package servlet.client;

import java.io.IOException;

import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.UserService;

@WebServlet(name = "SettingServlet", value = "/setting")
public class SettingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            request.setAttribute("user", user);
        }
        request.getRequestDispatcher("WEB-INF/views/settingView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/signin");
            return;
        }

        // Lấy dữ liệu từ form
        String username = request.getParameter("username");
        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String genderStr = request.getParameter("gender");
        String address = request.getParameter("address");

        int gender = 0;
        try {
            gender = Integer.parseInt(genderStr);
        } catch (NumberFormatException e) {
            // giữ gender = 0 nếu không hợp lệ
        }

        // Kiểm tra username đã tồn tại chưa (nếu người dùng đổi username)
        User userWithNewUsername = null;
        if (!currentUser.getUsername().equals(username)) {
            try {
                userWithNewUsername = userService.getByUsername(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (userWithNewUsername != null) {
            // Username đã tồn tại
            request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
            request.setAttribute("user", currentUser);
        } else {
            // Tạo đối tượng user mới với dữ liệu mới
            User updatedUser = new User();
            updatedUser.setId(currentUser.getId());
            updatedUser.setUsername(username);
            updatedUser.setPassword(currentUser.getPassword()); // giữ mật khẩu cũ
            updatedUser.setFullname(fullname);
            updatedUser.setEmail(email);
            updatedUser.setPhoneNumber(phoneNumber);
            updatedUser.setGender(gender);
            updatedUser.setAddress(address);
            updatedUser.setRole("CUSTOMER"); // giữ role

            try {
                userService.update(updatedUser);
                request.setAttribute("successMessage", "Cập nhật thành công!");
                request.setAttribute("user", updatedUser);
                session.setAttribute("currentUser", updatedUser);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Cập nhật không thành công!");
                request.setAttribute("user", currentUser);
            }
        }

        request.getRequestDispatcher("WEB-INF/views/settingView.jsp").forward(request, response);
    }
}
