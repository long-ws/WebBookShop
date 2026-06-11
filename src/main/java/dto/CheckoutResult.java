package dto;

import beans.Order;
import beans.Shipment;
import beans.vnpay.Payment;

import java.io.Serializable;

public class CheckoutResult implements Serializable {
    private Order order;
    private Payment payment;
    private Shipment shipment;

    public CheckoutResult() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }
}
