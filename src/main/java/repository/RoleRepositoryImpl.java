package repository;

import beans.common.Permission;
import beans.common.Role;
import dao.common.PermissionDAO;
import dao.common.PermissionDAOImpl;
import dao.common.RoleDAO;
import dao.common.RoleDAOImpl;
import dao.common.RolePermissionAssignmentDAO;
import dao.common.RolePermissionAssignmentDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoleRepositoryImpl implements RoleRepository {

	private final RoleDAO roleDAO;
	private final PermissionDAO permissionDAO;
	private final RolePermissionAssignmentDAO rolePermissionAssignmentDAO;

	public RoleRepositoryImpl() {
		this(new RoleDAOImpl(), new PermissionDAOImpl(), new RolePermissionAssignmentDAOImpl());
	}

	public RoleRepositoryImpl(RoleDAO roleDAO, PermissionDAO permissionDAO,
			RolePermissionAssignmentDAO rolePermissionAssignmentDAO) {
		this.roleDAO = roleDAO;
		this.permissionDAO = permissionDAO;
		this.rolePermissionAssignmentDAO = rolePermissionAssignmentDAO;
	}

	@Override
	public int insert(Connection conn, Role role) throws SQLException {
		return roleDAO.insert(conn, role);
	}

	@Override
	public void update(Connection conn, Role role) throws SQLException {
		roleDAO.update(conn, role);
	}

	@Override
	public boolean delete(Connection conn, List<Integer> roleIds) throws SQLException {
		if (roleIds == null || roleIds.isEmpty()) {
			return false;
		}
		for (Integer id : roleIds) {
			roleDAO.delete(conn, id);
		}
		return true;
	}

	@Override
	public Optional<Role> findById(Connection conn, int roleId) throws SQLException {
		Optional<Role> roleOpt = roleDAO.findById(conn, roleId);
		if (roleOpt.isPresent()) {
			Role role = roleOpt.get();
			role.setPermissions(loadPermissionsForRole(conn, roleId));
			return Optional.of(role);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Role> findByCode(Connection conn, String roleCode) throws SQLException {
		Optional<Role> roleOpt = roleDAO.findByCode(conn, roleCode);
		if (roleOpt.isPresent()) {
			Role role = roleOpt.get();
			role.setPermissions(loadPermissionsForRole(conn, role.getId()));
			return Optional.of(role);
		}
		return Optional.empty();
	}

	@Override
	public List<Role> findAll(Connection conn) throws SQLException {
		return roleDAO.findAll(conn);
	}

	@Override
	public List<Role> findAllActive(Connection conn) throws SQLException {
		return roleDAO.findAllActive(conn);
	}

	@Override
	public long count(Connection conn) throws SQLException {
		return roleDAO.count(conn);
	}

	@Override
	public boolean existsByCode(Connection conn, String roleCode) throws SQLException {
		return roleDAO.existsByCode(conn, roleCode);
	}

	@Override
	public boolean existsByName(Connection conn, String roleName) throws SQLException {
		return roleDAO.existsByName(conn, roleName);
	}

	@Override
	public boolean isSystemRole(Connection conn, int roleId) throws SQLException {
		Optional<Boolean> isSystemOpt = roleDAO.isSystemRole(conn, roleId);
		return isSystemOpt.orElse(false);
	}

	@Override
	public List<Permission> loadPermissionsForRole(Connection conn, int roleId) throws SQLException {
		List<Permission> permissions = new ArrayList<>();
		for (Integer permissionId : findPermissionIdsForRole(conn, roleId)) {
			Optional<Permission> permissionOpt = permissionDAO.findById(conn, permissionId);
			if (permissionOpt.isPresent() && permissionOpt.get().isActive()) {
				permissions.add(permissionOpt.get());
			}
		}
		return permissions;
	}

	@Override
	public List<Integer> findPermissionIdsForRole(Connection conn, int roleId) throws SQLException {
		return rolePermissionAssignmentDAO.findPermissionIdsByRoleId(conn, roleId);
	}

	@Override
	public void grantPermissionsToRole(Connection conn, int roleId, List<Integer> permissionIds) throws SQLException {
		rolePermissionAssignmentDAO.assignBatch(conn, roleId, permissionIds);
	}

	@Override
	public void revokePermissionsFromRole(Connection conn, int roleId, List<Integer> permissionIds)
			throws SQLException {
		rolePermissionAssignmentDAO.removeBatch(conn, roleId, permissionIds);
	}

	@Override
	public void clearRolePermissions(Connection conn, int roleId) throws SQLException {
		rolePermissionAssignmentDAO.removeAllByRoleId(conn, roleId);
	}

	@Override
	public boolean roleHasPermission(Connection conn, int roleId, String permissionCode) throws SQLException {
		Optional<Permission> permissionOpt = permissionDAO.findByCode(conn, permissionCode);
		if (permissionOpt.isEmpty() || !permissionOpt.get().isActive()) {
			return false;
		}
		return rolePermissionAssignmentDAO.hasPermission(conn, roleId, permissionOpt.get().getId());
	}

	@Override
	public boolean roleHasPermission(Connection conn, int roleId, int permissionId) throws SQLException {
		return rolePermissionAssignmentDAO.hasPermission(conn, roleId, permissionId);
	}

	@Override
	public Map<Integer, List<Integer>> findAllRolePermissionMappings(Connection conn) throws SQLException {
		return rolePermissionAssignmentDAO.findAllRolePermissionMappings(conn);
	}
}
