package servlet.general;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ConstantUtils;

@WebServlet(name = "ImageServlet", value = "/image/*")
public class ImageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private String imagePath;

    @Override
    public void init() throws ServletException {
        // Lấy đường dẫn tuyệt đối trên hệ thống từ đường dẫn trong ConstantUtils
        this.imagePath = getServletContext().getRealPath(ConstantUtils.IMAGE_PATH);

        if (this.imagePath == null) {
            throw new ServletException("Không tìm thấy thư mục images trong webapp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String requestedImage = request.getPathInfo();

        if (requestedImage == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File image = new File(imagePath, URLDecoder.decode(requestedImage, "UTF-8")).getCanonicalFile();
        File baseDir = new File(imagePath).getCanonicalFile();

        if (!image.getPath().startsWith(baseDir.getPath())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (!image.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(image.getName());

        if (contentType == null || !contentType.startsWith("image")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.reset();
        response.setContentType(contentType);
        response.setContentLengthLong(image.length());

        Files.copy(image.toPath(), response.getOutputStream());
    }
}
