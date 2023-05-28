package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.application.Fragments.ReservationFragment;
import com.example.application.Fragments.TripInfoFragment;
import com.example.application.ds.Trip;

public class ReserveTripActivity extends AppCompatActivity implements TripInfoFragment.TripInfoFragmentListener{

    int userId;
    Trip trip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_trip);

        this.userId = getIntent().getIntExtra("userId", 0);
        this.trip = (Trip) getIntent().getSerializableExtra("trip");
        getIntent().putExtra("userId", userId);
        getIntent().putExtra("trip", trip);
        replaceFragment(new TripInfoFragment());

    }

    private void replaceFragment (Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void openReservationFragment() {
        replaceFragment(new ReservationFragment());
    }
}