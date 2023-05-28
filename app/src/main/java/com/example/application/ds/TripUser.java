package com.example.application.ds;

import com.example.application.Enums.PassengerStatus;

import java.io.Serializable;

public class TripUser implements Serializable {

    private int id;
    private Trip trip;
    private User driver;
    private User passenger;
    private PassengerStatus passengerStatus;

    public TripUser() {

    }


    public PassengerStatus getStatus() {
        return passengerStatus;
    }

    public void setStatus(PassengerStatus status) {
        this.passengerStatus = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TripUser(int id, Trip trip, User driver, User passenger, PassengerStatus passengerStatus) {
        this.id = id;
        this.trip = trip;
        this.driver = driver;
        this.passenger = passenger;
        this.passengerStatus = passengerStatus;
    }


    public PassengerStatus getPassengerStatus() {
        return passengerStatus;
    }

    public void setPassengerStatus(PassengerStatus passengerStatus) {
        this.passengerStatus = passengerStatus;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    @Override
    public String toString() {
        return "TripUsers{" +
                "id=" + id +
                ", trip=" + trip +
                ", driver=" + driver +
                ", passenger=" + passenger +
                ", status=" + passengerStatus +
                '}';
    }
}
