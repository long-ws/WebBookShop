package servlet.client;

import java.io.IOException;

import beans.User;
import beans.common.Gender;
import beans.common.Language;
import constants.SessionConstants;
import dto.user.UserProfileRequest;
import exception.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.UserProfileService;
import service.UserProfileServiceImpl;

@WebServlet(name = "UserServlet", value = "/user")
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserProfileService userProfileService = new UserProfileServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/userView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute(SessionConstants.CURRENT_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/signin");
            return;
        }

        // Khởi tạo thực thể Gender truyền thống nếu có tham số truyền lên
        Gender gender = null;
        String genderStr = request.getParameter("gender");
        if (genderStr != null && !genderStr.isEmpty()) {
            try {
                int genderId = Integer.parseInt(genderStr);
                gender = new Gender();
                gender.setId(genderId);
            } catch (NumberFormatException e) {
                // Có thể log hoặc xử lý thêm nếu cần thiết
            }
        }

        // Khởi tạo thực thể Language hoặc fallback về ngôn ngữ mặc định (id = 1)
        Language lang = null;
        String langStr = request.getParameter("preferredLanguage");
        if (langStr != null && !langStr.isEmpty()) {
            try {
                int langId = Integer.parseInt(langStr);
                lang = new Language();
                lang.setId(langId);
            } catch (NumberFormatException e) {
                // Nếu ép kiểu lỗi, chuyển về cấu hình mặc định phía dưới
            }
        }
        
        if (lang == null) {
            lang = new Language();
            lang.setId(1);
        }

        // Tạo UserProfileRequest bất biến bằng chuỗi Builder Pattern thay thế hoàn toàn setter
        UserProfileRequest dto = new UserProfileRequest.Builder()
                .fullname(request.getParameter("fullname"))
                .email(request.getParameter("email"))
                .phoneNumber(request.getParameter("phoneNumber"))
                .avatarUrl(request.getParameter("avatarUrl"))
                .gender(gender)
                .preferredLanguage(lang)
                .build();

        try {
            userProfileService.updateUserProfile(currentUser.getId(), dto);
            User updatedUser = userProfileService.getById(currentUser.getId());
            if (updatedUser != null) {
                session.setAttribute(SessionConstants.CURRENT_USER, updatedUser);
            }
            session.setAttribute(SessionConstants.SUCCESS_MESSAGE, "Cập nhật thành công!");
            response.sendRedirect(request.getContextPath() + "/user");
        } catch (BusinessException e) {
            request.setAttribute("user", dto);
            request.setAttribute("errors", e.getErrors());
            request.getRequestDispatcher("/WEB-INF/views/userView.jsp").forward(request, response);
        }
    }
}