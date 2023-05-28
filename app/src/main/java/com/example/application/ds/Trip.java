package com.example.application.ds;

import com.example.application.Enums.TripStatus;

import java.io.Serializable;

public class Trip implements Serializable  {


    private int id;
    private String departure;
    private String arrival;
    private String departureDate;
    private Double price;
    private int numOfSeats;
    private User tripOwner;
    private TripStatus tripStatus;



    public Trip(int id, String departure, String arrival, String departureDate, Double price, int numOfSeats, User tripOwner) {
        this.id = id;
        this.departure = departure;
        this.arrival = arrival;
        this.departureDate = departureDate;
        this.price = price;
        this.numOfSeats = numOfSeats;
        this.tripOwner = tripOwner;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", price=" + price +
                ", numOfSeats=" + numOfSeats +
                ", tripOwner=" + tripOwner +
                ", tripStatus=" + tripStatus +
                '}';
    }

    public Trip() {

    }


    public String getDepartureDate() {
        return departureDate;
    }

    public TripStatus getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getNumOfSeats() {
        return numOfSeats;
    }

    public void setNumOfSeats(int numOfSeats) {
        this.numOfSeats = numOfSeats;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }


    public User getTripOwner() {
        return tripOwner;
    }

    public void setTripOwner(User tripOwner) {
        this.tripOwner = tripOwner;
    }

}
