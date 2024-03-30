package com.example.mdev1008_expense_tracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class AccountFragment extends Fragment implements TransactionAdapter.OnTransactionClickListener {
    private TransactionAdapter adapter;
    private TextView textBalanceAmount;
    private FireBaseHelper fireBaseHelper;
    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireBaseHelper = FireBaseHelper.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_transactions);
        textBalanceAmount = view.findViewById(R.id.transaction_balance);
        TextView accountHolderText = view.findViewById(R.id.text_card_holder);
        accountHolderText.setText(fireBaseHelper.getUserEmailWithoutDomain());
        adapter = new TransactionAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getTransactionsAsync();
        return view;
    }
    private void navigateToDetailsFragment(TransactionModel transaction) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TransactionDetailsFragment detailsFragment = TransactionDetailsFragment.newInstance(transaction);
        fragmentTransaction.replace(R.id.navFragmentView, detailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void getTransactionsAsync() {
        fireBaseHelper.getAllAsync("transactions").addOnSuccessListener(transactions -> {
            adapter.setData(transactions);
            calculateAccountBalance(transactions);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch transactions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void calculateAccountBalance(List<TransactionModel> transactions) {
        double totalIncome = 0;
        double totalExpenses = 0;

        for (TransactionModel transaction : transactions) {
            if (transaction.getType().equals("income")) {
                totalIncome += transaction.getAmount();
            } else if (transaction.getType().equals("expense")) {
                totalExpenses += transaction.getAmount();
            }
        }

        double accountBalance = totalIncome - totalExpenses;
        String balanceText = String.format(Locale.getDefault(), "$%.2f", accountBalance);

        textBalanceAmount.setText(balanceText);
    }

    @Override
    public void onTransactionClick(TransactionModel transaction) {
        navigateToDetailsFragment(transaction);
    }
}