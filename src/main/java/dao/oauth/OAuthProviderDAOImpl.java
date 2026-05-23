package dao.oauth;

import beans.oauth.OAuthProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OAuthProviderDAOImpl implements OAuthProviderDAO {

    private static final String SQL_FIND_BY_CODE = "SELECT id, code, name FROM oauth_provider WHERE code = ?";

    @Override
    public Optional<OAuthProvider> findByCode(Connection conn, String code) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CODE)) {
            ps.setString(1, code.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new OAuthProvider(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> findIdByCode(Connection conn, String code) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CODE)) {
            ps.setString(1, code.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("id"));
                }
            }
        }
        return Optional.empty();
    }
}
