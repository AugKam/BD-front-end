package com.example.application.adapter.Card;

import com.example.application.ds.Card;

public interface RecycleCardViewDataPass {
        void passDataFromAdapter(Card card);
        void openNewWindow(Card card);
        void loadMoneyView(Card card);
}

