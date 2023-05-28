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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Enums.CardStatus;
import com.example.application.Enums.PaymentMethod;
import com.example.application.R;
import com.example.application.RegisterActivity;
import com.example.application.adapter.Card.CardAdapter;
import com.example.application.adapter.Card.RecycleCardViewDataPass;
import com.example.application.adapter.MoneyTransfer.MoneyTransferAdapter;
import com.example.application.adapter.MoneyTransfer.RecycleMoneyTransferViewDataPass;
import com.example.application.ds.Card;
import com.example.application.ds.MoneyTransfer;
import com.example.application.ds.TripUser;
import com.example.application.ds.User;
import com.example.application.retrofit.MoneyTransferApi;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserApi;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SplittableRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BalanceFragment extends Fragment implements RecycleCardViewDataPass, RecycleMoneyTransferViewDataPass {



    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    Button addCardButton;

    int userId;
    TextInputEditText cardNumberField;
    TextInputEditText cvcField;
    TextInputEditText expireDateField, accountName, accountNumber, moneyField;
    TextView cardNumberView, expireDateView, cvcView, moneyView, virtualMoneyView, balanceView;
    Button cancelButton, openCashOutWindowButton, cashOutButton;
    RecyclerView recyclerView, transferRecyclerView;
    double balance;
    RetrofitService retrofitService = new RetrofitService();
    UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);



    public BalanceFragment() {
        // Required empty public constructor
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
        View v = inflater.inflate(R.layout.fragment_balance, container, false);
        addCardButton = v.findViewById(R.id.button);
        openCashOutWindowButton = v.findViewById(R.id.openCashOutWindowButton);

        moneyView = v.findViewById(R.id.moneyView);
        virtualMoneyView = v.findViewById(R.id.virtualMoneyView);
        userId = getActivity().getIntent().getIntExtra("userId", 0);
        recyclerView = v.findViewById(R.id.cardList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loadCards();


        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCardWindow();
            }
        });


        moneyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoneyTransferView();
            }
        });

        openCashOutWindowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCashOutWindow();
            }
        });


        return v;
    }

    private void openCashOutWindow() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View cashOutView = getLayoutInflater().inflate(R.layout.cash_out_popup, null);

        dialogBuilder.setView(cashOutView);
        dialog = dialogBuilder.create();
        dialog.show();

        balanceView = cashOutView.findViewById(R.id.balanceView);
        cashOutButton = cashOutView.findViewById(R.id.cashOutButton);
        accountName = cashOutView.findViewById(R.id.accountNameField);
        accountNumber = cashOutView.findViewById(R.id.accountNumberField);
        moneyField = cashOutView.findViewById(R.id.moneyField);



        balanceView.setText(balanceView.getText() + " " + String.valueOf(balance) + " eur.");
        cashOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkIfCashOutIsValid())
                cashOut();
            }
        });


        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.5);
        dialog.getWindow().setLayout(width, height);
    }

    private boolean checkIfCashOutIsValid() {
        if (accountName.getText().toString().length() < 3)
        {
            Toast.makeText(getActivity(), "Prašome įvesti gavėją", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (accountNumber.getText().toString().length() < 1)
        {
            Toast.makeText(getActivity(), "Prašome įvesti validų sąskaitos numerį", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (moneyField.getText().toString().length() == 0 || Integer.valueOf(moneyField.getText().toString()) == 0)
        {
            Toast.makeText(getActivity(), "Nenurodėte išsigryninimo sumos", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (Integer.valueOf(moneyField.getText().toString()) > balance)
        {
            Toast.makeText(getActivity(), "Neužtenka likučio", Toast.LENGTH_SHORT).show();
            return false;
        }

           else return true;
    }


    private void cashOut() {
        balance = balance - Integer.valueOf(moneyField.getText().toString());
        User user = new User();
        user.setId(userId);
        user.setBalance(balance);
        userApi.updateUserBalance(user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful())
                        {
                            moneyView.setText(String.valueOf(balance));
                            Toast.makeText(getActivity(), "Pinigai išgryninti", Toast.LENGTH_SHORT).show();
                        }
                        else             Toast.makeText(getActivity(), "Įvyko klaida", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

    }

    private void openMoneyTransferView() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View moneyTransfer = getLayoutInflater().inflate(R.layout.money_transfer_popup, null);

        dialogBuilder.setView(moneyTransfer);
        dialog = dialogBuilder.create();
        dialog.show();
        transferRecyclerView = moneyTransfer.findViewById(R.id.moneyTransferList);
        transferRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        int width = (int)(getResources().getDisplayMetrics().widthPixels);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width, height);
        loadTransferMoney();



    }

    private void loadTransferMoney() {
        User user = new User();
        user.setId(userId);
        MoneyTransferApi moneyTransferApi = retrofitService.getRetrofit().create(MoneyTransferApi.class);
        moneyTransferApi.getMoneyTransferByUser(user)
                .enqueue(new Callback<List<MoneyTransfer>>() {
                    @Override
                    public void onResponse(Call<List<MoneyTransfer>> call, Response<List<MoneyTransfer>> response) {
                        if (response.isSuccessful())
                        {
                        populateTransferListView(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MoneyTransfer>> call, Throwable t) {

                    }
                });
    }


    private void populateTransferListView(List<MoneyTransfer> moneyTransfers) {
        MoneyTransferAdapter moneyTransferAdapter = new MoneyTransferAdapter(moneyTransfers, userId, this);
        transferRecyclerView.setAdapter(moneyTransferAdapter);

    }

    public void openCardWindow(){
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View cardPopupWindow = getLayoutInflater().inflate(R.layout.add_card_popup, null);

        dialogBuilder.setView(cardPopupWindow);
        dialog = dialogBuilder.create();
        dialog.show();

        cardNumberField = cardPopupWindow.findViewById(R.id.cardField);
        cvcField = cardPopupWindow.findViewById(R.id.cvcField);
        expireDateField = cardPopupWindow.findViewById(R.id.expireField);
        cancelButton = cardPopupWindow.findViewById(R.id.cancelButton);
        addCardButton = cardPopupWindow.findViewById(R.id.addCardButton);

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


        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.5);
        dialog.getWindow().setLayout(width, height);



        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkIfCardValid())
                {
                    long cardNumber = Long.valueOf(cardNumberField.getText().toString());
                    int cvc = Integer.valueOf(cvcField.getText().toString());
                    int expireDate = Integer.valueOf(expireDateField.getText().toString().replace("/", ""));
                    addCard(cardNumber, cvc, expireDate);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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


    private void addCard(long cardNumber, int cvc, int expireDate ) {

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
                            loadCards();
                        }
                        else Toast.makeText(getActivity(), "Įvyko klaida", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
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

                        balance = user.getBalance();
                        moneyView.setText(String.valueOf(user.getBalance()));

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

    private void populateCardList(List<Card> cardList) {
        CardAdapter cardAdapter = new CardAdapter(cardList, this);
        recyclerView.setAdapter(cardAdapter);
    }

    @Override
    public void passDataFromAdapter(Card card) {
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

}