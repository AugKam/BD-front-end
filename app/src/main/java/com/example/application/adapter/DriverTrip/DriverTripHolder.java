package com.example.application.adapter.DriverTrip;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.application.R;
public class DriverTripHolder extends RecyclerView.ViewHolder {

    TextView departure, arrival, price, tripStatus;
    public DriverTripHolder(@NonNull View itemView) {
        super(itemView);
        departure = itemView.findViewById(R.id.passengerName);
        arrival = itemView.findViewById(R.id.tripArrival);
        price = itemView.findViewById(R.id.passengerStatus);
        tripStatus = itemView.findViewById(R.id.tripStatus);
    }
}
