package com.example.application.retrofit;

import com.example.application.ds.Trip;
import com.example.application.ds.TripUser;
import com.example.application.ds.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TripUsersApi {

    @POST("saveTripUsers")
    Call<TripUser> saveTripUsers(@Body TripUser tripUser);

    @POST("getActiveTripsByPassenger")
    Call<List<TripUser>> getActiveTripsByPassenger (@Body User user);

    @POST("getInactiveTripsByPassenger")
    Call<List<TripUser>> getInactiveTripsByPassenger (@Body User user);

    @POST("getTripUsersByTrip")
    Call<List<TripUser>> getTripUsersByTrip (@Body Trip trip);

    @POST("createReservation")
    Call<TripUser> createReservation(@Body TripUser tripUser);



}
