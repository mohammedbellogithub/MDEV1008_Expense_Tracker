package com.example.mdev1008_expense_tracker;


// Import statements
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// HomeFragment class
public class HomeFragment extends Fragment implements TransactionAdapter.OnTransactionClickListener {

    // Member variables
    private TransactionAdapter adapter;
    private FireBaseHelper fireBaseHelper;
    private Calendar calendar;

    // Default constructor
    public HomeFragment() {
        // Required empty public constructor
    }

    // Static method to create a new instance of HomeFragment
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // onCreate method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireBaseHelper = FireBaseHelper.getInstance();
    }

    // onCreateView method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize adapter and views
        RecyclerView recyclerView = view.findViewById(R.id.transactionRecyclerView);
        CalendarView calendarView = view.findViewById(R.id.calenderId);

        adapter = new TransactionAdapter(getContext(),this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize calendar instance
        calendar = Calendar.getInstance();

        // Set listener for calendar view date change
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Update the selected date in your Calendar instance
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Fetch transactions for the updated date
                getTransactionsByDateAsync();
            }
        });

        // Fetch transactions for the initial date
        getTransactionsByDateAsync();
        return view;
    }

    // Method to handle transaction item click
    @Override
    public void onTransactionClick(TransactionModel transaction) {
        navigateToDetailsFragment(transaction);
    }

    // Method to navigate to transaction details fragment
    private void navigateToDetailsFragment(TransactionModel transaction) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TransactionDetailsFragment detailsFragment = TransactionDetailsFragment.newInstance(transaction);
        fragmentTransaction.replace(R.id.navFragmentView, detailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    // Method to fetch transactions by date asynchronously
    private void getTransactionsByDateAsync() {
        fireBaseHelper.getByParamAsync("transactions", "date", getDate()).addOnSuccessListener(transactions -> {
            adapter.setData(transactions);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch transactions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Method to get formatted date
    private String getDate()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}