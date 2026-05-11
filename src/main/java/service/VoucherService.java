package service;

import beans.Voucher;
import dao.VoucherDao;

import java.sql.SQLException;
import java.util.List;

public class VoucherService{
    private final VoucherDao voucherDao;

    public VoucherService() {
        this.voucherDao = new VoucherDao();
    }
    public long insert(Voucher voucher) throws SQLException {
        return voucherDao.insert(voucher);
    }

    public void update(Voucher voucher) throws SQLException {
        voucherDao.update(voucher);
    }

    public void delete(long id) throws SQLException {
        voucherDao.delete(id);
    }

    public Voucher getById(long id) {
        return voucherDao.getById(id);
    }

    public List<Voucher> getAll() {
        return voucherDao.getAll();
    }

    public List<Voucher> getPart(int limit, int offset) {
        return voucherDao.getPart(limit, offset);
    }

    public List<Voucher> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return voucherDao.getOrderedPart(limit, offset, orderBy, orderDir);
    }

    public int count() {
        return voucherDao.count();
    }
}
