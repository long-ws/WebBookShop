package dao.common;

import beans.common.Permission;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PermissionDAO {
    
    int insert(Connection conn, Permission permission) throws SQLException;
    
    void update(Connection conn, Permission permission) throws SQLException;
    
    void delete(Connection conn, int permissionId) throws SQLException;
    
    Optional<Permission> findById(Connection conn, int permissionId) throws SQLException;
    
    Optional<Permission> findByCode(Connection conn, String code) throws SQLException;
    
    List<Permission> findAll(Connection conn) throws SQLException;
    
    List<Permission> findAllActive(Connection conn) throws SQLException;
    
    List<Permission> findByModule(Connection conn, String module) throws SQLException;
    
    long count(Connection conn) throws SQLException;
    
    boolean existsByCode(Connection conn, String code) throws SQLException;
    
    Optional<Boolean> isSystemPermission(Connection conn, int permissionId) throws SQLException;
    
    List<String> findAllModules(Connection conn) throws SQLException;
}
