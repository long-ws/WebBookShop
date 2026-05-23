package service;

import beans.common.Language;
import dao.common.LanguageDAO;
import dao.common.LanguageDAOImpl;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LanguageServiceImpl implements LanguageService {
    private final LanguageDAO languageDAO;

    public LanguageServiceImpl() {
        this.languageDAO = new LanguageDAOImpl();
    }

    public LanguageServiceImpl(LanguageDAO languageDAO) {
        this.languageDAO = languageDAO;
    }

    @Override
    public List<Language> getAllActiveLanguages() {
        try (Connection conn = DBConnection.getConnection()) {
            return languageDAO.findAllActive(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Language> findById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            return languageDAO.findById(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
