package com.example.application;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.application.Constants.Constants;
import com.example.application.Enums.TripStatus;
import com.example.application.adapter.Trip.RecycleTripViewDataPass;
import com.example.application.adapter.Trip.TripAdapter;
import com.example.application.ds.Trip;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.TripApi;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindTripActivity extends AppCompatActivity implements RecycleTripViewDataPass {

    RecyclerView recyclerView;
    TextInputEditText fromField;
    TextInputEditText toField;
    Button filterButton;
    String departure;
    String arrival;
    int userId;
    private boolean from;
    private boolean to;

    public static List<Place.Field> fields;
    private String TAG ="FindTripActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trip);

        fromField = findViewById(R.id.fromField);
        toField = findViewById(R.id.toField);
        filterButton = findViewById(R.id.filterButton);

        RetrofitService retrofitService = new RetrofitService();
        TripApi tripApi = retrofitService.getRetrofit().create(TripApi.class);

        this.userId = getIntent().getIntExtra("userId", 0);

        recyclerView = findViewById(R.id.tripList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Trip tripFilter = new Trip();
        tripFilter.setTripStatus(TripStatus.ACTIVE);
        filterTrips(tripApi.allActiveTrips(tripFilter));

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                departure = fromField.getText().toString();
                arrival = toField.getText().toString();

                Trip trip = new Trip();
                trip.setDeparture(fromField.getText().toString());
                trip.setArrival(toField.getText().toString());
                trip.setTripStatus(TripStatus.ACTIVE);

                if (departure.isEmpty() && arrival.isEmpty()) filterTrips(tripApi.allActiveTrips(trip));
                if (!departure.isEmpty() && !arrival.isEmpty()) filterTrips(tripApi.filterTripsByDepartureAndArrival(trip));
                if (departure.isEmpty() && !arrival.isEmpty()) filterTrips(tripApi.filterTripsByArrival(trip));
                if (!departure.isEmpty() && arrival.isEmpty()) filterTrips(tripApi.filterTripsByDeparture(trip));
            }
        });

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), Constants.apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);


        fromField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocomplete(true, false);
            }
        });

        toField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocomplete(false, true);
            }
        });


    }

    public void autocomplete(boolean isFrom, boolean isTo){
        Intent intent = new com.google.android.libraries.places.widget.Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(FindTripActivity.this);
        from = isFrom;
        to = isTo;
        startActivityForResult(intent, Constants.AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = com.google.android.libraries.places.widget.Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                if (from)
                    fromField.setText(place.getName());
                if (to)
                    toField.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = com.google.android.libraries.places.widget.Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                if (from)
                    fromField.setText("");
                if (to)
                    toField.setText("");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void filterTrips(Call<List<Trip>> listCall) {
            listCall.enqueue(new Callback<List<Trip>>() {
                @Override
                public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                    populateListView(response.body());
                }
                @Override
                public void onFailure(Call<List<Trip>> call, Throwable t) {
                    Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE, "Error occurred filterTrips", t);
                }
            });

    }

    private void populateListView(List<Trip> tripList) {
        TripAdapter tripAdapter = new TripAdapter(tripList, this);
        recyclerView.setAdapter(tripAdapter);

    }
    @Override
    protected void onResume() {
        RetrofitService retrofitService = new RetrofitService();
        TripApi tripApi = retrofitService.getRetrofit().create(TripApi.class);
        super.onResume();

        Trip trip = new Trip();
        trip.setTripStatus(TripStatus.ACTIVE);
        filterTrips(tripApi.allActiveTrips(trip));
    }


    @Override
    public void openNewWindow(Trip trip) {

    }
    @Override
    public void passDataFromAdapter(Trip trip) {
        Intent intent = new Intent(FindTripActivity.this, ReserveTripActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("trip", trip);
        startActivity(intent);
    }
}