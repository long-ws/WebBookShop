package dao;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {

    long insert(T t) throws SQLException;

    void update(T t) throws SQLException;

    void delete(long id) throws SQLException;

    T getById(long id);

    List<T> getAll();

    List<T> getPart(int limit, int offset);

    List<T> getOrderedPart(int limit, int offset, String orderBy, String orderDir);
}
