package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import beans.User;
import beans.common.Role;
import beans.common.UserStatus;
import beans.user.UserAuthInfo;
import beans.user.UserLocalAuth;
import beans.user.UserProfile;
import constants.UserConstants;
import utils.DBConnection;

public class UserRepositoryImpl implements UserRepository {

	@Override
	public long insert(User user) throws SQLException {
		long userId;
		try (Connection conn = DBConnection.getConnection()) {
			String userSql = "INSERT INTO user_account (status_id, token_version, created_at, updated_at) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
			try (PreparedStatement ps = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
				ps.setInt(1, user.getStatus() != null ? user.getStatus().getId() : UserConstants.Status.ACTIVE);
				ps.setInt(2, UserConstants.Security.TOKEN_VERSION_INITIAL);
				ps.executeUpdate();
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						userId = rs.getLong(1);
					else
						throw new SQLException("Tạo user thất bại, không lấy được id.");
				}
			}

			String profileSql = "INSERT INTO user_profile (user_id, fullname, avatar_url) VALUES (?, ?, ?)";
			try (PreparedStatement ps = conn.prepareStatement(profileSql)) {
				ps.setLong(1, userId);
				ps.setString(2, user.getProfile().getFullname());
				ps.setString(3, user.getProfile().getAvatarUrl());
				ps.executeUpdate();
			}

			String roleSql = "INSERT INTO user_role_registry (user_id, role_id) SELECT ?, id FROM role_registry WHERE code = 'CUSTOMER'";
			try (PreparedStatement ps = conn.prepareStatement(roleSql)) {
				ps.setLong(1, userId);
				ps.executeUpdate();
			}

			return userId;
		}
	}

	@Override
	public void update(User user) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			String userSql = "UPDATE user_account SET status_id=?, token_version=?, updated_at=CURRENT_TIMESTAMP WHERE id=?";
			try (PreparedStatement ps = conn.prepareStatement(userSql)) {
				ps.setInt(1, user.getStatus() != null ? user.getStatus().getId() : UserConstants.Status.ACTIVE);
				ps.setInt(2, user.getTokenVersion());
				ps.setLong(3, user.getId());
				ps.executeUpdate();
			}

			String profileSql = "UPDATE user_profile SET fullname=?, avatar_url=? WHERE user_id=?";
			try (PreparedStatement ps = conn.prepareStatement(profileSql)) {
				ps.setString(1, user.getProfile().getFullname());
				ps.setString(2, user.getProfile().getAvatarUrl());
				ps.setLong(3, user.getId());
				ps.executeUpdate();
			}
		}
	}

	@Override
	public void delete(long id) throws SQLException {
		String sql = "UPDATE user_account SET deleted_at=CURRENT_TIMESTAMP WHERE id=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			ps.executeUpdate();
		}
	}

	@Override
	public Optional<User> findById(long id) {
		String sql = "SELECT u.*, ul.username, ul.email, up.fullname, up.phone_number, up.gender_id, up.avatar_url, up.preferred_language_id FROM user_account u "
				+ "LEFT JOIN user_local ul ON u.id = ul.user_id "
				+ "LEFT JOIN user_profile up ON u.id = up.user_id WHERE u.id = ? AND u.deleted_at IS NULL";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					User user = mapResultSetToUser(rs);

					String username = rs.getString("username");
					String email = rs.getString("email");

					UserAuthInfo authInfo = new UserAuthInfo();
					UserLocalAuth localAuth = new UserLocalAuth();
					localAuth.setUsername(username);
					localAuth.setEmail(email);
					authInfo.setLocal(localAuth);
					user.setAuthInfo(authInfo);

					user.setUsername(username);
					user.setEmail(email);

					UserProfile profile = user.getProfile() != null ? user.getProfile() : new UserProfile();
					profile.setPhoneNumber(rs.getString("phone_number"));

					int genderId = rs.getInt("gender_id");
					if (!rs.wasNull()) {
						beans.common.Gender gender = new beans.common.Gender();
						gender.setId(genderId);
						profile.setGender(gender);
					}

					int langId = rs.getInt("preferred_language_id");
					if (!rs.wasNull()) {
						beans.common.Language lang = new beans.common.Language();
						lang.setId(langId);
						profile.setPreferredLanguage(lang);
					}

					user.setProfile(profile);

					user.setRole(getPrimaryRole(id, conn));
					return Optional.of(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public List<User> findAll(int limit, int offset, String orderBy, String orderDir) {
		List<User> users = new ArrayList<>();
		String sql = "SELECT u.*, ul.username, ul.email, up.fullname, up.phone_number, up.gender_id, up.avatar_url FROM user_account u "
				+ "LEFT JOIN user_local ul ON u.id = ul.user_id "
				+ "LEFT JOIN user_profile up ON u.id = up.user_id WHERE u.deleted_at IS NULL " + "ORDER BY " + orderBy
				+ " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					User user = mapResultSetToUser(rs);

					String username = rs.getString("username");
					String email = rs.getString("email");

					UserAuthInfo authInfo = new UserAuthInfo();
					UserLocalAuth localAuth = new UserLocalAuth();
					localAuth.setUsername(username);
					localAuth.setEmail(email);
					authInfo.setLocal(localAuth);
					user.setAuthInfo(authInfo);

					user.setUsername(username);
					user.setEmail(email);

					UserProfile profile = user.getProfile() != null ? user.getProfile() : new UserProfile();
					profile.setPhoneNumber(rs.getString("phone_number"));

					int genderId = rs.getInt("gender_id");
					if (!rs.wasNull()) {
						beans.common.Gender gender = new beans.common.Gender();
						gender.setId(genderId);
						profile.setGender(gender);
					}
					user.setProfile(profile);

					user.setRole(getPrimaryRole(user.getId(), conn));
					users.add(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	@Override
	public Optional<User> findByUsername(String username) {
		String sql = "SELECT u.*, ul.username, ul.password_hash, ul.email, up.fullname, up.phone_number, up.gender_id, up.avatar_url, up.preferred_language_id "
				+ "FROM user_account u " + "LEFT JOIN user_local ul ON u.id = ul.user_id "
				+ "LEFT JOIN user_profile up ON u.id = up.user_id " + "WHERE ul.username = ? AND u.deleted_at IS NULL";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					User user = mapResultSetToUser(rs);

					String usernameVal = rs.getString("username");
					String emailVal = rs.getString("email");

					UserAuthInfo authInfo = new UserAuthInfo();
					UserLocalAuth localAuth = new UserLocalAuth();
					localAuth.setUsername(usernameVal);
					localAuth.setPasswordHash(rs.getString("password_hash"));
					localAuth.setEmail(emailVal);
					authInfo.setLocal(localAuth);
					user.setAuthInfo(authInfo);

					user.setUsername(usernameVal);
					user.setEmail(emailVal);

					UserProfile profile = user.getProfile() != null ? user.getProfile() : new UserProfile();
					profile.setPhoneNumber(rs.getString("phone_number"));

					int genderId = rs.getInt("gender_id");
					if (!rs.wasNull()) {
						beans.common.Gender gender = new beans.common.Gender();
						gender.setId(genderId);
						profile.setGender(gender);
					}

					int langId = rs.getInt("preferred_language_id");
					if (!rs.wasNull()) {
						beans.common.Language lang = new beans.common.Language();
						lang.setId(langId);
						profile.setPreferredLanguage(lang);
					}

					user.setProfile(profile);

					user.setRole(getPrimaryRole(user.getId(), conn));
					return Optional.of(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Optional<User> findByEmail(String email) {
		String sql = "SELECT u.*, ul.username, ul.email, up.fullname, up.phone_number, up.gender_id, up.avatar_url, up.preferred_language_id FROM user_account u "
				+ "LEFT JOIN user_local ul ON u.id = ul.user_id " + "LEFT JOIN user_profile up ON u.id = up.user_id "
				+ "WHERE ul.email = ? AND u.deleted_at IS NULL";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					User user = mapResultSetToUser(rs);

					String usernameVal = rs.getString("username");
					String emailVal = rs.getString("email");

					UserAuthInfo authInfo = new UserAuthInfo();
					UserLocalAuth localAuth = new UserLocalAuth();
					localAuth.setUsername(usernameVal);
					localAuth.setEmail(emailVal);
					authInfo.setLocal(localAuth);
					user.setAuthInfo(authInfo);

					user.setUsername(usernameVal);
					user.setEmail(emailVal);

					UserProfile profile = user.getProfile() != null ? user.getProfile() : new UserProfile();
					profile.setPhoneNumber(rs.getString("phone_number"));

					int genderId = rs.getInt("gender_id");
					if (!rs.wasNull()) {
						beans.common.Gender gender = new beans.common.Gender();
						gender.setId(genderId);
						profile.setGender(gender);
					}

					int langId = rs.getInt("preferred_language_id");
					if (!rs.wasNull()) {
						beans.common.Language lang = new beans.common.Language();
						lang.setId(langId);
						profile.setPreferredLanguage(lang);
					}

					user.setProfile(profile);

					user.setRole(getPrimaryRole(user.getId(), conn));
					return Optional.of(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public boolean existsByUsername(String username) {
		String sql = "SELECT COUNT(*) FROM user_local WHERE username = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean existsByEmail(String email) {
		String sql = "SELECT COUNT(*) FROM user_local WHERE email = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public long count() {
		String sql = "SELECT COUNT(*) FROM user_account WHERE deleted_at IS NULL";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void changePassword(long userId, String hashedPassword) {
		String sql = "UPDATE user_local SET password_hash = ? WHERE user_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, hashedPassword);
			ps.setLong(2, userId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean incrementTokenVersion(long userId) {
		String sql = "UPDATE user_account SET token_version = COALESCE(token_version, 0) + 1 WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int getTokenVersion(long userId) {
		String sql = "SELECT token_version FROM user_account WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("token_version");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private Role getPrimaryRole(long userId, Connection conn) throws SQLException {
		String sql = "SELECT r.code FROM role_registry r INNER JOIN user_role_registry ur ON r.id = ur.role_id WHERE ur.user_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Role role = new Role();
					role.setCode(rs.getString("code"));
					return role;
				}
			}
		}
		return null;
	}

	private User mapResultSetToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getLong("id"));

		UserStatus status = new UserStatus();
		status.setId(rs.getInt("status_id"));
		user.setStatus(status);

		Role role = new Role();
		role.setCode("CUSTOMER");
		user.setRole(role);

		UserProfile profile = new UserProfile();
		profile.setFullname(rs.getString("fullname"));
		profile.setAvatarUrl(rs.getString("avatar_url"));
		user.setProfile(profile);

		return user;
	}
}
