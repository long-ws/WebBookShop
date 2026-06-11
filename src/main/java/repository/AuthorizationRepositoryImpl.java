package repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import beans.common.Permission;
import beans.common.Role;
import config.security.SecurityConfig;
import dao.common.PermissionDAO;
import dao.common.PermissionDAOImpl;
import dao.common.RoleDAO;
import dao.common.RoleDAOImpl;
import dao.common.RolePermissionAssignmentDAO;
import dao.common.RolePermissionAssignmentDAOImpl;
import dao.user.UserLocalDAO;
import dao.user.UserLocalDAOImpl;
import dao.user.UserRoleDAO;
import dao.user.UserRoleDAOImpl;

public class AuthorizationRepositoryImpl implements AuthorizationRepository {

	private final UserRoleDAO userRoleDAO;
	private final RoleDAO roleDAO;
	private final PermissionDAO permissionDAO;
	private final RolePermissionAssignmentDAO rolePermissionAssignmentDAO;
	private final UserLocalDAO userLocalDAO;

	public AuthorizationRepositoryImpl() {
		this(new UserRoleDAOImpl(), new RoleDAOImpl(), new PermissionDAOImpl(), new RolePermissionAssignmentDAOImpl(),
				new UserLocalDAOImpl());
	}

	public AuthorizationRepositoryImpl(UserRoleDAO userRoleDAO, RoleDAO roleDAO, PermissionDAO permissionDAO, RolePermissionAssignmentDAO rolePermissionAssignmentDAO) {
		this(userRoleDAO, roleDAO, permissionDAO, rolePermissionAssignmentDAO, new UserLocalDAOImpl());
	}

	public AuthorizationRepositoryImpl(UserRoleDAO userRoleDAO, RoleDAO roleDAO, PermissionDAO permissionDAO,
			RolePermissionAssignmentDAO rolePermissionAssignmentDAO, UserLocalDAO userLocalDAO) {
		this.userRoleDAO = userRoleDAO;
		this.roleDAO = roleDAO;
		this.permissionDAO = permissionDAO;
		this.rolePermissionAssignmentDAO = rolePermissionAssignmentDAO;
		this.userLocalDAO = userLocalDAO;
	}

	@Override
	public boolean userHasPermission(Connection conn, long userId, String permissionCode) throws SQLException {
		return rolePermissionAssignmentDAO.hasPermission(conn, userId, permissionCode);
	}

	@Override
	public boolean userHasAnyPermission(Connection conn, long userId, List<String> permissionCodes) throws SQLException {
		if (permissionCodes == null || permissionCodes.isEmpty()) {
			return false;
		}
		for (String code : permissionCodes) {
			if (userHasPermission(conn, userId, code)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean userHasRole(Connection conn, long userId, String roleCode) throws SQLException {
		Optional<Role> roleOpt = roleDAO.findByCode(conn, roleCode);
		if (roleOpt.isEmpty() || !roleOpt.get().isActive()) {
			return false;
		}
		int roleId = roleOpt.get().getId();
		return userRoleDAO.findRoleIdsByUserId(conn, userId).contains(roleId);
	}

	@Override
	public boolean userIsSuperAdmin(Connection conn, long userId) throws SQLException {
		if (SecurityConfig.isSuperAdminUserId(userId)) {
			return true;
		}
		Optional<beans.user.UserLocalAuth> localOpt = userLocalDAO.findByUserId(conn, userId);
		if (!localOpt.isPresent()) {
			return false;
		}
		return SecurityConfig.isSuperAdminUsername(localOpt.get().getUsername());
	}

	@Override
	public List<Permission> findPermissionsByUserId(Connection conn, long userId) throws SQLException {
		List<Permission> permissions = permissionDAO.findByUserId(conn, userId);

		permissions.sort(new Comparator<Permission>() {
			@Override
			public int compare(Permission a, Permission b) {
				int moduleCompare = nullSafe(a.getModule()).compareToIgnoreCase(nullSafe(b.getModule()));
				if (moduleCompare != 0) {
					return moduleCompare;
				}
				return nullSafe(a.getCode()).compareToIgnoreCase(nullSafe(b.getCode()));
			}
		});

		return permissions;
	}

	@Override
	public List<Role> findRolesByUserId(Connection conn, long userId) throws SQLException {
		List<Role> roles = roleDAO.findByUserId(conn, userId);

		roles.sort(new Comparator<Role>() {
			@Override
			public int compare(Role role1, Role role2) {
				int id1 = role1.getId();
				int id2 = role2.getId();
				return Integer.compare(id1, id2);
			}
		});
		return roles;
	}

	private String nullSafe(String value) {
		return value == null ? "" : value;
	}
}
