package dao.user;

import java.sql.Connection;
import java.sql.SQLException;

import beans.user.UserLocalAuth;

public interface UserLocalDAO {
    int insert(Connection conn, long userId, UserLocalAuth local) throws SQLException;
    void updateEmail(Connection conn, long userId, String email) throws SQLException;
    void updatePassword(Connection conn, long userId, String passwordHash) throws SQLException;
    void update(Connection conn, long userId, UserLocalAuth local) throws SQLException;

    java.util.Optional<UserLocalAuth> findByUserId(Connection conn, long userId) throws SQLException;
    java.util.Optional<Long> findUserIdByUsername(Connection conn, String username) throws SQLException;
    java.util.Optional<Long> findUserIdByEmail(Connection conn, String email) throws SQLException;
    boolean existsByUsername(Connection conn, String username, Long excludeUserId) throws SQLException;
    boolean existsByEmail(Connection conn, String email, Long excludeUserId) throws SQLException;
    java.util.List<Long> findAllUserIdsOrderByUsername(Connection conn, boolean ascending) throws SQLException;
}
