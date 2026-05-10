package service.oauth;

import service.OAuthService;

public class OAuthFactory {
    
    private static final OAuthService oauthService = new OAuthService();
    
    public static OAuthService get(String provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider không thể null");
        }
        
        String providerUpper = provider.toUpperCase();
        switch (providerUpper) {
            case "GOOGLE":
                return oauthService;
            default:
                throw new IllegalArgumentException("Không hỗ trợ: " + provider);
        }
    }
}