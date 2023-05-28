package com.example.application.retrofit;

import com.example.application.ds.Trip;
import com.example.application.ds.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TripApi {

    @POST("saveTrip")
    Call<Trip> saveTrip(@Body Trip trip);

    @POST("getAllActiveTrips")
    Call<List<Trip>> allActiveTrips(@Body Trip tripFilter);

    @POST("/filterTrips")
    Call<List<Trip>> filterTripsByDepartureAndArrival(@Body Trip trip);

    @POST("/filterTripsByDeparture")
    Call<List<Trip>> filterTripsByDeparture(@Body Trip trip);

    @POST("/filterTripsByArrival")
    Call<List<Trip>> filterTripsByArrival(@Body Trip trip);

    @POST("getActiveTripsByTripOwner")
    Call <List<Trip>> getActiveTripsByTripOwner(@Body User user);

    @POST("getInactiveTripsByTripOwner")
    Call <List<Trip>> getInactiveTripsByTripOwner(@Body User user);

    @POST("cancelTrip")
    Call <Trip> cancelTrip(@Body Trip trip);
}
