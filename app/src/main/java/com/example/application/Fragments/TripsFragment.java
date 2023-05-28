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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Enums.PassengerStatus;
import com.example.application.Enums.PaymentStatus;
import com.example.application.Enums.TripStatus;
import com.example.application.R;
import com.example.application.adapter.DriverTrip.DriverTripAdapter;
import com.example.application.adapter.PassengerTrip.PassengerTripAdapter;
import com.example.application.adapter.DriverTrip.RecycleDriverTripViewDataPass;
import com.example.application.adapter.PassengerTrip.RecyclePassengerTripViewDataPass;
import com.example.application.ds.MoneyTransfer;
import com.example.application.ds.Trip;
import com.example.application.ds.TripUser;
import com.example.application.ds.User;
import com.example.application.retrofit.MoneyTransferApi;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.TripApi;
import com.example.application.retrofit.TripUsersApi;
import com.example.application.retrofit.UserApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripsFragment extends Fragment implements RecyclePassengerTripViewDataPass, RecycleDriverTripViewDataPass, Serializable {


    public interface TripsFragmentListener{
        void openDriverFragment(Trip trip);

    }
    TripsFragmentListener listener;
    TextView fromToTextView;
    TextView priceView, emailView;
    TextView driverView, dateTextView;
    RecyclerView passengerRecyclerView;
    RecyclerView driverRecyclerView;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    Spinner driverSpinner, passengerSpinner;
    Button cancelTripButton, confirmButton;
    int userId;
    final String[] paths = {"Aktyvios", "Neaktyvios"};

    public TripsFragment() {
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
        View v = inflater.inflate(R.layout.fragment_trips, container, false);
        userId = getActivity().getIntent().getIntExtra("userId", 0);

        driverRecyclerView = v.findViewById(R.id.driverTripList);
        driverRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        driverSpinner = v.findViewById(R.id.driverSpinner);

        passengerRecyclerView = v.findViewById(R.id.passengerTripList);
        passengerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        passengerSpinner = v.findViewById(R.id.passengerSpinner);

        RetrofitService retrofitService = new RetrofitService();
        TripApi tripApi = retrofitService.getRetrofit().create(TripApi.class);
        TripUsersApi tripUsersApi = retrofitService.getRetrofit().create(TripUsersApi.class);

        User user = new User();
        user.setId(userId);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        passengerSpinner.setAdapter(adapter);
        passengerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        loadPassengerTrips(tripUsersApi.getActiveTripsByPassenger(user));
                        break;
                    case 1:
                        loadPassengerTrips(tripUsersApi.getInactiveTripsByPassenger(user));
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        driverSpinner.setAdapter(adapter);
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        loadDriverTrips(tripApi.getActiveTripsByTripOwner(user));
                        break;
                    case 1:
                        loadDriverTrips(tripApi.getInactiveTripsByTripOwner(user));;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    private void loadPassengerTrips(Call<List<TripUser>> Api) {
        Api
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

                    }
                });
    }

    private void loadDriverTrips(Call<List<Trip>> Api) {
        Api
                .enqueue(new Callback<List<Trip>>() {
                    @Override
                    public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                        populateDriverListView(response.body());

                    }

                    @Override
                    public void onFailure(Call<List<Trip>> call, Throwable t) {
                        Logger.getLogger(BalanceFragment.class.getName()).log(Level.SEVERE, "LoadTrips Error occurred", t);

                    }
                });

    }

    private void populateDriverListView(List<Trip> tripList) {
            DriverTripAdapter driverTripAdapter = new DriverTripAdapter(tripList, this);
            driverRecyclerView.setAdapter(driverTripAdapter);
    }

    private void populatePassengerListView(List<Integer> idList, List<User> userList, List<PassengerStatus> passengerStatusList, List<Trip> tripList, List<User> driverList) {
            PassengerTripAdapter passengerTripAdapter = new PassengerTripAdapter(idList, userList, passengerStatusList, tripList, driverList, this);
            passengerRecyclerView.setAdapter(passengerTripAdapter);
    }

    @Override
    public void passDataFromAdapter(Trip trip) {

    }

    @Override
    public void passTripFromAdapter(Trip trip) {
        listener.openDriverFragment(trip);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TripsFragment.TripsFragmentListener){
            listener = (TripsFragment.TripsFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void openPassengerTripWindow(int id, User passenger, PassengerStatus status, Trip trip, User driver) {

        RetrofitService retrofitService = new RetrofitService();
        TripUsersApi tripUsersApi = retrofitService.getRetrofit().create(TripUsersApi.class);

        TripUser tripUser = new TripUser();
        tripUser.setTrip(trip);
        tripUser.setDriver(driver);
        tripUser.setPassenger(passenger);
        tripUser.setStatus(status);
        tripUser.setId(id);

        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getLayoutInflater().inflate(R.layout.passenger_trip_popup, null);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        confirmButton = view.findViewById(R.id.endPassengersTrip);
        fromToTextView = view.findViewById(R.id.fromToTextView);
        priceView = view.findViewById(R.id.priceTextView);
        driverView = view.findViewById(R.id.driverTextView);
        cancelTripButton = view.findViewById(R.id.cancelTripButton);
        dateTextView = view.findViewById(R.id.dateTextView);
        emailView = view.findViewById(R.id.emailView);
        confirmButton.setVisibility(View.GONE);

        if (trip.getTripStatus() == TripStatus.FINISHED || trip.getTripStatus() == TripStatus.CANCELED || trip.getTripStatus() == TripStatus.ONGOING ||tripUser.getStatus() == PassengerStatus.REJECTED || tripUser.getStatus() == PassengerStatus.FINISHED ||tripUser.getStatus() == PassengerStatus.CANCELED)
        {
            cancelTripButton.setVisibility(View.GONE);
        }
        if (trip.getTripStatus() == TripStatus.ONGOING && tripUser.getPassengerStatus() == PassengerStatus.DELIVERED)
        {
            confirmButton.setVisibility(View.VISIBLE);
        }


        String date = trip.getDepartureDate();
        date = date.replace("T", " ");

        dateTextView.setText(date.substring(0, date.length() -3));
        fromToTextView.setText(trip.getDeparture() + " - " + trip.getArrival());
        priceView.setText(String.valueOf(trip.getPrice()));
        driverView.setText(trip.getTripOwner().getName() + " " + trip.getTripOwner().getSurname());
        emailView.setText(trip.getTripOwner().getEmail());

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width, height);


        cancelTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tripUser.getStatus() == PassengerStatus.CONFIRMED)
                updateTripInfo(tripUser.getTrip());
                MoneyTransfer moneyTransfer = new MoneyTransfer();
                moneyTransfer.setTripUser(tripUser);
                moneyTransfer.getTripUser().setPassengerStatus(PassengerStatus.CANCELED);
                cancelReservation(moneyTransfer);
                dialog.dismiss();
                loadPassengerTrips(tripUsersApi.getActiveTripsByPassenger(passenger));
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripUser tripUser = new TripUser(id, trip, driver, passenger, PassengerStatus.FINISHED);
                MoneyTransfer moneyTransfer = new MoneyTransfer();
                moneyTransfer.setTripUser(tripUser);
                moneyTransfer.setPaymentStatus(PaymentStatus.COMPLETED);
                finishTripForPassenger(moneyTransfer);
                loadPassengerTrips(tripUsersApi.getActiveTripsByPassenger(passenger));
                dialog.dismiss();

            }
        });
    }

    private void finishTripForPassenger(MoneyTransfer moneyTransfer) {
        RetrofitService retrofitService = new RetrofitService();
        MoneyTransferApi moneyTransferApi = retrofitService.getRetrofit().create(MoneyTransferApi.class);
        moneyTransferApi.finishTripForPassenger(moneyTransfer)
                .enqueue(new Callback<MoneyTransfer>() {
                    @Override
                    public void onResponse(Call<MoneyTransfer> call, Response<MoneyTransfer> response) {
                        if (response.isSuccessful())
                        {
                            Toast.makeText(getActivity(), "Kelionė baigta", Toast.LENGTH_SHORT).show();
                        }
                        else                         Toast.makeText(getActivity(), "Įvyko klaida", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<MoneyTransfer> call, Throwable t) {

                    }
                });
    }

    private void updateTripInfo(Trip trip) {
        RetrofitService retrofitService = new RetrofitService();
        TripApi tripApi = retrofitService.getRetrofit().create(TripApi.class);
        trip.setNumOfSeats(trip.getNumOfSeats() + 1);
        tripApi.saveTrip(trip)
                .enqueue(new Callback<Trip>() {
                    @Override
                    public void onResponse(Call<Trip> call, Response<Trip> response) {
                    }

                    @Override
                    public void onFailure(Call<Trip> call, Throwable t) {
                        Logger.getLogger(BalanceFragment.class.getName()).log(Level.SEVERE, "updateTripInfo Error occurred", t);

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

}