package com.example.application.retrofit;

import com.example.application.ds.Card;
import com.example.application.ds.Trip;
import com.example.application.ds.User;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApi {

    @GET("getUserById/{id}")
    Call<User> getUserById(@Path("id") int id);

    @GET("getUserCardsByUserId/{id}")
    Call <List<Card>> getUserCardsByUserId(@Path("id") int id);

    @POST("updateUserBalance")
    Call<User> updateUserBalance(@Body User user);

    @POST("saveUser")
    Call<User> saveUser(@Body User user);

    @POST("/login")
    Call<User> checkLogin(@Body User user);

    @POST("addCard")
    Call<User> addCard(@Body User user);




}
