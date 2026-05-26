package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import beans.common.Permission;
import beans.common.Role;
import repository.AuthorizationRepository;
import repository.AuthorizationRepositoryImpl;
import utils.DBConnection;

public class AuthorizationServiceImpl implements AuthorizationService {

	private final AuthorizationRepository authorizationRepository;

	public AuthorizationServiceImpl() {
		this(new AuthorizationRepositoryImpl());
	}

	public AuthorizationServiceImpl(AuthorizationRepository authorizationRepository) {
		this.authorizationRepository = authorizationRepository;
	}

	@Override
	public boolean hasPermission(long userId, String permissionCode) {
		try (Connection conn = DBConnection.getConnection()) {
			return authorizationRepository.userHasPermission(conn, userId, permissionCode);
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean hasAnyPermission(long userId, List<String> permissionCodes) {
		try (Connection conn = DBConnection.getConnection()) {
			return authorizationRepository.userHasAnyPermission(conn, userId, permissionCodes);
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean hasRole(long userId, String roleCode) {
		try (Connection conn = DBConnection.getConnection()) {
			return authorizationRepository.userHasRole(conn, userId, roleCode);
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean isSuperAdmin(long userId) {
		try (Connection conn = DBConnection.getConnection()) {
			return authorizationRepository.userIsSuperAdmin(conn, userId);
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public List<Permission> getPermissionsByUserId(long userId) {
		try (Connection conn = DBConnection.getConnection()) {
			return authorizationRepository.findPermissionsByUserId(conn, userId);
		} catch (SQLException e) {
			return new java.util.ArrayList<>();
		}
	}

	@Override
	public List<Role> getRolesByUserId(long userId) {
		try (Connection conn = DBConnection.getConnection()) {
			return authorizationRepository.findRolesByUserId(conn, userId);
		} catch (SQLException e) {
			return new java.util.ArrayList<>();
		}
	}
}
