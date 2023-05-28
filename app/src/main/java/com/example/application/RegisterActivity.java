package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.application.Constants.Constants;
import com.example.application.EmailService.EmailSendingService;
import com.example.application.PasswordEncryptDecrypt.AESCrypt;
import com.example.application.ds.User;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserApi;
import com.google.android.material.textfield.TextInputEditText;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();
    }

    private void initializeComponents() {
        TextInputEditText nameField = findViewById(R.id.nameField);
        TextInputEditText emailField = findViewById(R.id.emailField);
        TextInputEditText surnameField = findViewById(R.id.surnameField);
        TextInputEditText passwordField = findViewById(R.id.passwordField);
        TextInputEditText repeatedPasswordField = findViewById(R.id.repeatedPasswordField);
        Button buttonRegister = findViewById(R.id.registerButton);

        EmailSendingService emailSendingService = new EmailSendingService();
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(nameField.getText());
                String email = String.valueOf(emailField.getText());
                String surname = String.valueOf(surnameField.getText());
                String password = passwordField.getText().toString();
                String repeatedPassword = repeatedPasswordField.getText().toString();


                
                if(checkIfEmptyFields(name, surname, email, password, repeatedPassword)  || !isValidEmail(email) || !isValidPassword(password) ||!doesPasswordsMatch(password, repeatedPassword)){
                    return;
                }

                User user = new User();
                user.setEmail(email);
                user.setName(name.substring(0, 1).toUpperCase() + name.substring(1));
                user.setPassword(password);
                user.setSurname(surname.substring(0, 1).toUpperCase() + surname.substring(1));
                user.setBalance(0);
                user.setEmailApproved(true);


                String code = generateCode();
                emailSendingService.sendEmail(email, code);
                checkCodeWindow(code, user);
            }
        });

    }

    private void checkCodeWindow(String generatedCode, User user) {
        dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
        final View view = getLayoutInflater().inflate(R.layout.confirm_email_popup, null);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        TextInputEditText codeField = view.findViewById(R.id.codeFiled);
        Button checkCodeButton = view.findViewById(R.id.confirmButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);


        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.5);
        dialog.getWindow().setLayout(width, height);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        checkCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (generatedCode.equals(codeField.getText().toString())) {
                    try {
                        user.setPassword(AESCrypt.encrypt(user.getPassword()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    createUser(user);
                    dialog.dismiss();
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Įvestas neteisingas kodas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String generateCode() {
        String AlphaNumericStr = "0123456789";
        StringBuilder s = new StringBuilder(7);
        for (int i=0; i<5; i++) {
            int ch = (int)(AlphaNumericStr.length() * Math.random());
            s.append(AlphaNumericStr.charAt(ch));
        }
        return s.toString();
    }

    private boolean checkIfEmptyFields(String name, String surname, String email, String password, String repeatedPassword) {
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Prašome užpildyti visus laukus", Toast.LENGTH_SHORT).show();
            return true;
        }
        else return false;
    }

    private boolean isValidEmail(String email) {

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return true;
        }
        else{
            Toast.makeText(RegisterActivity.this, "Prašome įvesti validų el. pašto adresą", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(RegisterActivity.this, "Nepatikimas slaptažodis. Slaptažodį turi sudaryti 8 simboliai (bent viena didžioji, mažoji raidė, skaičius, spec. simbolis", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    public boolean doesPasswordsMatch(String password, String repeatedPassword) {
        if (password.equals(repeatedPassword)) {
            return true;
        }
        else{
            Toast.makeText(RegisterActivity.this, "Slaptažodžiai nesutampa", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void createUser(User user){
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
                        userApi.saveUser(user)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful())
                                {
                                    Toast.makeText(RegisterActivity.this, "Registracija baigta!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else Toast.makeText(RegisterActivity.this, "Šis el. pašto adresas jau panaudotas", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE,"Adduser Error occurred", t);
                            }
                        });

    }
}