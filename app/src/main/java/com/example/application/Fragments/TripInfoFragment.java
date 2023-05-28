package com.example.application.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.application.R;
import com.example.application.ds.Trip;
import com.example.application.ds.TripUser;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.TripUsersApi;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TripInfoFragment extends Fragment {

    public interface TripInfoFragmentListener{
        void openReservationFragment();

    }

    TextView fromToTextView, dateTextView;
    TextView priceView;
    TextView seatsView;
    TextView driverView, emailView;
    Button reserveButton;
    int userId;
    Trip trip;
    TripInfoFragmentListener listener;

    public TripInfoFragment() {
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
        View v = inflater.inflate(R.layout.fragment_trip_info, container, false);
        this.userId = getActivity().getIntent().getIntExtra("userId", 0);
        this.trip = (Trip) getActivity().getIntent().getSerializableExtra("trip");

        init(v);

        return v;
    }

    public void init(View v){
        fromToTextView = v.findViewById(R.id.fromToTextView);
        seatsView = v.findViewById(R.id.seatsTextView);
        priceView = v.findViewById(R.id.priceTextView);
        reserveButton = v.findViewById(R.id.reserveButton);
        driverView = v.findViewById(R.id.driverTextView);
        dateTextView = v.findViewById(R.id.dateTextView);
        emailView = v.findViewById(R.id.emailView);

        seatsView.setText(String.valueOf(trip.getNumOfSeats()));
        priceView.setText(String.valueOf(trip.getPrice()));
        fromToTextView.setText(trip.getDeparture() + " - " + trip.getArrival());
        driverView.setText(trip.getTripOwner().getName() + " " + trip.getTripOwner().getSurname());
        emailView.setText(trip.getTripOwner().getEmail());
        String date = trip.getDepartureDate();
        date = date.replace("T", " ");
        dateTextView.setText(date.substring(0, date.length() -3));

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkIfTUserIsTripOwner())
                {
                    checkIfReservationExist();
                }

            }
        });
    }

    private boolean checkIfTUserIsTripOwner(){
        if(trip.getTripOwner().getId() == userId) {
            Toast.makeText(getActivity(), "Jūs esate šios kelionės vairuotojas", Toast.LENGTH_SHORT).show();
            return true;
        }
        else return false;
    }

    private void checkIfReservationExist() {
            RetrofitService retrofitService = new RetrofitService();
            TripUsersApi tripUsersApi = retrofitService.getRetrofit().create(TripUsersApi.class);
            tripUsersApi.getTripUsersByTrip(trip)
                    .enqueue(new Callback<List<TripUser>>() {
                        @Override
                        public void onResponse(Call<List<TripUser>> call, Response<List<TripUser>> response) {
                            if (response.isSuccessful()) {
                                boolean checkIfExist = false;
                                List<TripUser> tripUsers = response.body();
                                for (TripUser temp : tripUsers) {
                                    if (temp.getPassenger().getId() == userId) {
                                        Toast.makeText(getActivity(), "Į šią kelionę jau registravotes", Toast.LENGTH_SHORT).show();
                                        checkIfExist = true;
                                    }
                                }
                                if (!checkIfExist) listener.openReservationFragment();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<TripUser>> call, Throwable t) {

                        }
                    });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TripInfoFragment.TripInfoFragmentListener){
            listener = (TripInfoFragment.TripInfoFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}