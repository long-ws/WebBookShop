package service;

import beans.vnpay.Payment;
import dao.PaymentDao;
import java.sql.SQLException;

public class PaymentService {
    private final PaymentDao dao = new PaymentDao();

    public boolean createPayment(Payment p) {
        try {
            long id = dao.createPayment(p);
            return id > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Payment getInitPayment(String vnpTxnRef){
        return dao.getInitPayment(vnpTxnRef);
    }
    public void updatePaymentResult(Payment p){
        dao.updatePaymentResult(p);
    }
    public Payment getPaymentByOrderId(long oId){
        return dao.getPaymentByOrderId(oId);
    }
    public boolean isPaymentExpired(long oId){
        return dao.isPaymentExpired(oId);
    }
}