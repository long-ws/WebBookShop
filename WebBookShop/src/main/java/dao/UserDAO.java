package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.User;
import utils.DBConnection;

public class UserDAO implements DAO<User> {

	public long insert(User user) throws SQLException {
		String sql = "INSERT INTO user (username, password, fullname, email, phoneNumber, gender, address, role) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			conn.setAutoCommit(false);

			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getFullname());
			ps.setString(4, user.getEmail());
			ps.setString(5, user.getPhoneNumber());
			ps.setInt(6, user.getGender());
			ps.setString(7, user.getAddress());
			ps.setString(8, user.getRole());

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Insert user failed, no rows affected");

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					conn.commit();
					return rs.getLong(1);
				} else {
					throw new SQLException("Insert user failed, no ID obtained");
				}
			}
		}
	}

	public void update(User user) throws SQLException {
		String sql = "UPDATE user SET username=?, password=?, fullname=?, email=?, phoneNumber=?, gender=?, address=?, role=? WHERE id=? and isDeleted = 0";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			conn.setAutoCommit(false);

			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getFullname());
			ps.setString(4, user.getEmail());
			ps.setString(5, user.getPhoneNumber());
			ps.setInt(6, user.getGender());
			ps.setString(7, user.getAddress());
			ps.setString(8, user.getRole());
			ps.setLong(9, user.getId());

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Update user failed, no rows affected");

			conn.commit();
		}
	}

	public void delete(long userId) throws SQLException {

	    String deleteCart = "DELETE FROM cart WHERE userId = ?";
	    String deleteWishlist = "DELETE FROM wishlist_item WHERE userId = ?";
	    String hideReview = "UPDATE product_review SET isShow = 0 WHERE userId = ?";
	    String softDeleteUser = "UPDATE user SET isDeleted = 1 WHERE id = ?";

	    try (Connection conn = DBConnection.getConnection()) {
	        conn.setAutoCommit(false);

	        try (
	            PreparedStatement psCart = conn.prepareStatement(deleteCart);
	            PreparedStatement psWishlist = conn.prepareStatement(deleteWishlist);
	            PreparedStatement psReview = conn.prepareStatement(hideReview);
	            PreparedStatement psUser = conn.prepareStatement(softDeleteUser);
	        ) {
	            psCart.setLong(1, userId);
	            psCart.executeUpdate();

	            psWishlist.setLong(1, userId);
	            psWishlist.executeUpdate();

	            psReview.setLong(1, userId);
	            psReview.executeUpdate();

	            psUser.setLong(1, userId);
	            int rows = psUser.executeUpdate();
	            if (rows == 0) {
	                throw new SQLException("Soft delete user failed, user not found");
	            }

	            conn.commit();
	        } catch (SQLException e) {
	            conn.rollback();
	            throw e;
	        }
	    }
	}


	public User getById(long id) {
		String sql = "SELECT * FROM user WHERE id=? and isDeleted = 0";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToUser(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<User> getAll() {
		List<User> list = new ArrayList<>();
		String sql = "SELECT * FROM user WHERE isDeleted = 0";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next())
				list.add(mapResultSetToUser(rs));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<User> getPart(int limit, int offset) {
		List<User> list = new ArrayList<>();
		String sql = "SELECT * FROM user WHERE isDeleted = 0 LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapResultSetToUser(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<User> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		List<User> list = new ArrayList<>();
		String sql = "SELECT * FROM user WHERE isDeleted = 0 ORDER BY " + orderBy + " " + orderDir + " LIMIT ? OFFSET ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, limit);
			ps.setInt(2, offset);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					list.add(mapResultSetToUser(rs));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public User getByUsername(String username) {
		String sql = "SELECT * FROM user WHERE username=? AND isDeleted = 0";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToUser(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean changePassword(long userId, String newPassword) {
		String sql = "UPDATE user SET password=? WHERE id=? AND isDeleted = 0";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			conn.setAutoCommit(false);

			ps.setString(1, newPassword);
			ps.setLong(2, userId);

			int rows = ps.executeUpdate();
			if (rows == 0)
				throw new SQLException("Change password failed, no rows affected");

			conn.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public User getByEmail(String email) {
		String sql = "SELECT * FROM user WHERE email=? isDeleted = 0 ";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToUser(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getByPhoneNumber(String phoneNumber) {
		String sql = "SELECT * FROM user WHERE phoneNumber=? and isDeleted = 0";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, phoneNumber);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapResultSetToUser(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int count() {
		String sql = "SELECT COUNT(id) FROM user Where isDeleted = 0";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next())
				return rs.getInt(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private User mapResultSetToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getLong("id"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setFullname(rs.getString("fullname"));
		user.setEmail(rs.getString("email"));
		user.setPhoneNumber(rs.getString("phoneNumber"));
		user.setGender(rs.getInt("gender")); // <-- dùng int
		user.setAddress(rs.getString("address"));
		user.setRole(rs.getString("role"));
		return user;
	}

}
