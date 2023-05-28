package com.example.application.ds;

import com.example.application.Enums.CardStatus;
import java.io.Serializable;


public class Card implements Serializable {
    private int id;
    private String cardNumber;
    private String cvc;
    private String expireDate;
    private CardStatus cardStatus;


    public Card() {
    }

    public Card(int id, String cardNumber, String cvc, String expireDate) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.expireDate = expireDate;
    }

    public Card(String cardNumber, String cvc, String expireDate) {
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.expireDate = expireDate;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardNumber=" + cardNumber +
                ", cvc=" + cvc +
                ", expireDate=" + expireDate +
                ", cardStatus=" + cardStatus +
                '}';
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
