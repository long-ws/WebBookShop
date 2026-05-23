package repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import beans.User;
import beans.common.Role;
import beans.user.UserAccount;
import beans.user.UserAuthInfo;
import beans.user.UserLocalAuth;
import constants.UserConstants;
import dao.common.RoleDAO;
import dao.common.RoleDAOImpl;
import dao.user.UserAccountDAO;
import dao.user.UserAccountDAOImpl;
import dao.user.UserLocalDAO;
import dao.user.UserLocalDAOImpl;
import dao.user.UserProfileDAO;
import dao.user.UserProfileDAOImpl;
import dao.user.UserRoleDAO;
import dao.user.UserRoleDAOImpl;

public class UserCrudRepositoryImpl implements UserCrudRepository {

	private final UserAccountDAO accountDAO;
	private final UserProfileDAO profileDAO;
	private final UserLocalDAO localDAO;
	private final UserRoleDAO userRoleDAO;
	private final RoleDAO roleDAO;

	public UserCrudRepositoryImpl() {
		this(new UserAccountDAOImpl(), new UserProfileDAOImpl(), new UserLocalDAOImpl(), new UserRoleDAOImpl(),
				new RoleDAOImpl());
	}

	public UserCrudRepositoryImpl(UserAccountDAO accountDAO, UserProfileDAO profileDAO, UserLocalDAO localDAO,
			UserRoleDAO userRoleDAO, RoleDAO roleDAO) {
		this.accountDAO = accountDAO;
		this.profileDAO = profileDAO;
		this.localDAO = localDAO;
		this.userRoleDAO = userRoleDAO;
		this.roleDAO = roleDAO;
	}

	@Override
	public long insert(Connection conn, User user) throws SQLException {
		if (user.getAuthInfo() == null || user.getAuthInfo().getLocal() == null) {
			throw new SQLException("Thiếu thông tin đăng nhập local");
		}
		if (user.getProfile() == null) {
			throw new SQLException("Thiếu thông tin profile");
		}

		UserAccount account = toAccount(user);
		long userId = accountDAO.insert(conn, account);
		localDAO.insert(conn, userId, user.getAuthInfo().getLocal());
		user.getProfile().setUserId(userId);
		profileDAO.insert(conn, user.getProfile());
		assignRole(conn, userId, user.getRole() != null ? user.getRole().getCode() : UserConstants.Role.CUSTOMER);
		return userId;
	}

	@Override
	public void update(Connection conn, User user) throws SQLException {
		UserAccount account = toAccount(user);
		account.setId(user.getId());
		accountDAO.update(conn, account);

		if (user.getAuthInfo() != null && user.getAuthInfo().getLocal() != null) {
			localDAO.update(conn, user.getId(), user.getAuthInfo().getLocal());
		}

		if (user.getProfile() != null) {
			user.getProfile().setUserId(user.getId());
			profileDAO.update(conn, user.getProfile());
		}

		if (user.getRole() != null) {
			userRoleDAO.delete(conn, user.getId());
			assignRole(conn, user.getId(), user.getRole().getCode());
		}
	}

	@Override
	public boolean delete(Connection conn, List<Long> userIds) throws SQLException {
		if (userIds == null || userIds.isEmpty()) {
			return false;
		}
		accountDAO.softDeleteBatch(conn, userIds);
		return true;
	}

