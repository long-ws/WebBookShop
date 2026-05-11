package repository;

import java.sql.SQLException;
import java.util.Optional;
import beans.user.User;

public interface UserRepository {
    
    /**
     * Lưu user mới. 
     */
    long save(User user) throws SQLException;
    
    /**
     * Tìm user theo ID
     */
    Optional<User> findById(long id);
}