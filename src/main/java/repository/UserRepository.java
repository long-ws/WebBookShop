package repository;

import java.sql.SQLException;
import java.util.Optional;
import beans.user.User;

public interface UserRepository {

	/**
	 * Lưu user mới.
	 */
	long save(User user) throws SQLException;

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
}