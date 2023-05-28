package com.example.application.retrofit;

import com.example.application.ds.MoneyTransfer;
import com.example.application.ds.TripUser;
import com.example.application.ds.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MoneyTransferApi {

    @POST("createMoneyTransfer")
    Call <MoneyTransfer> createMoneyTransfer (@Body MoneyTransfer moneyTransfer);

    @POST("cancelReservation")
    Call<MoneyTransfer> cancelReservation(@Body MoneyTransfer moneyTransfer);

    @POST("finishTripForPassenger")
    Call<MoneyTransfer> finishTripForPassenger(@Body MoneyTransfer moneyTransfer);

    @POST("getMoneyTransferByUser")
    Call <List<MoneyTransfer>> getMoneyTransferByUser(@Body User user);

    @POST("getPaymentMethod")
    Call <MoneyTransfer> getPaymentMethod(@Body TripUser tripUser);


}
