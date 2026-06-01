package service;

import beans.common.Language;

import java.util.List;
import java.util.Optional;

public interface LanguageService {
    List<Language> getAllActiveLanguages();
    Optional<Language> findById(int id);
}
