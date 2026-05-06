package service;

import java.sql.SQLException;
import java.util.List;

import beans.User;
import dao.UserDAO;

public class UserService {

	private final UserDAO userDAO;

	public UserService() {
		this.userDAO = new UserDAO();
	}

	public long insert(User user) throws SQLException {
		return userDAO.insert(user);
	}

	public void update(User user) throws SQLException {
		userDAO.update(user);
	}

	public void delete(long id) throws SQLException {
		userDAO.delete(id);
	}

	public User getById(long id) {
		return userDAO.getById(id);
	}

	public List<User> getAll() {
		return userDAO.getAll();
	}

	public List<User> getPart(int limit, int offset) {
		return userDAO.getPart(limit, offset);
	}

	public List<User> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		return userDAO.getOrderedPart(limit, offset, orderBy, orderDir);
	}

	public User getByUsername(String username) {
		return userDAO.getByUsername(username);
	}

	public void changePassword(long userId, String newPassword) {
		userDAO.changePassword(userId, newPassword);
	}

	public User getByEmail(String email) {
		return userDAO.getByEmail(email);
	}

	public User getByPhoneNumber(String phoneNumber) {
		return userDAO.getByPhoneNumber(phoneNumber);
	}

	public int count() {
		return userDAO.count();
	}
}
