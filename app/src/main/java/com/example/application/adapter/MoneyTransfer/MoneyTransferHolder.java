package com.example.application.adapter.MoneyTransfer;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

public class MoneyTransferHolder extends RecyclerView.ViewHolder{

    TextView name, trip, price, paymentStatus;
    public MoneyTransferHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.nameView);
        trip = itemView.findViewById(R.id.tripView);
        price = itemView.findViewById(R.id.priceView);
        paymentStatus = itemView.findViewById(R.id.paymentStatus);




    }
}