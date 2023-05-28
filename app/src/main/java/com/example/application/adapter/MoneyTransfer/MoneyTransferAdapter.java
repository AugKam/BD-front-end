package com.example.application.adapter.MoneyTransfer;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.application.Enums.PaymentMethod;
import com.example.application.Enums.PaymentStatus;
import com.example.application.R;
import com.example.application.ds.MoneyTransfer;

import java.util.List;

public class MoneyTransferAdapter extends RecyclerView.Adapter<MoneyTransferHolder>{

    RecycleMoneyTransferViewDataPass recycleViewDataPass;

    private List<MoneyTransfer> moneyTransfers;
    private int userId;

    public MoneyTransferAdapter(List<MoneyTransfer> moneyTransfers, int userId, RecycleMoneyTransferViewDataPass listener) {

        this.moneyTransfers = moneyTransfers;
        this.recycleViewDataPass = listener;
        this.userId = userId;
    }

    @NonNull
    @Override
    public MoneyTransferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.money_transfer_list_item, parent, false);
        return new MoneyTransferHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MoneyTransferHolder holder, int position) {
        MoneyTransfer moneyTransfer = moneyTransfers.get(position);


        if (moneyTransfer.getTripUser().getTrip().getTripOwner().getId() == userId)
        {

            if (moneyTransfer.getPaymentStatus() == PaymentStatus.CANCELED || moneyTransfer.getPaymentStatus() == PaymentStatus.RESERVED)
            {
                holder.itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = 0;
                params.width = 0;
                holder.itemView.setLayoutParams(params);
            }
            else
            {
                if (moneyTransfer.getPaymentMethod() == PaymentMethod.CASH) {
                    if (moneyTransfer.getUsedVirtualMoney() == 0)
                    {
                        holder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
                        holder.itemView.setLayoutParams(params);
                    }
                    else
                    {
                        holder.price.setText("+" + String.valueOf(moneyTransfer.getUsedVirtualMoney()));
                        holder.paymentStatus.setText("Gauta");
                        holder.trip.setText(moneyTransfer.getTripUser().getTrip().getDeparture() +  " - " + moneyTransfer.getTripUser().getTrip().getArrival());
                        holder.name.setText(moneyTransfer.getTripUser().getPassenger().getName() + " " + moneyTransfer.getTripUser().getPassenger().getSurname());
                    }
                }
                else
                {
                    holder.price.setText("+" + String.valueOf(moneyTransfer.getFinalPrice() + moneyTransfer.getUsedVirtualMoney()));
                    holder.paymentStatus.setText("Gauta");
                    holder.trip.setText(moneyTransfer.getTripUser().getTrip().getDeparture() +  " - " + moneyTransfer.getTripUser().getTrip().getArrival());
                    holder.name.setText(moneyTransfer.getTripUser().getPassenger().getName() + " " + moneyTransfer.getTripUser().getPassenger().getSurname());
                }
            }

        } else {

            if (moneyTransfer.getPaymentMethod() == PaymentMethod.CASH)
            {
                if (moneyTransfer.getUsedVirtualMoney() == 0)
                {
                    holder.itemView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }
                else
                {
                    if (moneyTransfer.getPaymentStatus() == PaymentStatus.RESERVED)
                    {
                        holder.price.setText("-" + String.valueOf(moneyTransfer.getUsedVirtualMoney()));
                        holder.paymentStatus.setText("Rezervuota");
                    }

                    if (moneyTransfer.getPaymentStatus() == PaymentStatus.COMPLETED)
                    {
                        holder.paymentStatus.setText("Mokėjimas atliktas");
                        holder.price.setText("-" + String.valueOf(moneyTransfer.getUsedVirtualMoney()));
                    }

                    if (moneyTransfer.getPaymentStatus() == PaymentStatus.CANCELED )
                    {
                        holder.paymentStatus.setText("Grąžinta");
                        holder.price.setText("+" + String.valueOf(moneyTransfer.getUsedVirtualMoney()));
                    }
                    holder.trip.setText(moneyTransfer.getTripUser().getTrip().getDeparture() +  " - " + moneyTransfer.getTripUser().getTrip().getArrival());
                    holder.name.setText(moneyTransfer.getTripUser().getDriver().getName() + " " + moneyTransfer.getTripUser().getDriver().getSurname());

                }
            }
            else
            {
                holder.trip.setText(moneyTransfer.getTripUser().getTrip().getDeparture() +  " - " + moneyTransfer.getTripUser().getTrip().getArrival());
                holder.name.setText(moneyTransfer.getTripUser().getDriver().getName() + " " + moneyTransfer.getTripUser().getDriver().getSurname());


                if (moneyTransfer.getPaymentStatus() == PaymentStatus.RESERVED)
                {
                    holder.price.setText("-" + String.valueOf(moneyTransfer.getFinalPrice() + moneyTransfer.getUsedVirtualMoney()));
                    holder.paymentStatus.setText("Rezervuota");
                }

                if (moneyTransfer.getPaymentStatus() == PaymentStatus.COMPLETED)
                {
                    holder.paymentStatus.setText("Mokėjimas atliktas");
                    holder.price.setText("-" + String.valueOf(moneyTransfer.getFinalPrice() + moneyTransfer.getUsedVirtualMoney()));
                }

                if (moneyTransfer.getPaymentStatus() == PaymentStatus.CANCELED )
                {
                    holder.paymentStatus.setText("Grąžinta");
                    holder.price.setText("+" + String.valueOf(moneyTransfer.getFinalPrice() + moneyTransfer.getUsedVirtualMoney()));
                }

            }

        }
    }




    @Override
    public int getItemCount() {
        return moneyTransfers.size();
    }
}
