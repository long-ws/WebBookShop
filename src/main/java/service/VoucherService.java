package service;

import beans.Voucher;
import dao.VoucherDao;

import java.sql.SQLException;
import java.util.List;

public class VoucherService {
    private final VoucherDao voucherDao;

    public VoucherService() {
        this.voucherDao = new VoucherDao();
    }

    public boolean createVoucher(Voucher voucher) {
        return voucherDao.createVoucher(voucher);
    }
    public int count() {
        return voucherDao.count();
    }

    public List<Voucher> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return voucherDao.getOrderedPart(limit, offset, orderBy, orderDir);
    }

    public Voucher getVoucherById(long vId) {
        return voucherDao.getVoucherById(vId);
    }

    public boolean deleteVoucher(long vId) {
        return voucherDao.deleteVoucher(vId);
    }

    public boolean updateVoucher(Voucher voucher) {
        return voucherDao.updateVoucher(voucher);
    }
    public Voucher getVoucherWithRelations(long vId){
        return voucherDao.getVoucherWithRelations(vId);
    }
}
