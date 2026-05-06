package service;

import java.sql.SQLException;
import java.util.List;
import dao.DAO;

public abstract class Service<T, D extends DAO<T>> implements DAO<T> {
	protected final D dao;

	public Service(D dao) {
		this.dao = dao;
	}

	@Override
	public long insert(T t) throws SQLException {
		return dao.insert(t);
	}

	@Override
	public void update(T t) throws SQLException {
		dao.update(t);
	}

	@Override
	public void delete(long id) throws SQLException {
		dao.delete(id);
	}

	@Override
	public T getById(long id) {
		return dao.getById(id);
	}

	@Override
	public List<T> getAll() {
		return dao.getAll();
	}

	@Override
	public List<T> getPart(int limit, int offset) {
		return dao.getPart(limit, offset);
	}

	@Override
	public List<T> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		return dao.getOrderedPart(limit, offset, orderBy, orderDir);
	}
}
