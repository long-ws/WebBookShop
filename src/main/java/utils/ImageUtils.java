package utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class ImageUtils {

    private static volatile String cachedImageDir = null;

    public static void init(ServletContext servletContext) {
        if (cachedImageDir == null) {
            synchronized (ImageUtils.class) {
                if (cachedImageDir == null) {
                    String absolute = ConstantUtils.IMAGE_ABSOLUTE_PATH;
                    if (absolute != null && !absolute.trim().isEmpty()) {
                        cachedImageDir = absolute.trim();
                        Path dir = Paths.get(cachedImageDir);
                        if (!Files.exists(dir)) {
                            try {
                                Files.createDirectories(dir);
                                System.out.println("ImageUtils: Created images directory at: " + dir.toAbsolutePath());
                            } catch (IOException e) {
                                System.err.println("ImageUtils: FAILED to create directory: " + dir.toAbsolutePath());
                                e.printStackTrace();
                            }
                        }
                        System.out.println("ImageUtils.init() -> Using IMAGE_ABSOLUTE_PATH: " + cachedImageDir);
                    } else {
                        String realPath = servletContext.getRealPath("/image");
                        if (realPath != null && !realPath.trim().isEmpty()) {
                            cachedImageDir = realPath.trim();
                        } else {
                            cachedImageDir = System.getProperty("user.dir")
                                    + File.separator + "webapp" + File.separator + "image";
                        }
                        Path dir = Paths.get(cachedImageDir);
                        if (!Files.exists(dir)) {
                            try {
                                Files.createDirectories(dir);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("ImageUtils.init() -> Using FALLBACK path: " + cachedImageDir);
                    }
                }
            }
        }
    }

    public static Path getImageDir() {
        String path = ConstantUtils.IMAGE_ABSOLUTE_PATH;
        if (path != null && !path.trim().isEmpty()) {
            return Paths.get(path.trim());
        }
        if (cachedImageDir != null) {
            return Paths.get(cachedImageDir);
        }
        return Paths.get(System.getProperty("user.dir"), "webapp", "image");
    }

    public static String uploadSingle(HttpServletRequest request) {
        return uploadSingle(request, "image");
    }

    public static String uploadSingle(HttpServletRequest request, String formFieldName) {
        try {
            System.out.println("[ImageUtils] === uploadSingle START ===");
            System.out.println("[ImageUtils] All parts in request:");
            for (Part p : request.getParts()) {
                System.out.println("  Part: name='" + p.getName() + "', size=" + p.getSize() + ", contentType=" + p.getContentType() + ", submittedFileName=" + p.getSubmittedFileName());
            }

            Part filePart = request.getPart(formFieldName);
            System.out.println("[ImageUtils] getPart('" + formFieldName + "') = " + (filePart == null ? "NULL" : "OK(size=" + filePart.getSize() + ")"));
            if (filePart == null || filePart.getSize() == 0) {
                System.out.println("[ImageUtils] uploadSingle: No file provided for field '" + formFieldName + "'");
                return null;
            }

            String contentType = filePart.getContentType();
            System.out.println("[ImageUtils] contentType = " + contentType);
            if (contentType == null || !contentType.startsWith("image/")) {
                System.out.println("[ImageUtils] uploadSingle: Invalid content type: " + contentType);
                return null;
            }

            String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            System.out.println("[ImageUtils] originalName = " + originalName);
            String savedName = generateFileName(originalName);
            System.out.println("[ImageUtils] savedName = " + savedName);
            Path targetPath = getImageDir().resolve(savedName);
            System.out.println("[ImageUtils] targetPath = " + targetPath.toAbsolutePath());

            System.out.println("[ImageUtils] uploadSingle -> field=" + formFieldName
                    + ", original=" + originalName
                    + ", saved=" + savedName
                    + ", target=" + targetPath.toAbsolutePath());

            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            if (Files.exists(targetPath)) {
                System.out.println("[ImageUtils] uploadSingle SUCCESS: " + targetPath.toAbsolutePath()
                        + " (" + Files.size(targetPath) + " bytes)");
            } else {
                System.err.println("[ImageUtils] uploadSingle FAILED: file not found after copy!");
            }

            return savedName;

        } catch (Exception e) {
            System.err.println("[ImageUtils] uploadSingle ERROR:");
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> uploadMultiple(HttpServletRequest request, String paramName) {
        List<String> uploadedFiles = new ArrayList<>();
        try {
            for (Part part : request.getParts()) {
                String partName = part.getName();
                if (!paramName.equals(partName)) continue;
                if (part.getSize() == 0) continue;
                String contentType = part.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) continue;
                String originalName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                if (originalName == null || originalName.trim().isEmpty()) continue;
                String savedName = generateFileName(originalName);
                Path targetPath = getImageDir().resolve(savedName);
                try (InputStream input = part.getInputStream()) {
                    Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                uploadedFiles.add(savedName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadedFiles;
    }

    public static void delete(String imageName) {
        if (imageName == null || imageName.trim().isEmpty()) return;
        Path imagePath = getImageDir().resolve(imageName).normalize();
        try {
            boolean deleted = Files.deleteIfExists(imagePath);
            if (deleted) {
                System.out.println("[ImageUtils] delete SUCCESS: " + imagePath);
            } else {
                System.out.println("[ImageUtils] delete: file not found (already gone?): " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("[ImageUtils] delete ERROR: " + imagePath);
            e.printStackTrace();
        }
    }

    private static String generateFileName(String originalName) {
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + ext;
    }
}
