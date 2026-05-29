package repository;

import beans.User;
import beans.user.UserAccount;
import beans.user.UserLocalAuth;
import beans.user.UserOAuthAuth;
import beans.user.UserProfile;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import utils.DBConnection;

public class UserRepositoryImpl implements UserRepository {

	private final UserCrudRepository userCrudRepository;
	private final UserAuthRepository userAuthRepository;
	private final UserQueryRepository userQueryRepository;
	private final UserCreationRepository userCreationRepository;

	public UserRepositoryImpl() {
		this(new UserCrudRepositoryImpl(), new UserAuthRepositoryImpl(), new UserQueryRepositoryImpl(),
				new UserCreationRepositoryImpl());
	}

	public UserRepositoryImpl(UserCrudRepository userCrudRepository, UserAuthRepository userAuthRepository,
			UserQueryRepository userQueryRepository, UserCreationRepository userCreationRepository) {
		this.userCrudRepository = userCrudRepository;
		this.userAuthRepository = userAuthRepository;
		this.userQueryRepository = userQueryRepository;
		this.userCreationRepository = userCreationRepository;
	}

	@Override
	public long insert(Connection conn, User user) throws SQLException {
		return userCrudRepository.insert(conn, user);
	}

	@Override
	public void update(Connection conn, User user) throws SQLException {
		userCrudRepository.update(conn, user);
	}

	@Override
	public boolean delete(Connection conn, List<Long> userIds) throws SQLException {
		return userCrudRepository.delete(conn, userIds);
	}

	@Override
	public void changePassword(Connection conn, long userId, String hashedPassword) throws SQLException {
		userAuthRepository.changePassword(conn, userId, hashedPassword);
	}

	@Override
	public boolean incrementTokenVersion(Connection conn, long userId) throws SQLException {
		return userAuthRepository.incrementTokenVersion(conn, userId);
	}

	@Override
	public long createLocalUser(Connection conn, UserAccount account, UserProfile profile, UserLocalAuth localAuth)
			throws SQLException {
		return userCreationRepository.createLocalUser(conn, account, profile, localAuth);
	}

	@Override
	public long createOAuthUser(Connection conn, UserAccount account, UserProfile profile, UserOAuthAuth oauthAuth)
			throws SQLException {
		return userCreationRepository.createOAuthUser(conn, account, profile, oauthAuth);
	}

	@Override
	public Optional<User> findById(Connection conn, long userId) throws SQLException {
		return userCrudRepository.findById(conn, userId);
	}

	@Override
	public Optional<User> findById(long userId) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			return userCrudRepository.findById(conn, userId);
		}
	}

	@Override
	public Optional<User> findByUsername(Connection conn, String username) throws SQLException {
		return userQueryRepository.findByUsername(conn, username);
	}

	@Override
	public Optional<User> findByEmail(Connection conn, String email) throws SQLException {
		return userQueryRepository.findByEmail(conn, email);
	}

	@Override
	public boolean existUserByUsername(Connection conn, String username) throws SQLException {
		return userQueryRepository.existUserByUsername(conn, username);
	}

	@Override
	public boolean existUserByUsername(Connection conn, String username, long excludeUserId) throws SQLException {
		return userQueryRepository.existUserByUsername(conn, username, excludeUserId);
	}

	@Override
	public boolean existUserByEmail(Connection conn, String email) throws SQLException {
		return userQueryRepository.existUserByEmail(conn, email);
	}

	@Override
	public boolean existUserByEmail(Connection conn, String email, long excludeUserId) throws SQLException {
		return userQueryRepository.existUserByEmail(conn, email, excludeUserId);
	}

	@Override
	public List<User> findAllUser(Connection conn) throws SQLException {
		return userCrudRepository.findAllUser(conn);
	}

	@Override
	public long count(Connection conn) throws SQLException {
		return userCrudRepository.count(conn);
	}

	@Override
	public int getTokenVersion(Connection conn, long userId) throws SQLException {
		return userAuthRepository.getTokenVersion(conn, userId);
	}
}
