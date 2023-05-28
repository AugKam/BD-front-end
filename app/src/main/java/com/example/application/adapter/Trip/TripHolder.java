package com.example.application.adapter.Trip;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.application.R;


public class TripHolder extends RecyclerView.ViewHolder {

    TextView departure, arrival, price;
    public TripHolder(@NonNull View itemView) {
        super(itemView);
        departure = itemView.findViewById(R.id.passengerName);
        arrival = itemView.findViewById(R.id.tripArrival);
        price = itemView.findViewById(R.id.passengerStatus);
    }
}
