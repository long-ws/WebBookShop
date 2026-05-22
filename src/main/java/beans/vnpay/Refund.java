package beans.vnpay;

import java.io.Serializable;
import java.sql.Timestamp;

public class Refund implements Serializable {
    private long id;
    private long orderId;
    private long userId;

    private Timestamp createAt;
    private String vnpRequestId;
    private String vnpTransactionType;
    private String vnpTxnRef;
    private double amount;

    private String vnpResponseCode;
    private String bankCode;
    private String vnpTransactionNo;
    private Timestamp payDate;
    private String vnpTransactionStatus;

    public Refund() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getVnpRequestId() {
        return vnpRequestId;
    }

    public void setVnpRequestId(String vnpRequestId) {
        this.vnpRequestId = vnpRequestId;
    }

    public String getVnpTransactionType() {
        return vnpTransactionType;
    }

    public void setVnpTransactionType(String vnpTransactionType) {
        this.vnpTransactionType = vnpTransactionType;
    }

    public String getVnpTxnRef() {
        return vnpTxnRef;
    }

    public void setVnpTxnRef(String vnpTxnRef) {
        this.vnpTxnRef = vnpTxnRef;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    public long getVnpAmount(){
        return (long) amount * 100;
    }

    public String getVnpTransactionNo() {
        return vnpTransactionNo;
    }

    public void setVnpTransactionNo(String vnpTransactionNo) {
        this.vnpTransactionNo = vnpTransactionNo;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createDate) {
        this.createAt = createDate;
    }

    public String getVnpResponseCode() {
        return vnpResponseCode;
    }

    public void setVnpResponseCode(String vnpResponseCode) {
        this.vnpResponseCode = vnpResponseCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Timestamp getPayDate() {
        return payDate;
    }

    public void setPayDate(Timestamp payDate) {
        this.payDate = payDate;
    }

    public String getVnpTransactionStatus() {
        return vnpTransactionStatus;
    }

    public void setVnpTransactionStatus(String vnpTransactionStatus) {
        this.vnpTransactionStatus = vnpTransactionStatus;
    }
}
