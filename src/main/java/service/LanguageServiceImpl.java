package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import beans.common.Language;
import dao.common.LanguageDAO;
import dao.common.LanguageDAOImpl;
import exception.BusinessException;
import utils.DbTransaction;
import utils.TransactionCallback;

public class LanguageServiceImpl implements LanguageService {
	private final LanguageDAO languageDAO;

	private static final String ERR_LANG_FETCH_ALL = "Không thể tải danh sách ngôn ngữ lúc này.";
	private static final String ERR_LANG_FETCH_ONE = "Không thể tải thông tin chi tiết của ngôn ngữ yêu cầu.";

	public LanguageServiceImpl() {
		this(new LanguageDAOImpl());
	}

	public LanguageServiceImpl(LanguageDAO languageDAO) {
		this.languageDAO = languageDAO;
	}

	@Override
	public List<Language> getAllActiveLanguages() throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<List<Language>>() {
				@Override
				public List<Language> doInTransaction(Connection conn) throws SQLException {
					return languageDAO.findAllActive(conn);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException(ERR_LANG_FETCH_ALL);
		}
	}

	@Override
	public Optional<Language> findById(int id) throws BusinessException {
		try {
			return DbTransaction.run(new TransactionCallback<Optional<Language>>() {
				@Override
				public Optional<Language> doInTransaction(Connection conn) throws SQLException {
					return languageDAO.findById(conn, id);
				}
			});
		} catch (SQLException e) {
			throw new BusinessException(ERR_LANG_FETCH_ONE);
		}
	}
}