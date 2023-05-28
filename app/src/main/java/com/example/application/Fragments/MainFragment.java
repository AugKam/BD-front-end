package com.example.application.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.application.AddTripActivity;
import com.example.application.FindTripActivity;
import com.example.application.R;

public class MainFragment extends Fragment {

    Button addTripButton;
    Button findTripButton;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    int userId;
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        userId = getActivity().getIntent().getIntExtra("userId", 0);
        addTripButton = v.findViewById(R.id.addTripButton);
        findTripButton = v.findViewById(R.id.findTripButton);

        addTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTripActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        findTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), FindTripActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);

            }
        });

        return v;
    }

}