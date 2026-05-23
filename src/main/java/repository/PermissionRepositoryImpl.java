package repository;

import beans.common.Permission;
import dao.common.PermissionDAO;
import dao.common.PermissionDAOImpl;
import dao.common.RolePermissionAssignmentDAO;
import dao.common.RolePermissionAssignmentDAOImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PermissionRepositoryImpl implements PermissionRepository {

	private final PermissionDAO permissionDAO;
	private final RolePermissionAssignmentDAO rolePermissionAssignmentDAO;

	public PermissionRepositoryImpl() {
		this(new PermissionDAOImpl(), new RolePermissionAssignmentDAOImpl());
	}

	public PermissionRepositoryImpl(PermissionDAO permissionDAO,
			RolePermissionAssignmentDAO rolePermissionAssignmentDAO) {
		this.permissionDAO = permissionDAO;
		this.rolePermissionAssignmentDAO = rolePermissionAssignmentDAO;
	}

	@Override
	public int insert(Connection conn, Permission permission) throws SQLException {
		return permissionDAO.insert(conn, permission);
	}

	@Override
	public void update(Connection conn, Permission permission) throws SQLException {
		permissionDAO.update(conn, permission);
	}

	@Override
	public boolean delete(Connection conn, List<Integer> permissionIds) throws SQLException {
		if (permissionIds == null || permissionIds.isEmpty()) {
			return false;
		}
		for (Integer id : permissionIds) {
			permissionDAO.delete(conn, id);
		}
		return true;
	}

	@Override
	public Optional<Permission> findById(Connection conn, int permissionId) throws SQLException {
		return permissionDAO.findById(conn, permissionId);
	}

	@Override
	public Optional<Permission> findByCode(Connection conn, String permissionCode) throws SQLException {
		return permissionDAO.findByCode(conn, permissionCode);
	}

	@Override
	public List<Permission> findAll(Connection conn) throws SQLException {
		return permissionDAO.findAll(conn);
	}

	@Override
	public List<Permission> findAllActive(Connection conn) throws SQLException {
		return permissionDAO.findAllActive(conn);
	}

	@Override
	public List<Permission> findByModule(Connection conn, String permissionModule) throws SQLException {
		return permissionDAO.findByModule(conn, permissionModule);
	}

	@Override
	public long count(Connection conn) throws SQLException {
		return permissionDAO.count(conn);
	}

	@Override
	public boolean existsByCode(Connection conn, String permissionCode) throws SQLException {
		return permissionDAO.existsByCode(conn, permissionCode);
	}

	@Override
	public boolean isSystemPermission(Connection conn, int permissionId) throws SQLException {
		Optional<Boolean> isSystemOpt = permissionDAO.isSystemPermission(conn, permissionId);
		return isSystemOpt.orElse(false);
	}

	@Override
	public List<String> findAllModules(Connection conn) throws SQLException {
		return permissionDAO.findAllModules(conn);
	}

	@Override
	public List<Integer> findRoleIdsWithPermission(Connection conn, int permissionId) throws SQLException {
		return rolePermissionAssignmentDAO.findRoleIdsByPermissionId(conn, permissionId);
	}

	@Override
	public Map<Integer, List<Integer>> findAllRolePermissionMappings(Connection conn) throws SQLException {
		return rolePermissionAssignmentDAO.findAllRolePermissionMappings(conn);
	}
}
