package dao.user;

import static config.db.DatabaseSchema.COL_PROFILE_AVATAR_URL;
import static config.db.DatabaseSchema.COL_PROFILE_EMAIL;
import static config.db.DatabaseSchema.COL_PROFILE_FULLNAME;
import static config.db.DatabaseSchema.COL_PROFILE_GENDER_ID;
import static config.db.DatabaseSchema.COL_PROFILE_PHONE_NUMBER;
import static config.db.DatabaseSchema.COL_PROFILE_PREFERRED_LANGUAGE_ID;
import static config.db.DatabaseSchema.COL_UPDATED_AT;
import static config.db.DatabaseSchema.COL_USER_ID;
import static config.db.DatabaseSchema.TABLE_USER_PROFILE;
import static config.db.DatabaseSchema.TABLE_USER_ACCOUNT;
import static config.db.DatabaseSchema.COL_ID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.common.Gender;
import beans.common.Language;
import beans.user.UserProfile;

public class UserProfileDAOImpl implements UserProfileDAO {

	private static final String SELECT_FIELDS = "%s, %s, %s, %s, %s, %s, %s, %s".formatted(COL_USER_ID, COL_PROFILE_FULLNAME, COL_PROFILE_PHONE_NUMBER, COL_PROFILE_EMAIL, COL_PROFILE_GENDER_ID,
			COL_PROFILE_PREFERRED_LANGUAGE_ID, COL_PROFILE_AVATAR_URL, COL_UPDATED_AT);

	private static final String SQL_INSERT = """
			INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s)
			VALUES (?, ?, ?, ?, ?, ?, ?)
			""".formatted(TABLE_USER_PROFILE, COL_USER_ID, COL_PROFILE_FULLNAME, COL_PROFILE_PHONE_NUMBER, COL_PROFILE_EMAIL, COL_PROFILE_GENDER_ID, COL_PROFILE_PREFERRED_LANGUAGE_ID,
			COL_PROFILE_AVATAR_URL);

	private static final String SQL_UPDATE = """
			UPDATE %s
			SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?
			WHERE %s = ?
			""".formatted(TABLE_USER_PROFILE, COL_PROFILE_FULLNAME, COL_PROFILE_PHONE_NUMBER, COL_PROFILE_EMAIL, COL_PROFILE_GENDER_ID, COL_PROFILE_PREFERRED_LANGUAGE_ID, COL_PROFILE_AVATAR_URL,
			COL_USER_ID);

	private static final String SQL_DELETE = "DELETE FROM %s WHERE %s = ?".formatted(TABLE_USER_PROFILE, COL_USER_ID);

	private static final String SQL_FIND_BY_ID = "SELECT %s FROM %s WHERE %s = ?".formatted(SELECT_FIELDS, TABLE_USER_PROFILE, COL_USER_ID);

	private static final String SQL_FIND_ID_BY_EMAIL = """
			SELECT %s
			FROM %s
			WHERE %s = ?
			LIMIT 1
			""".formatted(COL_USER_ID, TABLE_USER_PROFILE, COL_PROFILE_EMAIL);

	private static final String SQL_COUNT_BY_EMAIL = """
			SELECT COUNT(*)
			FROM %s
			WHERE %s = ?
			""".formatted(TABLE_USER_PROFILE, COL_PROFILE_EMAIL);

	private static final String SQL_COUNT_BY_EMAIL_EXCLUDE = """
			SELECT COUNT(*)
			FROM %s
			WHERE %s = ?
			  AND %s <> ?
			""".formatted(TABLE_USER_PROFILE, COL_PROFILE_EMAIL, COL_USER_ID);
	
	private static final String SQL_FIND_BY_USER_IDS = """
			SELECT %s
			FROM %s
			WHERE %s IN (
			""".formatted(SELECT_FIELDS, TABLE_USER_PROFILE, COL_USER_ID);

	private static final String SQL_FIND_IDS_ORDER_BY_FULLNAME = """
			SELECT p.%s
			FROM %s p
			JOIN %s a ON p.%s = a.%s
			WHERE a.deleted_at IS NULL
			ORDER BY p.%s %s
			""".formatted(COL_USER_ID, TABLE_USER_PROFILE, TABLE_USER_ACCOUNT, COL_USER_ID, COL_ID, COL_PROFILE_FULLNAME, "%s");

