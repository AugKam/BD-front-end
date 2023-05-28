package com.example.application.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Enums.CardStatus;
import com.example.application.Enums.PaymentMethod;
import com.example.application.R;
import com.example.application.adapter.Card.CardAdapter;
import com.example.application.adapter.Card.RecycleCardViewDataPass;
import com.example.application.ds.Card;
import com.example.application.ds.MoneyTransfer;
import com.example.application.ds.Trip;
import com.example.application.ds.TripUser;
import com.example.application.ds.User;
import com.example.application.retrofit.MoneyTransferApi;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.TripUsersApi;
import com.example.application.retrofit.UserApi;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReservationFragment extends Fragment implements RecycleCardViewDataPass {


    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    TextView tripPrice, usedVirtualMoney, balance, finalPrice, selectedCard, selectedCardText;
    RadioButton cashRadioButton, cardRadioButton;
    TextInputEditText cardNumberField;
    TextInputEditText cvcField;
    TextInputEditText expireDateField;
    TextView cardNumberView, expireDateView, cvcView;
    CheckBox saveCard;
    int userId;
    Trip trip;
    Button useAnotherCardButton, useThisCardButton, reserveSeatButton;
    RecyclerView recyclerView;
    Card paymentCard;

    RetrofitService retrofitService = new RetrofitService();
    UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

    public ReservationFragment() {
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
        View v = inflater.inflate(R.layout.fragment_reservation, container, false);
        this.userId = getActivity().getIntent().getIntExtra("userId", 0);
        this.trip = (Trip) getActivity().getIntent().getSerializableExtra("trip");


        init(v);

        return v;
    }

    private void init(View v) {
        tripPrice = v.findViewById(R.id.tripPrice);
        usedVirtualMoney = v.findViewById(R.id.usedVirtualMoney);
        balance = v.findViewById(R.id.virtualMoney);
        finalPrice = v.findViewById(R.id.finalPrice);
        cashRadioButton = v.findViewById(R.id.cashRadioButton);
        cardRadioButton = v.findViewById(R.id.cardRadioButton);
        selectedCard = v.findViewById(R.id.selectedCard);
        selectedCardText = v.findViewById(R.id.selectedCardText);
        reserveSeatButton = v.findViewById(R.id.reserveSeatButton);

        selectedCard.setVisibility(View.GONE);
        selectedCardText.setVisibility(View.GONE);

        tripPrice.setText(String.valueOf(trip.getPrice()));
        userApi.getUserById(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        balance.setText(String.valueOf(user.getBalance()));
                        countFinalPrice(trip.getPrice(),user.getBalance());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

        cardRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserCards();
            }
        });
        cashRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentCard = null;
                selectedCard.setVisibility(View.GONE);
                selectedCardText.setVisibility(View.GONE);
            }
        });

        reserveSeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelectedPaymentMethod();
            }
        });

    }

    private void getUserCards() {
        userApi.getUserCardsByUserId(userId)
                .enqueue(new Callback<List<Card>>() {
                    @Override
                    public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                        if (response.body().isEmpty())
                        {
                            openAddCardWindow();
                        }
                        else
                        {
                            openCardListWindow();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Card>> call, Throwable t) {

                    }
                });
    }

    public void checkSelectedPaymentMethod(){
        if (!cashRadioButton.isEnabled() && !cashRadioButton.isEnabled())
        {
            reserveTrip(PaymentMethod.VIRTUAL_MONEY);
        }
        else {
            if (cashRadioButton.isChecked()) {
                reserveTrip(PaymentMethod.CASH);
            }

            if (cardRadioButton.isChecked()) {
                if (paymentCard != null) {
                    reserveTrip(PaymentMethod.CARD);
                } else
                    Toast.makeText(getActivity(), "Nepasirinkote atsiskaitymo kortelės", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void countFinalPrice(double tripPrice, double balance) {
        if (tripPrice - balance <=0)
        {
            finalPrice.setText("0");
            usedVirtualMoney.setText("-" + tripPrice);
            cardRadioButton.setEnabled(false);
            cashRadioButton.setEnabled(false);
            cashRadioButton.setChecked(false);
        }
        if (tripPrice - balance > 0)
        {
            finalPrice.setText(String.valueOf(tripPrice - balance));
            usedVirtualMoney.setText(String.valueOf("-" + balance));
        }
    }

    private void openCardListWindow() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getLayoutInflater().inflate(R.layout.card_list_popup, null);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
        recyclerView = view.findViewById(R.id.cardList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        useAnotherCardButton = view.findViewById(R.id.useAnotherCard);



        loadCards();
        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.5);
        dialog.getWindow().setLayout(width, height);

        useAnotherCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openAddCardWindow();
            }
        });
    }

    private void openAddCardWindow() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View view = getLayoutInflater().inflate(R.layout.add_paymentcard_popup, null);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();


        useThisCardButton = view.findViewById(R.id.useThisCardButton);
        saveCard = view.findViewById(R.id.checkBox);
        cardNumberField = view.findViewById(R.id.cardField);
        cvcField = view.findViewById(R.id.cvcField);
        expireDateField = view.findViewById(R.id.expireField);

        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.5);
        dialog.getWindow().setLayout(width, height);
        expireDateField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = s.toString();
                if (current.length() == 2 && start == 1) {
                    expireDateField.setText(current + "/");
                    expireDateField.setSelection(current.length() + 1);
                }
                else if (current.length() == 2 && before == 1) {
                    current = current.substring(0, 1);
                    expireDateField.setText(current);
                    expireDateField.setSelection(current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        useThisCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfCardValid()){
                    long cardNumber = Long.parseLong(cardNumberField.getText().toString());
                    int cvc = Integer.parseInt(cvcField.getText().toString());
                    int expireDate = Integer.parseInt(expireDateField.getText().toString().replace("/", ""));
                    paymentCard = new Card(cardNumberField.getText().toString(), cvcField.getText().toString(), expireDateField.getText().toString().replace("/", ""));
                    if(saveCard.isChecked())
                    {
                        addCard(cardNumber, cvc, expireDate);
                    }
                    selectedCardText.setVisibility(View.VISIBLE);
                    selectedCard.setVisibility(View.VISIBLE);
                    selectedCard.setText("**** " + String.valueOf(cardNumber).substring(String.valueOf(cardNumber).length() - 4));
                    dialog.dismiss();
                }
            }
        });

    }

    private void addCard(long cardNumber, int cvc, int expireDate ){
        List <Card> newCard = new ArrayList<>();
        Card card = new Card();
        card.setCardNumber(String.valueOf(cardNumber));
        card.setCvc(String.valueOf(cvc));
        card.setExpireDate(String.valueOf(expireDate));
        card.setCardStatus(CardStatus.ACTIVE);
        User user = new User();
        user.setId(userId);
        newCard.add(card);
        user.setUserCards(newCard);
        userApi.addCard(user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.isSuccessful()){
                                            Toast.makeText(getActivity(), "Kortelė pridėta", Toast.LENGTH_SHORT).show();
                                        }
                                        else Toast.makeText(getActivity(), "Įvyko klaida", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    public void loadCards() {
        userApi.getUserById(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        if(response.isSuccessful()) {
                            List<Card> userCards = new ArrayList<>();
                            userCards.addAll(user.getUserCards());
                            populateCardList(userCards);
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Logger.getLogger(BalanceFragment.class.getName()).log(Level.SEVERE, "Card Error occurred", t);

                    }
                });

    }

    private void populateCardList(List<Card> userCards) {
        CardAdapter cardAdapter = new CardAdapter(userCards, this);
        recyclerView.setAdapter(cardAdapter);
    }


    @Override
    public void passDataFromAdapter(Card card) {
        this.paymentCard = card;
        selectedCardText.setVisibility(View.VISIBLE);
        selectedCard.setVisibility(View.VISIBLE);
        selectedCard.setText("**** " + String.valueOf(card.getCardNumber()).substring(String.valueOf(card.getCardNumber()).length() - 4));
        dialog.dismiss();

    }

    @Override
    public void openNewWindow(Card card) {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View cardInfoView = getLayoutInflater().inflate(R.layout.card_info_popup, null);
        dialogBuilder.setView(cardInfoView);
        dialog = dialogBuilder.create();
        dialog.show();


        cardNumberView = cardInfoView.findViewById(R.id.cardNumberView);
        expireDateView = cardInfoView.findViewById(R.id.expireDateView);
        cvcView = cardInfoView.findViewById(R.id.cvcView);


        String expireDate = String.valueOf(card.getExpireDate());
        cardNumberView.setText(String.valueOf(card.getCardNumber()));
        expireDateView.setText(expireDate.substring(0, 2) + "/" + expireDate.substring(2));
        cvcView.setText(String.valueOf(card.getCvc()));


        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.8);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.5);
        dialog.getWindow().setLayout(width, height);

    }

    @Override
    public void loadMoneyView(Card card) {

    }

    private boolean checkIfCardValid() {
        if (cardNumberField.getText().toString().length() < 16) {
            Toast.makeText(getActivity(), "Prašome įvesti validų koretlės numerį", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (cvcField.getText().toString().length() < 3)
        {
            Toast.makeText(getActivity(), "Prašome įvesti validų CVC numerį", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (expireDateField.getText().toString().length() < 5)
        {
            Toast.makeText(getActivity(), "Įvesta data netinkama", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            String expireDateTemp = expireDateField.getText().toString().replace("/", "");
            int inputYear, inputMonth, rightNowYear, rightNowMoth;
            Calendar rightNow = Calendar.getInstance();
            inputYear = Integer.valueOf(expireDateTemp.substring(0, 2));
            inputMonth = Integer.valueOf(expireDateTemp.substring(expireDateTemp.length() - 2));
            rightNowMoth = rightNow.get(Calendar.MONTH) +1 ;
            rightNowYear = rightNow.get(Calendar.YEAR) - 2000;

            if (inputYear < rightNowYear || inputYear >= rightNowYear && inputMonth < rightNowMoth || inputMonth >12) {
                Toast.makeText(getActivity(), "Įvesta data netinkama", Toast.LENGTH_SHORT).show();
                return false;
            }
            else
            {
                return true;
            }
        }
    }

    private void reserveTrip(PaymentMethod paymentMethod){
        long startTime = System.currentTimeMillis();
        TripUsersApi tripUsersApi = retrofitService.getRetrofit().create(TripUsersApi.class);
        TripUser tripUser = new TripUser();
        User passenger = new User();
        passenger.setId(userId);
        Trip selectedTrip = new Trip();
        selectedTrip.setId(trip.getId());
        tripUser.setTrip(selectedTrip);
        tripUser.setPassenger(passenger);
        tripUsersApi.createReservation(tripUser)
                .enqueue(new Callback<TripUser>() {
                    @Override
                    public void onResponse(Call<TripUser> call, Response<TripUser> response) {
                        if(response.isSuccessful())
                        {
                            Logger.getLogger(BalanceFragment.class.getName()).log(Level.SEVERE, "Atsakymo laikas, ms: " + String.valueOf(System.currentTimeMillis() - startTime));
                        createMoneyTransfer(response.body(), paymentMethod);}
                        else  Toast.makeText(getActivity(), "Įvyko klaida", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<TripUser> call, Throwable t) {

                    }
                });
    }

    private void createMoneyTransfer(TripUser body, PaymentMethod paymentMethod) {
        MoneyTransferApi moneyTransferApi = retrofitService.getRetrofit().create(MoneyTransferApi.class);
        MoneyTransfer moneyTransfer = new MoneyTransfer();
        moneyTransfer.setTripUser(body);
        moneyTransfer.setPaymentMethod(paymentMethod);
        moneyTransferApi.createMoneyTransfer(moneyTransfer)
                .enqueue(new Callback<MoneyTransfer>() {
                    @Override
                    public void onResponse(Call<MoneyTransfer> call, Response<MoneyTransfer> response) {
                        if (response.isSuccessful())
                        {
                            Toast.makeText(getActivity(), "Užrezervuota, laukite patvirtinimo", Toast.LENGTH_SHORT).show();
                        }
                        else                         Toast.makeText(getActivity(), "Įvyko klaida", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(Call<MoneyTransfer> call, Throwable t) {

                    }
                });
    }
}