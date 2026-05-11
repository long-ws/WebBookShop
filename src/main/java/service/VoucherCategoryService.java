package service;

import beans.VoucherCategory;
import dao.VoucherCategoryDao;

public class VoucherCategoryService {
    private final VoucherCategoryDao dao;
    public VoucherCategoryService() {
        dao = new VoucherCategoryDao();
    }
    public long insert(VoucherCategory o) {
        return dao.insert(o);
    }
}
