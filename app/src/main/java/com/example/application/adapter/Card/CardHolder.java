package com.example.application.adapter.Card;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

public class CardHolder  extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView cardStatusView, cardNumberView;
    public CardHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        cardStatusView = itemView.findViewById(R.id.cardStatusView);
        cardNumberView = itemView.findViewById(R.id.cardNumberView);




    }
}