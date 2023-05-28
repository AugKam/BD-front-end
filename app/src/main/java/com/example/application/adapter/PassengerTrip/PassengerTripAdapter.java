package com.example.application.adapter.PassengerTrip;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.application.Enums.PassengerStatus;
import com.example.application.R;
import com.example.application.ds.Trip;
import com.example.application.ds.User;


import java.util.List;

public class PassengerTripAdapter extends RecyclerView.Adapter<PassengerTripHolder> {

    RecyclePassengerTripViewDataPass recycleViewDataPass;
    private List<User> passengerList;
    private List<User> driverList;
    private List<Trip> tripList;
    private List<PassengerStatus> passengerStatusList;
    private List<Integer> idList;

    public PassengerTripAdapter(List<Integer> idList , List<User> passengerList, List<PassengerStatus> passengerStatusList, List<Trip> tripList, List<User> driverList, RecyclePassengerTripViewDataPass listener) {
        this.passengerList = passengerList;
        this.passengerStatusList = passengerStatusList;
        this.recycleViewDataPass = listener;
        this.driverList = driverList;
        this.tripList = tripList;
        this.idList = idList;
    }

    @NonNull
    @Override
    public PassengerTripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passenger_trip_list_item, parent, false);
        return new PassengerTripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerTripHolder holder, int position) {
        Trip trip = tripList.get(position);
        PassengerStatus status = passengerStatusList.get(position);
        User passenger = passengerList.get(position);
        User driver = driverList.get(position);
        int id = idList.get(position);

        holder.arrival.setText(trip.getArrival());
        holder.departure.setText(trip.getDeparture());
        holder.price.setText(trip.getPrice().toString());

        if (status == PassengerStatus.AWAITING){
            holder.approval.setText("Laukiama patvirtinimo");
            holder.approval.setTextColor(Color.BLUE);
        }
        if (status == PassengerStatus.REJECTED) {
            holder.approval.setText("Atmesta vairuotojo");
            holder.approval.setTextColor(Color.RED);
        }
        if (status == PassengerStatus.CONFIRMED) {
            holder.approval.setText("Patvirtinta");
            holder.approval.setTextColor(Color.GREEN);
        }
        if (status == PassengerStatus.CANCELED) {
            holder.approval.setText("Šią kelionė atšaukėte");
            holder.approval.setTextColor(Color.RED);
        }
        if (status == PassengerStatus.FINISHED) {
            holder.approval.setText("Kelionė baigta");
            holder.approval.setTextColor(Color.GREEN);
        }
        if (status == PassengerStatus.DELIVERED) {
            holder.approval.setText("Reik patvirtinti");
            holder.approval.setTextColor(Color.GREEN);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleViewDataPass.openPassengerTripWindow(id, passenger, status, trip, driver);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
