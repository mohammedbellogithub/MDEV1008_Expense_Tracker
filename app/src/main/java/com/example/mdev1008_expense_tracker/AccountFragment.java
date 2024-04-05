package com.example.mdev1008_expense_tracker;

// Import statements
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

// AccountFragment class
public class AccountFragment extends Fragment implements TransactionAdapter.OnTransactionClickListener {

    // Member variables
    private TransactionAdapter adapter;
    private TextView textBalanceAmount;
    private FireBaseHelper fireBaseHelper;

    // Default constructor
    public AccountFragment() {
        // Required empty public constructor
    }

    // Static method to create a new instance of AccountFragment
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize views
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_transactions);
        textBalanceAmount = view.findViewById(R.id.transaction_balance);
        TextView accountHolderText = view.findViewById(R.id.text_card_holder);

        // Set account holder text
        accountHolderText.setText(fireBaseHelper.getUserEmailWithoutDomain());

        // Initialize adapter and RecyclerView
        adapter = new TransactionAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        EditText searchText = view.findViewById(R.id.edit_text_search);

        // Set up search functionality
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Call searchTransaction when Enter key is pressed
                    searchTransaction(searchText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // Set up search button
        Button searchButton = view.findViewById(R.id.account_search_button);
        searchButton.setOnClickListener((search) -> searchTransaction(searchText.getText().toString()));

        // Fetch transactions asynchronously
        getTransactionsAsync();
        return view;
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

    // Method to fetch transactions asynchronously
    private void getTransactionsAsync() {
        fireBaseHelper.getAllAsync("transactions").addOnSuccessListener(transactions -> {
            adapter.setData(transactions);
            calculateAccountBalance(transactions);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch transactions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Method to calculate account balance
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

    // Method to handle transaction item click
    @Override
    public void onTransactionClick(TransactionModel transaction) {
        navigateToDetailsFragment(transaction);
    }

    // Method to search for transactions
    public void searchTransaction(String param){
        fireBaseHelper.getByParamAsync("transactions", "note", param).addOnSuccessListener(transactions -> {
            adapter.setData(transactions);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch transactions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}