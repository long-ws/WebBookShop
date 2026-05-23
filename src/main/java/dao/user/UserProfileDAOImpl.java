package dao.user;

import java.sql.*;
import java.util.Optional;
import beans.common.*;
import beans.user.UserProfile;

public class UserProfileDAOImpl implements UserProfileDAO {

	private static final String SQL_INSERT = "INSERT INTO user_profile (user_id, fullname, phone_number, email, gender_id, preferred_language_id, avatar_url) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_UPDATE = "UPDATE user_profile SET fullname=?, phone_number=?, email=?, gender_id=?, preferred_language_id=?, avatar_url=? WHERE user_id=?";
	private static final String SQL_DELETE = "DELETE FROM user_profile WHERE user_id = ?";
	private static final String SQL_FIND_BY_ID = "SELECT * FROM user_profile WHERE user_id = ?";

	@Override
	public int insert(Connection conn, UserProfile p) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
			setParameters(ps, p, false);
			return ps.executeUpdate();
		}
	}

	@Override
	public int update(Connection conn, UserProfile p) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
			setParameters(ps, p, true);
			return ps.executeUpdate();
		}
	}

	@Override
	public int delete(Connection conn, long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
			ps.setLong(1, userId);
			return ps.executeUpdate();
		}
	}

	@Override
	public Optional<UserProfile> findUserProfileById(Connection conn, long userId) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
			}
		}
	}

	private void setParameters(PreparedStatement ps, UserProfile profile, boolean isUpdate) throws SQLException {
		int idx = 1;

		if (!isUpdate) {
			ps.setLong(idx++, profile.getUserId());
		}

		ps.setString(idx++, profile.getFullname());
		ps.setString(idx++, profile.getPhoneNumber());
		ps.setString(idx++, profile.getEmail());

		setNullableInt(ps, idx++, profile.getGender() != null ? profile.getGender().getId() : null);
		setNullableInt(ps, idx++,
				profile.getPreferredLanguage() != null ? profile.getPreferredLanguage().getId() : null);

		ps.setString(idx++, profile.getAvatarUrl());

		if (isUpdate) {
			ps.setLong(idx, profile.getUserId());
		}
	}

	private void setNullableInt(PreparedStatement ps, int index, Integer id) throws SQLException {
		if (id != null) {
			ps.setInt(index, id);
		} else {
			ps.setNull(index, Types.INTEGER);
		}
	}

	private UserProfile mapRow(ResultSet rs) throws SQLException {
		UserProfile profile = new UserProfile();
		profile.setUserId(rs.getLong("user_id"));
		profile.setFullname(rs.getString("fullname"));
		profile.setPhoneNumber(rs.getString("phone_number"));
		profile.setEmail(rs.getString("email"));
		profile.setAvatarUrl(rs.getString("avatar_url"));
		profile.setUpdatedAt(rs.getTimestamp("updated_at"));

		int genderID = rs.getInt("gender_id");
		if (!rs.wasNull()) {
			Gender gender = new Gender();
			gender.setId(genderID);
			profile.setGender(gender);
		}

		int languageID = rs.getInt("preferred_language_id");
		if (!rs.wasNull()) {
			Language llanguage = new Language();
			llanguage.setId(languageID);
			profile.setPreferredLanguage(llanguage);
		}

		return profile;
	}
}