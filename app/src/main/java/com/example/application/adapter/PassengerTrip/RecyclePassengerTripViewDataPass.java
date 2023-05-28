package com.example.application.adapter.PassengerTrip;

import com.example.application.Enums.PassengerStatus;
import com.example.application.ds.Trip;
import com.example.application.ds.User;

public interface RecyclePassengerTripViewDataPass {
        void passDataFromAdapter(Trip trip);
        void openPassengerTripWindow(int id, User passenger, PassengerStatus status, Trip trip, User driver);
}
