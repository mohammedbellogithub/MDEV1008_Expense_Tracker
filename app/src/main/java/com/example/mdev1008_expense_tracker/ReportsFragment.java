package com.example.mdev1008_expense_tracker;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mdev1008_expense_tracker.databinding.FragmentReportsBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment implements TransactionAdapter.OnTransactionClickListener {
    private FireBaseHelper fireBaseHelper;
    private FragmentReportsBinding binding;
    private PieChart pieChart;

    // Callback interface to handle transaction loading results
    private interface TransactionsCallback {
        void onTransactionsLoaded(List<TransactionModel> transactions);
        void onFailure(String errorMessage);
    }

    // Default constructor
    public ReportsFragment() {
    }

    // Static method to create a new instance of the fragment
    public static ReportsFragment newInstance(String param1, String param2) {
        ReportsFragment fragment = new ReportsFragment();
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
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize RecyclerView for displaying transactions
        RecyclerView recyclerView = view.findViewById(R.id.reports_transactions);
        TransactionAdapter adapter = new TransactionAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch transactions asynchronously and update UI
        getTransactionsAsync(new TransactionsCallback() {
            @Override
            public void onTransactionsLoaded(List<TransactionModel> transactions) {
                adapter.setData(transactions);
                setupPieChart(transactions);
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to fetch transactions: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    // Fetch transactions asynchronously
    private void getTransactionsAsync(TransactionsCallback callback)  {
        fireBaseHelper.getAllAsync("transactions")
                .addOnSuccessListener(callback::onTransactionsLoaded)
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }

    // Set up the pie chart with income and expense data
    private void setupPieChart(List<TransactionModel> transactions) {
        // Calculate total income and expense from transactions
        double totalIncome = 0;
        double totalExpense = 0;
        for (TransactionModel transaction : transactions) {
            if (transaction.getType().equals("income")) {
                totalIncome += transaction.getAmount();
            } else if (transaction.getType().equals("expense")) {
                totalExpense += transaction.getAmount();
            }
        }

        // Create pie chart entries
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colorList = new ArrayList<>();
        if (totalExpense != 0 ){
            entries.add(new PieEntry((float) totalExpense, "Expense"));
            colorList.add(getResources().getColor(R.color.red));
        }
        if (totalIncome != 0 ){
            entries.add(new PieEntry((float) totalIncome, "Income"));;
            colorList.add(getResources().getColor(R.color.green));
        }

        // Configure pie chart dataset
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colorList);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        dataSet.setValueFormatter(new MyValueFormatter());
        dataSet.setSliceSpace(3f);

        // Create pie chart data and apply to the chart
        PieData data = new PieData(dataSet);
        binding.reportPieChart.setData(data);
        binding.reportPieChart.getDescription().setEnabled(false);
        binding.reportPieChart.invalidate();
    }

    // Handle transaction item click
    @Override
    public void onTransactionClick(TransactionModel transaction) {
        navigateToDetailsFragment(transaction);
    }

    // Navigate to transaction details fragment
    private void navigateToDetailsFragment(TransactionModel transaction) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TransactionDetailsFragment detailsFragment = TransactionDetailsFragment.newInstance(transaction);
        fragmentTransaction.replace(R.id.navFragmentView, detailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // Custom value formatter for the pie chart
    private static class MyValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            Locale locale = new Locale("en", "CA");
            Currency currency = Currency.getInstance("CAD");
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
            currencyFormatter.setCurrency(currency);
            return currencyFormatter.format(value);
        }
    }

}
