package com.example.application.ds;

import com.example.application.Enums.PaymentMethod;
import com.example.application.Enums.PaymentStatus;
import java.io.Serializable;

public class MoneyTransfer implements Serializable {

    private int id;
    private TripUser tripUser;
    private double finalPrice;
    private double usedVirtualMoney;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;


    public MoneyTransfer(TripUser tripUser, double finalPrice, double usedVirtualMoney, PaymentStatus paymentStatus, PaymentMethod paymentMethod) {
        this.tripUser = tripUser;
        this.finalPrice = finalPrice;
        this.usedVirtualMoney = usedVirtualMoney;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
    }

    public MoneyTransfer() {

    }

    public TripUser getTripUser() {
        return tripUser;
    }

    public void setTripUser(TripUser tripUser) {
        this.tripUser = tripUser;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public double getUsedVirtualMoney() {
        return usedVirtualMoney;
    }

    public void setUsedVirtualMoney(double usedVirtualMoney) {
        this.usedVirtualMoney = usedVirtualMoney;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "MoneyTransfer{" +
                "id=" + id +
                ", finalPrice=" + finalPrice +
                ", usedVirtualMoney=" + usedVirtualMoney +
                ", paymentStatus=" + paymentStatus +
                ", paymentMethod=" + paymentMethod +
                '}';
    }
}
