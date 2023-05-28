package com.example.application.adapter.Passenger;

import com.example.application.Enums.PassengerStatus;
import com.example.application.ds.Trip;
import com.example.application.ds.User;

public interface RecyclePassengerViewDataPass {
        void passDataFromAdapter(Trip trip);
        void openPassengerInfo(int id, User passenger, PassengerStatus status, Trip trip, User driver);
        void updatePassengerStatus(int id, User passenger, PassengerStatus status, Trip trip, User driver);

}
