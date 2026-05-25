package dao.oauth;

import static config.DatabaseConstants.COL_ID;
import static config.DatabaseConstants.COL_OAUTH_PROVIDER_CODE;
import static config.DatabaseConstants.COL_OAUTH_PROVIDER_NAME;
import static config.DatabaseConstants.TABLE_OAUTH_PROVIDER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import beans.oauth.OAuthProvider;

public class OAuthProviderDAOImpl implements OAuthProviderDAO {

	private static final String SQL_FIND_BY_CODE = """
			SELECT %s, %s, %s
			FROM %s
			WHERE %s = ?
			""".formatted(COL_ID, COL_OAUTH_PROVIDER_CODE, COL_OAUTH_PROVIDER_NAME, TABLE_OAUTH_PROVIDER, COL_OAUTH_PROVIDER_CODE);

	@Override
	public Optional<OAuthProvider> findByCode(final Connection conn, final String code) throws SQLException {
		if (code == null) {
			return Optional.empty();
		}

		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CODE)) {
			ps.setString(1, code.toUpperCase().trim());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					final OAuthProvider provider = new OAuthProvider(rs.getInt(COL_ID), rs.getString(COL_OAUTH_PROVIDER_CODE), rs.getString(COL_OAUTH_PROVIDER_NAME));
					return Optional.of(provider);
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public int findIdByCode(final Connection conn, final String code) throws SQLException {
		if (code == null) {
			return 0;
		}

		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CODE)) {
			ps.setString(1, code.toUpperCase().trim());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(COL_ID);
				}
			}
		}
		return 0;
	}
}