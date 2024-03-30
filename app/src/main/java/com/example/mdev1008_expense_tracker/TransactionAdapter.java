package com.example.mdev1008_expense_tracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    private final List<TransactionModel> transactionModelList;

    public interface OnTransactionClickListener {
        void onTransactionClick(TransactionModel transaction);
    }
    private OnTransactionClickListener mListener;

    public TransactionAdapter(Context context){
        transactionModelList = new ArrayList<>();
    }
    public TransactionAdapter(Context context, OnTransactionClickListener listener){
        transactionModelList = new ArrayList<>();
        this.mListener = listener;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<TransactionModel> newData) {
        transactionModelList.clear();
        transactionModelList.addAll(newData);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_row, parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TransactionModel transactionModel = transactionModelList.get(position);
        holder.note.setText(transactionModel.getNote());
        holder.category.setText(transactionModel.getCategory());
        holder.date.setText(transactionModel.getDate());
        holder.amount.setText(String.valueOf(transactionModel.getAmount()));

        // Determine transaction type and update amount accordingly
        if (Objects.equals(transactionModel.getType(), "expense")) {
            holder.amount.setText("- $CAD " + transactionModel.getAmount());
            holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        } else if (Objects.equals(transactionModel.getType(), "income")) {
            holder.amount.setText("+ $CAD " + transactionModel.getAmount());
            holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onTransactionClick(transactionModel);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
       return transactionModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView note,category,amount,date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById((R.id.transaction_note));
            category = itemView.findViewById((R.id.transaction_category));
            date = itemView.findViewById((R.id.transaction_date));
            amount = itemView.findViewById((R.id.transaction_amount));
        }
    }
}
