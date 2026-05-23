package dao.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import beans.common.Language;

public class LanguageDAOImpl implements LanguageDAO {

    private static final String BASE_SELECT = "SELECT id, code, name, description, created_at, is_active FROM language_registry";
    
    private static final String FIND_BY_ID = BASE_SELECT + " WHERE id = ? AND is_active = true";
    private static final String FIND_BY_CODE = BASE_SELECT + " WHERE code = ? AND is_active = true";
    private static final String FIND_ALL_ACTIVE = BASE_SELECT + " WHERE is_active = true ORDER BY name";
    private static final String FIND_ALL = BASE_SELECT + " ORDER BY name";

    private static final String COL_ID = "id";
    private static final String COL_CODE = "code";
    private static final String COL_NAME = "name";
    private static final String COL_DESC = "description";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_IS_ACTIVE = "is_active";

    @Override
    public Optional<Language> findById(Connection conn, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Language> findByCode(Connection conn, String code) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_BY_CODE)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Language> findAllActive(Connection conn) throws SQLException {
        List<Language> languages = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(FIND_ALL_ACTIVE); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                languages.add(mapRow(rs));
            }
        }
        return languages;
    }

    @Override
    public List<Language> findAll(Connection conn) throws SQLException {
        List<Language> languages = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(FIND_ALL); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                languages.add(mapRow(rs));
            }
        }
        return languages;
    }

    private Language mapRow(ResultSet rs) throws SQLException {
        Language language = new Language();
        language.setId(rs.getInt(COL_ID));
        language.setCode(rs.getString(COL_CODE));
        language.setName(rs.getString(COL_NAME));
        language.setDescription(rs.getString(COL_DESC));
        language.setCreatedAt(rs.getTimestamp(COL_CREATED_AT));
        language.setActive(rs.getBoolean(COL_IS_ACTIVE));
        return language;
    }
}