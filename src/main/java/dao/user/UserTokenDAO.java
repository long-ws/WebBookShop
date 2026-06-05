package dao.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface UserTokenDAO {
	long insertToken(final Connection conn, final long userId, final String tokenHash, final String tokenTypeCode, final int expiresInMinutes) throws SQLException;

	Long findActiveTokenId(final Connection conn, final long userId, final String tokenHash, final String tokenTypeCode) throws SQLException;

	boolean markTokenUsed(final Connection conn, final long tokenId) throws SQLException;

	int expireActiveTokens(final Connection conn, final long userId, final String tokenTypeCode) throws SQLException;

	Timestamp findLatestCreatedAt(final Connection conn, final long userId, final String tokenTypeCode) throws SQLException;

	int countCreatedAfterMinutes(final Connection conn, final long userId, final String tokenTypeCode, final int minutes) throws SQLException;

	int countCreatedAfterSeconds(final Connection conn, final long userId, final String tokenTypeCode, final int seconds) throws SQLException;
}
