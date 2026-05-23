package service.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import beans.User;
import beans.common.Language;
import beans.user.UserProfile;
import constants.SystemConstants;
import dao.common.LanguageDAO;

public class UserLanguageResolver {

	private final LanguageDAO languageDAO;

	public UserLanguageResolver(LanguageDAO languageDAO) {
		this.languageDAO = languageDAO;
	}

	public void resolve(Connection conn, User user, User existing) throws SQLException {
		UserProfile profile = user.getProfile();
		if (profile == null) {
			return;
		}

		Language preferredLanguage = profile.getPreferredLanguage();
		if (preferredLanguage != null && preferredLanguage.getId() > 0) {
			int selectedLanguageId = preferredLanguage.getId();
			Optional<Language> selectedLanguageOptional = languageDAO.findById(conn, selectedLanguageId);
			if (selectedLanguageOptional.isPresent()) {
				Language selectedLanguage = selectedLanguageOptional.get();
				profile.setPreferredLanguage(selectedLanguage);
			}
			return;
		}

		if (existing != null) {
			UserProfile existingProfile = existing.getProfile();
			if (existingProfile != null) {
				Language existingPreferredLanguage = existingProfile.getPreferredLanguage();
				if (existingPreferredLanguage != null) {
					profile.setPreferredLanguage(existingPreferredLanguage);
					return;
				}
			}
		}

		int defaultLanguageId = SystemConstants.DEFAULT_LANGUAGE_ID;
		Optional<Language> defaultLanguageOptional = languageDAO.findById(conn, defaultLanguageId);
		if (defaultLanguageOptional.isPresent()) {
			Language defaultLanguage = defaultLanguageOptional.get();
			profile.setPreferredLanguage(defaultLanguage);
		}
	}
}
