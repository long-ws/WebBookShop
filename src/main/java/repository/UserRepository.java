package repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import beans.User;

public interface UserRepository {

	/**
	 * Lưu user mới.
	 */
	long insert(User user) throws SQLException;

	void update(User user) throws SQLException;

	void delete(long id) throws SQLException;

	List<User> findAll(int limit, int offset, String orderBy, String orderDir);

	/**
	 * Tìm user theo ID
	 */
	Optional<User> findById(long id);

	/**
	 * Tìm người dùng theo Username.
	 */
	Optional<User> findByUsername(String username);

	/**
	 * Tìm người dùng theo Email.
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Kiểm tra xem Username đã tồn tại chưa.
	 */
	boolean existsByUsername(String username);

	/**
	 * Kiểm tra xem Email đã tồn tại chưa.
	 */
	boolean existsByEmail(String email);

	long count();

	void changePassword(long userId, String hashedPassword);

	boolean incrementTokenVersion(long userId);

	int getTokenVersion(long userId);
}