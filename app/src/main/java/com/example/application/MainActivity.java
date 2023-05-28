package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.application.Fragments.DriverFragment;
import com.example.application.Fragments.MainFragment;
import com.example.application.Fragments.BalanceFragment;
import com.example.application.Fragments.ProfileFragment;
import com.example.application.Fragments.TripsFragment;
import com.example.application.ds.Trip;
import com.example.application.ds.User;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserApi;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import org.jetbrains.annotations.NotNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements TripsFragment.TripsFragmentListener, DriverFragment.DriverFragmentListener {

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        replaceFragment(new MainFragment());
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        View headerView = navigationView.getHeaderView(0);
        TextView fullName = headerView.findViewById(R.id.name);
        TextView email = headerView.findViewById(R.id.email);

        userId = intent.getIntExtra("userId", 0);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        userApi.getUserById(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();

                        fullName.setText(user.getName() + " " + user.getSurname());
                        email.setText(user.getEmail());

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE, "Error occurred getUserById", t);
                    }
                });
        getIntent().putExtra("userId", userId);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                int id = item.getItemId();
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id)
                {
                    case R.id.nav_home:
                        replaceFragment(new MainFragment());break;
                    case R.id.myTrips:
                        replaceFragment(new TripsFragment());break;
                    case R.id.settings:
                        replaceFragment(new ProfileFragment());break;
                    case R.id.paymentMethod:
                        replaceFragment(new BalanceFragment());break;
                    case R.id.nav_logout:
                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                        startActivity(intent);
                        finish();
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    private void replaceFragment (Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void openDriverFragment(Trip trip) {
        DriverFragment driverFragment = new DriverFragment(trip);
        replaceFragment(driverFragment);
    }

    @Override
    public void replaceFragmentToTripsFragment() {
        replaceFragment(new TripsFragment());
    }
}