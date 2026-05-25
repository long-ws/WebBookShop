package service;

import java.sql.SQLException;
import java.util.List;

import beans.ShippingMethod;
import dao.ShippingMethodDAO;

public class ShippingMethodService {

	private final ShippingMethodDAO methodDAO;

	public ShippingMethodService() {
		this.methodDAO = new ShippingMethodDAO();
	}

	public long insert(ShippingMethod method) throws SQLException {
		return methodDAO.insert(method);
	}

	public void update(ShippingMethod method) throws SQLException {
		methodDAO.update(method);
	}

	public void delete(long id) throws SQLException {
		methodDAO.delete(id);
	}

	public ShippingMethod getById(long id) {
		return methodDAO.getById(id);
	}

	public ShippingMethod getByProviderType(String providerType) {
		return methodDAO.getByProviderType(providerType);
	}

	public List<ShippingMethod> getAll() {
		return methodDAO.getAll();
	}

	public List<ShippingMethod> getAllActive() {
		return methodDAO.getAllActive();
	}

	public List<ShippingMethod> getPart(int limit, int offset) {
		return methodDAO.getPart(limit, offset);
	}

	public List<ShippingMethod> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
		return methodDAO.getOrderedPart(limit, offset, orderBy, orderDir);
	}

	public int count() {
		return methodDAO.count();
	}

	public boolean updateStatus(long id, int status) {
		return methodDAO.updateStatus(id, status);
	}

	public boolean enableMethod(long id) {
		return methodDAO.updateStatus(id, 1);
	}

	public boolean disableMethod(long id) {
		return methodDAO.updateStatus(id, 0);
	}
}
