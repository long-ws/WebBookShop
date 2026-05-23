package repository;

import beans.User;
import dao.user.UserLocalDAO;
import dao.user.UserLocalDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class UserQueryRepositoryImpl implements UserQueryRepository {

	private final UserCrudRepository userCrudRepository;
	private final UserLocalDAO localDAO;

	public UserQueryRepositoryImpl() {
		this(new UserCrudRepositoryImpl(), new UserLocalDAOImpl());
	}

	public UserQueryRepositoryImpl(UserCrudRepository userCrudRepository, UserLocalDAO localDAO) {
		this.userCrudRepository = userCrudRepository;
		this.localDAO = localDAO;
	}

	@Override
	public Optional<User> findByUsername(Connection conn, String username) throws SQLException {
		Optional<Long> userIdOpt = localDAO.findUserIdByUsername(conn, username);
		if (userIdOpt.isEmpty()) {
			return Optional.empty();
		}
		return userCrudRepository.findById(conn, userIdOpt.get());
	}

	@Override
	public Optional<User> findByEmail(Connection conn, String email) throws SQLException {
		Optional<Long> userIdOpt = localDAO.findUserIdByEmail(conn, email);
		if (userIdOpt.isEmpty()) {
			return Optional.empty();
		}
		return userCrudRepository.findById(conn, userIdOpt.get());
	}

	@Override
	public boolean existUserByUsername(Connection conn, String username) throws SQLException {
		return localDAO.existsByUsername(conn, username, null);
	}

	@Override
	public boolean existUserByUsername(Connection conn, String username, long excludeUserId) throws SQLException {
		return localDAO.existsByUsername(conn, username, excludeUserId);
	}

	@Override
	public boolean existUserByEmail(Connection conn, String email) throws SQLException {
		return localDAO.existsByEmail(conn, email, null);
	}

	@Override
	public boolean existUserByEmail(Connection conn, String email, long excludeUserId) throws SQLException {
		return localDAO.existsByEmail(conn, email, excludeUserId);
	}
}
