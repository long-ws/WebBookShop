package dao.common;

import beans.common.Language;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface LanguageDAO {
	Optional<Language> findById(Connection conn, int id) throws SQLException;
	Optional<Language> findByCode(Connection conn, String code) throws SQLException;
	List<Language> findAllActive(Connection conn) throws SQLException;
	List<Language> findAll(Connection conn) throws SQLException;
}
