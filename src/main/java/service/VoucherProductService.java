package service;

import beans.VoucherProduct;
import dao.VoucherProductDao;

public class VoucherProductService {
    private final VoucherProductDao dao;
    public VoucherProductService() {
        dao = new VoucherProductDao();
    }
    public long insert(VoucherProduct o){
        return dao.insert(o);
    }
}
