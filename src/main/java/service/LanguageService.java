package service;

import java.util.List;
import java.util.Optional;

import beans.common.Language;
import exception.BusinessException;

public interface LanguageService {
	List<Language> getAllActiveLanguages() throws BusinessException;

	Optional<Language> findById(int id) throws BusinessException;
}	