	@Override
	public int insert(final Connection conn, final UserProfile p) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
			setParameters(ps, p, false);
			return ps.executeUpdate();
		}
	}

	@Override
	public int update(final Connection conn, final UserProfile p) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
			setParameters(ps, p, true);
			return ps.executeUpdate();
		}
	}

	@Override
	public int delete(final Connection conn, final long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
			ps.setLong(1, userId);
			return ps.executeUpdate();
		}
	}

	@Override
	public Optional<UserProfile> findUserProfileById(final Connection conn, final long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return Optional.of(mapRow(rs));
			}
		}
		return Optional.empty();
	}

	@Override
	public long findUserIdByEmail(final Connection conn, final String email) throws SQLException {
		if (email == null) {
			return 0L;
		}
		final String normalized = email.trim().toLowerCase();
		if (normalized.isEmpty()) {
			return 0L;
		}
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ID_BY_EMAIL)) {
			ps.setString(1, normalized);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getLong(COL_USER_ID);
				}
			}
		}
		return 0L;
	}

	@Override
	public boolean existsByEmail(final Connection conn, final String email, final Long excludeUserId) throws SQLException {
		if (email == null) {
			return false;
		}
		final String normalized = email.trim().toLowerCase();
		if (normalized.isEmpty()) {
			return false;
		}
		final String sql = (excludeUserId == null) ? SQL_COUNT_BY_EMAIL : SQL_COUNT_BY_EMAIL_EXCLUDE;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, normalized);
			if (excludeUserId != null) {
				ps.setLong(2, excludeUserId.longValue());
			}
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	@Override
	public List<Long> findAllIdsOrderByFullname(Connection conn, boolean ascending) throws SQLException {
		String direction = ascending ? "ASC" : "DESC";
		String sql = String.format(SQL_FIND_IDS_ORDER_BY_FULLNAME, direction);

		List<Long> ids = new ArrayList<Long>();
		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				ids.add(rs.getLong(COL_USER_ID));
			}
		}
		return ids;
	}
	
	@Override
	public Map<Long, UserProfile> findByUserIdsAsMap(Connection conn, List<Long> userIds) throws SQLException {
		Map<Long, UserProfile> result = new HashMap<>();
		if (userIds == null || userIds.isEmpty()) {
			return result;
		}
		
		final int MAX_BATCH_SIZE = 500;
		
		for (int startIndex = 0; startIndex < userIds.size(); startIndex = startIndex + MAX_BATCH_SIZE) {
			int endIndex = Math.min(startIndex + MAX_BATCH_SIZE, userIds.size());
			List<Long> currentBatchIds = userIds.subList(startIndex, endIndex);
			
			List<Long> sanitizedIds = new ArrayList<>();
			for (int i = 0; i < currentBatchIds.size(); i++) {
				Long id = currentBatchIds.get(i);
				if (id != null) {
					sanitizedIds.add(id);
				}
			}
			if (sanitizedIds.isEmpty()) {
				continue;
			}
			
			StringBuilder placeholdersBuilder = new StringBuilder();
			for (int i = 0; i < sanitizedIds.size(); i++) {
				if (i > 0) {
					placeholdersBuilder.append(',');
				}
				placeholdersBuilder.append('?');
			}
			
			String sql = SQL_FIND_BY_USER_IDS + placeholdersBuilder.toString() + ")";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				for (int i = 0; i < sanitizedIds.size(); i++) {
					ps.setLong(i + 1, sanitizedIds.get(i));
				}
				
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						UserProfile p = mapRow(rs);
						result.put(p.getUserId(), p);
					}
				}
			}
		}
		
		return result;
	}

	private void setParameters(final PreparedStatement ps, final UserProfile p, final boolean isUpdate) throws SQLException {
		int idx = 1;
		if (!isUpdate)
			ps.setLong(idx++, p.getUserId());

		ps.setString(idx++, p.getFullname());
		ps.setString(idx++, p.getPhoneNumber());
		ps.setString(idx++, p.getEmail());
		setNullableInt(ps, idx++, p.getGender() != null ? p.getGender().getId() : null);
		setNullableInt(ps, idx++, p.getPreferredLanguage() != null ? p.getPreferredLanguage().getId() : null);
		ps.setString(idx++, p.getAvatarUrl());

		if (isUpdate)
			ps.setLong(idx, p.getUserId());
	}

	private void setNullableInt(final PreparedStatement ps, final int index, final Integer id) throws SQLException {
		if (id != null)
			ps.setInt(index, id);
		else
			ps.setNull(index, Types.INTEGER);
	}

	private UserProfile mapRow(final ResultSet rs) throws SQLException {
		final UserProfile p = new UserProfile();
		p.setUserId(rs.getLong(COL_USER_ID));
		p.setFullname(rs.getString(COL_PROFILE_FULLNAME));
		p.setPhoneNumber(rs.getString(COL_PROFILE_PHONE_NUMBER));
		p.setEmail(rs.getString(COL_PROFILE_EMAIL));
		p.setAvatarUrl(rs.getString(COL_PROFILE_AVATAR_URL));
		p.setUpdatedAt(rs.getTimestamp(COL_UPDATED_AT));

		final int genderID = rs.getInt(COL_PROFILE_GENDER_ID);
		if (!rs.wasNull()) {
			Gender g = new Gender();
			g.setId(genderID);
			p.setGender(g);
		}

		final int languageID = rs.getInt(COL_PROFILE_PREFERRED_LANGUAGE_ID);
		if (!rs.wasNull()) {
			Language l = new Language();
			l.setId(languageID);
			p.setPreferredLanguage(l);
		}
		return p;
	}
}
