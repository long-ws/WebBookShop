package utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class ImageUtils {

    private static final Path IMAGE_DIR = Paths.get(ConstantUtils.IMAGE_PATH);

    /**
     * Upload image từ request
     * @return tên file ảnh nếu upload thành công, null nếu không có ảnh
     */
    public static String upload(HttpServletRequest request) {

        try {
            Part filePart = request.getPart("image");

            if (filePart == null || filePart.getSize() == 0) {
                return null;
            }

            String contentType = filePart.getContentType();
            if (contentType == null || !contentType.startsWith("image")) {
                return null;
            }

            if (!Files.exists(IMAGE_DIR)) {
                Files.createDirectories(IMAGE_DIR);
            }

            String fileName = Paths.get(filePart.getSubmittedFileName())
                    .getFileName().toString();

            Path targetLocation = IMAGE_DIR.resolve(fileName);

            try (InputStream fileContent = filePart.getInputStream()) {
                Files.copy(fileContent, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }


            String webImagePath = request.getServletContext().getRealPath("/image");
            Path webImageDir = Paths.get(webImagePath);

            if (!Files.exists(webImageDir)) {
                Files.createDirectories(webImageDir);
            }

            Path webImageFile = webImageDir.resolve(fileName);
            Files.copy(targetLocation, webImageFile, StandardCopyOption.REPLACE_EXISTING);

            return fileName; // lưu DB

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Xóa ảnh theo tên file
     */
    public static void delete(String imageName) {

        if (imageName == null || imageName.trim().isEmpty()) {
            return;
        }

        Path imagePath = IMAGE_DIR.resolve(imageName).normalize();

        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
