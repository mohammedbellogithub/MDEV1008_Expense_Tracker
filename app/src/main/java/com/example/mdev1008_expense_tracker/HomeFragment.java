package com.example.mdev1008_expense_tracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class HomeFragment extends Fragment implements TransactionAdapter.OnTransactionClickListener {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private FireBaseHelper fireBaseHelper;
    private CalendarView calendarView;
    private Calendar calendar;

    public HomeFragment() {
        // Required empty public constructor
    }

   public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.transactionRecyclerView);
        calendarView = view.findViewById(R.id.calenderId);

        adapter = new TransactionAdapter(getContext(),this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        calendar = Calendar.getInstance();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Update the selected date in your Calendar instance
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = dateFormat.format(calendar.getTime());

                // Fetch transactions for the updated date
                getTransactionsByDateAsync();
            }
        });

        getTransactionsByDateAsync();
        return view;
    }
    @Override
    public void onTransactionClick(TransactionModel transaction) {
        navigateToDetailsFragment(transaction);
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
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch transactions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    private void getTransactionsByDateAsync() {
        fireBaseHelper.getByParamAsync("transactions", "date", getDate()).addOnSuccessListener(transactions -> {
            adapter.setData(transactions);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch transactions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private String getDate()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}