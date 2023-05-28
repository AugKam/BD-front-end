package com.example.application.ds;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private int id;
    private String email;
    private String password;
    private String name;
    private String surname;
    @SerializedName("emailApproved")
    private boolean emailApproved;
    private List<Card> userCards;
    private double balance;

    public User(int id, String email, String password, String name, String surname, boolean isEmailApproved) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.emailApproved = isEmailApproved;
    }

    public User() {
    }

    public User(String email, String password, String name, String surname, boolean emailApproved, List<Card> userCards, double balance) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.emailApproved = emailApproved;
        this.userCards = userCards;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", emailApproved=" + emailApproved +
                ", userCards=" + userCards +
                ", balance=" + balance +
                '}';
    }

    public List<Card> getUserCards() {
        return userCards;
    }

    public void setUserCards(List<Card> userCards) {
        this.userCards = userCards;
    }


    public User(String email) {
        this.email = email;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public boolean isEmailApproved() {
        return emailApproved;
    }

    public void setEmailApproved(boolean emailApproved) {
        this.emailApproved = emailApproved;
    }

}

