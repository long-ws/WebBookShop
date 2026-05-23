package service;

import beans.vnpay.Refund;
import dao.RefundDao;

public class RefundService {
    private final RefundDao dao =  new RefundDao();
    public long createRefund(Refund r) {
        return dao.createRefund(r);
    }
    public boolean updateRefundResult(Refund r){
        return dao.updateRefundResult(r);
    }
}
