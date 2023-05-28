package com.example.application.adapter.Trip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.application.R;
import com.example.application.ds.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripHolder> {

    RecycleTripViewDataPass recycleViewDataPass;
    private List<Trip> tripList;

    public TripAdapter(List<Trip> tripList, RecycleTripViewDataPass listener) {
        this.tripList = tripList;
        this.recycleViewDataPass = listener;
    }

    @NonNull
    @Override
    public TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_list_item, parent, false);
        return new TripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.arrival.setText(trip.getArrival());
        holder.departure.setText(trip.getDeparture());
        holder.price.setText(trip.getPrice().toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleViewDataPass.passDataFromAdapter(trip);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
