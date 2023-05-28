package com.example.application.adapter.DriverTrip;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.application.Enums.TripStatus;
import com.example.application.R;
import com.example.application.ds.Trip;
import java.util.List;

public class DriverTripAdapter extends RecyclerView.Adapter<DriverTripHolder> {


    RecycleDriverTripViewDataPass recycleViewDataPass;
    private List<Trip> tripList;

    public DriverTripAdapter(List<Trip> tripList, RecycleDriverTripViewDataPass listener) {
        this.tripList = tripList;
        this.recycleViewDataPass = listener;

    }

    @NonNull
    @Override
    public DriverTripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_list_item, parent, false);
        return new DriverTripHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DriverTripHolder holder, int position) {
        Trip trip = tripList.get(position);

        holder.arrival.setText(trip.getArrival());
        holder.departure.setText(trip.getDeparture());
        holder.price.setText(trip.getPrice().toString());

        if (trip.getTripStatus() == TripStatus.ACTIVE){
            holder.tripStatus.setText("Būsima");
            holder.tripStatus.setTextColor(Color.BLUE);
        }
        if (trip.getTripStatus() == TripStatus.ONGOING){
            holder.tripStatus.setText("Vykstanti");
            holder.tripStatus.setTextColor(Color.GREEN);
        }
        if (trip.getTripStatus() == TripStatus.CANCELED){
            holder.tripStatus.setText("Atšaukta");
            holder.tripStatus.setTextColor(Color.RED);
        }
        if (trip.getTripStatus() == TripStatus.FINISHED){
            holder.tripStatus.setText("Kelionė baigta");
            holder.tripStatus.setTextColor(Color.GREEN);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleViewDataPass.passTripFromAdapter(trip);
            }
        });
    }


    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
