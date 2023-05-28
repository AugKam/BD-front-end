package com.example.application.adapter.PassengerTrip;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

public class PassengerTripHolder extends RecyclerView.ViewHolder {

    TextView departure, arrival, price, approval;

    public PassengerTripHolder(@NonNull View itemView) {
        super(itemView);
        departure = itemView.findViewById(R.id.passengerName);
        arrival = itemView.findViewById(R.id.tripArrival);
        price = itemView.findViewById(R.id.passengerStatus);
        approval = itemView.findViewById(R.id.tripApproval);

    }
}
