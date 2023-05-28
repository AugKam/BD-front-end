package com.example.application.adapter.Passenger;

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

public class PassengerAdapter extends RecyclerView.Adapter<PassengerHolder> {


    RecyclePassengerViewDataPass recycleViewDataPass;
    private List<User> passengerList;
    private List<User> driverList;
    private List<Trip> tripList;
    private List<PassengerStatus> passengerStatusList;
    private List<Integer> idList;

    public PassengerAdapter(List<Integer> idList ,List<User> passengerList, List<PassengerStatus> passengerStatusList, List<Trip> tripList, List<User> driverList, RecyclePassengerViewDataPass listener) {
        this.passengerList = passengerList;
        this.passengerStatusList = passengerStatusList;
        this.recycleViewDataPass = listener;
        this.driverList = driverList;
        this.tripList = tripList;
        this.idList = idList;
    }

    @NonNull
    @Override
    public PassengerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passenger_list_item, parent, false);
        return new PassengerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerHolder holder, int position) {
        User passenger = passengerList.get(position);
        PassengerStatus status = passengerStatusList.get(position);
        User driver = driverList.get(position);
        Trip trip = tripList.get(position);
        int id = idList.get(position);


        if (status != PassengerStatus.AWAITING)
        {
            holder.reject.setVisibility(View.GONE);
            holder.confirm.setVisibility(View.GONE);
        }

        holder.name.setText(passenger.getName() + " " + passenger.getSurname());
        if (status == PassengerStatus.AWAITING){
            holder.status.setText("Laukiama patvirtinimo");
            holder.status.setTextColor(Color.BLUE);
        }
        if (status == PassengerStatus.REJECTED) {
            holder.status.setText("Atmesta");
            holder.status.setTextColor(Color.RED);
        }
        if (status == PassengerStatus.CONFIRMED) {
            holder.status.setText("Patvirtinta");
            holder.status.setTextColor(Color.GREEN);
        }
        if (status == PassengerStatus.CANCELED) {
            holder.status.setText("Keleivis nusprendė nekeliauti");
            holder.status.setTextColor(Color.RED);
        }
        if (status == PassengerStatus.FINISHED) {
            holder.status.setText("Kelionė baigta");
            holder.status.setTextColor(Color.GREEN);
        }
        if (status == PassengerStatus.DELIVERED) {
            holder.status.setText("Keleivis pristatytas");
            holder.status.setTextColor(Color.GREEN);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleViewDataPass.openPassengerInfo(id, passenger, status, trip, driver);
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassengerStatus changedStatus = PassengerStatus.REJECTED;
                recycleViewDataPass.updatePassengerStatus(id, passenger, changedStatus, trip, driver);
            }
        });
        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassengerStatus changedStatus = PassengerStatus.CONFIRMED;
                recycleViewDataPass.updatePassengerStatus(id, passenger, changedStatus, trip, driver);
            }
        });

    }

    @Override
    public int getItemCount() {
        return passengerList.size();
    }
}
