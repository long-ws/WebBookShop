package repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import beans.User;
import beans.common.Role;
import beans.user.UserAccount;
import beans.user.UserAuthInfo;
import beans.user.UserLocalAuth;
import beans.user.UserProfile;
import constants.SystemConstants;
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
		this(new UserAccountDAOImpl(), new UserProfileDAOImpl(), new UserLocalDAOImpl(), new UserRoleDAOImpl(), new RoleDAOImpl());
	}

	public UserCrudRepositoryImpl(UserAccountDAO accountDAO, UserProfileDAO profileDAO, UserLocalDAO localDAO, UserRoleDAO userRoleDAO, RoleDAO roleDAO) {
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
		assignRole(conn, userId, user.getRole() != null ? user.getRole().getCode() : SystemConstants.DEFAULT_ROLE_CODE);
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
		if (userIds == null || userIds.isEmpty())
			return false;
		accountDAO.softDeleteBatch(conn, userIds);
		return true;
	}

	@Override
	public Optional<User> findById(Connection conn, long userId) throws SQLException {
		Optional<UserAccount> accountOpt = accountDAO.findById(conn, userId);
		if (!accountOpt.isPresent()) {
			return Optional.empty();
		}

		List<Long> ids = new ArrayList<>();
		ids.add(userId);

		Map<Long, UserLocalAuth> localByUserId = localDAO.findByUserIdsAsMap(conn, ids);
		Map<Long, UserProfile> profileByUserId = profileDAO.findByUserIdsAsMap(conn, ids);
		Map<Long, Integer> primaryRoleIdByUserId = userRoleDAO.findPrimaryRoleIdByUserIdsAsMap(conn, ids);

		List<Integer> roleIds = listUniqueRoleIds(ids, primaryRoleIdByUserId);
		Map<Integer, Role> roleById = roleDAO.findByIdsAsMap(conn, roleIds);

		return Optional.of(assembleUser(accountOpt.get(), localByUserId, profileByUserId, primaryRoleIdByUserId, roleById));
	}

	@Override
	public List<User> findAllUser(Connection conn) throws SQLException {
		List<Long> userIds = accountDAO.findAllIds(conn);
		if (userIds == null || userIds.isEmpty()) {
			return new ArrayList<>();
		}
		return findUsersByIds(conn, userIds);
	}

	@Override
	public long count(Connection conn) throws SQLException {
		return accountDAO.countNotDeleted(conn);
	}

	private List<User> findUsersByIds(final Connection conn, final List<Long> userIds) throws SQLException {

		List<UserAccount> accounts = accountDAO.findAllAccountsByIds(conn, userIds);
		if (accounts.isEmpty()) {
			return new ArrayList<>();
		}

		Map<Long, UserLocalAuth> localByUserId = localDAO.findByUserIdsAsMap(conn, userIds);
		Map<Long, UserProfile> profileByUserId = profileDAO.findByUserIdsAsMap(conn, userIds);
		Map<Long, Integer> primaryRoleIdByUserId = userRoleDAO.findPrimaryRoleIdByUserIdsAsMap(conn, userIds);

		List<Integer> roleIds = listUniqueRoleIds(userIds, primaryRoleIdByUserId);
		Map<Integer, Role> roleById = roleDAO.findByIdsAsMap(conn, roleIds);

		List<User> users = new ArrayList<>();
		for (int i = 0; i < accounts.size(); i++) {
			UserAccount account = accounts.get(i);
			User user = assembleUser(account, localByUserId, profileByUserId, primaryRoleIdByUserId, roleById);
			users.add(user);
		}

		return users;
	}

	private List<Integer> listUniqueRoleIds(final List<Long> userIds, final Map<Long, Integer> primaryRoleIdByUserId) {
		Set<Integer> uniqueRoleIds = new HashSet<>();
		for (int i = 0; i < userIds.size(); i++) {
			Long userId = userIds.get(i);
			if (userId != null) {
				Integer roleId = primaryRoleIdByUserId.get(userId);
				if (roleId != null) {
					uniqueRoleIds.add(roleId);
				}
			}
		}

		List<Integer> roleIds = new ArrayList<>();
		for (Integer roleId : uniqueRoleIds) {
			roleIds.add(roleId);
		}

		return roleIds;
	}

	private User assembleUser(final UserAccount account, final Map<Long, UserLocalAuth> localByUserId, final Map<Long, UserProfile> profileByUserId, final Map<Long, Integer> primaryRoleIdByUserId,
			final Map<Integer, Role> roleById) {
		User user = new User();
		user.setId(account.getId());
		user.setStatus(account.getStatus());
		user.setTokenVersion(account.getTokenVersion());
		user.setCreatedAt(account.getCreatedAt());
		user.setUpdatedAt(account.getUpdatedAt());

		UserLocalAuth local = localByUserId != null ? localByUserId.get(account.getId()) : null;
		if (local != null) {
			UserAuthInfo auth = new UserAuthInfo();
			auth.setLocal(local);
			auth.setHasLocalAuth(local.getUsername() != null);
			user.setAuthInfo(auth);
			user.setUsername(local.getUsername());
		}

		UserProfile profile = profileByUserId != null ? profileByUserId.get(account.getId()) : null;
		if (profile != null) {
			user.setProfile(profile);
		}

		Integer roleId = primaryRoleIdByUserId != null ? primaryRoleIdByUserId.get(account.getId()) : null;
		if (roleId != null) {
			Role role = roleById != null ? roleById.get(roleId) : null;
			if (role != null) {
				user.setRole(role);
			}
		}

		return user;
	}

	private void assignRole(Connection conn, long userId, String roleCode) throws SQLException {
		Optional<Role> roleOpt = roleDAO.findByCode(conn, roleCode);
		if (roleOpt.isPresent()) {
			userRoleDAO.assignByRoleId(conn, userId, roleOpt.get().getId());
		}
	}

	private UserAccount toAccount(User user) {
		UserAccount account = new UserAccount();
		account.setId(user.getId());
		account.setStatusId(user.getStatus() != null ? user.getStatus().getId() : UserConstants.Status.ACTIVE);
		account.setTokenVersion(user.getTokenVersion());
		return account;
	}
}
