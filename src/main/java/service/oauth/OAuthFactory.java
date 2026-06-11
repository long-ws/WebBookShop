package service.oauth;

public class OAuthFactory {
    
    private static final GoogleOAuthProvider googleOAuthProvider = new GoogleOAuthProvider();
    
    public static OAuthProvider get(String provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Không thể lấy thông tin provider");
        }
        
        String normalized = provider.trim().toLowerCase();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Không thể lấy thông tin provider");
        }

        switch (normalized) {
            case "google":
                return googleOAuthProvider;
            default:
                throw new IllegalArgumentException("Không hỗ trợ đăng nhập bằng: " + provider);
        }
    }
}
