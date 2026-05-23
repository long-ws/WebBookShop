package dao.common;

import beans.common.Role;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoleDAO {
    
    int insert(Connection conn, Role role) throws SQLException;
    
    void update(Connection conn, Role role) throws SQLException;
    
    void delete(Connection conn, int roleId) throws SQLException;
    
    Optional<Role> findById(Connection conn, int roleId) throws SQLException;
    
    Optional<Role> findByCode(Connection conn, String code) throws SQLException;
    
    List<Role> findAll(Connection conn) throws SQLException;
    
    List<Role> findAllActive(Connection conn) throws SQLException;
    
    long count(Connection conn) throws SQLException;
    
    boolean existsByCode(Connection conn, String code) throws SQLException;
    
    boolean existsByName(Connection conn, String name) throws SQLException;
    
    Optional<Boolean> isSystemRole(Connection conn, int roleId) throws SQLException;
}
