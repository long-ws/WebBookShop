package dao.user;

import beans.user.UserOAuthAuth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserOauthDAOImpl implements UserOauthDAO {

    private static final String SQL_INSERT = "INSERT INTO user_oauth (user_id, provider_id, provider_user_id, email, display_name, avatar_url) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_DELETE = "DELETE FROM user_oauth WHERE id=?";
    private static final String SQL_FIND_BY_PROVIDER_AND_USER_ID = "SELECT * FROM user_oauth WHERE provider_id = ? AND provider_user_id = ?";
    private static final String SQL_FIND_USER_ID_BY_OAUTH = "SELECT user_id FROM user_oauth WHERE provider_id = ? AND provider_user_id = ?";
    private static final String SQL_FIND_BY_USER_ID = "SELECT * FROM user_oauth WHERE user_id = ?";
    private static final String SQL_HAS_PROVIDER = "SELECT COUNT(*) FROM user_oauth WHERE user_id = ? AND provider_id = ?";
    private static final String SQL_COUNT_BY_USER_ID = "SELECT COUNT(*) FROM user_oauth WHERE user_id = ?";

    @Override
    public void insert(Connection conn, UserOAuthAuth oauthInfo) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setLong(1, oauthInfo.getUserId());
            ps.setInt(2, oauthInfo.getProviderId());
            ps.setString(3, oauthInfo.getProviderUserId());
            ps.setString(4, oauthInfo.getEmail());
            ps.setString(5, oauthInfo.getDisplayName());
            ps.setString(6, oauthInfo.getAvatarUrl());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Connection conn, long oauthId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, oauthId);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<UserOAuthAuth> findByProviderAndProviderUserId(Connection conn, int providerId, String providerUserId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_PROVIDER_AND_USER_ID)) {
            ps.setInt(1, providerId);
            ps.setString(2, providerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOAuthAuth(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> findUserIdByOAuth(Connection conn, int providerId, String providerUserId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_USER_ID_BY_OAUTH)) {
            ps.setInt(1, providerId);
            ps.setString(2, providerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("user_id"));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserOAuthAuth> findByUserId(Connection conn, long userId) throws SQLException {
        List<UserOAuthAuth> accounts = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToOAuthAuth(rs));
                }
            }
        }
        return accounts;
    }

    @Override
    public boolean hasOAuthProvider(Connection conn, long userId, int providerId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_HAS_PROVIDER)) {
            ps.setLong(1, userId);
            ps.setInt(2, providerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public int countByUserId(Connection conn, long userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_COUNT_BY_USER_ID)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private UserOAuthAuth mapResultSetToOAuthAuth(ResultSet rs) throws SQLException {
        UserOAuthAuth auth = new UserOAuthAuth();
        auth.setId(rs.getLong("id"));
        auth.setUserId(rs.getLong("user_id"));
        auth.setProviderId(rs.getInt("provider_id"));
        auth.setProviderUserId(rs.getString("provider_user_id"));
        auth.setEmail(rs.getString("email"));
        auth.setDisplayName(rs.getString("display_name"));
        auth.setAvatarUrl(rs.getString("avatar_url"));
        return auth;
    }
}
