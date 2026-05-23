package repository;

import dao.user.UserAccountDAO;
import dao.user.UserAccountDAOImpl;
import dao.user.UserLocalDAO;
import dao.user.UserLocalDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;

public class UserAuthRepositoryImpl implements UserAuthRepository {

	private final UserAccountDAO accountDAO;
	private final UserLocalDAO localDAO;

	public UserAuthRepositoryImpl() {
		this(new UserAccountDAOImpl(), new UserLocalDAOImpl());
	}

	public UserAuthRepositoryImpl(UserAccountDAO accountDAO, UserLocalDAO localDAO) {
		this.accountDAO = accountDAO;
		this.localDAO = localDAO;
	}

	@Override
	public void changePassword(Connection conn, long userId, String hashedPassword) throws SQLException {
		localDAO.updatePassword(conn, userId, hashedPassword);
	}

	@Override
	public boolean incrementTokenVersion(Connection conn, long userId) throws SQLException {
		return accountDAO.incrementTokenVersion(conn, userId);
	}

	@Override
	public int getTokenVersion(Connection conn, long userId) throws SQLException {
		return accountDAO.getTokenVersion(conn, userId);
	}
}
