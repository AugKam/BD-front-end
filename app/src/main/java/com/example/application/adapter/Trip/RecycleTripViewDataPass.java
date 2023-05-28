package com.example.application.adapter.Trip;


import com.example.application.ds.Trip;

public interface RecycleTripViewDataPass {
        void passDataFromAdapter(Trip trip);
        void openNewWindow(Trip trip);
}
