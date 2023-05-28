package com.example.application.adapter.Passenger;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

public class PassengerHolder extends RecyclerView.ViewHolder{

    TextView name, status;
    ImageButton confirm, reject;
    public PassengerHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.passengerName);
        status = itemView.findViewById(R.id.passengerStatus);
        confirm = itemView.findViewById(R.id.imageButtonConfirm);
        reject = itemView.findViewById(R.id.imageButtonReject);
    }
}