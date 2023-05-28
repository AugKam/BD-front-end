package com.example.application.adapter.Card;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.application.Enums.CardStatus;
import com.example.application.R;
import com.example.application.ds.Card;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardHolder>{

    RecycleCardViewDataPass recycleViewDataPass;

    private List<Card> cardList;

    public CardAdapter(List<Card> cardList, RecycleCardViewDataPass listener) {

        this.cardList = cardList;
        this.recycleViewDataPass = listener;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_item, parent, false);
        return new CardHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, int position) {
        Card card = cardList.get(position);
        String cardNumber = String.valueOf(card.getCardNumber());


        holder.cardNumberView.setText("**** " + cardNumber.substring(cardNumber.length() - 4));
        holder.cardNumberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleViewDataPass.passDataFromAdapter(card);
            }
        });

        if (card.getCardStatus() == CardStatus.INACTIVE) {
            holder.cardStatusView.setText("Nebegaliojanti");
            holder.cardStatusView.setTextColor(Color.RED);
        }


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleViewDataPass.openNewWindow(card);
            }

        });



    }



    @Override
    public int getItemCount() {
        return cardList.size();
    }
}
