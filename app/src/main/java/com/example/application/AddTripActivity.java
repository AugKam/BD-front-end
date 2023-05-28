package com.example.application;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.application.Constants.Constants;
import com.example.application.Enums.TripStatus;
import com.example.application.Fragments.DatePickerFragment;
import com.example.application.ds.Trip;
import com.example.application.ds.User;
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
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TextView dateView;
    TextView timeView;
    Button createButton;
    TextInputEditText fromField;
    TextInputEditText toField;
    EditText numOfSeats;
    EditText price;
    int hour, minute, userId;
    String departureDate;

    private boolean from;
    private boolean to;

    public static List<Place.Field> fields;
    private String TAG ="AddTripActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        init();
        userId = getIntent().getIntExtra("userId", 0);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkIfEmpty()) {
                    createTrip();
                    finish();
                }
            }
        });

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "Data");
            }
        });

        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showTimePicker();
            }
        });


        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), Constants.apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);


        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        toField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocomplete(false, true);
            }
        });
        fromField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autocomplete(true, false);
            }
        });

    }

    public void showTimePicker(){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                checkIfSelectedTimeValid(selectedHour, selectedMinute);
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTripActivity.this, style, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Pasirinkite laiką");
        timePickerDialog.show();
    }

    private void checkIfSelectedTimeValid(int selectedHour, int selectedMinute) {
        Calendar rightNow = Calendar.getInstance();
        hour = selectedHour;
        minute = selectedMinute;

        if (isItToday() && hour < rightNow.get(Calendar.HOUR_OF_DAY) || hour == rightNow.get(Calendar.HOUR_OF_DAY) && minute < rightNow.get(Calendar.MINUTE) && isItToday())
            Toast.makeText(AddTripActivity.this, "Pasirinkote netinkamą laiką", Toast.LENGTH_SHORT).show();
        else {
            if (hour < 10 && minute < 10)
                timeView.setText("0" + String.valueOf(hour) + ":0" + String.valueOf(minute));
            else {

                if (minute < 10)
                    timeView.setText(String.valueOf(hour) + ":0" + String.valueOf(minute));
                else
                    timeView.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
            }
        }
    }

    public boolean isItToday (){
        Calendar rightNow = Calendar.getInstance();
        String month, year, day;
        year = String.valueOf(rightNow.get(Calendar.YEAR));
        month = String.valueOf(rightNow.get(Calendar.MONTH));
        day = String.valueOf(rightNow.get(Calendar.DAY_OF_MONTH));

        if (rightNow.get(Calendar.MONTH) < 10) month = "0" + String.valueOf(rightNow.get(Calendar.MONTH) + 1);;
        if (rightNow.get(Calendar.DAY_OF_MONTH) < 10) day = "0" + String.valueOf(rightNow.get(Calendar.DAY_OF_MONTH));

        String todayDate = year + "-" +  month + "-" + day;

        if (todayDate.equals(departureDate)) return true;
        else return  false;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String monthNum;
        String dayNum;
        if (month < 10) monthNum = "0" + String.valueOf(month + 1);
        else monthNum = String.valueOf(month + 1);

        if (dayOfMonth < 10) dayNum = "0" + String.valueOf(dayOfMonth);
        else dayNum = String.valueOf(dayOfMonth);

        dateView.setText(String.valueOf(year) + "-" + monthNum + "-" +dayNum);
        departureDate = year + "-" +  monthNum + "-" + dayNum;

        if (isItToday() && hour < c.get(Calendar.HOUR_OF_DAY) || hour == c.get(Calendar.HOUR_OF_DAY) && minute < c.get(Calendar.MINUTE) && isItToday())
        {
            hour = c.get(Calendar.HOUR_OF_DAY) + 1;
            timeView.setText(String.valueOf(hour) + ":00");
        }
    }

    private boolean checkIfEmpty() {
        if (fromField.getText().toString().isEmpty())
        {
            Toast.makeText(AddTripActivity.this, "Nenurodėte išvykimo vietos!", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            if (toField.getText().toString().isEmpty())
            {
                Toast.makeText(AddTripActivity.this, "Nenurodėte atvykimo vietos!", Toast.LENGTH_SHORT).show();
                return true;
            }
            else if (Integer.valueOf(numOfSeats.getText().toString()) <= 0)
            {
                Toast.makeText(AddTripActivity.this, "Nurodykite laisvų vietų skaičių!", Toast.LENGTH_SHORT).show();
                return true;
            }
            else return false;
        }
    }


    public void autocomplete(boolean isFrom, boolean isTo){
        Intent autocompleteIntent = new com.google.android.libraries.places.widget.Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(AddTripActivity.this);
        from = isFrom;
        to = isTo;
        startActivityForResult(autocompleteIntent, Constants.AUTOCOMPLETE_REQUEST_CODE);
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
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createTrip() {
        RetrofitService retrofitService = new RetrofitService();
        TripApi tripApi = retrofitService.getRetrofit().create(TripApi.class);
        User user = new User();
        user.setId(userId);
        departureDate = departureDate + "T" + timeView.getText();
        Trip trip = new Trip();
        trip.setDeparture(fromField.getText().toString());
        trip.setArrival(toField.getText().toString());
        trip.setNumOfSeats(Integer.valueOf(numOfSeats.getText().toString()));
        trip.setDepartureDate(departureDate);
        trip.setTripStatus(TripStatus.ACTIVE);
        trip.setPrice(Double.valueOf(price.getText().toString()));
        trip.setTripOwner(user);
                        tripApi.saveTrip(trip)
                                .enqueue(new Callback<Trip>() {
                                    @Override
                                    public void onResponse(Call<Trip> call, Response<Trip> response) {
                                        if (response.isSuccessful())
                                            Toast.makeText(AddTripActivity.this, "Kelionė pridėta!", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(AddTripActivity.this, "Nepavyko pridėti kelionės!", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(Call<Trip> call, Throwable t) {
                                    }
                                });
    }

    public void init(){
        dateView = findViewById(R.id.dateView);
        timeView = findViewById(R.id.timeView);
        createButton = findViewById(R.id.createButton);
        fromField = findViewById(R.id.fromField);
        toField = findViewById(R.id.toField);
        price = findViewById(R.id.priceField);
        numOfSeats = findViewById(R.id.numOfSeatsField);
        price.setText(String.valueOf(0));
        numOfSeats.setText(String.valueOf(1));
        dateView.setPaintFlags(dateView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        timeView.setPaintFlags(timeView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        dateView.setTextColor(Color.BLUE);
        timeView.setTextColor(Color.BLUE);
        getCurrentDate();
    }

    public void getCurrentDate() {
        Calendar rightNow = Calendar.getInstance();
        String month, year, day;
        year = String.valueOf(rightNow.get(Calendar.YEAR));
        month = String.valueOf(rightNow.get(Calendar.MONTH));
        day = String.valueOf(rightNow.get(Calendar.DAY_OF_MONTH));
        hour = rightNow.get(Calendar.HOUR_OF_DAY) + 1;
        timeView.setText(String.valueOf(hour) + ":00");
        if (rightNow.get(Calendar.MONTH) < 10) month = "0" + String.valueOf(rightNow.get(Calendar.MONTH) + 1);;
        if (rightNow.get(Calendar.DAY_OF_MONTH) < 10) day = "0" + String.valueOf(rightNow.get(Calendar.DAY_OF_MONTH));
        departureDate = year + "-" +  month + "-" + day;
        dateView.setText(departureDate);

    }

}