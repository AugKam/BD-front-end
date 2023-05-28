package com.example.application.Fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.application.Constants.Constants;
import com.example.application.PasswordEncryptDecrypt.AESCrypt;
import com.example.application.R;
import com.example.application.ds.User;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserApi;
import com.google.android.material.textfield.TextInputEditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {


    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    TextView passwordView, emailView, nameAndSurnameView;
    TextInputEditText passwordField, newPasswordField, repeatedPasswordField;
    Button savePasswordButton;
    int userId;

    public ProfileFragment() {
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
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        this.userId = getActivity().getIntent().getIntExtra("userId", 0);


        passwordView = v.findViewById(R.id.passwordView);
        emailView = v.findViewById(R.id.emailView);
        nameAndSurnameView = v.findViewById(R.id.nameSurnameView);
        passwordView.setTextColor(Color.BLUE);
        passwordView.setPaintFlags(passwordView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        init();

        passwordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPasswordDialog();
            }
        });

        return v;
    }

    private void init() {
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
        userApi.getUserById(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.isSuccessful()){
                            User user = response.body();
                            emailView.setText(user.getEmail());
                            nameAndSurnameView.setText(user.getName() + " " + user.getSurname());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }



    private void openPasswordDialog() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View passwordView = getLayoutInflater().inflate(R.layout.password_popup, null);

        dialogBuilder.setView(passwordView);
        dialog = dialogBuilder.create();
        dialog.show();

        passwordField = passwordView.findViewById(R.id.passwordField);
        newPasswordField = passwordView.findViewById(R.id.newPasswordField);
        repeatedPasswordField = passwordView.findViewById(R.id.repeatedPasswordField);
        savePasswordButton = passwordView.findViewById(R.id.savePasswordButton);

        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidPassword(newPasswordField.getText().toString()) && doesPasswordsMatch(newPasswordField.getText().toString(), repeatedPasswordField.getText().toString()))
                updatePassword(newPasswordField.getText().toString(), passwordField.getText().toString());
            }
        });

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.6);
        dialog.getWindow().setLayout(width, height);
    }

    public boolean doesPasswordsMatch(String password, String repeatedPassword) {
        if (password.equals(repeatedPassword)) {
            return true;
        }
        else{
            Toast.makeText(getActivity(), "Slaptažodžiai nesutampa", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*>?.<,])(?=\\S+$).{" + Constants.PASSWORD_LENGTH+ ",}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        if (matcher.matches()) return true;
        else {
            Toast.makeText(getActivity(), "Nepatikimas slaptažodis. Slaptažodį turi sudaryti 7 simboliai(Bent viena didžioji, mažoji, skaičius, spec. simbolis", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
    private void updatePassword(String newPassword, String oldPassword) {
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
        userApi.getUserById(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        try {
                            if (user.getPassword() == AESCrypt.encrypt(oldPassword)){
                                Toast.makeText(getActivity(), "Įvedėte neteisingą slaptažodį", Toast.LENGTH_SHORT).show();
                                return;
                                }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setPassword(AESCrypt.encrypt(newPassword));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        userApi.saveUser(user)
                                .enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        Toast.makeText(getActivity(), "Slaptažodis atnaujitas", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {

                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }



}