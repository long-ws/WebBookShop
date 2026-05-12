package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import beans.common.Role;
import beans.common.UserStatus;
import beans.user.User;
import beans.user.UserAuthInfo;
import beans.user.UserLocalAuth;
import beans.user.UserProfile;
import constants.UserConstants;
import utils.DBConnection;

public class UserRepositoryImpl implements UserRepository {

	@Override
	public long save(User user) throws SQLException {
		long userId;
		try (Connection conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false);
			try {
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

				conn.commit();
				return userId;
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		}
	}

	@Override
	public Optional<User> findById(long id) {
		String sql = "SELECT u.*, up.fullname, up.avatar_url FROM user_account u "
				+ "LEFT JOIN user_profile up ON u.id = up.user_id WHERE u.id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					User user = new User();
					user.setId(rs.getLong("id"));

					UserProfile profile = new UserProfile();
					profile.setFullname(rs.getString("fullname"));
					profile.setAvatarUrl(rs.getString("avatar_url"));
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

	@Override
	public Optional<User> findByUsername(String username) {
		String sql = "SELECT u.*, ul.password_hash, ul.email, up.fullname, up.avatar_url " + "FROM user_account u "
				+ "LEFT JOIN user_local ul ON u.id = ul.user_id " + "LEFT JOIN user_profile up ON u.id = up.user_id "
				+ "WHERE ul.username = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					User user = mapResultSetToUser(rs);

					UserAuthInfo authInfo = new UserAuthInfo();
					UserLocalAuth localAuth = new UserLocalAuth();
					localAuth.setPasswordHash(rs.getString("password_hash"));
					localAuth.setEmail(rs.getString("email"));
					authInfo.setLocal(localAuth);
					user.setAuthInfo(authInfo);

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
		String sql = "SELECT u.*, up.fullname, up.avatar_url FROM user_account u "
				+ "LEFT JOIN user_local ul ON u.id = ul.user_id " + "LEFT JOIN user_profile up ON u.id = up.user_id "
				+ "WHERE ul.email = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					User user = mapResultSetToUser(rs);
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

	private User mapResultSetToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getLong("id"));

		// Status
		UserStatus status = new UserStatus();
		status.setId(rs.getInt("status_id"));
		user.setStatus(status);

		// Role (mặc định là CUSTOMER nếu chưa có)
		Role role = new Role();
		role.setCode("CUSTOMER");
		user.setRole(role);

		// Profile
		UserProfile profile = new UserProfile();
		profile.setFullname(rs.getString("fullname"));
		profile.setAvatarUrl(rs.getString("avatar_url"));
		user.setProfile(profile);

		return user;
	}
}