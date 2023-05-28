package com.example.application.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.application.Enums.PassengerStatus;
import com.example.application.Enums.PaymentMethod;
import com.example.application.Enums.PaymentStatus;
import com.example.application.Enums.TripStatus;
import com.example.application.R;
import com.example.application.adapter.Passenger.PassengerAdapter;
import com.example.application.adapter.Passenger.RecyclePassengerViewDataPass;
import com.example.application.ds.MoneyTransfer;
import com.example.application.ds.Trip;
import com.example.application.ds.TripUser;
import com.example.application.ds.User;
import com.example.application.retrofit.MoneyTransferApi;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.TripApi;
import com.example.application.retrofit.TripUsersApi;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverFragment extends Fragment implements RecyclePassengerViewDataPass {


    public interface DriverFragmentListener{
        void replaceFragmentToTripsFragment();
    }

    DriverFragmentListener listener;
    TextView fromToTextView,dateTextView;
    TextView priceView;
    TextView seatsView;
    TextView name, email, paymentMethodView;
    RecyclerView passengersRecyclerView;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    Button removeButton, finishTripButton;
    User passenger;
    User driver;
    Trip trip;
    int tripUsersId;
    PassengerStatus status;

    public DriverFragment(Trip trip){
        this.trip = trip;
    }

    public DriverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_driver, container, false);


        init(trip, v);
        loadPassengers(trip);


        return v;
    }

    private void loadPassengers(Trip trip) {
        RetrofitService retrofitService = new RetrofitService();
        TripUsersApi tripUsersApi = retrofitService.getRetrofit().create(TripUsersApi.class);

        tripUsersApi.getTripUsersByTrip(trip)
                .enqueue(new Callback<List<TripUser>>() {
                    @Override
                    public void onResponse(Call<List<TripUser>> call, Response<List<TripUser>> response) {
                        List <TripUser> tripUsers = response.body();
                        List <User> passengerList = new ArrayList<>();
                        List <PassengerStatus> passengerStatusList = new ArrayList<>();
                        List <User> driverList = new ArrayList<>();
                        List <Trip> tripList = new ArrayList<>();
                        List <Integer> idList = new ArrayList<>();
                        for (TripUser tripTemp : tripUsers) {
                            passengerList.add(tripTemp.getPassenger());
                            passengerStatusList.add(tripTemp.getStatus());
                            driverList.add(tripTemp.getDriver());
                            tripList.add(tripTemp.getTrip());
                            idList.add(tripTemp.getId());

                        }
                        populatePassengerListView(idList, passengerList, passengerStatusList, tripList, driverList);
                    }

                    @Override
                    public void onFailure(Call<List<TripUser>> call, Throwable t) {
                        Logger.getLogger(BalanceFragment.class.getName()).log(Level.SEVERE, "loadPassengers Error occurred", t);

                    }
                });
    }

    private void populatePassengerListView(List<Integer> idList, List<User> userList, List<PassengerStatus> passengerStatusList, List<Trip> tripList, List<User> driverList) {
        PassengerAdapter passengerAdapter = new PassengerAdapter(idList, userList, passengerStatusList, tripList, driverList, this);
        passengersRecyclerView.setAdapter(passengerAdapter);
    }

    private void init(Trip trip, View v) {

        fromToTextView = v.findViewById(R.id.fromToTextView);
        seatsView = v.findViewById(R.id.seatsTextView);
        priceView = v.findViewById(R.id.priceTextView);
        removeButton = v.findViewById(R.id.removeTripButton);
        dateTextView = v.findViewById(R.id.dateTextView);

        passengersRecyclerView = v.findViewById(R.id.passengersList);
        passengersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        seatsView.setText(String.valueOf(trip.getNumOfSeats()));
        priceView.setText(String.valueOf(trip.getPrice()));
        fromToTextView.setText(trip.getDeparture() + " - " + trip.getArrival());
        String date = trip.getDepartureDate();
        date = date.replace("T", " ");
        dateTextView.setText(date.substring(0, date.length() -3));


        if (trip.getTripStatus() == TripStatus.CANCELED || trip.getTripStatus() == TripStatus.FINISHED || trip.getTripStatus() == TripStatus.ONGOING)
        {
            removeButton.setVisibility(View.GONE);
        }
        else {
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trip.setTripStatus(TripStatus.CANCELED);
                    cancelTrip(trip);
                    listener.replaceFragmentToTripsFragment();

                }
            });
        }

    }

    private void cancelTrip(Trip trip) {
        RetrofitService retrofitService = new RetrofitService();
        TripApi tripApi = retrofitService.getRetrofit().create(TripApi.class);
        tripApi.cancelTrip(trip)
                .enqueue(new Callback<Trip>() {
                    @Override
                    public void onResponse(Call<Trip> call, Response<Trip> response) {
                        if (response.isSuccessful())
                        {
                            Toast.makeText(getActivity(), "Kelionė atšaukta", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Įvyko klaida", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Trip> call, Throwable t) {

                    }
                });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DriverFragment.DriverFragmentListener){
            listener = (DriverFragment.DriverFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    private void updatePassengerStatus(TripUser tripUser) {
        RetrofitService retrofitService = new RetrofitService();
        TripUsersApi tripUsersApi = retrofitService.getRetrofit().create(TripUsersApi.class);
        tripUsersApi.saveTripUsers(tripUser)
                .enqueue(new Callback<TripUser>() {
                    @Override
                    public void onResponse(Call<TripUser> call, Response<TripUser> response) {;
                        loadPassengers(trip);

                    }

                    @Override
                    public void onFailure(Call<TripUser> call, Throwable t) {
                        Logger.getLogger(BalanceFragment.class.getName()).log(Level.SEVERE, "loadPassengers Error occurred", t);
                    }
                });
    }




    @Override
    public void passDataFromAdapter(Trip trip) {

    }


    @Override
    public void openPassengerInfo(int id, User passenger, PassengerStatus status, Trip trip, User driver) {
        this.passenger = passenger;
        this.status = status;
        this.trip = trip;
        this.driver = driver;
        this.tripUsersId = id;


        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getLayoutInflater().inflate(R.layout.passenger_info_popup, null);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        initPassengerInfo(view);





        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.5);
        dialog.getWindow().setLayout(width, height);

        finishTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripUser tripUser = new TripUser(id, trip, driver, passenger, PassengerStatus.DELIVERED);
                updatePassengerStatus(tripUser);
                dialog.dismiss();
            }
        });


    }


    @Override
    public void updatePassengerStatus(int id, User passenger, PassengerStatus status, Trip trip, User driver) {
            TripUser tripUser = new TripUser(id, trip, driver, passenger, status);
            updatePassengerStatus(tripUser);
            if (status == PassengerStatus.REJECTED)
            {
                MoneyTransfer moneyTransfer = new MoneyTransfer();
                moneyTransfer.setTripUser(tripUser);
                moneyTransfer.setPaymentStatus(PaymentStatus.CANCELED);
                cancelReservation(moneyTransfer);
            }
        if (status == PassengerStatus.CONFIRMED)
        {
            trip.setNumOfSeats(trip.getNumOfSeats() - 1);
            updateTripInfo(trip);
        }

    }

    private void updateTripInfo(Trip trip) {
        RetrofitService retrofitService = new RetrofitService();
        TripApi tripApi = retrofitService.getRetrofit().create(TripApi.class);
        tripApi.saveTrip(trip)
                .enqueue(new Callback<Trip>() {
                    @Override
                    public void onResponse(Call<Trip> call, Response<Trip> response) {
                        if (response.isSuccessful()) {
                        seatsView.setText(String.valueOf(response.body().getNumOfSeats()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Trip> call, Throwable t) {

                    }
                });
    }

    private void cancelReservation(MoneyTransfer moneyTransfer) {
        RetrofitService retrofitService = new RetrofitService();
        MoneyTransferApi moneyTransferApi = retrofitService.getRetrofit().create(MoneyTransferApi.class);
        moneyTransferApi.cancelReservation(moneyTransfer)
                .enqueue(new Callback<MoneyTransfer>() {
                    @Override
                    public void onResponse(Call<MoneyTransfer> call, Response<MoneyTransfer> response) {

                    }

                    @Override
                    public void onFailure(Call<MoneyTransfer> call, Throwable t) {

                    }
                });


    }


    private void initPassengerInfo(View view) {
        finishTripButton = view.findViewById(R.id.finishTripButton);
        name = view.findViewById(R.id.nameView);
        email = view.findViewById(R.id.emailView);
        paymentMethodView = view.findViewById(R.id.paymentMethodView);

        if (trip.getTripStatus() == TripStatus.ACTIVE)
        {
            finishTripButton.setVisibility(View.GONE);
        }

        if (trip.getTripStatus() == TripStatus.CANCELED || trip.getTripStatus() == TripStatus.FINISHED || status == PassengerStatus.CANCELED || status == PassengerStatus.REJECTED && trip.getTripStatus() == TripStatus.ONGOING)
        {
            finishTripButton.setVisibility(View.GONE);
        }
        if (status == PassengerStatus.REJECTED && trip.getTripStatus() != TripStatus.ONGOING)
        {
            finishTripButton.setVisibility(View.GONE);
        }

        if (trip.getTripStatus() == TripStatus.ONGOING && status == PassengerStatus.CONFIRMED)
        {
            finishTripButton.setVisibility(View.VISIBLE);

        }

        if (status == PassengerStatus.DELIVERED || status == PassengerStatus.FINISHED)
        {
            finishTripButton.setVisibility(View.GONE);
        }



        name.setText(passenger.getName() + " " +passenger.getSurname() );
        email.setText(passenger.getEmail());

        initPaymentMethod();

    }

    private void initPaymentMethod() {
        RetrofitService retrofitService = new RetrofitService();
        MoneyTransferApi moneyTransferApi = retrofitService.getRetrofit().create(MoneyTransferApi.class);
        TripUser tripUser = new TripUser();
        tripUser.setId(tripUsersId);
        moneyTransferApi.getPaymentMethod(tripUser)
                .enqueue(new Callback<MoneyTransfer>() {
                    @Override
                    public void onResponse(Call<MoneyTransfer> call, Response<MoneyTransfer> response) {
                        if (response.body().getPaymentMethod() == PaymentMethod.CARD ||response.body().getPaymentMethod() == PaymentMethod.VIRTUAL_MONEY )
                        paymentMethodView.setText("VIRTUALIAIS PINIGAIS");
                        else {
                            if (response.body().getPaymentMethod() == PaymentMethod.CASH && response.body().getUsedVirtualMoney() == 0)
                                paymentMethodView.setText("GRYNAISIAIS");
                            else
                            {
                                paymentMethodView.setText(response.body().getUsedVirtualMoney()  + " eur. virtualiais pinigais ir " + response.body().getFinalPrice() + " eur. grynaisiais" );
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<MoneyTransfer> call, Throwable t) {

                    }
                });
    }

}