	@Override
	public Optional<User> findById(Connection conn, long userId) throws SQLException {
		Optional<UserAccount> accountOpt = accountDAO.findById(conn, userId);
		if (accountOpt.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(assembleUser(conn, accountOpt.get()));
	}

	@Override
	public List<User> findAllUser(Connection conn) throws SQLException {
		return findAllUser(conn, null, null);
	}

	@Override
	public List<User> findAllUser(Connection conn, String orderBy, String orderDir) throws SQLException {
		List<User> users = new ArrayList<>();
		for (Long userId : resolveOrderedUserIds(conn, orderBy, orderDir)) {
			findById(conn, userId).ifPresent(users::add);
		}
		return users;
	}

	@Override
	public long count(Connection conn) throws SQLException {
		return accountDAO.countNotDeleted(conn);
	}

	private void assignRole(Connection conn, long userId, String roleCode) throws SQLException {
		Optional<Role> roleOpt = roleDAO.findByCode(conn, roleCode);
		if (roleOpt.isEmpty()) {
			throw new SQLException("Không tìm thấy vai trò: " + roleCode);
		}
		userRoleDAO.assignByRoleId(conn, userId, roleOpt.get().getId());
	}

	private UserAccount toAccount(User user) {
		UserAccount account = new UserAccount();
		account.setId(user.getId());
		account.setStatusId(user.getStatus() != null ? user.getStatus().getId() : UserConstants.Status.ACTIVE);
		account.setTokenVersion(user.getTokenVersion());
		return account;
	}

	private User assembleUser(Connection conn, UserAccount account) throws SQLException {
		User user = new User();
		user.setId(account.getId());
		user.setStatus(account.getStatus());
		user.setTokenVersion(account.getTokenVersion());
		user.setLastLoginAt(account.getLastLoginAt());
		user.setRememberToken(account.getRememberToken());
		user.setRememberExpiresAt(account.getRememberExpiresAt());
		user.setDeletedAt(account.getDeletedAt());
		user.setCreatedAt(account.getCreatedAt());
		user.setUpdatedAt(account.getUpdatedAt());

		Optional<UserLocalAuth> localOpt = localDAO.findByUserId(conn, account.getId());
		UserAuthInfo auth = new UserAuthInfo();
		if (localOpt.isPresent()) {
			UserLocalAuth local = localOpt.get();
			auth.setLocal(local);
			auth.setHasLocalAuth(local.getUsername() != null);
			user.setUsername(local.getUsername());
		} else {
			auth.setHasLocalAuth(false);
		}
		user.setAuthInfo(auth);

		profileDAO.findUserProfileById(conn, account.getId()).ifPresent(user::setProfile);

		List<Integer> roleIds = userRoleDAO.findRoleIdsByUserId(conn, account.getId());
		if (!roleIds.isEmpty()) {
			roleDAO.findById(conn, roleIds.get(0)).ifPresent(user::setRole);
		}

		return user;
	}

	private List<Long> resolveOrderedUserIds(Connection conn, String orderBy, String orderDir) throws SQLException {
		boolean ascending = orderDir != null && "ASC".equalsIgnoreCase(orderDir.trim());
		if (orderBy == null) {
			List<Long> ids = new ArrayList<>();
			for (UserAccount account : accountDAO.findAllNotDeleted(conn)) {
				ids.add(account.getId());
			}
			return ids;
		}
		switch (orderBy.trim().toLowerCase()) {
		case "username":
			return localDAO.findAllUserIdsOrderByUsername(conn, ascending);
		case "fullname":
			List<User> users = new ArrayList<>();
			for (UserAccount account : accountDAO.findAllNotDeleted(conn)) {
				users.add(assembleUser(conn, account));
			}
			users.sort(Comparator.comparing(u -> u.getProfile() != null ? nullSafe(u.getProfile().getFullname()) : "",
					String.CASE_INSENSITIVE_ORDER));
			if (!ascending) {
				java.util.Collections.reverse(users);
			}
			List<Long> ids = new ArrayList<>();
			for (User user : users) {
				ids.add(user.getId());
			}
			return ids;
		case "email":
			List<User> byEmail = new ArrayList<>();
			for (UserAccount account : accountDAO.findAllNotDeleted(conn)) {
				byEmail.add(assembleUser(conn, account));
			}
			byEmail.sort(Comparator.comparing(
					u -> u.getAuthInfo() != null && u.getAuthInfo().getLocal() != null
							? nullSafe(u.getAuthInfo().getLocal().getEmail())
							: "",
					String.CASE_INSENSITIVE_ORDER));
			if (!ascending) {
				java.util.Collections.reverse(byEmail);
			}
			List<Long> emailIds = new ArrayList<>();
			for (User user : byEmail) {
				emailIds.add(user.getId());
			}
			return emailIds;
		case "created_at":
			List<UserAccount> accounts = accountDAO.findAllNotDeleted(conn);
			accounts.sort(Comparator.comparing(UserAccount::getCreatedAt,
					Comparator.nullsLast(Comparator.naturalOrder())));
			if (!ascending) {
				java.util.Collections.reverse(accounts);
			}
			List<Long> createdIds = new ArrayList<>();
			for (UserAccount account : accounts) {
				createdIds.add(account.getId());
			}
			return createdIds;
		case "id":
		default:
			List<UserAccount> byId = accountDAO.findAllNotDeleted(conn);
			if (!ascending) {
				java.util.Collections.reverse(byId);
			}
			List<Long> defaultIds = new ArrayList<>();
			for (UserAccount account : byId) {
				defaultIds.add(account.getId());
			}
			return defaultIds;
		}
	}

	private String nullSafe(String value) {
		return value == null ? "" : value;
	}
}
