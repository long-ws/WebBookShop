package repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import beans.common.Permission;

public interface PermissionRepository {

	int insert(Connection conn, Permission permission) throws SQLException;

	void update(Connection conn, Permission permission) throws SQLException;

	boolean delete(Connection conn, List<Integer> permissionIds) throws SQLException;

	Optional<Permission> findById(Connection conn, int permissionId) throws SQLException;

	Optional<Permission> findByCode(Connection conn, String permissionCode) throws SQLException;

	List<Permission> findAll(Connection conn) throws SQLException;

	List<Permission> findAllActive(Connection conn) throws SQLException;

	List<Permission> findByModule(Connection conn, String permissionModule) throws SQLException;

	long count(Connection conn) throws SQLException;

	boolean existsByCode(Connection conn, String permissionCode) throws SQLException;

	boolean isSystemPermission(Connection conn, int permissionId) throws SQLException;

	List<String> findAllModules(Connection conn) throws SQLException;

	List<Integer> findRoleIdsWithPermission(Connection conn, int permissionId) throws SQLException;

	Map<Integer, List<Integer>> findAllRolePermissionMappings(Connection conn) throws SQLException;
}
