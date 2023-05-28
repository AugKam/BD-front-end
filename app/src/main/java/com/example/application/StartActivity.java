package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.application.PasswordEncryptDecrypt.AESCrypt;
import com.example.application.ds.User;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserApi;
import com.google.android.material.textfield.TextInputEditText;
import java.util.logging.Level;
import java.util.logging.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StartActivity extends AppCompatActivity {
    Button registerButton;
    Button loginButton;
    TextInputEditText emailField;
    TextInputEditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                User user = new User();
                user.setEmail(email);
                user.setPassword(password);
                try {
                    user.setPassword(AESCrypt.encrypt(user.getPassword()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userApi.checkLogin(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        intent.putExtra("userId", response.body().getId());
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(StartActivity.this, "Vartotojas nerastas", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE,"Error occurred", t);
                    }
                });
            }
        });
    }
}