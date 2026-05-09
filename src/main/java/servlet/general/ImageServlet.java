package servlet.general;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ConstantUtils;

@WebServlet(name = "ImageServlet", value = "/image/*")
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private String imageBaseDir;

    @Override
    public void init() throws ServletException {
        String configuredPath = ConstantUtils.IMAGE_ABSOLUTE_PATH;

        if (configuredPath == null || configuredPath.trim().isEmpty()) {
            throw new ServletException(
                "IMAGE_ABSOLUTE_PATH is not configured in ConstantUtils. "
                + "Please set IMAGE_ABSOLUTE_PATH to your images directory.");
        }

        this.imageBaseDir = configuredPath.trim();
        Path imageDir = Paths.get(this.imageBaseDir);

        if (!Files.exists(imageDir)) {
            try {
                Files.createDirectories(imageDir);
                System.out.println("[ImageServlet] Created images directory: " + imageDir.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("[ImageServlet] FAILED to create directory: " + imageDir.toAbsolutePath());
                e.printStackTrace();
                throw new ServletException("Cannot create images directory: " + imageDir.toAbsolutePath(), e);
            }
        }

        System.out.println("[ImageServlet] Initialized with IMAGE_ABSOLUTE_PATH: " + this.imageBaseDir);
        System.out.println("[ImageServlet] Directory exists: " + Files.exists(imageDir));
        System.out.println("[ImageServlet] Is writable: " + Files.isWritable(imageDir));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.trim().isEmpty()) {
            System.out.println("[ImageServlet] doGet: No path info provided");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fileName = URLDecoder.decode(pathInfo, "UTF-8");
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        if (fileName.contains("..")) {
            System.out.println("[ImageServlet] doGet: Path traversal attempt blocked: " + pathInfo);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Path imagePath = Paths.get(imageBaseDir, fileName).normalize().toAbsolutePath();
        Path basePath = Paths.get(imageBaseDir).normalize().toAbsolutePath();

        System.out.println("[ImageServlet] doGet request: " + pathInfo);
        System.out.println("[ImageServlet] Resolved path: " + imagePath);
        System.out.println("[ImageServlet] Base path: " + basePath);

        if (!imagePath.startsWith(basePath)) {
            System.out.println("[ImageServlet] doGet: Path traversal attempt blocked");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (!Files.exists(imagePath)) {
            System.out.println("[ImageServlet] doGet: File not found: " + imagePath);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!Files.isRegularFile(imagePath)) {
            System.out.println("[ImageServlet] doGet: Not a file: " + imagePath);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimeType = getServletContext().getMimeType(imagePath.getFileName().toString());
        if (mimeType == null || !mimeType.startsWith("image/")) {
            System.out.println("[ImageServlet] doGet: Unknown/invalid MIME type: " + mimeType);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.reset();
        response.setContentType(mimeType);
        response.setContentLengthLong(Files.size(imagePath));

        System.out.println("[ImageServlet] Serving: " + imagePath + " (" + mimeType + ", " + Files.size(imagePath) + " bytes)");

        Files.copy(imagePath, response.getOutputStream());
        response.getOutputStream().flush();
    }
}
